package com.agrismart.agrimallbackend.interceptor;

import com.alibaba.fastjson.JSON;
import com.agrismart.agrimallbackend.common.bean.CodeMsg;
import com.agrismart.agrimallbackend.common.constant.SessionConstant;
import com.agrismart.agrimallbackend.common.util.StringUtil;
import com.agrismart.agrimallbackend.entity.admin.Admin;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

/**
 * 后台管理员登录拦截器。
 *
 * 该拦截器实现了 {@link HandlerInterceptor} 接口，用于拦截后台管理系统的请求，
 * 验证管理员是否已登录。如果未登录，则拒绝访问并返回相应的错误信息或重定向到登录页面。
 *
 * 拦截逻辑：
 *
 * - 检查请求 URI 是否包含 "/admin/" 路径
 * - 从 Session 中获取管理员登录信息
 * - 如果已登录，将管理员 ID 设置到请求属性中（key: "aid"），供后续使用
 * - 如果未登录且请求路径包含 "/admin/"，则拒绝访问
 *
 * 响应处理：
 *
 * - AJAX 请求：返回 JSON 格式的错误信息（{@link CodeMsg#USER_SESSION_EXPIRED}）
 * - 普通请求：重定向到登录页面（/admin/system/login）
 *
 * 配置位置：
 *
 * - 在 {@link com.agrismart.agrimallbackend.config.WebMvcConfig} 中注册
 * - 排除路径在 {@link com.agrismart.agrimallbackend.common.constant.RuntimeConstant#ADMIN_LOGIN_EXCLUDE_PATH_PATTERNS} 中定义
 *
 * @author agrimall
 * @see com.agrismart.agrimallbackend.config.WebMvcConfig
 * @see com.agrismart.agrimallbackend.common.constant.RuntimeConstant
 * @see com.agrismart.agrimallbackend.common.constant.SessionConstant
 * @since 1.0
 */
@Component
public class AdminInterceptor implements HandlerInterceptor {

    /**
     * 日志记录器。
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(AdminInterceptor.class);

    /**
     * 请求处理前的拦截方法。
     *
     * 在控制器方法执行前进行拦截，验证管理员登录状态。
     *
     * 处理流程：
     *
     * - 获取请求 URI 和 Session
     * - 从 Session 中获取管理员登录信息
     * - 如果已登录，将管理员 ID 设置到请求属性中（key: "aid"）
     * - 如果未登录且请求路径包含 "/admin/"，则拒绝访问并返回错误信息或重定向
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
        HttpSession session = request.getSession();
        Object attribute = session.getAttribute(SessionConstant.SESSION_ADMIN_LOGIN_KEY);
        
        // 如果已登录，将管理员 ID 设置到请求属性中，供后续使用
        // 注意：即使路径被排除，如果用户已登录，也要设置 aid，以便需要管理员ID的接口可以使用
        if (attribute instanceof Admin) {
            request.setAttribute("aid", ((Admin) attribute).getId());
        }
        
        // 如果未登录且请求路径包含 "/admin/"，则拒绝访问
        if (attribute == null && requestURI.contains("/admin/")) {
            LOGGER.info("当前URL={}", requestURI);
            
            // 处理 AJAX 请求
            if (StringUtil.isAjax(request)) {
                try {
                    response.setCharacterEncoding("UTF-8");
                    response.getWriter().write(JSON.toJSONString(CodeMsg.USER_SESSION_EXPIRED));
                } catch (IOException e) {
                    LOGGER.error("写入响应失败", e);
                }
                return false;
            }
            
            // 处理普通请求，重定向到登录页面
            try {
                LOGGER.info("没有登录或session失效，跳转登录界面！当前URL={}", requestURI);
                response.sendRedirect("/admin/system/login");
            } catch (IOException e) {
                LOGGER.error("重定向失败", e);
            }
            return false;
        }
        return true;
    }
}

