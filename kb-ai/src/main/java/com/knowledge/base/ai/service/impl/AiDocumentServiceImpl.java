package com.knowledge.base.ai.service.impl;

import com.knowledge.base.ai.config.ModelProvider;
import com.knowledge.base.ai.dto.DocumentProcessDTO;
import com.knowledge.base.ai.service.AiDocumentService;
import com.knowledge.base.ai.vo.DocumentProcessVO;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.output.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * AI文档处理服务实现类
 *
 * <p>按照阿里巴巴Java开发规范设计，实现AI文档处理相关业务逻辑</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AiDocumentServiceImpl implements AiDocumentService {

    private final ModelProvider modelProvider;
    private final StringRedisTemplate redisTemplate;

    private static final String SUMMARY_CACHE_PREFIX = "ai:summary:";
    private static final Duration SUMMARY_CACHE_TTL = Duration.ofDays(7);

    /**
     * 计算内容摘要的缓存Key，基于内容MD5去重
     *
     * @param content 文档内容
     * @return 缓存Key
     */
    private String buildSummaryCacheKey(String content) {
        return SUMMARY_CACHE_PREFIX + DigestUtils.md5DigestAsHex(content.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 从Redis缓存中获取摘要，缓存命中返回摘要内容，未命中返回null
     *
     * @param content 文档内容
     * @return 缓存的摘要内容，未命中返回null
     */
    private String getCachedSummary(String content) {
        try {
            if (redisTemplate == null) return null;
            return redisTemplate.opsForValue().get(buildSummaryCacheKey(content));
        } catch (Exception e) {
            log.warn("读取摘要缓存失败：{}", e.getMessage());
            return null;
        }
    }

    /**
     * 将摘要写入Redis缓存
     *
     * @param content 文档内容
     * @param summary 摘要内容
     */
    private void cacheSummary(String content, String summary) {
        try {
            if (redisTemplate == null) return;
            redisTemplate.opsForValue().set(buildSummaryCacheKey(content), summary, SUMMARY_CACHE_TTL);
            log.info("摘要已缓存：key={}", buildSummaryCacheKey(content));
        } catch (Exception e) {
            log.warn("写入摘要缓存失败：{}", e.getMessage());
        }
    }

    /**
     * 生成文档摘要
     *
     * @param content 文档内容
     * @param length  摘要长度
     * @return 摘要内容
     */
    @Override
    public String generateSummary(String content, Integer length) {
        log.info("生成文档摘要：contentLength={}, summaryLength={}", content.length(), length);

        if (length == null || length <= 0) {
            length = 200;
        }

        // 截断长内容，与generateSummaryByContent保持一致
        String truncContent = truncateContent(content, 8000);

        // 先查Redis缓存
        String cached = getCachedSummary(truncContent);
        if (cached != null) {
            log.info("摘要缓存命中");
            return cached;
        }

        String prompt = String.format("""
                请为以下文档生成一个简洁的摘要，要求：
                1. 摘要长度约%d字
                2. 涵盖文档的核心内容和主要观点
                3. 语言简洁明了
                4. 不要添加任何额外的解释

                文档内容：
                %s
                """, length, truncContent);

        try {
            UserMessage userMessage = UserMessage.from(prompt);
            Response<AiMessage> response = modelProvider.getDefaultModel().generate(userMessage);
            String summary = response.content().text().trim();

            // 写入Redis缓存
            cacheSummary(truncContent, summary);

            return summary;
        } catch (Exception e) {
            log.error("生成摘要失败：{}", e.getMessage(), e);
            throw new RuntimeException("生成摘要失败: " + e.getMessage());
        }
    }

    /**
     * 生成文档大纲
     *
     * @param content 文档内容
     * @param level   大纲层级
     * @return 大纲内容
     */
    @Override
    public String generateOutline(String content, Integer level) {
        log.info("生成文档大纲：contentLength={}, level={}", content.length(), level);

        if (level == null || level <= 0 || level > 3) {
            level = 2;
        }

        String prompt = String.format("""
                请为以下文档生成一个结构化的大纲，要求：
                1. 大纲层级为%d级
                2. 使用标准的标题层级格式（如#、##、###）
                3. 体现文档的逻辑结构和主要内容
                4. 每个层级用"一、"、"二、"等标记
                5. 不要添加任何额外的解释

                文档内容：
                %s
                """, level, content);

        try {
            UserMessage userMessage = UserMessage.from(prompt);
            Response<AiMessage> response = modelProvider.getDefaultModel().generate(userMessage);
            return response.content().text().trim();
        } catch (Exception e) {
            log.error("生成大纲失败：{}", e.getMessage(), e);
            throw new RuntimeException("生成大纲失败: " + e.getMessage());
        }
    }

    /**
     * 扩展内容
     *
     * @param content 文档内容
     * @param expType 扩展类型
     * @return 扩展内容
     */
    @Override
    public String expandContent(String content, String expType) {
        log.info("扩展内容：contentLength={}, expType={}", content.length(), expType);

        String typeDesc = getTypeDescription(expType);
        String prompt = String.format("""
                请对以下文档内容进行%s，要求：
                1. 保持原文档的风格和语气
                2. 扩展内容要符合文档主题
                3. 扩展后的内容要连贯、逻辑清晰
                4. 不要添加任何额外的解释

                原文档内容：
                %s
                """, typeDesc, content);

        try {
            UserMessage userMessage = UserMessage.from(prompt);
            Response<AiMessage> response = modelProvider.getDefaultModel().generate(userMessage);
            return response.content().text().trim();
        } catch (Exception e) {
            log.error("扩展内容失败：{}", e.getMessage(), e);
            throw new RuntimeException("扩展内容失败: " + e.getMessage());
        }
    }

    /**
     * 优化表达
     *
     * @param content 文档内容
     * @param target  优化目标
     * @return 优化内容
     */
    @Override
    public String optimizeContent(String content, String target) {
        log.info("优化内容：contentLength={}, target={}", content.length(), target);

        String targetDesc = getOptimizationDescription(target);
        String prompt = String.format("""
                请优化以下文档内容，要求：
                %s
                5. 保持原文档的核心意思和信息
                6. 优化后的内容要更加专业、易读
                7. 不要添加任何额外的解释

                原文档内容：
                %s
                """, targetDesc, content);

        try {
            UserMessage userMessage = UserMessage.from(prompt);
            Response<AiMessage> response = modelProvider.getDefaultModel().generate(userMessage);
            return response.content().text().trim();
        } catch (Exception e) {
            log.error("优化内容失败：{}", e.getMessage(), e);
            throw new RuntimeException("优化内容失败: " + e.getMessage());
        }
    }

    /**
     * 添加示例
     *
     * @param content 文档内容
     * @param expType 示例类型
     * @return 添加示例后的内容
     */
    @Override
    public String addExample(String content, String expType) {
        log.info("添加示例：contentLength={}, expType={}", content.length(), expType);

        String typeDesc = getExampleDescription(expType);
        String prompt = String.format("""
                请为以下文档内容添加合适的示例，要求：
                %s
                3. 示例要与文档内容紧密相关
                4. 示例要具体、实用
                5. 在适当的位置插入示例
                6. 返回完整的文档内容（包含新增的示例）
                7. 不要添加任何额外的解释

                原文档内容：
                %s
                """, typeDesc, content);

        try {
            UserMessage userMessage = UserMessage.from(prompt);
            Response<AiMessage> response = modelProvider.getDefaultModel().generate(userMessage);
            return response.content().text().trim();
        } catch (Exception e) {
            log.error("添加示例失败：{}", e.getMessage(), e);
            throw new RuntimeException("添加示例失败: " + e.getMessage());
        }
    }

    /**
     * 处理文档（统一入口）
     *
     * @param processDTO 处理请求
     * @return 处理结果
     */
    @Override
    public DocumentProcessVO processDocument(DocumentProcessDTO processDTO) {
        log.info("处理文档：processType={}", processDTO.getProcessType());

        DocumentProcessVO result = new DocumentProcessVO();
        result.setProcessType(processDTO.getProcessType());

        try {
            String processedContent;

            switch (processDTO.getProcessType()) {
                case "summary":
                    Integer summaryLength = processDTO.getProcessParams() != null
                            ? processDTO.getProcessParams().getSummaryLength()
                            : 200;
                    processedContent = generateSummary(processDTO.getContent(), summaryLength);
                    result.setProcessedContent(processedContent);
                    break;

                case "outline":
                    Integer outlineLevel = processDTO.getProcessParams() != null
                            ? processDTO.getProcessParams().getOutlineLevel()
                            : 2;
                    processedContent = generateOutline(processDTO.getContent(), outlineLevel);
                    result.setProcessedContent(processedContent);
                    break;

                case "expansion":
                    String expansionType = processDTO.getProcessParams() != null
                            ? processDTO.getProcessParams().getExpansionType()
                            : "detail";
                    processedContent = expandContent(processDTO.getContent(), expansionType);
                    result.setProcessedContent(processedContent);
                    break;

                case "optimization":
                    String optimizationTarget = processDTO.getProcessParams() != null
                            ? processDTO.getProcessParams().getOptimizationTarget()
                            : "readability";
                    processedContent = optimizeContent(processDTO.getContent(), optimizationTarget);
                    result.setProcessedContent(processedContent);
                    break;

                case "example":
                    String exampleType = processDTO.getProcessParams() != null
                            ? processDTO.getProcessParams().getExampleType()
                            : "code";
                    processedContent = addExample(processDTO.getContent(), exampleType);
                    result.setProcessedContent(processedContent);
                    break;

                default:
                    throw new RuntimeException("不支持的处理类型：" + processDTO.getProcessType());
            }

            result.setSuccess(true);
            result.setMessage("处理成功");

        } catch (Exception e) {
            log.error("处理文档失败：{}", e.getMessage(), e);
            result.setSuccess(false);
            result.setMessage("处理失败: " + e.getMessage());
        }

        return result;
    }

    /**
     * 生成文档摘要（文件上传方式）
     *
     * @param file   文档文件
     * @param userId 用户ID
     * @return 处理结果
     */
    @Override
    public DocumentProcessVO generateSummary(MultipartFile file, Long userId) {
        log.info("生成文档摘要（文件上传）：fileName={}, userId={}", file.getOriginalFilename(), userId);

        DocumentProcessVO result = new DocumentProcessVO();
        result.setProcessType("summary");

        try {
            String content = new String(file.getBytes(), StandardCharsets.UTF_8);
            String summary = generateSummary(content, 200);

            result.setProcessedContent(summary);
            result.setSuccess(true);
            result.setMessage("生成成功");

        } catch (IOException e) {
            log.error("读取文件失败：{}", e.getMessage(), e);
            result.setSuccess(false);
            result.setMessage("读取文件失败: " + e.getMessage());
        } catch (Exception e) {
            log.error("生成摘要失败：{}", e.getMessage(), e);
            result.setSuccess(false);
            result.setMessage("生成失败: " + e.getMessage());
        }

        return result;
    }

    /**
     * 生成文档大纲（文件上传方式）
     *
     * @param file   文档文件
     * @param userId 用户ID
     * @return 处理结果
     */
    @Override
    public DocumentProcessVO generateOutline(MultipartFile file, Long userId) {
        log.info("生成文档大纲（文件上传）：fileName={}, userId={}", file.getOriginalFilename(), userId);

        DocumentProcessVO result = new DocumentProcessVO();
        result.setProcessType("outline");

        try {
            String content = new String(file.getBytes(), StandardCharsets.UTF_8);
            String outline = generateOutline(content, 2);

            result.setProcessedContent(outline);
            result.setSuccess(true);
            result.setMessage("生成成功");

        } catch (IOException e) {
            log.error("读取文件失败：{}", e.getMessage(), e);
            result.setSuccess(false);
            result.setMessage("读取文件失败: " + e.getMessage());
        } catch (Exception e) {
            log.error("生成大纲失败：{}", e.getMessage(), e);
            result.setSuccess(false);
            result.setMessage("生成失败: " + e.getMessage());
        }

        return result;
    }

    /**
     * 扩展内容（DTO方式）
     *
     * @param dto    处理请求
     * @param userId 用户ID
     * @return 处理结果
     */
    @Override
    public DocumentProcessVO expandContent(DocumentProcessDTO dto, Long userId) {
        log.info("扩展内容（DTO方式）：userId={}", userId);

        DocumentProcessVO result = new DocumentProcessVO();
        result.setProcessType("expansion");

        try {
            String expType = dto.getProcessParams() != null ? dto.getProcessParams().getExpansionType() : "detail";
            String expanded = expandContent(dto.getContent(), expType);

            result.setProcessedContent(expanded);
            result.setSuccess(true);
            result.setMessage("扩展成功");

        } catch (Exception e) {
            log.error("扩展内容失败：{}", e.getMessage(), e);
            result.setSuccess(false);
            result.setMessage("扩展失败: " + e.getMessage());
        }

        return result;
    }

    /**
     * 优化内容（DTO方式）
     *
     * @param dto    处理请求
     * @param userId 用户ID
     * @return 处理结果
     */
    @Override
    public DocumentProcessVO optimizeContent(DocumentProcessDTO dto, Long userId) {
        log.info("优化内容（DTO方式）：userId={}", userId);

        DocumentProcessVO result = new DocumentProcessVO();
        result.setProcessType("optimization");

        try {
            String target = dto.getProcessParams() != null ? dto.getProcessParams().getOptimizationTarget() : "readability";
            String optimized = optimizeContent(dto.getContent(), target);

            result.setProcessedContent(optimized);
            result.setSuccess(true);
            result.setMessage("优化成功");

        } catch (Exception e) {
            log.error("优化内容失败：{}", e.getMessage(), e);
            result.setSuccess(false);
            result.setMessage("优化失败: " + e.getMessage());
        }

        return result;
    }

    /**
     * 流式生成摘要
     *
     * @param file   文档文件
     * @param userId 用户ID
     * @return SSE事件流
     */
    @Override
    public SseEmitter generateSummaryStream(MultipartFile file, Long userId) {
        log.info("流式生成摘要：fileName={}, userId={}", file.getOriginalFilename(), userId);

        SseEmitter emitter = new SseEmitter(30 * 60 * 1000L);

        try {
            String content = new String(file.getBytes(), StandardCharsets.UTF_8);
            String summary = generateSummary(content, 200);

            emitter.send(SseEmitter.event()
                    .name("message")
                    .data(summary));
            emitter.complete();

        } catch (Exception e) {
            log.error("流式生成摘要失败：{}", e.getMessage(), e);
            emitter.completeWithError(e);
        }

        return emitter;
    }

    /**
     * 流式生成大纲
     *
     * @param file   文档文件
     * @param userId 用户ID
     * @return SSE事件流
     */
    @Override
    public SseEmitter generateOutlineStream(MultipartFile file, Long userId) {
        log.info("流式生成大纲：fileName={}, userId={}", file.getOriginalFilename(), userId);

        SseEmitter emitter = new SseEmitter(30 * 60 * 1000L);

        try {
            String content = new String(file.getBytes(), StandardCharsets.UTF_8);
            String outline = generateOutline(content, 2);

            emitter.send(SseEmitter.event()
                    .name("message")
                    .data(outline));
            emitter.complete();

        } catch (Exception e) {
            log.error("流式生成大纲失败：{}", e.getMessage(), e);
            emitter.completeWithError(e);
        }

        return emitter;
    }

    /**
     * 内容截断：超长文档取头尾拼接，减少LLM token消耗
     *
     * @param content  原始内容
     * @param maxChars 最大字符数
     * @return 截断后的内容
     */
    private String truncateContent(String content, int maxChars) {
        if (content == null || content.length() <= maxChars) {
            return content;
        }
        int headLen = (int) (maxChars * 0.75);
        int tailLen = maxChars - headLen;
        String head = content.substring(0, headLen);
        String tail = content.substring(content.length() - tailLen);
        return head + "\n\n...\n\n" + tail;
    }

    /**
     * 按句号/换行切分文本为句子块，用于流式逐句推送
     *
     * @param text 原始文本
     * @return 句子块列表
     */
    static List<String> chunkBySentence(String text) {
        if (text == null || text.isEmpty()) {
            return List.of();
        }
        List<String> chunks = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            current.append(c);
            if (c == '。' || c == '\n' || c == '！' || c == '？') {
                String chunk = current.toString().trim();
                if (!chunk.isEmpty()) {
                    chunks.add(chunk);
                }
                current.setLength(0);
            }
        }
        String remaining = current.toString().trim();
        if (!remaining.isEmpty()) {
            chunks.add(remaining);
        }
        return chunks;
    }

    /**
     * 基于内容生成摘要（非流式）
     *
     * @param dto    处理请求
     * @param userId 用户ID
     * @return 处理结果
     */
    @Override
    public DocumentProcessVO generateSummaryByContent(DocumentProcessDTO dto, Long userId) {
        log.info("基于内容生成摘要（非流式）：userId={}, title={}", userId, dto.getTitle());

        DocumentProcessVO result = new DocumentProcessVO();
        result.setProcessType("summary");

        try {
            String content = truncateContent(dto.getContent(), 8000);

            // 先查Redis缓存
            String cached = getCachedSummary(content);
            if (cached != null) {
                log.info("摘要缓存命中：contentMd5={}", DigestUtils.md5DigestAsHex(content.getBytes(StandardCharsets.UTF_8)));
                result.setProcessedContent(cached);
                result.setOriginalContent(dto.getContent());
                result.setSuccess(true);
                result.setMessage("缓存命中");
                return result;
            }

            Integer length = dto.getProcessParams() != null
                    ? dto.getProcessParams().getSummaryLength()
                    : 200;
            if (length == null || length <= 0) {
                length = 200;
            }

            String prompt = buildSummaryPrompt(content, dto.getTitle(), length);
            UserMessage userMessage = UserMessage.from(prompt);
            Response<AiMessage> response = modelProvider.getDefaultModel().generate(userMessage);
            String summary = response.content().text().trim();

            // 写入Redis缓存
            cacheSummary(content, summary);

            result.setProcessedContent(summary);
            result.setOriginalContent(dto.getContent());
            result.setSuccess(true);
            result.setMessage("生成成功");

        } catch (Exception e) {
            log.error("基于内容生成摘要失败：{}", e.getMessage(), e);
            result.setSuccess(false);
            result.setMessage("生成失败: " + e.getMessage());
        }

        return result;
    }

    /**
     * 基于内容流式生成摘要
     *
     * @param dto    处理请求
     * @param userId 用户ID
     * @return SSE事件流
     */
    @Override
    public SseEmitter generateSummaryByContentStream(DocumentProcessDTO dto, Long userId) {
        log.info("基于内容流式生成摘要：userId={}, title={}", userId, dto.getTitle());

        SseEmitter emitter = new SseEmitter(5 * 60 * 1000L);

        try {
            String content = truncateContent(dto.getContent(), 8000);

            // 先查Redis缓存，命中直接流式返回缓存的摘要
            String cached = getCachedSummary(content);
            if (cached != null) {
                log.info("摘要缓存命中（流式）：contentMd5={}", DigestUtils.md5DigestAsHex(content.getBytes(StandardCharsets.UTF_8)));
                new Thread(() -> {
                    try {
                        List<String> chunks = chunkBySentence(cached);
                        for (int i = 0; i < chunks.size(); i++) {
                            emitter.send(SseEmitter.event()
                                    .name("message")
                                    .data(chunks.get(i)));
                            if (i < chunks.size() - 1) {
                                Thread.sleep(30);
                            }
                        }
                        DocumentProcessVO result = new DocumentProcessVO();
                        result.setProcessType("summary");
                        result.setProcessedContent(cached);
                        result.setSuccess(true);
                        result.setMessage("缓存命中");
                        emitter.send(SseEmitter.event().name("done").data(result));
                        emitter.complete();
                    } catch (Exception e) {
                        try {
                            emitter.send(SseEmitter.event().name("error").data(e.getMessage()));
                        } catch (IOException ignored) {}
                        emitter.completeWithError(e);
                    }
                }, "summary-stream-cached-" + userId).start();
                return emitter;
            }

            Integer lengthParam = dto.getProcessParams() != null
                    ? dto.getProcessParams().getSummaryLength()
                    : 200;
            final Integer length = (lengthParam == null || lengthParam <= 0) ? 200 : lengthParam;

            // 在线程中异步执行，避免阻塞Controller线程
            final String finalContent = content;
            new Thread(() -> {
                try {
                    String prompt = buildSummaryPrompt(finalContent, dto.getTitle(), length);
                    UserMessage userMessage = UserMessage.from(prompt);
                    Response<AiMessage> response = modelProvider.getDefaultModel().generate(userMessage);
                    String summary = response.content().text().trim();

                    // 写入Redis缓存
                    cacheSummary(finalContent, summary);

                    // 按句子切分并逐句流式推送
                    List<String> chunks = chunkBySentence(summary);
                    for (int i = 0; i < chunks.size(); i++) {
                        emitter.send(SseEmitter.event()
                                .name("message")
                                .data(chunks.get(i)));
                        // 句子间小间隔，模拟打字效果
                        if (i < chunks.size() - 1) {
                            Thread.sleep(40);
                        }
                    }

                    // 发送完成事件
                    DocumentProcessVO result = new DocumentProcessVO();
                    result.setProcessType("summary");
                    result.setProcessedContent(summary);
                    result.setSuccess(true);
                    result.setMessage("生成成功");
                    emitter.send(SseEmitter.event()
                            .name("done")
                            .data(result));
                    emitter.complete();

                } catch (Exception e) {
                    log.error("流式生成摘要失败：{}", e.getMessage(), e);
                    try {
                        emitter.send(SseEmitter.event()
                                .name("error")
                                .data("生成失败: " + e.getMessage()));
                        emitter.completeWithError(e);
                    } catch (IOException ex) {
                        emitter.completeWithError(ex);
                    }
                }
            }, "summary-stream-" + userId).start();

        } catch (Exception e) {
            log.error("流式生成摘要启动失败：{}", e.getMessage(), e);
            emitter.completeWithError(e);
        }

        return emitter;
    }

    /**
     * 构建摘要Prompt
     *
     * @param content 文档内容
     * @param title   文档标题
     * @param length  摘要长度
     * @return Prompt文本
     */
    private String buildSummaryPrompt(String content, String title, int length) {
        StringBuilder sb = new StringBuilder();
        sb.append("请为以下文档生成一个简洁的摘要，要求：\n");
        sb.append("1. 摘要长度约").append(length).append("字\n");
        sb.append("2. 涵盖文档的核心内容和主要观点\n");
        sb.append("3. 语言简洁明了\n");
        sb.append("4. 不要添加任何额外的解释\n");
        if (title != null && !title.isEmpty()) {
            sb.append("\n文档标题：").append(title).append("\n");
        }
        sb.append("\n文档内容：\n").append(content);
        return sb.toString();
    }

    private String getTypeDescription(String expType) {
        return switch (expType != null ? expType.toLowerCase() : "") {
            case "detail" -> "详细阐述每个要点，增加更多细节和说明";
            case "example" -> "为关键概念添加具体的示例说明";
            case "theory" -> "添加相关的理论依据和参考文献";
            case "practice" -> "补充实践案例和应用场景";
            default -> "扩展内容，使其更加完整和丰富";
        };
    }

    private String getOptimizationDescription(String target) {
        return switch (target != null ? target.toLowerCase() : "") {
            case "readability" -> "1. 提高文档的可读性和易懂性\n2. 使用更简洁明了的语言\n3. 优化句子结构\n4. 适当分段";
            case "professional" -> "1. 使用更专业的术语和表达方式\n2. 优化文档的格式和结构\n3. 提高文档的专业性和权威性\n4. 使用正式的语言风格";
            case "seo" -> "1. 优化关键词使用\n2. 提高内容的相关性\n3. 添加适当的标题和子标题\n4. 优化内容结构";
            default -> "1. 提高文档的整体质量\n2. 优化语言表达\n3. 改善文档结构\n4. 增强信息传达效果";
        };
    }

    private String getExampleDescription(String expType) {
        return switch (expType != null ? expType.toLowerCase() : "") {
            case "code" -> "1. 添加代码示例\n2. 代码要有注释说明\n3. 代码格式要规范";
            case "case" -> "1. 添加实际案例\n2. 案例要有代表性\n3. 案例要有详细说明";
            case "data" -> "1. 添加数据示例\n2. 数据要真实可信\n3. 数据要有来源说明";
            default -> "1. 添加适当的示例\n2. 示例要与内容相关\n3. 示例要有说明";
        };
    }
}