package com.agrismart.agrimallbackend.common.util;

import com.agrismart.agrimallbackend.annotation.ValidateEntity;
import com.agrismart.agrimallbackend.common.bean.CodeMsg;

import java.lang.reflect.Field;
import java.math.BigDecimal;

/**
 * 实体校验工具类。
 *
 * 该类提供了基于 {@link ValidateEntity} 注解的实体字段验证功能。
 * 通过反射机制扫描对象的所有字段，对标记了 {@link ValidateEntity} 注解的字段进行验证。
 *
 * 支持的验证类型：
 *
 * - 必填验证：检查字段是否为 null
 * - 字符串长度验证：检查 String 类型字段的长度范围
 * - 数值范围验证：检查数值类型字段（Integer、Long、Float、Double、BigDecimal）的取值范围
 *
 * 使用示例：
 * <pre>
 * {@code
 * User user = new User();
 * user.setUsername("test");
 * CodeMsg result = ValidateEntityUtil.validate(user);
 * if (result.getCode() != 0) {
 *     // 验证失败，result.getMsg() 包含错误信息
 * }
 * }
 * </pre>
 *
 * @author agrimall
 * @see ValidateEntity
 * @see CodeMsg
 * @since 1.0
 */
public final class ValidateEntityUtil {

    /**
     * 私有构造函数，防止实例化。
     * 这是一个工具类，所有方法都是静态方法，不需要实例化。
     */
    private ValidateEntityUtil() {
    }

    /**
     * 验证对象的所有字段。
     *
     * 该方法会遍历对象的所有字段，对标记了 {@link ValidateEntity} 注解的字段进行验证。
     * 验证顺序：必填验证 → 字符串长度验证 → 数值范围验证。
     *
     * 如果某个字段验证失败，会立即返回包含错误信息的 {@link CodeMsg} 对象。
     * 如果所有字段验证通过，返回 {@link CodeMsg#SUCCESS}。
     *
     * @param object 待验证的对象，不能为 null
     * @return 验证结果，如果验证通过返回 {@link CodeMsg#SUCCESS}，
     *         否则返回包含错误信息的 CodeMsg 对象
     * @throws IllegalStateException 如果反射访问字段时发生异常
     */
    public static CodeMsg validate(Object object) {
        Field[] declaredFields = object.getClass().getDeclaredFields();
        for (Field field : declaredFields) {
            ValidateEntity annotation = field.getAnnotation(ValidateEntity.class);
            field.setAccessible(true);
            if (annotation != null) {
                if (annotation.required()) {
                    try {
                        Object value = field.get(object);
                        if (value == null) {
                            CodeMsg codeMsg = CodeMsg.VALIDATE_ENTITY_ERROR;
                            codeMsg.setMsg(annotation.errorRequiredMsg());
                            return codeMsg;
                        }
                    } catch (IllegalAccessException e) {
                        throw new IllegalStateException(e);
                    }
                }
                try {
                    Object value = field.get(object);
                    CodeMsg stringResult = confirmStringLength(value, annotation);
                    if (stringResult.getCode() != 0) {
                        return stringResult;
                    }
                    CodeMsg numberResult = confirmNumberValue(value, annotation);
                    if (numberResult.getCode() != 0) {
                        return numberResult;
                    }
                } catch (IllegalAccessException e) {
                    throw new IllegalStateException(e);
                }
            }
        }
        return CodeMsg.SUCCESS;
    }

    /**
     * 验证字符串字段的长度。
     *
     * 检查字符串字段的长度是否在注解指定的范围内。
     * 验证时会先去除字符串两端的空白字符（trim），然后进行长度比较。
     *
     * @param value      字段值，必须是 String 类型
     * @param annotation 字段上的 ValidateEntity 注解
     * @return 验证结果，如果验证通过返回 {@link CodeMsg#SUCCESS}，
     *         否则返回包含错误信息的 CodeMsg 对象
     */
    private static CodeMsg confirmStringLength(Object value, ValidateEntity annotation) {
        if (value instanceof String) {
            if (annotation.requiredMinLength()) {
                if (value.toString().trim().length() < annotation.minLength()) {
                    CodeMsg codeMsg = CodeMsg.VALIDATE_ENTITY_ERROR;
                    codeMsg.setMsg(annotation.errorMinLengthMsg());
                    return codeMsg;
                }
            }
            if (annotation.requiredMaxLength()) {
                if (value.toString().trim().length() > annotation.maxLength()) {
                    CodeMsg codeMsg = CodeMsg.VALIDATE_ENTITY_ERROR;
                    codeMsg.setMsg(annotation.errorMaxLengthMsg());
                    return codeMsg;
                }
            }
        }
        return CodeMsg.SUCCESS;
    }

