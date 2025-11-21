package com.agrismart.agrimallbackend.entity.common;

import com.agrismart.agrimallbackend.entity.home.Address;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 订单实体类。
 *
 * 该实体类对应数据库中的订单表，用于存储用户的订单信息。
 * 订单包含订单基本信息、订单状态、配送地址和订单项列表。
 * 采用软删除机制，用户删除订单不会真正删除数据。
 *
 * 使用场景：
 *
 * - 订单生成和提交
 * - 订单状态管理
 * - 订单查询和统计
 * - 订单删除（软删除）
 *
 * 相关枚举：
 *
 * - {@link com.agrismart.agrimallbackend.common.enums.OrderStateEnum}：订单状态枚举
 * - {@link com.agrismart.agrimallbackend.common.enums.OrderDeleteEnum}：订单删除状态枚举
 *
 * @author agrimall
 * @see com.agrismart.agrimallbackend.entity.common.User
 * @see com.agrismart.agrimallbackend.entity.home.Address
 * @see com.agrismart.agrimallbackend.entity.common.OrderItem
 * @see com.agrismart.agrimallbackend.common.enums.OrderStateEnum
 * @see com.agrismart.agrimallbackend.common.enums.OrderDeleteEnum
 * @since 1.0
 */
@Data
@NoArgsConstructor
public class Order {
    /**
     * 订单 ID。
     * 主键，自增。
     */
    private Long id;

    /**
     * 订单流水号。
     * 唯一标识，通常使用雪花算法生成。
     */
    private Long orderNo;

    /**
     * 订单所属用户 ID。
     * 关联到 {@link com.agrismart.agrimallbackend.entity.common.User} 表。
     */
    private Long userId;

    /**
     * 订单所属用户对象。
     * 关联对象，用于查询时关联用户信息。
     */
    private User user;

    /**
     * 订单状态。
     * 取值：0-未支付，1-已支付待发货，2-已取消，3-已送达待签收，4-已签收，5-已发货。
     * 对应 {@link com.agrismart.agrimallbackend.common.enums.OrderStateEnum}。
     */
    private Integer state;

    /**
     * 订单总价。
     * 所有订单项的总价之和。
     */
    private BigDecimal totalPrice;

    /**
     * 订单对应的配送地址 ID。
     * 关联到 {@link com.agrismart.agrimallbackend.entity.home.Address} 表。
     */
    private Long addressId;

    /**
     * 订单对应的配送地址对象。
     * 关联对象，用于查询时关联地址信息。
     */
    private Address address;

    /**
     * 订单留言。
     * 可选字段，用户下单时的备注信息。
     */
    private String remark;

    /**
     * 用户是否删除订单。
     * 取值：0-未删除，1-已删除。对应 {@link com.agrismart.agrimallbackend.common.enums.OrderDeleteEnum}。
     * 采用软删除机制，删除后订单数据仍然保留在数据库中。
     */
    private Integer isDeleted;

    /**
     * 订单对应的订单项列表。
     * 包含订单中所有商品的详细信息（商品ID、名称、价格、数量等）。
     */
    private List<OrderItem> orderItemList;

    /**
     * 订单创建时间。
     * 系统自动设置。
     */
    private Date createTime;

    /**
     * 订单更新时间。
     * 系统自动更新。
     */
    private Date updateTime;

    /**
     * 构造函数（用于订单生成）。
     *
     * @param orderNo    订单流水号
     * @param userId     用户 ID
     * @param state      订单状态
     * @param totalPrice 订单总价
     */
    public Order(Long orderNo, Long userId, Integer state, BigDecimal totalPrice) {
        this.orderNo = orderNo;
        this.userId = userId;
        this.state = state;
        this.totalPrice = totalPrice;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }
}
