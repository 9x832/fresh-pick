package com.agrismart.agrimallbackend.common.constant;

/**
 * Session 相关常量配置类。
 *
 * 该类定义了系统中用于存储 Session 数据的键名常量。
 * 主要用于统一管理 Session 中存储的数据的键名，避免硬编码字符串。
 *
 * 主要用途：
 *
 * - 统一管理 Session 键名，便于维护和修改
 * - 避免在代码中硬编码字符串，提高代码可读性和可维护性
 * - 减少因拼写错误导致的 Session 数据获取失败问题
 *
 * 使用场景：
 *
 * - 在 {@link com.agrismart.agrimallbackend.service.admin.impl.AdminServiceImpl} 中存储和获取管理员登录信息
 * - 在 {@link com.agrismart.agrimallbackend.interceptor.AdminInterceptor} 中检查管理员登录状态
 * - 在各个 Controller 中获取当前登录的管理员信息
 *
 * 使用示例：
 * <pre>
 * {@code
 * // 存储管理员登录信息
 * request.getSession().setAttribute(SessionConstant.SESSION_ADMIN_LOGIN_KEY, admin);
 *
 * // 获取管理员登录信息
 * Admin admin = (Admin) request.getSession().getAttribute(SessionConstant.SESSION_ADMIN_LOGIN_KEY);
 *
 * // 清除管理员登录信息
 * request.getSession().setAttribute(SessionConstant.SESSION_ADMIN_LOGIN_KEY, null);
 * }
 * </pre>
 *
 * @author agrimall
 * @see com.agrismart.agrimallbackend.service.admin.impl.AdminServiceImpl
 * @see com.agrismart.agrimallbackend.interceptor.AdminInterceptor
 * @since 1.0
 */
public final class SessionConstant {

    /**
     * 私有构造函数，防止实例化。
     * 这是一个常量类，所有字段都是静态常量，不需要实例化。
     */
    private SessionConstant() {
    }

    /**
     * Session 中存储管理员登录信息的键名。
     *
     * 该常量用于在 HTTP Session 中存储和获取已登录的管理员对象。
     * 当管理员成功登录后，会将 {@link com.agrismart.agrimallbackend.entity.admin.Admin} 对象
     * 存储到 Session 中，键名为该常量值。
     *
     * 使用场景：
     *
     * - 管理员登录成功后，将管理员信息存储到 Session
     * - 拦截器中检查管理员是否已登录
     * - Controller 中获取当前登录的管理员信息
     * - 管理员登出时，清除 Session 中的管理员信息
     *
     * 注意：
     *
     * - 该键名仅用于后台管理员登录，前台用户使用 JWT token 进行认证
     * - Session 中的值类型为 {@link com.agrismart.agrimallbackend.entity.admin.Admin}
     * - 如果 Session 中不存在该键或值为 null，表示管理员未登录
     *
     */
    public static final String SESSION_ADMIN_LOGIN_KEY = "loginedAdmin";
}

