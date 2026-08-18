package com.knowledge.base.ai.vo;

import com.knowledge.base.ai.rag.kag.retrieval.GraphContext;
import com.knowledge.base.ai.vo.CitationVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * AI对话响应VO
 *
 * @author 苏三
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "AI对话响应")
public class ChatResponseVO {

    /**
     * 对话ID
     */
    @Schema(description = "对话ID")
    private Long conversationId;

    /**
     * 消息ID
     */
    @Schema(description = "消息ID")
    private Long messageId;

    /**
     * AI回复内容
     */
    @Schema(description = "AI回复内容")
    private String content;

    /**
     * 使用Token数
     */
    @Schema(description = "使用Token数")
    private Integer tokens;

    /**
     * 对话标题
     */
    @Schema(description = "对话标题")
    private String title;

    /**
     * 引用来源列表（RAG模式）
     */
    @Schema(description = "引用来源")
    private List<CitationVO> citations;

    /**
     * 是否来自知识库
     */
    @Schema(description = "是否来自知识库检索增强")
    private boolean fromKnowledgeBase;

    /**
     * 知识图谱检索上下文（KAG模式，包含实体、路径、关联文本块）
     */
    @Schema(description = "知识图谱上下文")
    private GraphContext graphContext;
}
