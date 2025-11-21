package com.agrismart.agrimallbackend.controller.admin;

import com.agrismart.agrimallbackend.common.util.StringUtil;
import com.agrismart.agrimallbackend.dto.response.ResponseVo;
import com.agrismart.agrimallbackend.service.common.ICommentService;
import com.agrismart.agrimallbackend.service.common.IUserService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 前台用户管理控制器。
 *
 * 该控制器提供后台管理系统对前台用户的管理接口，包括：
 *
 * - 前台用户列表查询（支持分页和搜索）
 * - 用户评论管理（支持分页和搜索）
 * - 重置用户密码
 * - 删除用户账号
 *
 * 接口路径：{@code /api/admin/users}
 *
 * 权限要求：
 *
 * - 需要管理员登录（通过 {@link com.agrismart.agrimallbackend.interceptor.AdminInterceptor} 验证）
 * - 需要相应的菜单权限
 *
 * @author agrimall
 * @see com.agrismart.agrimallbackend.service.common.IUserService
 * @see com.agrismart.agrimallbackend.service.common.ICommentService
 * @since 1.0
 */
@RestController
@RequestMapping("/api/admin/users")
public class UserManageController {

    /**
     * 用户服务接口。
     */
    private final IUserService userService;

    /**
     * 评论服务接口。
     */
    private final ICommentService commentService;

    /**
     * 构造函数，注入依赖。
     *
     * @param userService    用户服务接口
     * @param commentService 评论服务接口
     */
    @Autowired
    public UserManageController(IUserService userService,
                                ICommentService commentService) {
        this.userService = userService;
        this.commentService = commentService;
    }

    /**
     * 分页查询前台用户列表。
     *
     * 支持按内容（用户名、邮箱、手机号等）搜索，如果不提供搜索内容则查询所有用户。
     *
     * @param pageNum  页码，从 1 开始，默认为 1
     * @param pageSize 每页大小，默认为 5
     * @param content  搜索内容（可选），用于模糊搜索用户名、邮箱、手机号等
     * @return 分页信息，包含用户列表
     */
    @GetMapping
    public ResponseVo<PageInfo> list(@RequestParam(defaultValue = "1") Integer pageNum,
                                     @RequestParam(defaultValue = "5") Integer pageSize,
                                     @RequestParam(required = false) String content) {
        return (StringUtil.isEmpty(content))
                ? userService.getUserByPage(pageNum, pageSize)
                : userService.getUserByPageAndContent(pageNum, pageSize, content);
    }

    /**
     * 分页查询用户评论列表。
     *
     * 支持按内容搜索评论，如果不提供搜索内容则查询所有评论。
     *
     * @param pageNum  页码，从 1 开始，默认为 1
     * @param pageSize 每页大小，默认为 5
     * @param content  搜索内容（可选），用于模糊搜索评论内容
     * @return 分页信息，包含评论列表
     */
    @GetMapping("/comments")
    public ResponseVo<PageInfo> comments(@RequestParam(defaultValue = "1") Integer pageNum,
                                         @RequestParam(defaultValue = "5") Integer pageSize,
                                         @RequestParam(required = false) String content) {
        return (StringUtil.isEmpty(content))
                ? commentService.selectByPage(pageNum, pageSize)
                : commentService.selectByPageAndSearchContent(content, pageNum, pageSize);
    }

    /**
     * 重置用户密码。
     *
     * 管理员可以为指定用户重置密码，新密码会进行验证后保存。
     *
     * @param userId   用户 ID（路径变量）
     * @param password 新密码
     * @return 操作结果，true 表示密码重置成功
     */
    @PostMapping("/{userId}/password")
    public ResponseVo<Boolean> updatePassword(@PathVariable("userId") Long userId,
                                              @RequestParam("password") String password) {
        return userService.updateUserPasswd(password, userId);
    }

    /**
     * 删除用户账号。
     *
     * 根据用户 ID 删除指定的前台用户账号。
     *
     * @param userId 用户 ID（路径变量）
     * @return 操作结果，true 表示删除成功
     */
    @DeleteMapping("/{userId}")
    public ResponseVo<Boolean> delete(@PathVariable("userId") Long userId) {
        return userService.deleteUser(userId);
    }
}

