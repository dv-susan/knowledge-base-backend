package com.knowledge.base.ai.config;

import com.knowledge.base.ai.vo.ModelVO;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * AI模型提供器
 *
 * <p>管理多个AI大模型实例，根据模型名称路由到对应的ChatLanguageModel。
 * ChatLanguageModel 按需懒加载创建，仅注册已配置API Key的模型。</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Slf4j
@Component
public class ModelProvider {

    // ==================== 通义千问配置 ====================

    @Value("${qwen.api-key}")
    private String qwenApiKey;

    @Value("${qwen.base-url}")
    private String qwenBaseUrl;

    @Value("${qwen.chat.options.model}")
    private String qwenModel;

    @Value("${qwen.chat.options.max-tokens}")
    private Integer qwenMaxTokens;

    @Value("${qwen.chat.options.temperature}")
    private Double qwenTemperature;

    // ==================== DeepSeek配置 ====================

    @Value("${deepseek.api-key}")
    private String deepseekApiKey;

    @Value("${deepseek.base-url}")
    private String deepseekBaseUrl;

    @Value("${deepseek.model}")
    private String deepseekModel;

    @Value("${deepseek.max-tokens}")
    private Integer deepseekMaxTokens;

    @Value("${deepseek.temperature}")
    private Double deepseekTemperature;

    // ==================== 内部状态 ====================

    @Value("${ai.default-model:qwen}")
    private String defaultModel;

    /** 模型实例缓存（懒加载） */
    private final Map<String, ChatLanguageModel> instanceCache = new ConcurrentHashMap<>();

    /** 流式模型实例缓存（懒加载） */
    private final Map<String, StreamingChatLanguageModel> streamingInstanceCache = new ConcurrentHashMap<>();

    /** 模型的可用性（基于API Key是否配置） */
    private final Map<String, Boolean> modelAvailable = new LinkedHashMap<>();

    /** 模型元信息 */
    private final Map<String, ModelVO> modelInfoMap = new LinkedHashMap<>();

    @PostConstruct
    public void init() {
        checkAndRegister("qwen", "通义千问", "阿里云大语言模型，支持多轮对话、文本生成等", qwenApiKey);
        checkAndRegister("deepseek", "DeepSeek", "深度求索大语言模型，擅长代码生成和深度推理", deepseekApiKey);

        if (modelInfoMap.isEmpty()) {
            log.error("❌ 所有AI模型均未配置API Key！请至少设置 QWEN_API_KEY 或 DEEPSEEK_API_KEY 环境变量");
        }

        if (modelAvailable.containsKey(defaultModel) && modelAvailable.get(defaultModel)) {
            log.info("✅ 默认模型 '{}' 已就绪", defaultModel);
        } else {
            log.warn("⚠️ 默认模型 '{}' API Key 未配置，请设置对应的环境变量后重启", defaultModel);
        }
    }

    private void checkAndRegister(String key, String displayName, String description, String apiKey) {
        boolean available = apiKey != null && !apiKey.isEmpty();
        modelAvailable.put(key, available);
        if (available) {
            modelInfoMap.put(key, ModelVO.builder()
                    .key(key)
                    .displayName(displayName)
                    .description(description)
                    .isDefault(key.equals(defaultModel))
                    .build());
            log.info("✅ 模型 '{}' ({}) 已注册（API Key 已配置）", key, displayName);
        } else {
            log.warn("⚠️ 模型 '{}' ({}) API Key 未配置，该模型不可用", key, displayName);
        }
    }

    /**
     * 懒加载构建 ChatLanguageModel 实例（阻塞式）
     */
    private ChatLanguageModel buildModel(String modelName) {
        switch (modelName) {
            case "qwen":
                return OpenAiChatModel.builder()
                        .baseUrl(qwenBaseUrl)
                        .apiKey(qwenApiKey)
                        .modelName(qwenModel)
                        .maxTokens(qwenMaxTokens)
                        .temperature(qwenTemperature)
                        .build();
            case "deepseek":
                return OpenAiChatModel.builder()
                        .baseUrl(deepseekBaseUrl)
                        .apiKey(deepseekApiKey)
                        .modelName(deepseekModel)
                        .maxTokens(deepseekMaxTokens)
                        .temperature(deepseekTemperature)
                        .build();
            default:
                throw new IllegalStateException("未知模型：" + modelName);
        }
    }

