package com.agrismart.agrimallbackend.common.enums;

/**
 * 订单删除状态枚举类。
 *
 * 该枚举用于标识用户是否删除了订单。
 * 订单删除采用软删除机制，即标记为已删除但数据仍保留在数据库中，便于后续数据分析和恢复。
 *
 * 枚举值说明：
 *
 * - {@link #NO}：未删除（code: 0），订单正常显示在用户的订单列表中
 * - {@link #YES}：已删除（code: 1），订单被用户删除，不再显示在订单列表中
 *
 * 使用场景：
 *
 * - 在 {@link com.agrismart.agrimallbackend.entity.common.Order} 实体中标识订单是否被用户删除
 * - 在 {@link com.agrismart.agrimallbackend.service.common.impl.OrderServiceImpl} 中查询订单时过滤已删除的订单
 * - 在 {@link com.agrismart.agrimallbackend.controller.home.HomeUserController} 中查询用户订单列表时，只返回未删除的订单
 * - 用户删除订单时，将订单标记为已删除状态
 *
 * 使用示例：
 * <pre>
 * {@code
 * // 查询未删除的订单
 * List<Order> orders = orderMapper.selectByIsDeleted(
 *     userId, OrderDeleteEnum.NO.getCode());
 *
 * // 用户删除订单（软删除）
 * order.setIsDeleted(OrderDeleteEnum.YES.getCode());
 * orderMapper.updateByPrimaryKeySelective(order);
 *
 * // 过滤已删除的订单
 * if (OrderDeleteEnum.NO.getCode().equals(order.getIsDeleted())) {
 *     // 处理未删除的订单
 * }
 * }
 * </pre>
 *
 * @author agrimall
 * @see com.agrismart.agrimallbackend.entity.common.Order
 * @see com.agrismart.agrimallbackend.service.common.impl.OrderServiceImpl
 * @see com.agrismart.agrimallbackend.controller.home.HomeUserController
 * @since 1.0
 */
public enum OrderDeleteEnum {

    /**
     * 未删除。
     * code: 0，表示订单未被用户删除，正常显示在订单列表中。
     */
    NO(0, "未删除"),

    /**
     * 已删除。
     * code: 1，表示订单已被用户删除，不再显示在订单列表中。采用软删除机制，数据仍保留在数据库中。
     */
    YES(1, "已删除"),

    ;

    /**
     * 枚举值对应的代码。
     * 用于在数据库和业务逻辑中存储和判断订单删除状态。
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
    OrderDeleteEnum(Integer code, String desc) {
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

