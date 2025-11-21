package com.agrismart.agrimallbackend.vo;

import java.math.BigDecimal;
import java.util.List;

/**
 * 购物车视图对象。
 *
 * 该 VO 用于封装购物车查询的返回数据，包含购物车中所有商品的详细信息和购物车总价。
 * 用于前台购物车列表展示。
 *
 * 使用场景：
 *
 * - 在 {@link com.agrismart.agrimallbackend.service.home.ICartService#list} 中返回购物车数据
 * - 前台购物车页面展示
 *
 * @author agrimall
 * @see com.agrismart.agrimallbackend.service.home.ICartService
 * @see com.agrismart.agrimallbackend.vo.CartProductVo
 * @since 1.0
 */
public class CartVo {

    /**
     * 购物车商品列表。
     * 包含购物车中每个商品的详细信息（商品ID、名称、图片、价格、数量、小计等）。
     */
    private List<CartProductVo> cartProductVoList;

    /**
     * 购物车总价。
     * 所有商品的小计之和，即购物车中所有商品的总金额。
     */
    private BigDecimal cartTotalPrice;

    public List<CartProductVo> getCartProductVoList() {
        return cartProductVoList;
    }

    public void setCartProductVoList(List<CartProductVo> cartProductVoList) {
        this.cartProductVoList = cartProductVoList;
    }

    public BigDecimal getCartTotalPrice() {
        return cartTotalPrice;
    }

    public void setCartTotalPrice(BigDecimal cartTotalPrice) {
        this.cartTotalPrice = cartTotalPrice;
    }
}

