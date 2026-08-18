package com.knowledge.base.document.service.impl;

import com.knowledge.base.document.service.FileUploadService;
import com.knowledge.base.document.service.MarkdownProcessService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Markdown处理服务实现类
 *
 * <p>处理Markdown内容，包括图片URL替换</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Slf4j
@Service
public class MarkdownProcessServiceImpl implements MarkdownProcessService {

    @Resource
    private FileUploadService fileUploadService;

    /**
     * Markdown图片正则表达式
     * 匹配格式：![alt](url) 或 ![alt](url "title")
     */
    private static final Pattern IMAGE_PATTERN = Pattern.compile(
            "!\\[([^\\]]*)\\]\\(([^)]+)\\)"
    );

    /**
     * HTML图片标签正则表达式
     * 匹配格式：<img src="url" />
     */
    private static final Pattern HTML_IMAGE_PATTERN = Pattern.compile(
            "<img[^>]+src=[\"']([^\"']+)[\"'][^>]*>"
    );

    @Override
    public MarkdownProcessResult processImages(String content) {
        if (!StringUtils.hasText(content)) {
            return new MarkdownProcessResult(content, Collections.emptyMap(), 0, 0);
        }

        log.info("开始处理Markdown中的图片");

        // 提取所有图片URL
        List<String> imageUrls = extractImageUrls(content);
        if (imageUrls.isEmpty()) {
            log.info("未发现需要处理的图片");
            return new MarkdownProcessResult(content, Collections.emptyMap(), 0, 0);
        }

        log.info("发现{}个图片URL", imageUrls.size());

        // 上传外部图片并构建URL映射
        Map<String, String> urlMappings = new HashMap<>();
        int successCount = 0;
        int failureCount = 0;

        for (String imageUrl : imageUrls) {
            if (fileUploadService.isExternalImageUrl(imageUrl)) {
                try {
                    String newUrl = fileUploadService.uploadImageFromUrl(imageUrl);
                    urlMappings.put(imageUrl, newUrl);
                    successCount++;
                    log.info("图片上传成功：{} -> {}", imageUrl, newUrl);
                } catch (Exception e) {
                    log.error("图片上传失败：{}", imageUrl, e);
                    failureCount++;
                }
            }
        }

        // 替换Markdown中的图片URL
        String processedContent = replaceImageUrls(content, urlMappings);

        log.info("图片处理完成：成功{}个，失败{}个", successCount, failureCount);

        return new MarkdownProcessResult(processedContent, urlMappings, successCount, failureCount);
    }

    @Override
    public List<String> extractImageUrls(String content) {
        if (!StringUtils.hasText(content)) {
            return Collections.emptyList();
        }

        Set<String> urls = new LinkedHashSet<>();

        // 提取Markdown格式的图片
        Matcher matcher = IMAGE_PATTERN.matcher(content);
        while (matcher.find()) {
            String url = matcher.group(2);
            // 移除URL中的title部分
            int spaceIndex = url.indexOf(' ');
            if (spaceIndex > 0) {
                url = url.substring(0, spaceIndex);
            }
            urls.add(url.trim());
        }

        // 提取HTML格式的图片
        matcher = HTML_IMAGE_PATTERN.matcher(content);
        while (matcher.find()) {
            String url = matcher.group(1);
            urls.add(url);
        }

        return new ArrayList<>(urls);
    }

    @Override
    public String replaceImageUrls(String content, Map<String, String> urlMappings) {
        if (urlMappings == null || urlMappings.isEmpty()) {
            return content;
        }

        String result = content;

        // 替换Markdown格式的图片URL
        for (Map.Entry<String, String> entry : urlMappings.entrySet()) {
            String oldUrl = entry.getKey();
            String newUrl = entry.getValue();

            // 转义正则特殊字符
            String escapedOldUrl = Pattern.quote(oldUrl);

            // 替换 ![alt](oldUrl) 格式
            result = result.replaceAll(
                    "(!\\[[^\\]]*\\]\\()" + escapedOldUrl + "(\\))",
                    "$1" + newUrl + "$2"
            );

            // 替换HTML格式的图片URL
            result = result.replaceAll(
                    "(<img[^>]+src=[\"'])" + escapedOldUrl + "([\"'][^>]*>)",
                    "$1" + newUrl + "$2"
            );
        }

        return result;
    }

    @Override
    public String generateSummary(String content, int maxLength) {
        if (!StringUtils.hasText(content)) {
            return "";
        }

        // 简单的Markdown转HTML（移除图片、代码块等）
        String summary = content
                // 移除图片
                .replaceAll("!\\[[^\\]]*\\]\\([^)]+\\)", "")
                // 移除代码块
                .replaceAll("```[\\s\\S]*?```", "")
                // 移除行内代码
                .replaceAll("`[^`]+`", "")
                // 移除链接
                .replaceAll("\\[[^\\]]+\\]\\([^)]+\\)", "")
                // 移除标题符号
                .replaceAll("^#+\\s*", "")
                // 移除加粗、斜体等格式符号
                .replaceAll("[*_*#]+", "")
                // 移除多余的空行
                .replaceAll("\\n+", "\n")
                .trim();

        // 截取指定长度
        if (summary.length() > maxLength) {
            summary = summary.substring(0, maxLength) + "...";
        }

        return summary;
    }
}
