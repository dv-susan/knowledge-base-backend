package com.knowledge.base.userauth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.knowledge.base.userauth.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 用户Mapper接口
 *
 * <p>按照阿里巴巴Java开发规范设计，提供用户数据访问操作</p>
 *
 * @author 苏三
 * @since 1.0.0
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return 用户信息
     */
    User selectByUsername(@Param("username") String username);

    /**
     * 根据邮箱查询用户
     *
     * @param email 邮箱
     * @return 用户信息
     */
    User selectByEmail(@Param("email") String email);

    /**
     * 根据手机号查询用户
     *
     * @param phone 手机号
     * @return 用户信息
     */
    User selectByPhone(@Param("phone") String phone);

    /**
     * 统计用户数量
     *
     * @return 用户数量
     */
    int countUsers();

    /**
     * 统计用户文档数（查询 kb_document 跨库视图）
     *
     * @param authorId 作者ID
     * @return 文档数量
     */
    Long countDocumentsByAuthorId(@Param("authorId") Long authorId);

    /**
     * 统计用户总获赞数（查询 kb_document 跨库视图）
     *
     * @param authorId 作者ID
     * @return 总点赞数
     */
    Long sumLikesByAuthorId(@Param("authorId") Long authorId);

    /**
     * 统计用户总浏览量（查询 kb_document 跨库视图）
     *
     * @param authorId 作者ID
     * @return 总浏览量
     */
    Long sumViewsByAuthorId(@Param("authorId") Long authorId);
}