    /**
     * 懒加载构建 StreamingChatLanguageModel 实例（流式输出）
     */
    private StreamingChatLanguageModel buildStreamingModel(String modelName) {
        switch (modelName) {
            case "qwen":
                return OpenAiStreamingChatModel.builder()
                        .baseUrl(qwenBaseUrl)
                        .apiKey(qwenApiKey)
                        .modelName(qwenModel)
                        .maxTokens(qwenMaxTokens)
                        .temperature(qwenTemperature)
                        .build();
            case "deepseek":
                return OpenAiStreamingChatModel.builder()
                        .baseUrl(deepseekBaseUrl)
                        .apiKey(deepseekApiKey)
                        .modelName(deepseekModel)
                        .maxTokens(deepseekMaxTokens)
                        .temperature(deepseekTemperature)
                        .build();
            default:
                throw new IllegalStateException("未知模型：" + modelName);
        }
    }

    /**
     * 根据模型名称获取ChatLanguageModel实例（懒加载）
     *
     * @param modelName 模型名称（qwen | deepseek）
     * @return ChatLanguageModel实例
     * @throws IllegalStateException 如果请求的模型未配置API Key
     */
    public ChatLanguageModel getModel(String modelName) {
        String key = resolveModelKey(modelName);
        if (!modelAvailable.containsKey(key) || !modelAvailable.get(key)) {
            throw new IllegalStateException(
                    "模型 '" + key + "' 未配置API Key，无法使用。请设置环境变量后重启服务。");
        }
        return instanceCache.computeIfAbsent(key, k -> {
            log.info("🔄 首次调用，构建 ChatLanguageModel：{}", k);
            return buildModel(k);
        });
    }

    /**
     * 根据模型名称获取 StreamingChatLanguageModel 实例（流式输出，懒加载）
     *
     * @param modelName 模型名称（qwen | deepseek）
     * @return StreamingChatLanguageModel 实例
     * @throws IllegalStateException 如果请求的模型未配置API Key
     */
    public StreamingChatLanguageModel getStreamingModel(String modelName) {
        String key = resolveModelKey(modelName);
        if (!modelAvailable.containsKey(key) || !modelAvailable.get(key)) {
            throw new IllegalStateException(
                    "模型 '" + key + "' 未配置API Key，无法使用。请设置环境变量后重启服务。");
        }
        return streamingInstanceCache.computeIfAbsent(key, k -> {
            log.info("🔄 首次调用，构建 StreamingChatLanguageModel：{}", k);
            return buildStreamingModel(k);
        });
    }

    /**
     * 获取默认模型
     * @throws IllegalStateException 如果没有任何可用模型
     */
    public ChatLanguageModel getDefaultModel() {
        if (modelInfoMap.isEmpty()) {
            throw new IllegalStateException(
                    "没有可用的AI模型。请至少设置 QWEN_API_KEY 或 DEEPSEEK_API_KEY 环境变量后重启服务。");
        }
        if (modelAvailable.containsKey(defaultModel) && modelAvailable.get(defaultModel)) {
            return getModel(defaultModel);
        }
        // 兜底：返回第一个可用的模型
        String firstAvailable = modelAvailable.entrySet().stream()
                .filter(Map.Entry::getValue)
                .map(Map.Entry::getKey)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("没有可用的AI模型"));
        log.warn("默认模型 '{}' 不可用，降级使用：{}", defaultModel, firstAvailable);
        return getModel(firstAvailable);
    }

    /**
     * 获取默认模型名称
     */
    public String getDefaultModelName() {
        return defaultModel;
    }

    /**
     * 获取所有可用的模型信息列表（仅返回已配置API Key的模型）
     */
    public List<ModelVO> getAvailableModels() {
        return new ArrayList<>(modelInfoMap.values());
    }

    private String resolveModelKey(String modelName) {
        if (modelName == null || modelName.isEmpty()) {
            return defaultModel;
        }
        return modelName.toLowerCase();
    }
}
