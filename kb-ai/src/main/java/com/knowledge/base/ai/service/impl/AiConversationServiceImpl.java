package com.knowledge.base.ai.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.knowledge.base.ai.dto.ChatRequestDTO;
import com.knowledge.base.ai.entity.Conversation;
import com.knowledge.base.ai.entity.Message;
import com.knowledge.base.ai.mapper.ConversationMapper;
import com.knowledge.base.ai.mapper.MessageMapper;
import com.knowledge.base.ai.service.AiConversationService;
import com.knowledge.base.ai.vo.ConversationVO;
import com.knowledge.base.ai.vo.MessageVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * AI对话管理Service实现类
 *
 * <p>按照阿里巴巴Java开发规范设计，实现对话管理相关业务逻辑</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Slf4j
@Service
public class AiConversationServiceImpl extends ServiceImpl<ConversationMapper, Conversation> implements AiConversationService {

    @Resource
    private ConversationMapper conversationMapper;

    @Resource
    private MessageMapper messageMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createConversation(ChatRequestDTO requestDTO, Long userId) {
        log.info("创建对话：userId={}", userId);

        // 构建对话实体，使用用户第一个问题作为标题
        String title = requestDTO.getContent();
        if (title != null && title.length() > 30) {
            title = title.substring(0, 30);
        }
        Conversation conversation = new Conversation();
        conversation.setTitle(title != null ? title : "新对话");
        conversation.setUserId(userId);
        conversation.setModel(requestDTO.getModel() != null ? requestDTO.getModel() : "qwen"); // 默认模型
        conversation.setSystemPrompt(requestDTO.getSystemPrompt());
        conversation.setTokensUsed(0);
        conversation.setMessageCount(0);
        conversation.setStatus(0); // 0-进行中
        conversation.setCreatedAt(LocalDateTime.now());
        conversation.setUpdatedAt(LocalDateTime.now());
        conversation.setDeleted(0);

        // 保存对话
        int count = conversationMapper.insert(conversation);
        if (count <= 0) {
            throw new RuntimeException("创建对话失败");
        }

        log.info("对话创建成功：conversationId={}", conversation.getId());
        return conversation.getId();
    }

    /** {@inheritDoc} */
    @Override
    public ConversationVO getConversation(Long conversationId, Long userId) {
        log.info("获取对话详情：conversationId={}, userId={}", conversationId, userId);

        if (conversationId == null) {
            throw new RuntimeException("对话ID不能为空");
        }

        Conversation conversation = conversationMapper.selectById(conversationId);
        if (conversation == null) {
            throw new RuntimeException("对话不存在");
        }

        // 验证用户权限
        if (!conversation.getUserId().equals(userId)) {
            throw new RuntimeException("无权访问该对话");
        }

        ConversationVO vo = convertToVO(conversation);

        // 加载对话消息
        List<Message> messages = messageMapper.selectList(
                new LambdaQueryWrapper<Message>()
                        .eq(Message::getConversationId, conversationId)
                        .orderByAsc(Message::getCreatedAt)
        );
        vo.setMessages(messages.stream().map(this::convertMessageToVO).collect(Collectors.toList()));

        return vo;
    }

    /** {@inheritDoc} */
    @Override
    public IPage<ConversationVO> listConversations(Long userId, Long current, Long size) {
        log.info("获取对话列表：userId={}, current={}, size={}", userId, current, size);

        // 构建查询条件
        LambdaQueryWrapper<Conversation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Conversation::getUserId, userId)
                .orderByDesc(Conversation::getUpdatedAt);

        // 分页查询
        Page<Conversation> page = new Page<>(current, size);
        IPage<Conversation> conversationPage = conversationMapper.selectPage(page, wrapper);

