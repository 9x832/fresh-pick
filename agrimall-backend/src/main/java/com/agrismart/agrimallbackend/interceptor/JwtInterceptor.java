package com.agrismart.agrimallbackend.interceptor;

import com.alibaba.fastjson.JSON;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.agrismart.agrimallbackend.common.bean.CodeMsg;
import com.agrismart.agrimallbackend.common.constant.RuntimeConstant;
import com.agrismart.agrimallbackend.common.util.JwtUtil;
import com.agrismart.agrimallbackend.common.util.StringUtil;
import com.agrismart.agrimallbackend.service.home.ICartService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

/**
 * 前台用户登录拦截器（JWT Token 验证）。
 *
 * 该拦截器实现了 {@link HandlerInterceptor} 接口，用于拦截前台用户系统的请求，
 * 验证用户是否已登录（通过 JWT Token）。如果未登录或 Token 无效，则拒绝访问并返回相应的错误信息或重定向到登录页面。
 *
 * 拦截逻辑：
 *
 * - 检查请求 URI 是否包含 "/home/" 路径
 * - 从请求头（Authorization: Bearer token）或 Cookie（my_token）中获取 JWT Token
 * - 验证 Token 的有效性
 * - 如果 Token 有效，从 Token 中提取用户信息并设置到请求属性中
 * - 如果 Token 无效或不存在，检查是否为无需认证的路径，如果不是则拒绝访问
 *
 * Token 获取顺序：
 *
 * - 优先从请求头 "Authorization" 中获取（格式：Bearer token）
 * - 如果请求头中没有，则从 Cookie "my_token" 中获取
 *
 * 请求属性设置：
 *
 * - id：用户 ID
 * - username：用户名
 * - email：用户邮箱
 * - phone：用户手机号
 * - headPic：用户头像路径
 * - cartTotal：购物车商品总数
 *
 * 响应处理：
 *
 * - AJAX 请求：返回 JSON 格式的错误信息（{@link CodeMsg#USER_SESSION_EXPIRED}）
 * - 普通请求：重定向到登录页面（/home/user/login）
 *
 * 配置位置：
 *
 * - 在 {@link com.agrismart.agrimallbackend.config.WebMvcConfig} 中注册
 * - 排除路径在 {@link com.agrismart.agrimallbackend.common.constant.RuntimeConstant#USER_NOT_NEED_CONFIRM_URL} 中定义
 *
 * @author agrimall
 * @see com.agrismart.agrimallbackend.config.WebMvcConfig
 * @see com.agrismart.agrimallbackend.common.constant.RuntimeConstant
 * @see com.agrismart.agrimallbackend.common.util.JwtUtil
 * @since 1.0
 */
@Component
public class JwtInterceptor implements HandlerInterceptor {

    /**
     * 日志记录器。
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(JwtInterceptor.class);

    /**
     * 购物车服务接口。
     * 用于获取用户购物车商品总数。
     */
    private final ICartService cartService;

    /**
     * 构造函数，注入依赖。
     *
     * @param cartService 购物车服务接口
     */
    @Autowired
    public JwtInterceptor(ICartService cartService) {
        this.cartService = cartService;
    }

