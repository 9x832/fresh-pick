package com.agrismart.agrimallbackend.common.enums;

/**
 * 邮件删除状态枚举类。
 *
 * 该枚举用于标识后台管理系统中邮件的删除状态。
 * 由于邮件涉及发送者和接收者双方，删除状态需要区分是哪一方删除了邮件。
 *
 * 枚举值说明：
 *
 * - {@link #ALL_NOT_DELETE}：双方均未删除（code: 1），发送者和接收者都能看到该邮件
 * - {@link #SENDER_DELETE}：发送者删除（code: 2），只有接收者能看到该邮件
 * - {@link #RECEIVER_DELETE}：接收者删除（code: 3），只有发送者能看到该邮件
 *
 * 使用场景：
 *
 * - 在 {@link com.agrismart.agrimallbackend.entity.admin.Mail} 实体中标识邮件的删除状态
 * - 在 {@link com.agrismart.agrimallbackend.service.admin.impl.MailServiceImpl} 中查询和管理邮件删除状态
 * - 在邮件列表查询时，根据当前用户角色（发送者/接收者）和删除状态过滤邮件
 *
 * 使用示例：
 * <pre>
 * {@code
 * // 设置邮件为双方均未删除
 * mail.setIsDeleted(MailDeleteStateEnum.ALL_NOT_DELETE.getCode());
 *
 * // 发送者删除邮件
 * mail.setIsDeleted(MailDeleteStateEnum.SENDER_DELETE.getCode());
 *
 * // 接收者删除邮件
 * mail.setIsDeleted(MailDeleteStateEnum.RECEIVER_DELETE.getCode());
 *
 * // 查询未删除的邮件（根据当前用户角色）
 * List<Mail> mails = mailMapper.selectByDeleteState(
 *     MailDeleteStateEnum.ALL_NOT_DELETE.getCode());
 * }
 * </pre>
 *
 * @author agrimall
 * @see com.agrismart.agrimallbackend.entity.admin.Mail
 * @see com.agrismart.agrimallbackend.service.admin.impl.MailServiceImpl
 * @since 1.0
 */
public enum MailDeleteStateEnum {

    /**
     * 双方均未删除。
     * code: 1，表示发送者和接收者都未删除该邮件，双方都能看到该邮件。
     */
    ALL_NOT_DELETE(1, "双方均未删除"),

    /**
     * 发送者删除。
     * code: 2，表示发送者已删除该邮件，只有接收者能看到该邮件。
     */
    SENDER_DELETE(2, "发送者删除"),

    /**
     * 接收者删除。
     * code: 3，表示接收者已删除该邮件，只有发送者能看到该邮件。
     */
    RECEIVER_DELETE(3, "接收者删除"),

    ;

    /**
     * 枚举值对应的代码。
     * 用于在数据库和业务逻辑中存储和判断邮件删除状态。
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
    MailDeleteStateEnum(Integer code, String desc) {
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

