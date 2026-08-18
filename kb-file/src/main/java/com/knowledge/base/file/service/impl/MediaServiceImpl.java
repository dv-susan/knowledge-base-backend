package com.knowledge.base.file.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.knowledge.base.common.exception.BusinessException;
import com.knowledge.base.file.config.FileStorageProperties;
import com.knowledge.base.file.entity.FileInfo;
import com.knowledge.base.file.mapper.FileMapper;
import com.knowledge.base.file.service.MediaService;
import com.knowledge.base.file.storage.FileStorage;
import com.knowledge.base.file.storage.FileStorageFactory;
import com.knowledge.base.file.vo.MediaMetadata;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.TimeUnit;

/**
 * 媒体处理服务实现
 * 通过命令行调用FFmpeg/FFprobe实现音视频处理
 *
 * @author 苏三
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MediaServiceImpl extends ServiceImpl<FileMapper, FileInfo> implements MediaService {

    private final FileMapper fileMapper;
    private final FileStorageFactory storageFactory;
    private final FileStorageProperties storageProperties;

    @Override
    public MediaMetadata probeMediaInfo(Long fileId) {
        FileInfo fileInfo = fileMapper.selectById(fileId);
        if (fileInfo == null) {
            throw new BusinessException("文件不存在");
        }

        String ffprobePath = storageProperties.getFfmpeg().getFfprobePath();

        // 检查 ffprobe 是否可用，不可用则跳过元数据提取，仅记录 WARN
        if (!new File(ffprobePath).exists()) {
            log.warn("ffprobe 不可用（路径：{}），跳过媒体元数据提取：fileId={}", ffprobePath, fileId);
            return MediaMetadata.builder().build();
        }

        Path tempFile = null;

        try {
            // 从RUSTFS下载文件到临时目录
            tempFile = downloadToTemp(fileInfo);
            log.info("提取媒体元数据：fileId={}, tempPath={}", fileId, tempFile);

            // 调用ffprobe
            ProcessBuilder pb = new ProcessBuilder(
                    ffprobePath,
                    "-v", "quiet",
                    "-print_format", "json",
                    "-show_format",
                    "-show_streams",
                    tempFile.toString()
            );

            Process process = pb.start();
            String output = readProcessOutput(process);
            boolean completed = process.waitFor(30, TimeUnit.SECONDS);

            if (!completed || process.exitValue() != 0) {
                String errorOutput = readErrorOutput(process);
                log.error("ffprobe执行失败：exitCode={}, error={}", process.exitValue(), errorOutput);
                throw new BusinessException("提取媒体元数据失败");
            }

            return parseFfprobeOutput(output);

        } catch (IOException | InterruptedException e) {
            log.warn("提取媒体元数据异常（不影响上传）：fileId={}, error={}", fileId, e.getMessage());
            return MediaMetadata.builder().build();
        } finally {
            deleteTempFile(tempFile);
        }
    }

    @Override
    public String transcodeToHls(Long fileId) {
        FileInfo fileInfo = fileMapper.selectById(fileId);
        if (fileInfo == null) {
            throw new BusinessException("文件不存在");
        }

        String ffmpegPath = storageProperties.getFfmpeg().getPath();

        // 检查 ffmpeg 是否可用
        if (!new File(ffmpegPath).exists()) {
            log.warn("ffmpeg 不可用（路径：{}），跳过HLS转码：fileId={}", ffmpegPath, fileId);
            throw new BusinessException("HLS转码服务不可用，请安装ffmpeg后重试");
        }

        int segmentTime = storageProperties.getFfmpeg().getHlsSegmentTime();
        Path tempFile = null;
        Path hlsDir = null;

        try {
            // 从RUSTFS下载原始文件
            tempFile = downloadToTemp(fileInfo);
            log.info("开始HLS转码：fileId={}, tempPath={}", fileId, tempFile);

            // 创建HLS临时目录
            hlsDir = Files.createTempDirectory("hls_" + fileId + "_");
            String hlsBasePath = fileInfo.getFilePath().replaceFirst("\\.[^.]+$", "");
            String hlsRelativePath = hlsBasePath + "/hls";

            // 转码360P
            Path hls360Dir = Paths.get(hlsDir.toString(), "360p");
            Files.createDirectories(hls360Dir);
            transcodeResolution(ffmpegPath, tempFile.toString(), hls360Dir, segmentTime,
                    640, 360, 28, 64);

            // 转码720P
            Path hls720Dir = Paths.get(hlsDir.toString(), "720p");
            Files.createDirectories(hls720Dir);
            transcodeResolution(ffmpegPath, tempFile.toString(), hls720Dir, segmentTime,
                    1280, 720, 23, 128);

            // 生成master播放列表
            String masterPlaylist = generateMasterPlaylist();
            Path masterFile = Paths.get(hlsDir.toString(), "master.m3u8");
            Files.writeString(masterFile, masterPlaylist);

            // 上传HLS文件到RUSTFS
            FileStorage storage = storageFactory.getStorage();
            uploadHlsFiles(storage, hlsDir, hlsRelativePath);

            // 更新DB中的HLS路径
            fileInfo.setHlsPath(hlsRelativePath);
            fileMapper.updateById(fileInfo);

            log.info("HLS转码完成：fileId={}, hlsPath={}", fileId, hlsRelativePath);
            return hlsRelativePath;

        } catch (Exception e) {
            log.error("HLS转码失败：fileId={}, error={}", fileId, e.getMessage(), e);
            throw new BusinessException("HLS转码失败: " + e.getMessage());
        } finally {
            deleteTempFile(tempFile);
            deleteTempDir(hlsDir);
        }
    }

    @Override
    public String generateThumbnail(Long fileId) {
        FileInfo fileInfo = fileMapper.selectById(fileId);
        if (fileInfo == null) {
            throw new BusinessException("文件不存在");
        }

        String ffmpegPath = storageProperties.getFfmpeg().getPath();

        // 检查 ffmpeg 是否可用
        if (!new File(ffmpegPath).exists()) {
            log.warn("ffmpeg 不可用（路径：{}），跳过缩略图生成：fileId={}", ffmpegPath, fileId);
            throw new BusinessException("缩略图服务不可用，请安装ffmpeg后重试");
        }

        int thumbnailTime = storageProperties.getFfmpeg().getThumbnailTime();
        Path tempFile = null;
        Path thumbnailFile = null;

        try {
            tempFile = downloadToTemp(fileInfo);
            thumbnailFile = Files.createTempFile("thumbnail_", ".jpg");
            log.info("生成缩略图：fileId={}, thumbnailPath={}", fileId, thumbnailFile);

            ProcessBuilder pb = new ProcessBuilder(
                    ffmpegPath,
                    "-i", tempFile.toString(),
                    "-ss", formatTime(thumbnailTime),
                    "-vframes", "1",
                    "-q:v", "2",
                    "-y",
                    thumbnailFile.toString()
            );

            Process process = pb.start();
            boolean completed = process.waitFor(30, TimeUnit.SECONDS);

            if (!completed || process.exitValue() != 0) {
                String errorOutput = readErrorOutput(process);
                log.error("缩略图生成失败：exitCode={}, error={}", process.exitValue(), errorOutput);
                throw new BusinessException("生成缩略图失败");
            }

            // 上传缩略图到RUSTFS
            String basePath = fileInfo.getFilePath().replaceFirst("\\.[^.]+$", "");
            String thumbnailRelativePath = basePath + "/thumbnail.jpg";

            FileStorage storage = storageFactory.getStorage();
            try (InputStream is = new FileInputStream(thumbnailFile.toFile())) {
                long fileSize = Files.size(thumbnailFile);
                storage.upload(is, thumbnailRelativePath, fileSize);
            }

            // 更新DB中的缩略图路径
            fileInfo.setThumbnailPath(thumbnailRelativePath);
            fileMapper.updateById(fileInfo);

            log.info("缩略图生成完成：fileId={}, thumbnailPath={}", fileId, thumbnailRelativePath);
            return thumbnailRelativePath;

        } catch (Exception e) {
            log.error("缩略图生成失败：fileId={}, error={}", fileId, e.getMessage(), e);
            throw new BusinessException("生成缩略图失败: " + e.getMessage());
        } finally {
            deleteTempFile(tempFile);
            deleteTempFile(thumbnailFile);
        }
    }

    @Override
    public void updateTranscodeStatus(Long fileId, String status) {
        FileInfo fileInfo = fileMapper.selectById(fileId);
        if (fileInfo != null) {
            fileInfo.setTranscodeStatus(status);
            fileMapper.updateById(fileInfo);
            log.info("更新转码状态：fileId={}, status={}", fileId, status);
        }
    }

    // ==================== 私有方法 ====================

    /**
     * 从RUSTFS下载文件到临时目录
     */
    private Path downloadToTemp(FileInfo fileInfo) throws IOException {
        FileStorage storage = storageFactory.getStorage();
        try (InputStream is = storage.getInputStream(fileInfo.getFilePath())) {
            String extension = FileUtil.extName(fileInfo.getOriginalName());
            Path tempFile = Files.createTempFile("media_", "." + (extension.isEmpty() ? "tmp" : extension));
            Files.copy(is, tempFile, StandardCopyOption.REPLACE_EXISTING);
            return tempFile;
        }
    }

    /**
     * 转码指定分辨率的HLS
     */
    private void transcodeResolution(String ffmpegPath, String inputPath, Path outputDir,
                                      int segmentTime, int width, int height, int crf, int audioBitrate)
            throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder(
                ffmpegPath,
                "-i", inputPath,
                "-c:v", "libx264",
                "-preset", "fast",
                "-crf", String.valueOf(crf),
                "-vf", "scale=" + width + ":" + height,
                "-c:a", "aac",
                "-b:a", audioBitrate + "k",
                "-hls_time", String.valueOf(segmentTime),
                "-hls_list_size", "0",
                "-hls_segment_filename", outputDir + "/%03d.ts",
                "-y",
                outputDir + "/index.m3u8"
        );

        log.info("转码{}P：{}", height, String.join(" ", pb.command()));
        Process process = pb.start();
        boolean completed = process.waitFor(300, TimeUnit.SECONDS); // 5分钟超时

        if (!completed || process.exitValue() != 0) {
            String errorOutput = readErrorOutput(process);
            throw new BusinessException("转码" + height + "P失败: " + errorOutput);
        }
    }

    /**
     * 生成Master HLS播放列表
     */
    private String generateMasterPlaylist() {
        return """
                #EXTM3U
                #EXT-X-STREAM-INF:BANDWIDTH=600000,RESOLUTION=640x360
                360p/index.m3u8
                #EXT-X-STREAM-INF:BANDWIDTH=1500000,RESOLUTION=1280x720
                720p/index.m3u8
                """;
    }

    /**
     * 上传HLS目录下所有文件到RUSTFS
     */
    private void uploadHlsFiles(FileStorage storage, Path hlsDir, String hlsRelativePath)
            throws IOException {
        // 上传master.m3u8
        Path masterFile = hlsDir.resolve("master.m3u8");
        try (InputStream is = new FileInputStream(masterFile.toFile())) {
            storage.upload(is, hlsRelativePath + "/master.m3u8", Files.size(masterFile));
        }

        // 上传各分辨率目录
        for (String quality : new String[]{"360p", "720p"}) {
            Path qualityDir = hlsDir.resolve(quality);
            if (!Files.isDirectory(qualityDir)) {
                continue;
            }
            try (var files = Files.list(qualityDir)) {
                files.forEach(file -> {
                    try (InputStream is = new FileInputStream(file.toFile())) {
                        String relativeKey = hlsRelativePath + "/" + quality + "/" + file.getFileName();
                        storage.upload(is, relativeKey, file.toFile().length());
                    } catch (IOException e) {
                        throw new RuntimeException("上传HLS分片失败: " + file.getFileName(), e);
                    }
                });
            }
        }
    }

    /**
     * 解析ffprobe JSON输出
     */
    private MediaMetadata parseFfprobeOutput(String jsonOutput) {
        try {
            JSONObject root = JSONUtil.parseObj(jsonOutput);
            JSONObject format = root.getJSONObject("format");
            JSONArray streams = root.getJSONArray("streams");

            Integer duration = format != null ? format.getDouble("duration").intValue() : null;
            Integer bitrate = format != null ? format.getInt("bit_rate") : null;
            // bit_rate单位是bps，转换为kbps
            if (bitrate != null) {
                bitrate = bitrate / 1000;
            }

            String resolution = null;
            String videoCodec = null;
            String audioCodec = null;

            if (streams != null) {
                for (int i = 0; i < streams.size(); i++) {
                    JSONObject stream = streams.getJSONObject(i);
                    String codecType = stream.getStr("codec_type");
                    if ("video".equals(codecType)) {
                        Integer width = stream.getInt("width");
                        Integer height = stream.getInt("height");
                        if (width != null && height != null) {
                            resolution = width + "x" + height;
                        }
                        videoCodec = stream.getStr("codec_name");
                    } else if ("audio".equals(codecType)) {
                        audioCodec = stream.getStr("codec_name");
                    }
                }
            }

            return MediaMetadata.builder()
                    .duration(duration)
                    .resolution(resolution)
                    .bitrate(bitrate)
                    .videoCodec(videoCodec)
                    .audioCodec(audioCodec)
                    .build();

        } catch (Exception e) {
            log.error("解析ffprobe输出失败：{}", e.getMessage(), e);
            return MediaMetadata.builder().build();
        }
    }

    /**
     * 读取进程标准输出
     */
    private String readProcessOutput(Process process) throws IOException {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            return sb.toString();
        }
    }

    /**
     * 读取进程错误输出
     */
    private String readErrorOutput(Process process) throws IOException {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getErrorStream()))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            return sb.toString();
        }
    }

    /**
     * 格式化秒数为 HH:MM:SS
     */
    private String formatTime(int seconds) {
        int h = seconds / 3600;
        int m = (seconds % 3600) / 60;
        int s = seconds % 60;
        return String.format("%02d:%02d:%02d", h, m, s);
    }

    /**
     * 删除临时文件
     */
    private void deleteTempFile(Path path) {
        if (path != null) {
            try {
                Files.deleteIfExists(path);
            } catch (IOException e) {
                log.warn("删除临时文件失败：{}", path);
            }
        }
    }

    /**
     * 递归删除临时目录
     */
    private void deleteTempDir(Path dir) {
        if (dir != null && Files.exists(dir)) {
            try {
                FileUtil.del(dir.toFile());
            } catch (Exception e) {
                log.warn("删除临时目录失败：{}", dir);
            }
        }
    }
}
