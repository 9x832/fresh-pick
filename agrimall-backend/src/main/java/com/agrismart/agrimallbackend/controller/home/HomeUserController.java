package com.agrismart.agrimallbackend.controller.home;

import com.agrismart.agrimallbackend.common.bean.CodeMsg;
import com.agrismart.agrimallbackend.common.enums.OrderDeleteEnum;
import com.agrismart.agrimallbackend.common.enums.OrderStateEnum;
import com.agrismart.agrimallbackend.dto.response.ResponseVo;
import com.agrismart.agrimallbackend.entity.common.User;
import com.agrismart.agrimallbackend.service.common.ICommentService;
import com.agrismart.agrimallbackend.service.common.IOrderService;
import com.agrismart.agrimallbackend.service.common.IUserService;
import com.github.pagehelper.PageInfo;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 前台用户管理控制器。
 *
 * 该控制器提供前台用户相关的功能接口，包括：
 *
 * - 用户登录和注册
 * - 用户信息更新和密码修改
 * - 用户评论查询
 * - 用户订单查询（支持按状态筛选和统计）
 *
 * 接口路径：{@code /api/home/user}
 *
 * 权限要求：
 *
 * - 登录和注册接口不需要认证
 * - 其他接口需要用户登录（通过 {@link com.agrismart.agrimallbackend.interceptor.JwtInterceptor} 验证）
 *
 * @author agrimall
 * @see com.agrismart.agrimallbackend.service.common.IUserService
 * @see com.agrismart.agrimallbackend.service.common.IOrderService
 * @see com.agrismart.agrimallbackend.service.common.ICommentService
 * @since 1.0
 */
@RestController
@RequestMapping("/api/home/user")
public class HomeUserController {

    /**
     * 用户服务接口。
     */
    private final IUserService userService;

    /**
     * 订单服务接口。
     */
    private final IOrderService orderService;

    /**
     * 评论服务接口。
     */
    private final ICommentService commentService;

    /**
     * 构造函数，注入依赖。
     *
     * @param userService    用户服务接口
     * @param orderService   订单服务接口
     * @param commentService 评论服务接口
     */
    @Autowired
    public HomeUserController(IUserService userService,
                              IOrderService orderService,
                              ICommentService commentService) {
        this.userService = userService;
        this.orderService = orderService;
        this.commentService = commentService;
    }

    /**
     * 用户登录接口。
     *
     * 验证用户名和密码，登录成功后生成 JWT Token 并返回给客户端。
     *
     * @param username 用户名
     * @param password 密码
     * @return 操作结果，true 表示登录成功
     */
    @PostMapping("/login")
    public ResponseVo<Boolean> login(@RequestParam String username,
                                     @RequestParam String password) {
        return userService.login(username, password);
    }

    /**
     * 用户注册接口。
     *
     * 注册新用户，需要提供用户信息、确认密码和验证码。
     * 注册成功后会发送欢迎邮件。
     *
     * @param user      用户实体对象，包含用户的基本信息
     * @param repassword 确认密码
     * @param captcha   验证码
     * @param request   HTTP 请求对象，用于验证码验证
     * @return 操作结果，true 表示注册成功
     */
    @PostMapping("/register")
    public ResponseVo<Boolean> register(User user,
                                        @RequestParam("repassword") String repassword,
                                        @RequestParam("captcha") String captcha,
                                        HttpServletRequest request) {
        return userService.register(user, repassword, captcha, request);
    }

    /**
     * 更新用户信息。
     *
     * 更新当前登录用户的基本信息（如昵称、邮箱、手机号、头像等）。
     *
     * @param user 用户实体对象，包含要更新的信息
     * @return 操作结果，成功时返回提示消息
     */
    @PostMapping("/update-info")
    public ResponseVo<String> updateInfo(User user) {
        return userService.updateInfo(user);
    }

