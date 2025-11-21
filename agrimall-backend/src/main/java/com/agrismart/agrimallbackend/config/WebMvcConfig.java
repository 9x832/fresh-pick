package com.agrismart.agrimallbackend.config;

import com.agrismart.agrimallbackend.common.constant.RuntimeConstant;
import com.agrismart.agrimallbackend.interceptor.AdminInterceptor;
import com.agrismart.agrimallbackend.interceptor.JwtInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Spring MVC 配置类。
 *
 * 该类实现了 {@link WebMvcConfigurer} 接口，用于配置 Spring MVC 的相关功能。
 * 主要包括拦截器注册和静态资源处理配置。
 *
 * 主要功能：
 *
 * - 注册后台管理员登录拦截器（{@link AdminInterceptor}）
 * - 注册前台用户 JWT 认证拦截器（{@link JwtInterceptor}）
 * - 配置静态资源访问路径（上传文件访问）
 *
 * 拦截器执行顺序：
 *
 * - AdminInterceptor：检查后台管理员登录状态（Session 认证）
 * - JwtInterceptor：检查前台用户登录状态（JWT 认证）
 *
 * 使用场景：
 *
 * - 保护需要登录才能访问的接口
 * - 区分前后台不同的认证方式
 * - 提供上传文件的静态资源访问
 *
 * @author agrimall
 * @see com.agrismart.agrimallbackend.interceptor.AdminInterceptor
 * @see com.agrismart.agrimallbackend.interceptor.JwtInterceptor
 * @see com.agrismart.agrimallbackend.common.constant.RuntimeConstant
 * @since 1.0
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    /**
     * 后台管理员登录拦截器。
     * 用于检查后台管理员是否已登录（基于 Session）。
     */
    private final AdminInterceptor adminInterceptor;

    /**
     * 前台用户 JWT 认证拦截器。
     * 用于检查前台用户是否已登录（基于 JWT token）。
     */
    private final JwtInterceptor jwtInterceptor;

    /**
     * 构造函数，注入拦截器。
     *
     * @param adminInterceptor 后台管理员登录拦截器
     * @param jwtInterceptor   前台用户 JWT 认证拦截器
     */
    @Autowired
    public WebMvcConfig(AdminInterceptor adminInterceptor,
                        JwtInterceptor jwtInterceptor) {
        this.adminInterceptor = adminInterceptor;
        this.jwtInterceptor = jwtInterceptor;
    }

    /**
     * 注册拦截器。
     *
     * 该方法注册了两个拦截器：
     *
     * - AdminInterceptor：拦截所有路径，但排除 {@link RuntimeConstant#ADMIN_LOGIN_EXCLUDE_PATH_PATTERNS} 中定义的路径
     * - JwtInterceptor：拦截所有路径，但排除 {@link RuntimeConstant#USER_LOGIN_EXCLUDE_PATH_PATTERNS} 中定义的路径
     *
     * 拦截器执行顺序：
     *
     * - AdminInterceptor 先执行，检查后台管理员登录状态
     * - JwtInterceptor 后执行，检查前台用户登录状态
     *
     * 排除路径说明：
     *
     * - 登录接口、静态资源、公共接口等不需要认证的路径会被排除
     * - 排除路径列表在 {@link RuntimeConstant} 中统一管理
     *
     * @param registry 拦截器注册表
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册后台管理员登录拦截器
        registry.addInterceptor(adminInterceptor)
                .addPathPatterns("/**")  // 拦截所有路径
                .excludePathPatterns(RuntimeConstant.ADMIN_LOGIN_EXCLUDE_PATH_PATTERNS.toArray(new String[0]));  // 排除不需要拦截的路径
        
        // 注册前台用户 JWT 认证拦截器
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/**")  // 拦截所有路径
                .excludePathPatterns(RuntimeConstant.USER_LOGIN_EXCLUDE_PATH_PATTERNS.toArray(new String[0]));  // 排除不需要拦截的路径
    }

    /**
     * 配置静态资源处理器。
     *
     * 该方法配置了上传文件的静态资源访问路径。
     * 通过该配置，可以通过 HTTP 请求直接访问服务器上的上传文件。
     *
     * 配置说明：
     *
     * - 资源访问路径：{@code /upload/**}，匹配所有以 /upload/ 开头的请求
     * - 资源存储位置：{@code file:upload/}，指向项目根目录下的 upload 文件夹
     *
     * 使用示例：
     *
     * - 访问图片：{@code http://localhost:8080/upload/photo/20251112/image.jpg}
     * - 访问附件：{@code http://localhost:8080/upload/attachment/20200807/file.docx}
     *
     * 注意：
     *
     * - 资源路径使用 {@code file:} 前缀，表示文件系统路径
     * - 路径相对于项目根目录，不是 classpath
     * - 生产环境建议使用 Nginx 等 Web 服务器处理静态资源，提高性能
     *
     * @param registry 静态资源注册表
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/upload/**")           // 资源访问路径
                .addResourceLocations("file:upload/");      // 资源存储位置
    }
}

