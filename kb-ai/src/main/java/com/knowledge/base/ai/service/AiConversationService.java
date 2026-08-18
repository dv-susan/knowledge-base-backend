package com.knowledge.base.ai.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.knowledge.base.ai.dto.ChatRequestDTO;
import com.knowledge.base.ai.entity.Conversation;
import com.knowledge.base.ai.vo.ConversationVO;

/**
 * AI对话管理服务接口
 *
 * @author 苏三
 * @since 1.0.0
 */
public interface AiConversationService {

    /**
     * 创建对话（通过ChatRequestDTO）
     *
     * @param requestDTO 对话请求
     * @param userId     用户ID
     * @return 对话ID
     */
    Long createConversation(ChatRequestDTO requestDTO, Long userId);

    /**
     * 创建对话（通过标题）
     *
     * @param title  对话标题
     * @param userId 用户ID
     * @return 对话ID
     */
    Long createConversation(String title, Long userId);

    /**
     * 获取对话详情
     *
     * @param conversationId 对话ID
     * @param userId         用户ID
     * @return 对话详情
     */
    ConversationVO getConversation(Long conversationId, Long userId);

    /**
     * 获取对话列表
     *
     * @param userId    用户ID
     * @param current   当前页
     * @param size      每页大小
     * @return 对话列表
     */
    IPage<ConversationVO> listConversations(Long userId, Long current, Long size);

    /**
     * 删除对话
     *
     * @param conversationId 对话ID
     * @param userId         用户ID
     * @return 是否成功
     */
    boolean deleteConversation(Long conversationId, Long userId);

    /**
     * 更新对话状态
     *
     * @param conversationId 对话ID
     * @param status         状态
     * @return 是否成功
     */
    boolean updateStatus(Long conversationId, Integer status);

    /**
     * 更新对话Token统计
     *
     * @param conversationId 对话ID
     * @param tokens         Token数量
     * @return 是否成功
     */
    boolean updateTokens(Long conversationId, Integer tokens);

    /**
     * 根据ID获取对话实体
     *
     * @param conversationId 对话ID
     * @return 对话实体
     */
    Conversation getById(Long conversationId);
}
