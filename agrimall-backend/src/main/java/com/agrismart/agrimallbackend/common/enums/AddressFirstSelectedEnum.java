package com.agrismart.agrimallbackend.common.enums;

/**
 * 地址是否是首选地址枚举类。
 *
 * 该枚举用于标识用户收货地址是否为首选地址。
 * 用户可以将其中一个地址设置为首选地址，在生成订单时会优先使用首选地址。
 *
 * 枚举值说明：
 *
 * - {@link #NO}：不是首选地址（code: 0）
 * - {@link #YES}：是首选地址（code: 1）
 *
 * 使用场景：
 *
 * - 在 {@link com.agrismart.agrimallbackend.entity.home.Address} 实体中标识地址是否为首选
 * - 在 {@link com.agrismart.agrimallbackend.service.home.impl.AddressServiceImpl} 中设置和查询首选地址
 * - 在 {@link com.agrismart.agrimallbackend.service.common.impl.OrderServiceImpl} 中获取首选地址用于订单生成
 * - 在 {@link com.agrismart.agrimallbackend.controller.home.HomeOrderController} 中获取订单的首选地址
 *
 * 使用示例：
 * <pre>
 * {@code
 * // 设置为首选地址
 * address.setFirstSelected(AddressFirstSelectedEnum.YES.getCode());
 *
 * // 查询首选地址
 * Address address = addressMapper.selectByUserIdAndFirstSelected(
 *     userId, AddressFirstSelectedEnum.YES.getCode());
 *
 * // 判断是否为首选地址
 * if (AddressFirstSelectedEnum.YES.getCode().equals(address.getFirstSelected())) {
 *     // 处理首选地址逻辑
 * }
 * }
 * </pre>
 *
 * @author agrimall
 * @see com.agrismart.agrimallbackend.entity.home.Address
 * @see com.agrismart.agrimallbackend.service.home.impl.AddressServiceImpl
 * @since 1.0
 */
public enum AddressFirstSelectedEnum {

    /**
     * 不是首选地址。
     * code: 0，表示该地址不是用户的首选收货地址。
     */
    NO(0, "不是"),

    /**
     * 是首选地址。
     * code: 1，表示该地址是用户的首选收货地址，在生成订单时会优先使用。
     */
    YES(1, "是"),

    ;

    /**
     * 枚举值对应的代码。
     * 用于在数据库和业务逻辑中存储和判断地址是否为首选。
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
    AddressFirstSelectedEnum(Integer code, String desc) {
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

