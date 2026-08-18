package com.knowledge.base.common.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.BlockAttackInnerInterceptor;
import com.knowledge.base.common.utils.SnowflakeIdGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis Plus配置类
 *
 * <p>功能说明：</p>
 * <ul>
 *   <li>乐观锁插件：自动处理version字段的并发控制</li>
 *   <li>分页插件：自动处理分页查询</li>
 *   <li>防止全表更新和删除插件</li>
 * </ul>
 *
 * <p>使用说明：</p>
 * <ul>
 *   <li>乐观锁：在实体类的version字段上添加@Version注解</li>
 *   <li>分页：使用Page<T>对象进行分页查询</li>
 *   <li>逻辑删除：在实体类的deleted字段上添加@TableLogic注解</li>
 * </ul>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Configuration
public class MybatisPlusConfig {

    /**
     * 配置MyBatis Plus拦截器
     *
     * <p>拦截器执行顺序（按添加顺序）：</p>
     * <ol>
     *   <li>乐观锁拦截器：在更新时自动处理version字段</li>
     *   <li>分页拦截器：在查询时自动处理分页</li>
     *   <li>防止全表更新和删除拦截器</li>
     * </ol>
     *
     * @return MyBatisPlusInterceptor
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();

        // 添加乐观锁插件
        // 注意：必须在分页插件之前添加
        interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());

        // 添加分页插件
        // 自动识别数据库类型，根据项目配置的DbType
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));

        // 添加防止全表更新和删除插件
        interceptor.addInnerInterceptor(new BlockAttackInnerInterceptor());

        return interceptor;
    }

    /**
     * 自定义ID生成器（雪花算法）
     *
     * <p>优先级最高，会覆盖MyBatis-Plus默认的IdWorker</p>
     * <p>使用 @TableId(type = IdType.ASSIGN_ID) 时自动调用此生成器</p>
     */
    @Bean
    public IdentifierGenerator customIdGenerator() {
        return new IdentifierGenerator() {
            @Override
            public Number nextId(Object entity) {
                return SnowflakeIdGenerator.getInstance().nextId();
            }
        };
    }
}