    /**
     * 请求处理前的拦截方法。
     *
     * 在控制器方法执行前进行拦截，验证用户 JWT Token。
     *
     * 处理流程：
     *
     * - 检查请求 URI 是否包含 "/home/" 路径
     * - 从请求头或 Cookie 中获取 JWT Token
     * - 如果 Token 为空，检查是否为无需认证的路径
     * - 如果 Token 存在，验证 Token 的有效性
     * - 如果 Token 有效，从 Token 中提取用户信息并设置到请求属性中
     * - 如果 Token 无效，检查是否为无需认证的路径，如果不是则拒绝访问
     * - 返回 true 表示继续处理，返回 false 表示中断请求
     *
     * @param request  HTTP 请求对象
     * @param response HTTP 响应对象
     * @param handler  处理器对象
     * @return true 表示继续处理请求，false 表示中断请求
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String requestURI = request.getRequestURI();
        boolean skipCheck = false;
        String token = "";
        
        // 只拦截包含 "/home/" 的请求路径
        if (requestURI.contains("/home/")) {
            // 优先从请求头中获取 Token
            String authHeader = request.getHeader("Authorization");
            if (!StringUtil.isEmpty(authHeader) && authHeader.startsWith("Bearer ")) {
                token = authHeader.substring("Bearer ".length());
            }
            
            // 如果请求头中没有，则从 Cookie 中获取
            if (StringUtil.isEmpty(token)) {
                Cookie[] cookies = request.getCookies();
                if (cookies != null) {
                    for (Cookie cookie : cookies) {
                        if ("my_token".equals(cookie.getName())) {
                            token = cookie.getValue();
                        }
                    }
                }
            }
            
            LOGGER.info("当前URL={}", requestURI);
            LOGGER.info("当前路径获取到的token={}", token);
            
            // 如果 Token 为空，检查是否为无需认证的路径
            if (StringUtil.isEmpty(token)) {
                for (String str : RuntimeConstant.USER_NOT_NEED_CONFIRM_URL) {
                    if (requestURI.equals(str) || requestURI.startsWith(str + "/")) {
                        skipCheck = true;
                        break;
                    }
                }
                if (skipCheck) {
                    return true;
                }
                return handleUnAuthorized(request, response, requestURI);
            }
            
            // 验证 Token 的有效性
            try {
                DecodedJWT decodedJWT = JwtUtil.verifyToken(token);
                
                // 从 Token 中提取用户信息并设置到请求属性中
                Long id = decodedJWT.getClaim("id").asLong();
                if (id == null) {
                    id = Long.valueOf(decodedJWT.getClaim("id").asString());
                }
                request.setAttribute("id", id);
                request.setAttribute("username", decodedJWT.getClaim("username").asString());
                request.setAttribute("email", decodedJWT.getClaim("email").asString());
                request.setAttribute("phone", decodedJWT.getClaim("phone").asString());
                request.setAttribute("headPic", decodedJWT.getClaim("headPic").asString());
                
                // 获取购物车商品总数并设置到请求属性中
                request.setAttribute("cartTotal", cartService.total(id).getData());
            } catch (Exception e) {
                LOGGER.warn("token 校验失败", e);
                
                // Token 验证失败，检查是否为无需认证的路径
                for (String str : RuntimeConstant.USER_NOT_NEED_CONFIRM_URL) {
                    if (requestURI.equals(str) || requestURI.startsWith(str + "/")) {
                        skipCheck = true;
                        break;
                    }
                }
                if (skipCheck) {
                    return true;
                }
                return handleUnAuthorized(request, response, requestURI);
            }
        }
        return true;
    }

    /**
     * 处理未授权请求。
     *
     * 当用户未登录或 Token 无效时，根据请求类型返回相应的错误信息或重定向到登录页面。
     *
     * @param request   HTTP 请求对象
     * @param response  HTTP 响应对象
     * @param requestURI 请求 URI
     * @return false，表示中断请求
     */
    private boolean handleUnAuthorized(HttpServletRequest request,
                                       HttpServletResponse response,
                                       String requestURI) {
        // 处理 AJAX 请求
        if (StringUtil.isAjax(request)) {
            try {
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write(JSON.toJSONString(CodeMsg.USER_SESSION_EXPIRED));
            } catch (IOException ioException) {
                LOGGER.error("写入响应失败", ioException);
            }
            return false;
        }
        
        // 处理普通请求，重定向到登录页面
        try {
            LOGGER.info("没有登录或token非法，跳转登录界面！当前URL={}", requestURI);
            response.sendRedirect("/home/user/login");
        } catch (IOException ioException) {
            LOGGER.error("重定向失败", ioException);
        }
        return false;
    }
}
