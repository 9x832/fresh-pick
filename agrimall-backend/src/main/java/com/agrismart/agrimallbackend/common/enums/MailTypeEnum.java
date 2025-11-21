package com.agrismart.agrimallbackend.common.enums;

/**
 * 邮件类型枚举类。
 *
 * 该枚举用于标识系统自动发送邮件的类型。
 * 系统会在特定业务场景下自动发送邮件通知用户，不同类型的邮件有不同的模板和内容。
 *
 * 枚举值说明：
 *
 * - {@link #USER_REGISTER}：用户注册邮件（code: 1），用户注册成功后自动发送欢迎邮件
 * - {@link #ORDER_SUBMIT}：订单提交邮件（code: 2），用户提交订单后自动发送订单确认邮件
 *
 * 使用场景：
 *
 * - 在 {@link com.agrismart.agrimallbackend.service.common.impl.UserServiceImpl} 中发送用户注册邮件
 * - 在 {@link com.agrismart.agrimallbackend.service.common.impl.OrderServiceImpl} 中发送订单提交邮件
 * - 在 {@link com.agrismart.agrimallbackend.common.util.MailUtil} 中根据邮件类型选择不同的邮件模板
 *
 * 使用示例：
 * <pre>
 * {@code
 * // 发送用户注册邮件
 * mailUtil.sendMailAsync(MailTypeEnum.USER_REGISTER.getCode(), userEmail, "");
 *
 * // 发送订单提交邮件
 * mailUtil.sendMailAsync(MailTypeEnum.ORDER_SUBMIT.getCode(), userEmail, orderInfo);
 *
 * // 根据邮件类型选择模板
 * if (MailTypeEnum.USER_REGISTER.getCode().equals(mailType)) {
 *     // 使用用户注册邮件模板
 * }
 * }
 * </pre>
 *
 * @author agrimall
 * @see com.agrismart.agrimallbackend.common.util.MailUtil
 * @see com.agrismart.agrimallbackend.service.common.impl.UserServiceImpl
 * @see com.agrismart.agrimallbackend.service.common.impl.OrderServiceImpl
 * @since 1.0
 */
public enum MailTypeEnum {

    /**
     * 用户注册邮件。
     * code: 1，用户注册成功后系统自动发送的欢迎邮件。
     */
    USER_REGISTER(1, "用户注册"),

    /**
     * 订单提交邮件。
     * code: 2，用户提交订单后系统自动发送的订单确认邮件。
     */
    ORDER_SUBMIT(2, "订单提交"),

    ;

    /**
     * 枚举值对应的代码。
     * 用于在数据库和业务逻辑中存储和判断邮件类型。
     */
    Integer code;

    /**
     * 枚举值的描述信息。
     * 用于显示和说明该枚举值的含义。
     */
    String desc;

    /**
     * 枚举构造函数。
     *
     * @param code 枚举值对应的代码
     * @param desc 枚举值的描述信息
     */
    MailTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 获取枚举值对应的代码。
     *
     * @return 枚举值对应的代码
     */
    public Integer getCode() {
        return code;
    }

    /**
     * 设置枚举值对应的代码。
     * 注意：通常不建议修改枚举值的 code，该方法主要用于兼容性考虑。
     *
     * @param code 枚举值对应的代码
     */
    public void setCode(Integer code) {
        this.code = code;
    }

    /**
     * 获取枚举值的描述信息。
     *
     * @return 枚举值的描述信息
     */
    public String getDesc() {
        return desc;
    }

    /**
     * 设置枚举值的描述信息。
     * 注意：通常不建议修改枚举值的 desc，该方法主要用于兼容性考虑。
     *
     * @param desc 枚举值的描述信息
     */
    public void setDesc(String desc) {
        this.desc = desc;
    }
}

