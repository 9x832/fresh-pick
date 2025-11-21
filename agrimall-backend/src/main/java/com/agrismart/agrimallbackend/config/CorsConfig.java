package com.agrismart.agrimallbackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.time.Duration;

/**
 * 全局跨域资源共享（CORS）配置类。
 *
 * 该类配置了系统的跨域访问策略，允许前端应用从不同域名访问后端 API。
 * 通过配置 CORS 过滤器，解决浏览器同源策略限制，实现前后端分离架构。
 *
 * 配置特性：
 *
 * - 允许携带凭证（cookies、authorization headers 等）
 * - 允许所有来源的跨域请求（生产环境建议限制具体域名）
 * - 允许所有 HTTP 方法（GET、POST、PUT、DELETE 等）
 * - 允许所有请求头
 * - 预检请求缓存时间：1 小时
 *
 * 使用场景：
 *
 * - 前端应用部署在不同域名时访问后端 API
 * - 开发环境下的跨域调试
 * - 前后端分离架构的跨域支持
 *
 * 注意：
 *
 * - 当前配置允许所有来源，生产环境建议限制为具体的前端域名
 * - 预检请求（OPTIONS）的响应会被缓存 1 小时，减少重复请求
 * - 允许携带凭证，确保 JWT token 等认证信息可以正常传递
 *
 * @author agrimall
 * @since 1.0
 */
@Configuration
public class CorsConfig {

    /**
     * CORS 过滤器 Bean。
     *
     * 创建并配置跨域过滤器，对所有请求路径（/**）应用跨域配置。
     * 该过滤器会在请求处理前进行跨域检查，自动添加必要的 CORS 响应头。
     *
     * 配置说明：
     *
     * - {@code setAllowCredentials(true)}：允许携带凭证（cookies、authorization headers）
     * - {@code addAllowedOriginPattern(CorsConfiguration.ALL)}：允许所有来源（使用模式匹配）
     * - {@code addAllowedHeader(CorsConfiguration.ALL)}：允许所有请求头
     * - {@code addAllowedMethod(CorsConfiguration.ALL)}：允许所有 HTTP 方法
     * - {@code setMaxAge(Duration.ofHours(1))}：预检请求缓存时间 1 小时
     *
     * 预检请求（Preflight Request）：
     *
     * - 对于复杂请求（如带自定义头的 POST 请求），浏览器会先发送 OPTIONS 预检请求
     * - 预检请求的响应会被缓存，减少重复的预检请求
     * - 缓存时间设置为 1 小时，平衡性能和安全性
     *
     * @return CORS 过滤器实例
     */
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowCredentials(true);                              // 允许携带凭证
        configuration.addAllowedOriginPattern(CorsConfiguration.ALL);          // 允许所有来源
        configuration.addAllowedHeader(CorsConfiguration.ALL);                 // 允许所有请求头
        configuration.addAllowedMethod(CorsConfiguration.ALL);                 // 允许所有 HTTP 方法
        configuration.setMaxAge(Duration.ofHours(1));                         // 预检请求缓存时间：1 小时

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);                // 对所有路径应用跨域配置
        return new CorsFilter(source);
    }
}

