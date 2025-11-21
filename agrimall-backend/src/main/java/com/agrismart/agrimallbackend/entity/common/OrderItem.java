package com.agrismart.agrimallbackend.entity.common;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 订单项实体类。
 *
 * 该实体类对应数据库中的订单详情表，用于存储订单中每个商品的详细信息。
 * 一个订单可以包含多个订单项，每个订单项对应一个商品及其购买数量和价格信息。
 *
 * 使用场景：
 *
 * - 订单生成时创建订单项
 * - 订单详情展示
 * - 订单总价计算
 *
 * 注意：
 *
 * - 订单项中保存了商品下单时的快照信息（名称、图片、价格），即使商品信息后续变更，订单项中的信息不会改变
 * - 商品小计 = 商品单价 × 购买数量
 *
 * @author agrimall
 * @see com.agrismart.agrimallbackend.entity.common.Order
 * @see com.agrismart.agrimallbackend.entity.common.Product
 * @since 1.0
 */
@Data
@NoArgsConstructor
public class OrderItem {
    /**
     * 订单项 ID。
     * 主键，自增。
     */
    private Long id;

    /**
     * 订单项对应的订单 ID。
     * 关联到 {@link com.agrismart.agrimallbackend.entity.common.Order} 表。
     */
    private Long orderId;

    /**
     * 订单项对应的商品 ID。
     * 关联到 {@link com.agrismart.agrimallbackend.entity.common.Product} 表。
     */
    private Long productId;

    /**
     * 商品名称（快照）。
     * 下单时的商品名称，用于订单详情展示。
     */
    private String productName;

    /**
     * 商品图片路径（快照）。
     * 下单时的商品图片，用于订单详情展示。
     */
    private String productPic;

    /**
     * 商品单价（快照）。
     * 下单时的商品价格，用于订单详情展示和总价计算。
     */
    private BigDecimal productPrice;

    /**
     * 商品购买数量。
     */
    private Integer quantity;

    /**
     * 商品小计。
     * 计算公式：商品单价 × 购买数量。
     */
    private BigDecimal totalPrice;

    /**
     * 订单项创建时间。
     * 系统自动设置。
     */
    private Date createTime;

    /**
     * 订单项更新时间。
     * 系统自动更新。
     */
    private Date updateTime;

    /**
     * 构造函数（用于订单项生成）。
     *
     * @param productId    商品 ID
     * @param quantity     购买数量
     * @param productName  商品名称
     * @param productPic   商品图片路径
     * @param productPrice 商品单价
     * @param totalPrice   商品小计
     */
    public OrderItem(Long productId, Integer quantity, String productName, String productPic, BigDecimal productPrice, BigDecimal totalPrice) {
        this.productId = productId;
        this.quantity = quantity;
        this.productName = productName;
        this.productPic = productPic;
        this.productPrice = productPrice;
        this.totalPrice = totalPrice;
    }

    public void setProductName(String productName) {
        this.productName = productName == null ? null : productName.trim();
    }

    public void setProductPic(String productPic) {
        this.productPic = productPic == null ? null : productPic.trim();
    }
}