    /**
     * 验证数值字段的取值范围。
     *
     * 支持以下数值类型的验证：
     *
     * - Integer、Long、Float、Double：使用 Double.parseDouble() 进行转换和比较
     * - BigDecimal：使用 BigDecimal.compareTo() 进行精确比较
     *
     * 对于 BigDecimal 类型，使用精确的数值比较，避免浮点数精度问题。
     *
     * @param value      字段值，必须是数值类型（Integer、Long、Float、Double、BigDecimal）
     * @param annotation 字段上的 ValidateEntity 注解
     * @return 验证结果，如果验证通过返回 {@link CodeMsg#SUCCESS}，
     *         否则返回包含错误信息的 CodeMsg 对象
     */
    private static CodeMsg confirmNumberValue(Object value, ValidateEntity annotation) {
        if (isNumberObject(value)) {
            if (annotation.requiredMinValue()) {
                if (Double.parseDouble(value.toString().trim()) < annotation.minValue()) {
                    CodeMsg codeMsg = CodeMsg.VALIDATE_ENTITY_ERROR;
                    codeMsg.setMsg(annotation.errorMinValueMsg());
                    return codeMsg;
                }
            }
            if (annotation.requiredMaxValue()) {
                if (Double.parseDouble(value.toString().trim()) > annotation.maxValue()) {
                    CodeMsg codeMsg = CodeMsg.VALIDATE_ENTITY_ERROR;
                    codeMsg.setMsg(annotation.errorMaxValueMsg());
                    return codeMsg;
                }
            }
        } else if (isBigDecimalObject(value)) {
            BigDecimal inputValue = new BigDecimal(value.toString().trim());
            if (annotation.requiredMinValue()) {
                BigDecimal minValue = BigDecimal.valueOf(annotation.minValue());
                if (inputValue.compareTo(minValue) < 0) {
                    CodeMsg codeMsg = CodeMsg.VALIDATE_ENTITY_ERROR;
                    codeMsg.setMsg(annotation.errorMinValueMsg());
                    return codeMsg;
                }
            }
            if (annotation.requiredMaxValue()) {
                BigDecimal maxValue = BigDecimal.valueOf(annotation.maxValue());
                if (inputValue.compareTo(maxValue) > 0) {
                    CodeMsg codeMsg = CodeMsg.VALIDATE_ENTITY_ERROR;
                    codeMsg.setMsg(annotation.errorMaxValueMsg());
                    return codeMsg;
                }
            }
        }
        return CodeMsg.SUCCESS;
    }

    /**
     * 判断对象是否为基本数值类型。
     *
     * 支持的数值类型：Integer、Long、Float、Double。
     * 注意：BigDecimal 不在此方法判断范围内，使用 {@link #isBigDecimalObject(Object)} 判断。
     *
     * @param object 待判断的对象
     * @return true 如果是 Integer、Long、Float 或 Double 类型，否则返回 false
     */
    private static boolean isNumberObject(Object object) {
        return object instanceof Integer
                || object instanceof Long
                || object instanceof Float
                || object instanceof Double;
    }

    /**
     * 判断对象是否为 BigDecimal 类型。
     *
     * BigDecimal 类型需要单独处理，因为它的比较需要使用 compareTo() 方法，
     * 而不是直接使用比较运算符，以确保数值比较的精确性。
     *
     * @param object 待判断的对象
     * @return true 如果是 BigDecimal 类型，否则返回 false
     */
    private static boolean isBigDecimalObject(Object object) {
        return object instanceof BigDecimal;
    }
}

