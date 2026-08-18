package com.knowledge.base.common.config;

import com.baomidou.mybatisplus.annotation.Version;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 带乐观锁的基础实体类
 *
 * <p>继承BaseEntity，增加乐观锁version字段</p>
 * <p>用于需要乐观锁控制的实体类</p>
 *
 * <p>使用说明：</p>
 * <ul>
 *   <li>需要在数据库表中添加version字段</li>
 *   <li>MyBatis Plus会自动处理乐观锁逻辑</li>
 *   <li>更新时会自动检查version并递增</li>
 * </ul>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public abstract class BaseEntityWithVersion extends BaseEntity {

    /**
     * 乐观锁版本号
     *
     * <p>MyBatis Plus会自动处理乐观锁：</p>
     * <ul>
     *   <li>查询时：读取version值</li>
     *   <li>更新时：检查version是否匹配，匹配则递增并更新，否则返回失败</li>
     * </ul>
     *
     * <p>数据库表设计要求：</p>
     * <ul>
     *   <li>字段名：version</li>
     *   <li>类型：INT</li>
     *   <li>默认值：0</li>
     *   <li>非空</li>
     * </ul>
     */
    @Version
    private Integer version;
}
