package com.agrismart.agrimallbackend.controller.home;

import com.agrismart.agrimallbackend.dto.response.ResponseVo;
import com.agrismart.agrimallbackend.entity.common.Comment;
import com.agrismart.agrimallbackend.service.common.ICommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 前台商品评论控制器。
 *
 * 该控制器提供前台用户对商品进行评论的功能接口。
 *
 * 接口路径：{@code /api/home/comment}
 *
 * 权限要求：需要用户登录（通过 {@link com.agrismart.agrimallbackend.interceptor.JwtInterceptor} 验证）。
 *
 * @author agrimall
 * @see com.agrismart.agrimallbackend.service.common.ICommentService
 * @see com.agrismart.agrimallbackend.entity.common.Comment
 * @since 1.0
 */
@RestController
@RequestMapping("/api/home/comment")
public class HomeCommentController {

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
    public HomeCommentController(ICommentService commentService) {
        this.commentService = commentService;
    }

    /**
     * 提交商品评论。
     *
     * 当前登录用户对指定商品提交评论，包括评论内容和评分等。
     *
     * @param comment 评论实体对象，包含商品 ID、评论内容、评分等信息
     * @param uid     用户 ID（从请求属性中获取，由拦截器设置）
     * @return 操作结果，true 表示评论提交成功
     */
    @PostMapping
    public ResponseVo<Boolean> submit(@org.springframework.web.bind.annotation.RequestBody Comment comment,
                                      @RequestAttribute("id") Long uid) {
        return commentService.submitComment(uid, comment);
    }
}

