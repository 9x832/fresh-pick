package com.agrismart.agrimallbackend.common.util;

import jakarta.servlet.http.HttpServletRequest;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 字符串与请求相关工具类。
 *
 * 提供常用的字符串处理和 HTTP 请求判断功能。
 *
 * @author agrimall
 * @since 1.0
 */
public final class StringUtil {

    /**
     * 私有构造函数，防止实例化。
     * 这是一个工具类，所有方法都是静态方法，不需要实例化。
     */
    private StringUtil() {
    }

    /**
     * 获取指定格式的日期字符串。
     *
     * 将 Date 对象按照指定的格式字符串转换为字符串。
     *
     * 使用示例：
     * <pre>
     * {@code
     * Date date = new Date();
     * String dateStr = StringUtil.getFormatterDate(date, "yyyy-MM-dd HH:mm:ss");
     * // 结果：2024-01-01 12:00:00
     * }
     * </pre>
     *
     * @param date      待格式化的日期对象，不能为 null
     * @param formatter 日期格式字符串，例如："yyyy-MM-dd HH:mm:ss"
     * @return 格式化后的日期字符串
     * @throws NullPointerException 如果 date 或 formatter 为 null
     * @throws IllegalArgumentException 如果 formatter 格式不正确
     */
    public static String getFormatterDate(Date date, String formatter) {
        SimpleDateFormat sdf = new SimpleDateFormat(formatter);
        return sdf.format(date);
    }

    /**
     * 判断是否为 Ajax 请求。
     *
     * 通过检查 HTTP 请求头中的 "X-Requested-With" 字段来判断是否为 Ajax 请求。
     * 如果该字段的值为 "XMLHttpRequest"，则认为是 Ajax 请求。
     *
     * 该方法通常用于在拦截器中判断请求类型，以便返回不同的响应格式。
     *
     * @param request HTTP 请求对象，不能为 null
     * @return true 如果是 Ajax 请求，否则返回 false
     */
    public static boolean isAjax(HttpServletRequest request) {
        String header = request.getHeader("X-Requested-With");
        return "XMLHttpRequest".equals(header);
    }

    /**
     * 判断字符串是否为空。
     *
     * 字符串为空的条件：
     *
     * - 字符串为 null
     * - 字符串为空字符串 ""
     *
     * 注意：该方法不会去除字符串两端的空白字符后再判断。
     * 如果需要去除空白字符，请先调用 {@link String#trim()} 方法。
     *
     * @param str 待判断的字符串
     * @return true 如果字符串为 null 或空字符串，否则返回 false
     */
    public static boolean isEmpty(String str) {
        return str == null || "".equals(str);
    }
}

