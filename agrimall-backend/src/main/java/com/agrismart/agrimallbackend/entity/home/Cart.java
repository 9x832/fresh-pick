package com.agrismart.agrimallbackend.entity.home;

import com.agrismart.agrimallbackend.annotation.ValidateEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 购物车实体类。
 *
 * 该实体类对应数据库中的购物车表，用于存储用户添加到购物车的商品信息。
 * 购物车记录用户想要购买的商品及其数量，用户可以从购物车生成订单。
 *
 * 使用场景：
 *
 * - 商品添加到购物车
 * - 购物车商品数量更新
 * - 购物车商品删除
 * - 从购物车生成订单
 *
 * 注意：
 *
 * - 购物车记录与用户关联，每个用户有独立的购物车
 * - 同一商品在购物车中只存在一条记录，添加相同商品时会更新数量
 *
 * @author agrimall
 * @see com.agrismart.agrimallbackend.entity.common.Product
 * @see com.agrismart.agrimallbackend.entity.common.User
 * @since 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cart {

    /**
     * 购买的商品 ID。
     * 必填字段，关联到 {@link com.agrismart.agrimallbackend.entity.common.Product} 表。
     */
    @ValidateEntity(required = true, errorRequiredMsg = "购买的商品id不能为空！")
    private Long productId;

    /**
     * 购买的商品数量。
     * 必填字段，最小值为 1。
     */
    @ValidateEntity(required = true, requiredMinValue = true, minValue = 1, errorRequiredMsg = "购买的商品数量不能为空！", errorMinValueMsg = "购买的商品数量不能小于1！")
    private Integer quantity;

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}

