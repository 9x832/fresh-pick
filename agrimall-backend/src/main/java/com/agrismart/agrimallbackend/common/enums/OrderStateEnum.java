package com.agrismart.agrimallbackend.common.enums;

/**
 * 订单状态枚举类。
 *
 * 该枚举用于标识订单在整个生命周期中的不同状态。
 * 订单状态从创建到完成会经历多个阶段，每个状态代表订单的不同处理阶段。
 *
 * 订单状态流转流程：
 *
 * - {@link #NO_PAY}：未支付 - 订单创建后的初始状态
 * - {@link #PAYED}：已支付，待发货 - 用户完成支付后
 * - {@link #SEND}：已发货 - 商家发货后
 * - {@link #ARRIVED}：已送达，待签收 - 商品送达后
 * - {@link #SIGN}：已签收 - 用户签收后，订单完成
 * - {@link #CANCELED}：已取消 - 订单在任何阶段都可能被取消
 *
 * 枚举值说明：
 *
 * - {@link #NO_PAY}：未支付（code: 0），订单已创建但用户尚未支付
 * - {@link #PAYED}：已支付，待发货（code: 1），用户已完成支付，等待商家发货
 * - {@link #CANCELED}：已取消（code: 2），订单被取消，可能由用户或系统取消
 * - {@link #ARRIVED}：已送达，待签收（code: 3），商品已送达，等待用户签收
 * - {@link #SIGN}：已签收（code: 4），用户已签收商品，订单完成
 * - {@link #SEND}：已发货（code: 5），商家已发货，商品在运输途中
 *
 * 使用场景：
 *
 * - 在 {@link com.agrismart.agrimallbackend.entity.common.Order} 实体中标识订单状态
 * - 在 {@link com.agrismart.agrimallbackend.service.common.impl.OrderServiceImpl} 中更新订单状态
 * - 在 {@link com.agrismart.agrimallbackend.controller.home.HomeUserController} 中按状态查询订单
 * - 在订单管理界面中显示订单状态和统计各状态的订单数量
 *
 * 使用示例：
 * <pre>
 * {@code
 * // 创建订单，初始状态为未支付
 * order.setState(OrderStateEnum.NO_PAY.getCode());
 *
 * // 用户支付后，更新为已支付状态
 * order.setState(OrderStateEnum.PAYED.getCode());
 *
 * // 商家发货后，更新为已发货状态
 * order.setState(OrderStateEnum.SEND.getCode());
 *
 * // 按状态查询订单
 * List<Order> orders = orderMapper.selectByState(
 *     userId, OrderStateEnum.PAYED.getCode());
 *
 * // 统计各状态的订单数量
 * int count = orderService.selectByOrderStateAndUserIdAndIsDeleted(
 *     OrderStateEnum.PAYED.getCode(), userId, OrderDeleteEnum.NO.getCode()).size();
 * }
 * </pre>
 *
 * @author agrimall
 * @see com.agrismart.agrimallbackend.entity.common.Order
 * @see com.agrismart.agrimallbackend.service.common.impl.OrderServiceImpl
 * @see com.agrismart.agrimallbackend.controller.home.HomeUserController
 * @since 1.0
 */
public enum OrderStateEnum {

    /**
     * 未支付。
     * code: 0，表示订单已创建但用户尚未完成支付，这是订单的初始状态。
     */
    NO_PAY(0, "未支付"),

    /**
     * 已支付，待发货。
     * code: 1，表示用户已完成支付，订单等待商家发货处理。
     */
    PAYED(1, "已支付，代发货"),

    /**
     * 已取消。
     * code: 2，表示订单已被取消，可能由用户主动取消或系统自动取消（如超时未支付）。
     */
    CANCELED(2, "已取消"),

    /**
     * 已送达，待签收。
     * code: 3，表示商品已送达用户指定地址，等待用户签收确认。
     */
    ARRIVED(3, "已送达，待签收"),

    /**
     * 已签收。
     * code: 4，表示用户已签收商品，订单完成。这是订单的最终完成状态。
     */
    SIGN(4, "已签收"),

    /**
     * 已发货。
     * code: 5，表示商家已发货，商品正在运输途中，等待送达。
     */
    SEND(5, "已发货"),

    ;

    /**
     * 枚举值对应的代码。
     * 用于在数据库和业务逻辑中存储和判断订单状态。
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
    OrderStateEnum(Integer code, String desc) {
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

