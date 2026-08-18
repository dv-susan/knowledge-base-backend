package com.knowledge.base.common.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.knowledge.base.common.utils.UserContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * MyBatis Plus字段自动填充处理器
 *
 * <p>按照阿里巴巴Java开发规范设计，自动填充创建时间、更新时间等字段</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Slf4j
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    /**
     * 插入时自动填充
     *
     * @param metaObject 元对象
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        log.debug("开始插入填充...");
        LocalDateTime now = LocalDateTime.now();
        Long userId = UserContextUtil.getUserId();

        // 填充创建时间
        if (metaObject.hasSetter("createdAt")) {
            this.strictInsertFill(metaObject, "createdAt", LocalDateTime.class, now);
        }

        // 填充更新时间
        if (metaObject.hasSetter("updatedAt")) {
            this.strictInsertFill(metaObject, "updatedAt", LocalDateTime.class, now);
        }

        // 填充创建人ID
        if (userId != null) {
            if (metaObject.hasSetter("createBy")) {
                this.strictInsertFill(metaObject, "createBy", Long.class, userId);
            }

            // 填充更新人ID
            if (metaObject.hasSetter("updatedBy")) {
                this.strictInsertFill(metaObject, "updatedBy", Long.class, userId);
            }
        }
    }

    /**
     * 更新时自动填充
     *
     * @param metaObject 元对象
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        log.debug("开始更新填充...");
        Long userId = UserContextUtil.getUserId();

        // 填充更新时间
        if (metaObject.hasSetter("updatedAt")) {
            this.strictUpdateFill(metaObject, "updatedAt", LocalDateTime.class, LocalDateTime.now());
        }

        // 填充更新人ID
        if (userId != null) {
            if (metaObject.hasSetter("updateBy")) {
                this.strictUpdateFill(metaObject, "updateBy", Long.class, userId);
            }
            if (metaObject.hasSetter("updatedBy")) {
                this.strictUpdateFill(metaObject, "updatedBy", Long.class, userId);
            }
        }
    }
}
