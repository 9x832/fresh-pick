package com.agrismart.agrimallbackend.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 实体字段验证自定义注解类。
 *
 * 该注解用于标记实体类的字段，通过 {@link com.agrismart.agrimallbackend.common.util.ValidateEntityUtil}
 * 工具类进行字段值的验证。支持以下验证规则：
 *
 * - 必填验证：检查字段值是否为null
 * - 字符串长度验证：检查字符串字段的长度范围
 * - 数值范围验证：检查数值字段的取值范围
 *
 * 使用示例：
 * <pre>
 * {@code
 * @ValidateEntity(
 *     required = true,
 *     requiredMinLength = true,
 *     requiredMaxLength = true,
 *     minLength = 6,
 *     maxLength = 16,
 *     errorRequiredMsg = "密码不能为空！",
 *     errorMinLengthMsg = "密码长度不能小于6！",
 *     errorMaxLengthMsg = "密码长度不能大于16！"
 * )
 * private String password;
 * }
 * </pre>
 *
 * @author agrimall
 * @see com.agrismart.agrimallbackend.common.util.ValidateEntityUtil
 * @since 1.0
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidateEntity {

    /**
     * 是否必填。
     * 当设置为 true 时，字段值不能为 null，否则验证失败。
     *
     * @return true 表示该字段必填，false 表示可选
     */
    boolean required() default false;

    /**
     * 是否启用最大长度验证。
     * 当设置为 true 时，会检查字符串字段的长度是否超过 maxLength 指定的值。
     *
     * @return true 表示启用最大长度验证，false 表示不验证
     */
    boolean requiredMaxLength() default false;

    /**
     * 是否启用最小长度验证。
     * 当设置为 true 时，会检查字符串字段的长度是否小于 minLength 指定的值。
     *
     * @return true 表示启用最小长度验证，false 表示不验证
     */
    boolean requiredMinLength() default false;

    /**
     * 是否启用最大值验证。
     * 当设置为 true 时，会检查数值字段的值是否超过 maxValue 指定的值。
     * 支持的数值类型：Integer、Long、Float、Double、BigDecimal
     *
     * @return true 表示启用最大值验证，false 表示不验证
     */
    boolean requiredMaxValue() default false;

    /**
     * 是否启用最小值验证。
     * 当设置为 true 时，会检查数值字段的值是否小于 minValue 指定的值。
     * 支持的数值类型：Integer、Long、Float、Double、BigDecimal
     *
     * @return true 表示启用最小值验证，false 表示不验证
     */
    boolean requiredMinValue() default false;

    /**
     * 字符串字段的最大长度限制。
     * 当 requiredMaxLength 为 true 时，此值生效。
     * 默认值为 -1，表示不限制。
     *
     * @return 最大长度值，-1 表示不限制
     */
    int maxLength() default -1;

    /**
     * 字符串字段的最小长度限制。
     * 当 requiredMinLength 为 true 时，此值生效。
     * 默认值为 -1，表示不限制。
     *
     * @return 最小长度值，-1 表示不限制
     */
    int minLength() default -1;

    /**
     * 数值字段的最大值限制。
     * 当 requiredMaxValue 为 true 时，此值生效。
     * 默认值为 -1，表示不限制。
     *
     * @return 最大值，-1 表示不限制
     */
    double maxValue() default -1;

    /**
     * 数值字段的最小值限制。
     * 当 requiredMinValue 为 true 时，此值生效。
     * 默认值为 -1，表示不限制。
     *
     * @return 最小值，-1 表示不限制
     */
    double minValue() default -1;

    /**
     * 必填验证失败时的错误提示消息。
     * 当 required 为 true 且字段值为 null 时，返回此消息。
     *
     * @return 错误提示消息
     */
    String errorRequiredMsg() default "";

    /**
     * 最小长度验证失败时的错误提示消息。
     * 当 requiredMinLength 为 true 且字符串长度小于 minLength 时，返回此消息。
     *
     * @return 错误提示消息
     */
    String errorMinLengthMsg() default "";

    /**
     * 最大长度验证失败时的错误提示消息。
     * 当 requiredMaxLength 为 true 且字符串长度大于 maxLength 时，返回此消息。
     *
     * @return 错误提示消息
     */
    String errorMaxLengthMsg() default "";

    /**
     * 最小值验证失败时的错误提示消息。
     * 当 requiredMinValue 为 true 且数值小于 minValue 时，返回此消息。
     *
     * @return 错误提示消息
     */
    String errorMinValueMsg() default "";

    /**
     * 最大值验证失败时的错误提示消息。
     * 当 requiredMaxValue 为 true 且数值大于 maxValue 时，返回此消息。
     *
     * @return 错误提示消息
     */
    String errorMaxValueMsg() default "";
}

