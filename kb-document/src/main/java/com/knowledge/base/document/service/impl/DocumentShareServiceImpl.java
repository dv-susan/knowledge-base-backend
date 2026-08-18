package com.knowledge.base.document.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.knowledge.base.common.exception.BusinessException;
import com.knowledge.base.common.utils.SnowflakeIdGenerator;
import com.knowledge.base.document.dto.ShareDTO;
import com.knowledge.base.document.entity.Document;
import com.knowledge.base.document.entity.DocumentShare;
import com.knowledge.base.document.mapper.DocumentShareMapper;
import com.knowledge.base.document.service.DocumentService;
import com.knowledge.base.document.service.DocumentShareService;
import com.knowledge.base.document.utils.UserContext;
import com.knowledge.base.document.vo.ShareVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 文档分享服务实现类
 *
 * @author 苏三
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentShareServiceImpl extends ServiceImpl<DocumentShareMapper, DocumentShare>
        implements DocumentShareService {

    private final DocumentService documentService;

    private static final String SHARE_BASE_URL = "/share/";

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ShareVO createShare(ShareDTO shareDTO) {
        log.info("创建分享链接：documentId={}, shareType={}, expireType={}",
                shareDTO.getDocumentId(), shareDTO.getShareType(), shareDTO.getExpireType());

        Document document = documentService.getById(shareDTO.getDocumentId());
        if (document == null) {
            throw new BusinessException("文档不存在");
        }

        if (document.getDeleted() != null && document.getDeleted() == 1) {
            throw new BusinessException("文档已被删除，无法分享");
        }

        Long userId = UserContext.getCurrentUserId();
        String userName = UserContext.getCurrentUserName();

        DocumentShare share = new DocumentShare();
        share.setId(SnowflakeIdGenerator.getInstance().nextId());
        share.setShareId(generateShareId());
        share.setDocumentId(shareDTO.getDocumentId());
        share.setTitle(document.getTitle());
        share.setShareType(shareDTO.getShareType() != null ? shareDTO.getShareType() : 1);
        share.setExpireType(shareDTO.getExpireType() != null ? shareDTO.getExpireType() : 1);
        share.setAccessLimit(shareDTO.getAccessLimit() != null ? shareDTO.getAccessLimit() : 0);
        share.setRequirePassword(shareDTO.getRequirePassword() != null ? shareDTO.getRequirePassword() : 0);
        share.setSharerId(userId);
        share.setSharerName(userName);
        share.setDescription(shareDTO.getDescription());
        share.setAccessCount(0);
        share.setStatus(0);
        share.setShareTime(LocalDateTime.now());

        if (shareDTO.getExpireType() != null && shareDTO.getExpireType() == 2) {
            if (shareDTO.getExpireTime() == null || shareDTO.getExpireTime().isEmpty()) {
                throw new BusinessException("限时分享需要指定过期时间");
            }
            share.setExpireTime(LocalDateTime.parse(shareDTO.getExpireTime(),
                    DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        }

        if (shareDTO.getRequirePassword() != null && shareDTO.getRequirePassword() == 1) {
            if (shareDTO.getPassword() == null || shareDTO.getPassword().isEmpty()) {
                throw new BusinessException("需要设置访问密码");
            }
            share.setPassword(DigestUtil.md5Hex(shareDTO.getPassword()));
        }

        baseMapper.insert(share);
        log.info("分享链接创建成功：shareId={}", share.getShareId());

        return convertToShareVO(share);
    }

    @Override
    public ShareVO getShareById(String shareId) {
        log.info("获取分享信息：shareId={}", shareId);

        DocumentShare share = baseMapper.selectByShareId(shareId);
        if (share == null) {
            throw new BusinessException("分享不存在或已失效");
        }

        if (share.getStatus() != 0 || share.getDeleted() == 1) {
            throw new BusinessException("分享已失效");
        }

        if (share.getExpireType() == 2 && share.getExpireTime() != null
                && share.getExpireTime().isBefore(LocalDateTime.now())) {
            throw new BusinessException("分享已过期");
        }

        return convertToShareVO(share);
    }

    @Override
    public boolean verifyShareAccess(String shareId, String password) {
        DocumentShare share = baseMapper.selectByShareId(shareId);
        if (share == null || share.getStatus() != 0 || share.getDeleted() == 1) {
            return false;
        }

        if (share.getExpireType() == 2 && share.getExpireTime() != null
                && share.getExpireTime().isBefore(LocalDateTime.now())) {
            return false;
        }

        if (share.getAccessLimit() > 0 && share.getAccessCount() >= share.getAccessLimit()) {
            return false;
        }

        if (share.getRequirePassword() == 1) {
            if (password == null || password.isEmpty()) {
                return false;
            }
            return DigestUtil.md5Hex(password).equals(share.getPassword());
        }

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long accessShare(String shareId, String password) {
        log.info("访问分享链接：shareId={}", shareId);

        DocumentShare share = baseMapper.selectByShareId(shareId);
        if (share == null || share.getStatus() != 0 || share.getDeleted() == 1) {
            throw new BusinessException("分享不存在或已失效");
        }

        if (share.getExpireType() == 2 && share.getExpireTime() != null
                && share.getExpireTime().isBefore(LocalDateTime.now())) {
            throw new BusinessException("分享已过期");
        }

        if (share.getAccessLimit() > 0 && share.getAccessCount() >= share.getAccessLimit()) {
            throw new BusinessException("访问次数已达上限");
        }

        if (share.getRequirePassword() == 1) {
            if (password == null || password.isEmpty()) {
                throw new BusinessException("请输入访问密码");
            }
            if (!DigestUtil.md5Hex(password).equals(share.getPassword())) {
                throw new BusinessException("访问密码错误");
            }
        }

        share.setAccessCount(share.getAccessCount() == null ? 1 : share.getAccessCount() + 1);
        baseMapper.updateById(share);

        log.info("分享访问成功：shareId={}, documentId={}", shareId, share.getDocumentId());
        return share.getDocumentId();
    }

    @Override
    public List<ShareVO> getSharesByDocumentId(Long documentId) {
        log.info("获取文档的所有分享：documentId={}", documentId);

        List<DocumentShare> shares = baseMapper.selectValidSharesByDocumentId(documentId);
        return shares.stream()
                .map(this::convertToShareVO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ShareVO> getMyShares() {
        Long userId = UserContext.getCurrentUserId();
        log.info("获取当前用户的分享列表：userId={}", userId);

        List<DocumentShare> shares = baseMapper.selectBySharerId(userId);
        return shares.stream()
                .map(this::convertToShareVO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteShare(String shareId) {
        log.info("删除分享链接：shareId={}", shareId);

        DocumentShare share = baseMapper.selectByShareId(shareId);
        if (share == null) {
            throw new BusinessException("分享不存在");
        }

        Long userId = UserContext.getCurrentUserId();
        if (!share.getSharerId().equals(userId)) {
            throw new BusinessException("无权限删除此分享");
        }

        share.setStatus(2);
        baseMapper.updateById(share);

        log.info("分享已删除：shareId={}", shareId);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchDeleteShares(List<String> shareIds) {
        if (shareIds == null || shareIds.isEmpty()) {
            return 0;
        }

        Long userId = UserContext.getCurrentUserId();
        log.info("批量删除分享：shareIds={}, userId={}", shareIds, userId);

        LambdaQueryWrapper<DocumentShare> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(DocumentShare::getShareId, shareIds)
                .eq(DocumentShare::getSharerId, userId)
                .eq(DocumentShare::getStatus, 0);

        List<DocumentShare> shares = baseMapper.selectList(queryWrapper);
        if (shares.isEmpty()) {
            return 0;
        }

        shares.forEach(share -> share.setStatus(2));
        this.updateBatchById(shares);

        log.info("批量删除分享完成：count={}", shares.size());
        return shares.size();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateShare(String shareId, ShareDTO shareDTO) {
        log.info("更新分享设置：shareId={}", shareId);

        DocumentShare share = baseMapper.selectByShareId(shareId);
        if (share == null) {
            throw new BusinessException("分享不存在");
        }

        Long userId = UserContext.getCurrentUserId();
        if (!share.getSharerId().equals(userId)) {
            throw new BusinessException("无权限修改此分享");
        }

        if (shareDTO.getExpireType() != null) {
            share.setExpireType(shareDTO.getExpireType());
            if (shareDTO.getExpireType() == 2 && shareDTO.getExpireTime() != null) {
                share.setExpireTime(LocalDateTime.parse(shareDTO.getExpireTime(),
                        DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            }
        }

        if (shareDTO.getAccessLimit() != null) {
            share.setAccessLimit(shareDTO.getAccessLimit());
        }

        if (shareDTO.getRequirePassword() != null) {
            share.setRequirePassword(shareDTO.getRequirePassword());
            if (shareDTO.getRequirePassword() == 1 && shareDTO.getPassword() != null) {
                share.setPassword(DigestUtil.md5Hex(shareDTO.getPassword()));
            }
        }

        if (shareDTO.getDescription() != null) {
            share.setDescription(shareDTO.getDescription());
        }

        baseMapper.updateById(share);

        log.info("分享设置已更新：shareId={}", shareId);
        return true;
    }

    private String generateShareId() {
        return IdUtil.fastSimpleUUID().substring(0, 12);
    }

    private ShareVO convertToShareVO(DocumentShare share) {
        ShareVO.ShareVOBuilder builder = ShareVO.builder()
                .shareId(share.getShareId())
                .shareUrl(SHARE_BASE_URL + share.getShareId())
                .documentId(share.getDocumentId())
                .title(share.getTitle())
                .shareType(share.getShareType())
                .shareTypeDesc(share.getShareType() == 2 ? "私信分享" : "公开链接")
                .expireType(share.getExpireType())
                .expireTime(share.getExpireTime())
                .requirePassword(share.getRequirePassword() != null && share.getRequirePassword() == 1)
                .sharerName(share.getSharerName())
                .shareTime(share.getShareTime())
                .accessCount(share.getAccessCount())
                .description(share.getDescription());

        if (share.getExpireType() != null && share.getExpireType() == 2 && share.getExpireTime() != null) {
            builder.expired(share.getExpireTime().isBefore(LocalDateTime.now()));
        } else {
            builder.expired(false);
        }

        return builder.build();
    }
}