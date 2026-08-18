package com.knowledge.base.ai.service.impl;

import com.knowledge.base.ai.config.ModelProvider;
import com.knowledge.base.ai.dto.WritingRequestDTO;
import com.knowledge.base.ai.service.AiWritingService;
import com.knowledge.base.ai.vo.WritingResultVO;
import com.knowledge.base.ai.vo.WritingTemplateVO;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.output.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * AI写作服务实现类
 *
 * <p>按照阿里巴巴Java开发规范设计，提供AI写作（生成、扩写、优化、续写）的业务逻辑。
 * 支持同步生成和SSE流式生成两种模式。</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AiWritingServiceImpl implements AiWritingService {

    private final ModelProvider modelProvider;

    /** {@inheritDoc} */
    @Override
    public WritingResultVO generate(WritingRequestDTO dto, Long userId) {
        String modelName = dto.getModel() != null ? dto.getModel() : modelProvider.getDefaultModelName();
        log.info("AI写作生成请求：userId={}, model={}, actionType={}", userId, modelName, dto.getActionType());

        String prompt = buildWritingPrompt(dto);
        logModelCallParams("生成", modelName, dto, prompt);

        ChatLanguageModel model = resolveModel(dto.getModel());

        try {
            UserMessage userMessage = UserMessage.from(prompt);
            log.info(">>> 开始调用大模型 [{}]：promptLength={}", modelName, prompt.length());
            Response<AiMessage> response = model.generate(userMessage);
            String content = response.content().text().trim();

            logModelCallResult("生成", modelName, response, content);
            return WritingResultVO.builder()
                    .content(content)
                    .tokens(response.tokenUsage() != null ? response.tokenUsage().totalTokenCount() : null)
                    .wordCount(content.length())
                    .model(modelName)
                    .build();
        } catch (Exception e) {
            log.error("AI写作生成失败：model={}, error={}", modelName, e.getMessage(), e);
            throw new RuntimeException("AI写作生成失败: " + e.getMessage());
        }
    }

    /** {@inheritDoc} */
    @Override
    public SseEmitter generateStream(WritingRequestDTO dto, Long userId) {
        String modelName = dto.getModel() != null ? dto.getModel() : modelProvider.getDefaultModelName();
        log.info("AI写作流式生成请求：userId={}, model={}", userId, modelName);

        SseEmitter emitter = new SseEmitter(30 * 60 * 1000L);
        String prompt = buildWritingPrompt(dto);
        logModelCallParams("流式生成", modelName, dto, prompt);

        ChatLanguageModel model = resolveModel(dto.getModel());

        try {
            UserMessage userMessage = UserMessage.from(prompt);
            log.info(">>> 开始调用大模型 [{}] (流式)：promptLength={}", modelName, prompt.length());
            Response<AiMessage> response = model.generate(userMessage);
            String fullContent = response.content().text().trim();

            logModelCallResult("流式生成", modelName, response, fullContent);

            // 按字符分块发送，模拟流式输出
            int chunkSize = 10;
            for (int i = 0; i < fullContent.length(); i += chunkSize) {
                int end = Math.min(i + chunkSize, fullContent.length());
                String chunk = fullContent.substring(i, end);
                emitter.send(SseEmitter.event()
                        .name("message")
                        .data(chunk));
            }

            // 发送完成事件
            WritingResultVO result = WritingResultVO.builder()
                    .content(fullContent)
                    .tokens(response.tokenUsage() != null ? response.tokenUsage().totalTokenCount() : null)
                    .wordCount(fullContent.length())
                    .model(modelName)
                    .build();
            emitter.send(SseEmitter.event()
                    .name("done")
                    .data(result));
            emitter.complete();

            log.info("AI写作流式生成完成：userId={}, wordCount={}", userId, fullContent.length());
        } catch (IOException e) {
            log.error("AI写作流式生成SSE发送失败：model={}, error={}", modelName, e.getMessage(), e);
            emitter.completeWithError(e);
        } catch (Exception e) {
            log.error("AI写作流式生成失败：model={}, error={}", modelName, e.getMessage(), e);
            try {
                emitter.send(SseEmitter.event()
                        .name("error")
                        .data("AI写作生成失败: " + e.getMessage()));
                emitter.complete();
            } catch (IOException ex) {
                emitter.completeWithError(ex);
            }
        }

        return emitter;
    }

    /** {@inheritDoc} */
    @Override
    public WritingResultVO expand(WritingRequestDTO dto, Long userId) {
        String modelName = dto.getModel() != null ? dto.getModel() : modelProvider.getDefaultModelName();
        log.info("AI写作扩写请求：userId={}, model={}, topic={}", userId, modelName, dto.getTopic());

        if (dto.getExistingContent() == null || dto.getExistingContent().isEmpty()) {
            throw new RuntimeException("扩写功能需要提供已有内容（existingContent）");
        }

        String prompt = String.format("""
                你是一位专业的文档写作助手。请对以下内容进行扩写，要求：
                1. 保持原文的主题和风格
                2. 丰富内容细节，增加深度和广度
                3. 补充相关的背景信息、论证和实例
                4. 保持逻辑连贯，结构清晰
                5. 不要添加任何额外的解释，直接输出扩写后的完整内容

                原文主题：%s

                原文内容：
                %s
                """, dto.getTopic(), dto.getExistingContent());

        logModelCallParams("扩写", modelName, dto, prompt);
        ChatLanguageModel model = resolveModel(dto.getModel());

        try {
            UserMessage userMessage = UserMessage.from(prompt);
            log.info(">>> 开始调用大模型 [{}] (扩写)：promptLength={}", modelName, prompt.length());
            Response<AiMessage> response = model.generate(userMessage);
            String content = response.content().text().trim();

            logModelCallResult("扩写", modelName, response, content);
            return WritingResultVO.builder()
                    .content(content)
                    .tokens(response.tokenUsage() != null ? response.tokenUsage().totalTokenCount() : null)
                    .wordCount(content.length())
                    .model(modelName)
                    .build();
        } catch (Exception e) {
            log.error("AI写作扩写失败：model={}, error={}", modelName, e.getMessage(), e);
            throw new RuntimeException("AI写作扩写失败: " + e.getMessage());
        }
    }

    /** {@inheritDoc} */
    @Override
    public WritingResultVO optimize(WritingRequestDTO dto, Long userId) {
        String modelName = dto.getModel() != null ? dto.getModel() : modelProvider.getDefaultModelName();
        log.info("AI写作优化请求：userId={}, model={}, topic={}", userId, modelName, dto.getTopic());

        if (dto.getExistingContent() == null || dto.getExistingContent().isEmpty()) {
            throw new RuntimeException("优化功能需要提供已有内容（existingContent）");
        }

        String styleDesc = getStyleDescription(dto.getStyle());
        String prompt = String.format("""
                你是一位专业的文档编辑助手。请对以下内容进行优化和润色，要求：
                1. %s
                2. 修正语法错误和表达不清的地方
                3. 优化句子结构，使语言更加流畅
                4. 改善段落组织，增强可读性
                5. 保持原文档的核心意思和信息不变

                原文标题：%s

                原文内容：
                %s
                """, styleDesc, dto.getTopic(), dto.getExistingContent());

        logModelCallParams("优化", modelName, dto, prompt);
        ChatLanguageModel model = resolveModel(dto.getModel());

        try {
            UserMessage userMessage = UserMessage.from(prompt);
            log.info(">>> 开始调用大模型 [{}] (优化)：promptLength={}", modelName, prompt.length());
            Response<AiMessage> response = model.generate(userMessage);
            String content = response.content().text().trim();

            logModelCallResult("优化", modelName, response, content);
            return WritingResultVO.builder()
                    .content(content)
                    .tokens(response.tokenUsage() != null ? response.tokenUsage().totalTokenCount() : null)
                    .wordCount(content.length())
                    .model(modelName)
                    .build();
        } catch (Exception e) {
            log.error("AI写作优化失败：model={}, error={}", modelName, e.getMessage(), e);
            throw new RuntimeException("AI写作优化失败: " + e.getMessage());
        }
    }

    /** {@inheritDoc} */
    @Override
    public WritingResultVO continueWriting(WritingRequestDTO dto, Long userId) {
        String modelName = dto.getModel() != null ? dto.getModel() : modelProvider.getDefaultModelName();
        log.info("AI写作续写请求：userId={}, model={}, topic={}", userId, modelName, dto.getTopic());

        if (dto.getExistingContent() == null || dto.getExistingContent().isEmpty()) {
            throw new RuntimeException("续写功能需要提供已有内容（existingContent）");
        }

        String prompt = String.format("""
                你是一位专业的文档写作助手。请从以下内容的结尾继续进行写作，要求：
                1. 延续原文的主题、风格和逻辑
                2. 内容自然衔接，不要重复已有的内容
                3. 进一步深入展开论述或补充后续内容
                4. 保持结构完整，逻辑清晰
                5. 不要添加任何额外的解释，直接输出续写内容

                原文主题：%s

                已有内容：
                %s

                请从以上内容的结尾处继续写下去。
                """, dto.getTopic(), dto.getExistingContent());

        logModelCallParams("续写", modelName, dto, prompt);
        ChatLanguageModel model = resolveModel(dto.getModel());

        try {
            UserMessage userMessage = UserMessage.from(prompt);
            log.info(">>> 开始调用大模型 [{}] (续写)：promptLength={}", modelName, prompt.length());
            Response<AiMessage> response = model.generate(userMessage);
            String content = response.content().text().trim();

            logModelCallResult("续写", modelName, response, content);
            return WritingResultVO.builder()
                    .content(content)
                    .tokens(response.tokenUsage() != null ? response.tokenUsage().totalTokenCount() : null)
                    .wordCount(content.length())
                    .model(modelName)
                    .build();
        } catch (Exception e) {
            log.error("AI写作续写失败：model={}, error={}", modelName, e.getMessage(), e);
            throw new RuntimeException("AI写作续写失败: " + e.getMessage());
        }
    }

    /** {@inheritDoc} */
    @Override
    @Cacheable(value = "writingTemplates")
    public List<WritingTemplateVO> getTemplates() {
        log.info("获取写作模板列表（未命中缓存，重新构建）");
        List<WritingTemplateVO> templates = new ArrayList<>();

        templates.add(WritingTemplateVO.builder()
                .id("tech-solution")
                .name("技术方案")
                .description("适用于撰写技术方案文档，包含背景分析、方案设计、技术选型、实施计划等完整结构")
                .category("技术文档")
                .prompt("请撰写一份技术方案，包含以下部分：\n一、项目背景与目标\n二、技术现状分析\n三、方案设计\n四、技术选型说明\n五、实施计划\n六、风险评估与应对")
                .suggestedContentType("article")
                .suggestedStyle("technical")
                .build());

        templates.add(WritingTemplateVO.builder()
                .id("project-report")
                .name("项目报告")
                .description("适用于项目总结报告，包含项目概述、执行过程、成果分析、经验总结等")
                .category("工作汇报")
                .prompt("请撰写一份项目总结报告，包含以下部分：\n一、项目概述\n二、执行过程\n三、主要成果\n四、问题与挑战\n五、经验与反思\n六、下一步计划")
                .suggestedContentType("report")
                .suggestedStyle("formal")
                .build());

        templates.add(WritingTemplateVO.builder()
                .id("prd")
                .name("产品需求文档(PRD)")
                .description("适用于产品需求文档，包含需求背景、功能描述、用户场景、验收标准等")
                .category("产品文档")
                .prompt("请撰写一份产品需求文档(PRD)，包含以下部分：\n一、需求背景与目标\n二、目标用户分析\n三、核心功能说明\n四、用户使用流程\n五、交互设计要求\n六、验收标准")
                .suggestedContentType("documentation")
                .suggestedStyle("technical")
                .build());

        templates.add(WritingTemplateVO.builder()
                .id("api-doc")
                .name("API接口文档")
                .description("适用于编写API接口文档，包含接口说明、请求参数、响应格式、示例代码等")
                .category("技术文档")
                .prompt("请撰写一份API接口文档，包含以下部分：\n一、接口概述\n二、认证方式\n三、请求格式说明\n四、响应格式说明\n五、各接口详细说明（含请求示例和响应示例）\n六、错误码说明")
                .suggestedContentType("documentation")
                .suggestedStyle("technical")
                .build());

        templates.add(WritingTemplateVO.builder()
                .id("weekly-report")
                .name("周报/工作汇报")
                .description("适用于周报或工作汇报，包含本周工作、下周计划、问题反馈等")
                .category("工作汇报")
                .prompt("请撰写一份工作周报，包含以下部分：\n一、本周完成工作\n二、重点工作进展\n三、遇到的问题及解决方案\n四、下周工作计划\n五、需要协调的事项")
                .suggestedContentType("report")
                .suggestedStyle("formal")
                .build());

        templates.add(WritingTemplateVO.builder()
                .id("meeting-minutes")
                .name("会议纪要")
                .description("适用于会议纪要，包含会议信息、讨论内容、决议事项、待办任务等")
                .category("工作汇报")
                .prompt("请撰写一份会议纪要，包含以下部分：\n一、会议基本信息（时间、地点、参会人员）\n二、会议议题\n三、讨论内容要点\n四、决议事项\n五、待办任务及负责人\n六、下次会议时间")
                .suggestedContentType("email")
                .suggestedStyle("formal")
                .build());

        templates.add(WritingTemplateVO.builder()
                .id("announcement")
                .name("公告通知")
                .description("适用于公司内部公告或通知，包含公告事项、具体内容、执行要求等")
                .category("行政文档")
                .prompt("请撰写一份公告通知，包含以下部分：\n一、公告标题\n二、公告缘由\n三、具体事项说明\n四、执行要求或时间安排\n五、联系方式")
                .suggestedContentType("announcement")
                .suggestedStyle("formal")
                .build());

        templates.add(WritingTemplateVO.builder()
                .id("email-template")
                .name("邮件模板")
                .description("适用于编写工作邮件，包含邮件主题、称呼、正文、结尾等")
                .category("日常沟通")
                .prompt("请撰写一封工作邮件，包含以下要素：\n1. 清晰明确的邮件主题\n2. 恰当的称呼\n3. 简洁明了的事项说明\n4. 必要的背景信息\n5. 明确的行动要求或期望\n6. 专业的结尾和签名")
                .suggestedContentType("email")
                .suggestedStyle("formal")
                .build());

        return templates;
    }

    /**
     * 构建写作Prompt
     *
     * @param dto 写作请求参数
     * @return 完整的Prompt字符串
     */
    private String buildWritingPrompt(WritingRequestDTO dto) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("你是一位专业的文档写作助手。请根据以下要求进行写作。\n\n");

        // 内容类型说明
        String contentType = dto.getContentType() != null ? dto.getContentType() : "article";
        String contentTypeDesc = switch (contentType) {
            case "report" -> "这是一份报告，要求结构清晰，数据翔实，论证充分";
            case "documentation" -> "这是一份技术文档，要求准确、规范、可操作，包含代码示例";
            case "email" -> "这是一封邮件，要求礼貌、简洁、重点突出";
            case "announcement" -> "这是一份公告，要求正式、严谨、表述清楚";
            default -> "这是一篇文章，要求主题明确，逻辑清晰，表达流畅";
        };
        prompt.append(contentTypeDesc).append("。\n\n");

        // 风格说明
        String style = dto.getStyle() != null ? dto.getStyle() : "formal";
        String styleDesc = switch (style) {
            case "casual" -> "请使用轻松随意的风格，避免过于正式的表达";
            case "technical" -> "请使用专业技术风格，适当使用专业术语，注重准确性和严谨性";
            case "creative" -> "请使用具有创造性的表达方式，不拘泥于传统格式";
            case "academic" -> "请使用学术论文风格，注重论证的严谨性和引用的规范性";
            default -> "请使用正式规范的风格，表达专业、得体";
        };
        prompt.append(styleDesc).append("。\n");

        // 语气说明
        if (dto.getTone() != null && !dto.getTone().isEmpty()) {
            String toneDesc = switch (dto.getTone().toLowerCase()) {
                case "enthusiastic" -> "语气热情积极，富有感染力";
                case "serious" -> "语气严肃认真，注重权威性";
                case "friendly" -> "语气友好亲切，拉近与读者的距离";
                case "authoritative" -> "语气权威专业，展现专业深度";
                default -> "语气平和中性，客观陈述";
            };
            prompt.append(toneDesc).append("。\n");
        }

        // 字数说明
        int length = (dto.getLength() != null && dto.getLength() > 0) ? dto.getLength() : 800;
        prompt.append("字数要求：约").append(length).append("字。\n\n");

        // 主题和要求
        prompt.append("写作主题：").append(dto.getTopic()).append("\n");
        if (dto.getRequirements() != null && !dto.getRequirements().isEmpty()) {
            prompt.append("写作要求：").append(dto.getRequirements()).append("\n");
        }

        // 已有内容（用于扩写、优化、续写场景）
        if (dto.getExistingContent() != null && !dto.getExistingContent().isEmpty()) {
            prompt.append("\n参考内容：\n```\n")
                    .append(dto.getExistingContent())
                    .append("\n```\n");
        }

        prompt.append("\n请直接输出写作结果，不要添加额外的解释说明。使用Markdown格式组织内容。");
        return prompt.toString();
    }

    /**
     * 根据模型名称解析ChatLanguageModel
     *
     * @param modelName 模型名称，为空则使用默认模型
     * @return ChatLanguageModel实例
     */
    private ChatLanguageModel resolveModel(String modelName) {
        if (modelName != null && !modelName.isEmpty()) {
            return modelProvider.getModel(modelName);
        }
        return modelProvider.getDefaultModel();
    }

    /**
     * 打印大模型调用参数日志
     *
     * @param action    操作名称（生成/扩写/优化/续写）
     * @param modelName 模型名称
     * @param dto       请求参数
     * @param prompt    完整的Prompt文本
     */
    private void logModelCallParams(String action, String modelName, WritingRequestDTO dto, String prompt) {
        log.info("══════ AI写作[{}] - 模型调用参数 ══════", action);
        log.info("  模型名称: {}", modelName);
        log.info("  写作主题: {}", dto.getTopic());
        log.info("  内容类型: {}", dto.getContentType() != null ? dto.getContentType() : "article(默认)");
        log.info("  写作风格: {}", dto.getStyle() != null ? dto.getStyle() : "formal(默认)");
        log.info("  语气:     {}", dto.getTone() != null ? dto.getTone() : "neutral(默认)");
        log.info("  期望字数: {}", (dto.getLength() != null && dto.getLength() > 0) ? dto.getLength() : 800);
        log.info("  写作要求: {}", dto.getRequirements() != null && !dto.getRequirements().isEmpty()
                ? truncateForLog(dto.getRequirements(), 200) : "无");
        if (dto.getExistingContent() != null && !dto.getExistingContent().isEmpty()) {
            log.info("  已有内容长度: {} 字符", dto.getExistingContent().length());
        }
        log.info("  Prompt总长度: {} 字符", prompt.length());
        log.info("══════════════════════════════════════════");
    }

    /**
     * 打印大模型调用结果日志
     *
     * @param action    操作名称
     * @param modelName 模型名称
     * @param response  模型响应
     * @param content   响应内容
     */
    private void logModelCallResult(String action, String modelName, Response<AiMessage> response, String content) {
        Integer inputTokens = response.tokenUsage() != null ? response.tokenUsage().inputTokenCount() : null;
        Integer outputTokens = response.tokenUsage() != null ? response.tokenUsage().outputTokenCount() : null;
        Integer totalTokens = response.tokenUsage() != null ? response.tokenUsage().totalTokenCount() : null;
        log.info("<<< 大模型调用完成 [{}]：model={}, 输出长度={}字, " +
                        "inputTokens={}, outputTokens={}, totalTokens={}",
                action, modelName, content.length(),
                inputTokens, outputTokens, totalTokens);
    }

    /**
     * 截断日志文本（避免日志过长）
     *
     * @param text     原始文本
     * @param maxChars 最大字符数
     * @return 截断后的文本
     */
    private String truncateForLog(String text, int maxChars) {
        if (text == null) return "null";
        if (text.length() <= maxChars) return text;
        return text.substring(0, maxChars) + "... (总长: " + text.length() + "字符)";
    }

    /**
     * 获取风格描述（用于优化场景）
     *
     * @param style 风格标识
     * @return 中文风格描述
     */
    private String getStyleDescription(String style) {
        if (style == null || style.isEmpty()) {
            return "提升文章的整体可读性和专业性";
        }
        return switch (style.toLowerCase()) {
            case "casual" -> "使用轻松易读的表达方式，使文章更加亲切";
            case "technical" -> "使用更专业精准的技术术语，提升技术准确性";
            case "creative" -> "使用更有创意和感染力的表达方式";
            case "academic" -> "使用更严谨的学术表达，增强论证逻辑";
            default -> "提升文章的整体可读性和专业性";
        };
    }
}