        // 转换为VO
        return conversationPage.convert(this::convertToVO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteConversation(Long conversationId, Long userId) {
        log.info("删除对话：conversationId={}, userId={}", conversationId, userId);

        if (conversationId == null) {
            throw new RuntimeException("对话ID不能为空");
        }

        Conversation conversation = conversationMapper.selectById(conversationId);
        if (conversation == null) {
            throw new RuntimeException("对话不存在");
        }

        // 验证用户权限
        if (!conversation.getUserId().equals(userId)) {
            throw new RuntimeException("无权删除该对话");
        }

        // 删除对话
        int count = conversationMapper.deleteById(conversationId);

        // TODO: 级联删除相关消息

        log.info("对话删除成功：conversationId={}", conversationId);
        return count > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateStatus(Long conversationId, Integer status) {
        log.info("更新对话状态：conversationId={}, status={}", conversationId, status);

        if (conversationId == null) {
            throw new RuntimeException("对话ID不能为空");
        }

        Conversation conversation = conversationMapper.selectById(conversationId);
        if (conversation == null) {
            throw new RuntimeException("对话不存在");
        }

        conversation.setStatus(status);
        conversation.setUpdatedAt(LocalDateTime.now());

        int count = conversationMapper.updateById(conversation);
        return count > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateTokens(Long conversationId, Integer tokens) {
        log.info("更新对话Token统计：conversationId={}, tokens={}", conversationId, tokens);

        if (conversationId == null) {
            throw new RuntimeException("对话ID不能为空");
        }

        Conversation conversation = conversationMapper.selectById(conversationId);
        if (conversation == null) {
            throw new RuntimeException("对话不存在");
        }

        conversation.setTokensUsed((conversation.getTokensUsed() != null ? conversation.getTokensUsed() : 0) + tokens);
        conversation.setUpdatedAt(LocalDateTime.now());

        int count = conversationMapper.updateById(conversation);
        return count > 0;
    }

    /**
     * 更新对话标题（在首次对话后调用）
     *
     * @param conversationId 对话ID
     * @param title          标题
     * @return 是否成功
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean updateTitle(Long conversationId, String title) {
        log.info("更新对话标题：conversationId={}, title={}", conversationId, title);

        if (conversationId == null) {
            throw new RuntimeException("对话ID不能为空");
        }

        Conversation conversation = conversationMapper.selectById(conversationId);
        if (conversation == null) {
            throw new RuntimeException("对话不存在");
        }

        conversation.setTitle(title);
        conversation.setUpdatedAt(LocalDateTime.now());

        int count = conversationMapper.updateById(conversation);
        return count > 0;
    }

    /**
     * 增加消息数量
     *
     * @param conversationId 对话ID
     * @param count          增加数量
     * @return 是否成功
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean incrementMessageCount(Long conversationId, Integer count) {
        log.info("增加消息数量：conversationId={}, count={}", conversationId, count);

        if (conversationId == null) {
            throw new RuntimeException("对话ID不能为空");
        }

        Conversation conversation = conversationMapper.selectById(conversationId);
        if (conversation == null) {
            throw new RuntimeException("对话不存在");
        }

        conversation.setMessageCount((conversation.getMessageCount() != null ? conversation.getMessageCount() : 0) + count);
        conversation.setUpdatedAt(LocalDateTime.now());

        int updateCount = conversationMapper.updateById(conversation);
        return updateCount > 0;
    }

    /**
     * 消息实体转VO
     *
     * @param message 消息实体
     * @return 消息VO
     */
    private MessageVO convertMessageToVO(Message message) {
        return MessageVO.builder()
                .id(message.getId())
                .conversationId(message.getConversationId())
                .role(message.getRole())
                .content(message.getContent())
                .tokens(message.getTokens())
                .createdAt(message.getCreatedAt())
                .build();
    }

    /**
     * 转换为VO
     *
     * @param conversation 对话实体
     * @return 对话VO
     */
    private ConversationVO convertToVO(Conversation conversation) {
        return ConversationVO.builder()
                .id(conversation.getId())
                .title(conversation.getTitle())
                .model(conversation.getModel())
                .tokensUsed(conversation.getTokensUsed())
                .messageCount(conversation.getMessageCount())
                .status(conversation.getStatus())
                .createdAt(conversation.getCreatedAt())
                .updatedAt(conversation.getUpdatedAt())
                .build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createConversation(String title, Long userId) {
        log.info("创建对话（通过标题）：title={}, userId={}", title, userId);

        Conversation conversation = new Conversation();
        conversation.setTitle(title);
        conversation.setUserId(userId);
        conversation.setModel("qwen");
        conversation.setTokensUsed(0);
        conversation.setMessageCount(0);
        conversation.setStatus(0);
        conversation.setCreatedAt(LocalDateTime.now());
        conversation.setUpdatedAt(LocalDateTime.now());
        conversation.setDeleted(0);

        int count = conversationMapper.insert(conversation);
        if (count <= 0) {
            throw new RuntimeException("创建对话失败");
        }

        log.info("对话创建成功：conversationId={}", conversation.getId());
        return conversation.getId();
    }

    /** {@inheritDoc} */
    @Override
    public Conversation getById(Long conversationId) {
        return conversationMapper.selectById(conversationId);
    }
}
