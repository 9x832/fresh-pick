package com.agrismart.agrimallbackend.common.enums;

/**
 * 管理员状态枚举类。
 *
 * 该枚举用于标识后台管理员账户的状态。
 * 管理员账户可以被启用或冻结，冻结状态的管理员无法登录系统。
 *
 * 枚举值说明：
 *
 * - {@link #OPEN}：启用状态（code: 1），管理员可以正常登录和使用系统
 * - {@link #STOP}：冻结状态（code: 2），管理员无法登录系统
 *
 * 使用场景：
 *
 * - 在 {@link com.agrismart.agrimallbackend.entity.admin.Admin} 实体中标识管理员状态
 * - 在 {@link com.agrismart.agrimallbackend.service.admin.impl.AdminServiceImpl} 中切换管理员状态（启用/冻结）
 * - 在管理员登录时检查账户是否被冻结，冻结状态的管理员无法登录
 *
 * 使用示例：
 * <pre>
 * {@code
 * // 设置管理员为启用状态
 * admin.setState(AdminStateEnum.OPEN.getCode());
 *
 * // 设置管理员为冻结状态
 * admin.setState(AdminStateEnum.STOP.getCode());
 *
 * // 检查管理员是否被冻结
 * if (AdminStateEnum.STOP.getCode().equals(admin.getState())) {
 *     return ResponseVo.errorByMsg(CodeMsg.USER_STATE_ERROR);
 * }
 *
 * // 切换管理员状态
 * if (admin.getState().intValue() == AdminStateEnum.OPEN.getCode()) {
 *     admin.setState(AdminStateEnum.STOP.getCode()); // 从启用切换到冻结
 * } else {
 *     admin.setState(AdminStateEnum.OPEN.getCode()); // 从冻结切换到启用
 * }
 * }
 * </pre>
 *
 * @author agrimall
 * @see com.agrismart.agrimallbackend.entity.admin.Admin
 * @see com.agrismart.agrimallbackend.service.admin.impl.AdminServiceImpl
 * @since 1.0
 */
public enum AdminStateEnum {

    /**
     * 启用状态。
     * code: 1，表示管理员账户处于启用状态，可以正常登录和使用系统功能。
     */
    OPEN(1, "启用"),

    /**
     * 冻结状态。
     * code: 2，表示管理员账户处于冻结状态，无法登录系统。通常用于临时禁用某个管理员账户。
     */
    STOP(2, "冻结"),

    ;

    /**
     * 枚举值对应的代码。
     * 用于在数据库和业务逻辑中存储和判断管理员状态。
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
    AdminStateEnum(Integer code, String desc) {
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

