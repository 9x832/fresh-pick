package com.agrismart.agrimallbackend.common.constant;

import java.util.Arrays;
import java.util.List;

/**
 * 系统运行时常量配置类。
 *
 * 该类定义了系统中拦截器需要排除的 URL 路径列表。
 * 这些路径在拦截器配置中使用，用于指定哪些请求路径不需要进行登录验证或权限检查。
 *
 * 主要用途：
 *
 * - 配置拦截器的排除路径，避免对静态资源、登录页面等路径进行拦截
 * - 区分前台用户和后台管理员的拦截规则
 * - 定义无需验证的公开访问路径
 *
 * 使用场景：
 *
 * - 在 {@link com.agrismart.agrimallbackend.config.WebMvcConfig} 中配置拦截器时使用
 * - 在 {@link com.agrismart.agrimallbackend.interceptor.AdminInterceptor} 中判断是否需要拦截
 * - 在 {@link com.agrismart.agrimallbackend.interceptor.JwtInterceptor} 中判断是否需要验证
 *
 * @author agrimall
 * @see com.agrismart.agrimallbackend.config.WebMvcConfig
 * @see com.agrismart.agrimallbackend.interceptor.AdminInterceptor
 * @see com.agrismart.agrimallbackend.interceptor.JwtInterceptor
 * @since 1.0
 */
public final class RuntimeConstant {

    /**
     * 私有构造函数，防止实例化。
     * 这是一个常量类，所有字段都是静态常量，不需要实例化。
     */
    private RuntimeConstant() {
    }

    /**
     * 后台管理员登录拦截器无需拦截的 URL 路径列表。
     *
     * 该列表中的路径在 {@link com.agrismart.agrimallbackend.interceptor.AdminInterceptor}
     * 中不会被拦截，即使用户未登录也可以访问这些路径。
     *
     * 包含的路径类型：
     *
     * - 管理员登录相关路径
     * - 静态资源路径（CSS、JS、图片、字体等）
     * - 公共接口路径（验证码、文件上传、富文本编辑器等）
     * - 前台用户登录和注册接口
     *
     * 注意：路径支持通配符 {@code /**}，表示匹配该路径下的所有子路径。
     *
     */
    public static final List<String> ADMIN_LOGIN_EXCLUDE_PATH_PATTERNS = Arrays.asList(
            "/admin/system/login",
            "/common/cpacha/generate_cpacha",
            "/admin/login/**",
            "/admin/common/**",
            "/admin/X-admin-2.2/**",
            "/home/css/**",
            "/home/common/**",
            "/home/font/**",
            "/home/images/**",
            "/home/js/**",
            "/home/system/js/slider.js",
            "/ueditor/**",
            "/photo/**",
            "/api/common/captcha/**",
            "/api/common/ueditor/**",
            "/api/common/photo/**",
            "/api/common/upload/photo",
            "/api/home/user/login",
            "/api/home/user/register",
            "/api/admin/login"
    );

    /**
     * 前台用户登录拦截器无需拦截的 URL 路径列表。
     *
     * 该列表中的路径在 {@link com.agrismart.agrimallbackend.interceptor.JwtInterceptor}
     * 中不会被拦截，即使用户未登录也可以访问这些路径。
     *
     * 包含的路径类型：
     *
     * - 静态资源路径（CSS、JS、图片、字体等）
     * - 公共接口路径（验证码、文件上传、富文本编辑器等）
     * - 前台用户登录和注册接口
     * - 管理员相关路径（允许未登录用户访问管理员登录页面）
     *
     * 注意：
     *
     * - 该列表不包含管理员登录接口，因为前台用户拦截器不应该拦截管理员路径
     * - 路径支持通配符 {@code /**}，表示匹配该路径下的所有子路径
     *
     */
    public static final List<String> USER_LOGIN_EXCLUDE_PATH_PATTERNS = Arrays.asList(
            "/admin/system/login",
            "/home/system/js/slider.js",
            "/common/cpacha/generate_cpacha",
            "/admin/login/**",
            "/admin/common/**",
            "/admin/X-admin-2.2/**",
            "/home/css/**",
            "/home/common/**",
            "/home/font/**",
            "/home/images/**",
            "/home/js/**",
            "/ueditor/**",
            "/photo/**",
            "/api/common/captcha/**",
            "/api/common/ueditor/**",
            "/api/common/photo/**",
            "/api/home/user/login",
            "/api/home/user/register"
    );

    /**
     * 前台用户访问无需验证的 URL 路径列表。
     *
     * 该列表定义了前台用户可以无需登录即可访问的页面和接口路径。
     * 这些路径在 {@link com.agrismart.agrimallbackend.interceptor.JwtInterceptor}
     * 中即使没有有效的 JWT token 也可以访问。
     *
     * 包含的路径类型：
     *
     * - 前台首页
     * - 用户登录和注册页面
     * - 商品展示页面（水果、蔬菜、商品详情）
     * - 商品查询接口（商品列表、热销商品、商品详情）
     *
     * 使用场景：
     *
     * - 当用户未登录或 token 无效时，允许访问这些公开页面和接口
     * - 用于区分需要登录的页面和公开访问的页面
     *
     * 注意：
     *
     * - 路径 {@code /api/home/products/{id}} 中的 {@code {id}} 是路径变量，表示任意商品ID
     * - 该列表主要用于 JWT 拦截器中的特殊处理逻辑
     *
     */
    public static final List<String> USER_NOT_NEED_CONFIRM_URL = Arrays.asList(
            "/home/system/index",           // 前台首页
            "/home/user/login",             // 用户登录页面
            "/home/user/register",          // 用户注册页面
            "/home/product/fruit",          // 水果商品页面
            "/home/product/vegetable",      // 蔬菜商品页面
            "/home/product/detail",        // 商品详情页面
            "/api/home/products",           // 商品列表接口
            "/api/home/products/top",       // 热销商品接口
            "/api/home/products/{id}"       // 商品详情接口（{id}为路径变量）
    );
}

