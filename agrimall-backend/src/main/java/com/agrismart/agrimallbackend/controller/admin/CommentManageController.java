package com.agrismart.agrimallbackend.controller.admin;

import com.agrismart.agrimallbackend.dto.response.ResponseVo;
import com.agrismart.agrimallbackend.service.common.ICommentService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 评论管理控制器。
 *
 * 该控制器提供后台管理系统对用户评论的管理接口，包括：
 *
 * - 评论列表查询（支持分页和搜索）
 * - 评论删除
 *
 * 接口路径：{@code /api/admin/comments}
 *
 * 权限要求：
 *
 * - 需要管理员登录（通过 {@link com.agrismart.agrimallbackend.interceptor.AdminInterceptor} 验证）
 * - 需要相应的菜单权限
 *
 * @author agrimall
 * @see com.agrismart.agrimallbackend.service.common.ICommentService
 * @since 1.0
 */
@RestController
@RequestMapping("/api/admin/comments")
public class CommentManageController {

    /**
     * 评论服务接口。
     */
    private final ICommentService commentService;

    /**
     * 构造函数，注入依赖。
     *
     * @param commentService 评论服务接口
     */
    @Autowired
    public CommentManageController(ICommentService commentService) {
        this.commentService = commentService;
    }

    /**
     * 分页查询评论列表。
     *
     * 支持按内容搜索评论，如果不提供搜索内容则查询所有评论。
     *
     * @param pageNum  页码，从 1 开始，默认为 1
     * @param pageSize 每页大小，默认为 5
     * @param content  搜索内容（可选），用于模糊搜索评论内容
     * @return 分页信息，包含评论列表
     */
    @GetMapping
    public ResponseVo<PageInfo> list(@RequestParam(defaultValue = "1") Integer pageNum,
                                     @RequestParam(defaultValue = "5") Integer pageSize,
                                     @RequestParam(required = false) String content) {
        return (content == null || content.trim().isEmpty())
                ? commentService.selectByPage(pageNum, pageSize)
                : commentService.selectByPageAndSearchContent(content, pageNum, pageSize);
    }

    /**
     * 删除评论。
     *
     * 根据评论 ID 删除指定的用户评论。
     *
     * @param commentId 评论 ID（路径变量）
     * @return 操作结果，true 表示删除成功
     */
    @DeleteMapping("/{commentId}")
    public ResponseVo<Boolean> delete(@PathVariable("commentId") Long commentId) {
        return commentService.deleteComment(commentId);
    }
}

