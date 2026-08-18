package com.knowledge.base.ai.rag.kag.extraction.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.knowledge.base.ai.config.ModelProvider;
import com.knowledge.base.ai.config.KAGProperties;
import com.knowledge.base.ai.dto.kag.extraction.ExtractedEntity;
import com.knowledge.base.ai.dto.kag.extraction.ExtractedRelation;
import com.knowledge.base.ai.dto.kag.extraction.ExtractionResult;
import com.knowledge.base.ai.rag.kag.extraction.ExtractionException;
import com.knowledge.base.ai.rag.kag.extraction.ExtractionService;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.input.Prompt;
import dev.langchain4j.model.input.PromptTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 基于LLM的实体关系抽取服务实现
 *
 * <p>核心逻辑：Build prompt → LLM completion → JSON parse → return ExtractionResult。
 * 使用精心设计的Prompt模板确保LLM输出结构化JSON。</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LLMExtractionServiceImpl implements ExtractionService {

    private final ModelProvider modelProvider;
    private final KAGProperties kagProperties;

    /** 用于从LLM回复中提取JSON的正则 */
    private static final Pattern JSON_BLOCK_PATTERN = Pattern.compile(
            "\\{[^{}]*(?:\\{[^{}]*\\}[^{}]*)*\\}",
            Pattern.DOTALL);

    /** 抽取系统提示词 */
    private static final String EXTRACTION_SYSTEM_PROMPT = """
            你是知识图谱构建专家。请严格从以下文档片段中抽取知识实体和关系。

            ## 抽取规则
            1. 实体应该是文档中明确提到的、有实际意义的技术概念（技术栈名、API名、配置项名、核心概念、工具名、流程名）
            2. 不要抽取过于泛化的概念（如"系统"、"功能"、"数据"等）
            3. 关系必须是实体之间在文档中有明确依据的关联
            4. 每个文档片段最多抽取%d个实体和%d个关系

            ## 实体类型
            - TECH_STACK: 技术栈/框架（如 Spring Boot, Redis, MySQL）
            - API: 接口/API（如 REST API, getUserById）
            - CONFIG: 配置项（如 application.yml, server.port）
            - CONCEPT: 核心概念（如 控制反转, AOP, 分布式事务）
            - TOOL: 工具/中间件（如 Docker, Maven, Git）
            - PROCESS: 流程/步骤（如 部署流程, 认证流程）

            ## 关系类型
            - DEPENDS_ON: 依赖关系（A依赖B才能运行）
            - USES: 使用关系（A使用B的功能）
            - CONFIGURES: 配置关系（A用于配置B）
            - HAS_PART: 组成关系（A是B的一部分）
            - RELATED_TO: 其他相关关系

            ## 输出格式
            严格返回以下JSON格式，不要包含任何markdown标记或额外解释：
            {
              "entities": [
                {"name": "实体名", "type": "实体类型", "description": "一句话描述", "aliases": ["别名1"]}
              ],
              "relations": [
                {"source": "源实体名", "target": "目标实体名", "relation": "关系类型", "weight": 0.8}
              ]
            }
            """;

    /** {@inheritDoc} */
    @Override
    public ExtractionResult extract(String content, String heading, Long docId, String documentTitle) {
        if (content == null || content.isBlank()) {
            return ExtractionResult.builder()
                    .chunkId(null).docId(docId)
                    .entities(Collections.emptyList())
                    .relations(Collections.emptyList())
                    .build();
        }

        int maxEntities = kagProperties.getExtraction().getMaxEntitiesPerChunk();
        int maxRelations = kagProperties.getExtraction().getMaxRelationsPerChunk();
        String modelName = kagProperties.getExtraction().getModel();
        int maxRetries = kagProperties.getExtraction().getMaxRetries();

        String systemPrompt = EXTRACTION_SYSTEM_PROMPT.formatted(maxEntities, maxRelations);
        String userPrompt = buildUserPrompt(content, heading, documentTitle);

        ExtractionResult result = null;
        for (int attempt = 0; attempt <= maxRetries; attempt++) {
            try {
                ChatLanguageModel model = getModel(modelName);
                String response = model.generate(
                        dev.langchain4j.data.message.SystemMessage.from(systemPrompt),
                        dev.langchain4j.data.message.UserMessage.from(userPrompt)
                ).content().text();

                result = parseResponse(response, docId);
                break;
            } catch (Exception e) {
                log.warn("Entity extraction attempt {} failed for docId={}: {}",
                        attempt + 1, docId, e.getMessage());
                if (attempt == maxRetries) {
                    log.error("Entity extraction failed after {} retries for docId={}",
                            maxRetries + 1, docId);
                    throw new ExtractionException(
                            "Entity extraction failed: " + e.getMessage(), docId, null, e);
                }
            }
        }

        log.debug("Extracted {} entities and {} relations for docId={}",
                result.getEntities().size(), result.getRelations().size(), docId);
        return result;
    }

    /** {@inheritDoc} */
    @Override
    public List<ExtractionResult> extractBatch(List<ExtractionInput> inputs) {
        if (inputs == null || inputs.isEmpty()) {
            return Collections.emptyList();
        }

        List<ExtractionResult> results = new ArrayList<>();
        for (ExtractionInput input : inputs) {
            try {
                ExtractionResult result = extract(
                        input.content(), input.heading(), input.docId(), input.documentTitle());
                if (result != null && !result.isEmpty()) {
                    results.add(result);
                }
            } catch (ExtractionException e) {
                log.warn("Skipping failed extraction for docId={}: {}", e.getDocId(), e.getMessage());
            }
        }
        return results;
    }

    // ==================== Private Methods ====================

    /**
     * 构建用户提示词
     */
    private String buildUserPrompt(String content, String heading, String documentTitle) {
        StringBuilder sb = new StringBuilder();
        sb.append("文档标题：").append(documentTitle != null ? documentTitle : "未知").append("\n");
        if (heading != null && !heading.isBlank()) {
            sb.append("所属章节：").append(heading).append("\n");
        }
        sb.append("\n=== 文档内容 ===\n");
        sb.append(content.length() > 3000 ? content.substring(0, 3000) : content);
        return sb.toString();
    }

    /**
     * 解析LLM响应为ExtractionResult
     */
    private ExtractionResult parseResponse(String response, Long docId) {
        // 提取JSON块
        String json = extractJson(response);
        if (json == null || json.isBlank()) {
            log.warn("No valid JSON found in LLM extraction response for docId={}", docId);
            return ExtractionResult.builder()
                    .docId(docId)
                    .entities(Collections.emptyList())
                    .relations(Collections.emptyList())
                    .build();
        }

        JSONObject root = JSON.parseObject(json);

        List<ExtractedEntity> entities = parseEntities(root.getJSONArray("entities"));
        List<ExtractedRelation> relations = parseRelations(root.getJSONArray("relations"));

        return ExtractionResult.builder()
                .docId(docId)
                .entities(entities)
                .relations(relations)
                .build();
    }

    /**
     * 从LLM回复中提取JSON字符串
     */
    private String extractJson(String response) {
        if (response == null) return null;

        // Try to find a JSON block wrapped in ```json ... ```
        Pattern markdownJson = Pattern.compile("```(?:json)?\\s*\\n?([\\s\\S]*?)```");
        Matcher m = markdownJson.matcher(response);
        if (m.find()) {
            return m.group(1).trim();
        }

        // Fallback: find the largest JSON object
        Matcher jsonMatcher = JSON_BLOCK_PATTERN.matcher(response);
        String longest = null;
        while (jsonMatcher.find()) {
            String candidate = jsonMatcher.group();
            if (longest == null || candidate.length() > longest.length()) {
                longest = candidate;
            }
        }
        return longest;
    }

    private List<ExtractedEntity> parseEntities(JSONArray entitiesArray) {
        if (entitiesArray == null || entitiesArray.isEmpty()) return Collections.emptyList();

        List<ExtractedEntity> entities = new ArrayList<>();
        for (int i = 0; i < entitiesArray.size(); i++) {
            JSONObject obj = entitiesArray.getJSONObject(i);
            String name = obj.getString("name");
            if (name == null || name.isBlank()) continue;

            List<String> aliases = new ArrayList<>();
            JSONArray aliasesArray = obj.getJSONArray("aliases");
            if (aliasesArray != null) {
                for (int j = 0; j < aliasesArray.size(); j++) {
                    aliases.add(aliasesArray.getString(j));
                }
            }

            entities.add(ExtractedEntity.builder()
                    .name(name.trim())
                    .type(obj.getString("type"))
                    .description(obj.getString("description"))
                    .aliases(aliases)
                    .confidence(obj.getDouble("confidence"))
                    .build());
        }
        return entities;
    }

    private List<ExtractedRelation> parseRelations(JSONArray relationsArray) {
        if (relationsArray == null || relationsArray.isEmpty()) return Collections.emptyList();

        List<ExtractedRelation> relations = new ArrayList<>();
        for (int i = 0; i < relationsArray.size(); i++) {
            JSONObject obj = relationsArray.getJSONObject(i);
            String source = obj.getString("source");
            String target = obj.getString("target");
            String relation = obj.getString("relation");
            if (source == null || target == null || relation == null) continue;

            Double weight = obj.getDouble("weight");
            relations.add(ExtractedRelation.builder()
                    .source(source.trim()).target(target.trim())
                    .relation(relation.trim().toUpperCase())
                    .weight(weight != null ? weight : 0.8)
                    .build());
        }
        return relations;
    }

    private ChatLanguageModel getModel(String modelName) {
        if (modelName != null && !modelName.isBlank()) {
            return modelProvider.getModel(modelName);
        }
        return modelProvider.getDefaultModel();
    }
}
