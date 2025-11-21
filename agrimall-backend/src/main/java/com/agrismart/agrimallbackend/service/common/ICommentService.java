package com.agrismart.agrimallbackend.service.common;

import com.github.pagehelper.PageInfo;
import com.agrismart.agrimallbackend.dto.response.ResponseVo;
import com.agrismart.agrimallbackend.entity.common.Comment;

import java.util.List;

/**
 * 评论服务接口。
 *
 * 该接口定义了商品评论相关的业务操作方法，包括：
 *
 * - 评论提交
 * - 评论查询（支持按商品、用户、内容搜索）
 * - 评论删除
 *
 * 注意：
 *
 * - 提交评论时，会自动更新商品的评论数量
 * - 评论查询支持分页
 *
 * @author agrimall
 * @see com.agrismart.agrimallbackend.service.common.impl.CommentServiceImpl
 * @see com.agrismart.agrimallbackend.entity.common.Comment
 * @since 1.0
 */
public interface ICommentService {

    /**
     * 提交评论。
     * 提交评论后，会自动更新商品的评论数量。
     * 使用事务保证数据一致性。
     *
     * @param uid     用户 ID
     * @param comment 评论对象（包含商品 ID 和评论内容）
     * @return 操作结果
     */
    ResponseVo<Boolean> submitComment(Long uid, Comment comment);

    /**
     * 按商品 ID 分页查询评论列表。
     * 用于商品详情页展示评论。
     *
     * @param productId 商品 ID
     * @param pageNum   页码，从 1 开始
     * @param pageSize  每页大小
     * @return 包含分页信息的评论列表
     */
    ResponseVo<PageInfo> selectByProductIdAndPage(Long productId, Integer pageNum, Integer pageSize);

    /**
     * 分页查询评论列表。
     * 用于后台管理系统。
     *
     * @param pageNum  页码，从 1 开始
     * @param pageSize 每页大小
     * @return 包含分页信息的评论列表
     */
    ResponseVo<PageInfo> selectByPage(Integer pageNum, Integer pageSize);

    /**
     * 按内容分页查询评论列表。
     * 用于后台管理系统，支持按评论内容搜索。
     *
     * @param content  搜索内容（支持模糊搜索）
     * @param pageNum  页码，从 1 开始
     * @param pageSize 每页大小
     * @return 包含分页信息的评论列表
     */
    ResponseVo<PageInfo> selectByPageAndSearchContent(String content, Integer pageNum, Integer pageSize);

    /**
     * 删除评论。
     *
     * @param commentId 评论 ID
     * @return 操作结果
     */
    ResponseVo<Boolean> deleteComment(Long commentId);

    /**
     * 按用户 ID 分页查询评论列表。
     * 用于前台用户查看自己的评论。
     *
     * @param pageNum  页码，从 1 开始
     * @param pageSize 每页大小
     * @param userId   用户 ID
     * @return 包含分页信息的评论列表
     */
    ResponseVo<PageInfo> selectByPageAndUserId(Integer pageNum, Integer pageSize, Long userId);

    /**
     * 根据商品 ID 查询评论列表。
     * 用于商品详情页，返回该商品的所有评论（不分页）。
     *
     * @param productId 商品 ID
     * @return 评论列表
     */
    List<Comment> selectByProductId(Long productId);
}