    /**
     * 修改用户密码。
     *
     * 修改当前登录用户的密码，需要提供原密码、新密码和确认新密码。
     *
     * @param prePassword   原密码
     * @param newPassword   新密码
     * @param reNewPassword 确认新密码
     * @param request       HTTP 请求对象，用于获取当前登录用户信息
     * @return 操作结果，true 表示密码修改成功
     */
    @PostMapping("/update-password")
    public ResponseVo<Boolean> updatePassword(@RequestParam("prePassword") String prePassword,
                                              @RequestParam("newPassword") String newPassword,
                                              @RequestParam("reNewPassword") String reNewPassword,
                                              HttpServletRequest request) {
        return userService.updatePasswd(prePassword, newPassword, reNewPassword, request);
    }

    /**
     * 分页查询当前用户的评论列表。
     *
     * 查询当前登录用户发表的所有评论。
     *
     * @param uid      用户 ID（从请求属性中获取，由拦截器设置）
     * @param pageNum  页码，从 1 开始，默认为 1
     * @param pageSize 每页大小，默认为 5
     * @return 分页信息，包含评论列表
     */
    @GetMapping("/comments")
    public ResponseVo<PageInfo> comments(@RequestAttribute("id") Long uid,
                                         @RequestParam(defaultValue = "1") Integer pageNum,
                                         @RequestParam(defaultValue = "5") Integer pageSize) {
        return commentService.selectByPageAndUserId(pageNum, pageSize, uid);
    }

    /**
     * 分页查询当前用户的订单列表。
     *
     * 查询当前登录用户的订单，支持按订单状态筛选。
     * 返回结果包含订单列表、各状态订单数量统计和总订单数。
     *
     * @param uid      用户 ID（从请求属性中获取，由拦截器设置）
     * @param pageNum  页码，从 1 开始，默认为 1
     * @param pageSize 每页大小，默认为 1
     * @param state    订单状态（可选），用于筛选指定状态的订单
     * @return 包含订单列表和统计信息的响应对象
     *
     * - pageInfo：分页信息，包含订单列表
     * - stateCount：各状态订单数量统计（Map，key 为状态码，value 为数量）
     * - totalCount：总订单数
     * - activeState：当前筛选的状态（如果提供了 state 参数）
     *
     */
    @GetMapping("/orders")
    public ResponseVo<Map<String, Object>> orders(@RequestAttribute("id") Long uid,
                                                  @RequestParam(defaultValue = "1") Integer pageNum,
                                                  @RequestParam(defaultValue = "1") Integer pageSize,
                                                  @RequestParam(value = "state", required = false) Integer state) {
        // 根据是否提供状态参数，选择不同的查询方法
        ResponseVo<PageInfo> pageInfoVo;
        if (state != null) {
            // 查询指定状态的订单
            pageInfoVo = orderService.selectByPageAndUserIdAndState(
                    uid, OrderDeleteEnum.NO.getCode(), state, pageNum, pageSize);
        } else {
            // 查询所有未删除的订单
            pageInfoVo = orderService.selectByPageAndUserIdAndIsDeleted(
                    uid, OrderDeleteEnum.NO.getCode(), pageNum, pageSize);
        }
        if (!CodeMsg.SUCCESS.getCode().equals(pageInfoVo.getCode())) {
            return ResponseVo.errorByMsg(CodeMsg.SYSTEM_ERROR);
        }

        // 统计各状态的订单数量
        Map<String, Object> payload = new HashMap<>(6);
        Map<String, Integer> stateCount = new HashMap<>(OrderStateEnum.values().length + 1);
        int totalCount = 0;
        for (OrderStateEnum orderStateEnum : OrderStateEnum.values()) {
            int count = orderService
                    .selectByOrderStateAndUserIdAndIsDeleted(orderStateEnum.getCode(), uid, OrderDeleteEnum.NO.getCode())
                    .size();
            stateCount.put(orderStateEnum.getCode().toString(), count);
            totalCount += count;
        }
        payload.put("pageInfo", pageInfoVo.getData());
        payload.put("stateCount", stateCount);
        payload.put("totalCount", totalCount);
        payload.put("activeState", state);
        return ResponseVo.success(payload);
    }
}

