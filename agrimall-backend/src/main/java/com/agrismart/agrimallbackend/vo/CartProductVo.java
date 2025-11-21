package com.agrismart.agrimallbackend.vo;

import java.math.BigDecimal;

/**
 * 购物车商品视图对象。
 *
 * 该 VO 用于封装购物车中单个商品的详细信息，包含商品基本信息和购物车中的数量、小计等。
 * 用于购物车列表中的商品项展示。
 *
 * 使用场景：
 *
 * - 在 {@link com.agrismart.agrimallbackend.service.home.ICartService#list} 中封装购物车商品信息
 * - 作为 {@link com.agrismart.agrimallbackend.vo.CartVo} 的商品列表元素
 * - 前台购物车页面商品项展示
 *
 * 注意：
 *
 * - 商品小计 = 商品单价 × 购买数量
 * - 商品信息从数据库查询后填充到该 VO 中
 *
 * @author agrimall
 * @see com.agrismart.agrimallbackend.service.home.ICartService
 * @see com.agrismart.agrimallbackend.vo.CartVo
 * @see com.agrismart.agrimallbackend.entity.common.Product
 * @since 1.0
 */
public class CartProductVo {
    /**
     * 商品 ID。
     */
    private Long productId;

    /**
     * 商品名称。
     */
    private String productName;

    /**
     * 商品图片路径。
     */
    private String productPic;

    /**
     * 商品单价。
     */
    private BigDecimal price;

    /**
     * 商品小计。
     * 计算公式：商品单价 × 购买数量。
     */
    private BigDecimal productTotalPrice;

    /**
     * 购买数量。
     */
    private Integer quantity;

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductPic() {
        return productPic;
    }

    public void setProductPic(String productPic) {
        this.productPic = productPic;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getProductTotalPrice() {
        return productTotalPrice;
    }

    public void setProductTotalPrice(BigDecimal productTotalPrice) {
        this.productTotalPrice = productTotalPrice;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    /**
     * 无参构造函数。
     */
    public CartProductVo() {
    }

    /**
     * 构造函数（用于创建购物车商品 VO）。
     *
     * @param productId 商品 ID
     * @param quantity  购买数量
     */
    public CartProductVo(Long productId, Integer quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }
}

