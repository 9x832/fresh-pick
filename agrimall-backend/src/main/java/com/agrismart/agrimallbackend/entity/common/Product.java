package com.agrismart.agrimallbackend.entity.common;

import com.agrismart.agrimallbackend.annotation.ValidateEntity;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 商品实体类。
 *
 * 该实体类对应数据库中的商品表，用于存储电商平台的商品信息。
 * 商品包含基本信息（名称、价格、库存等）、销售统计（销量、评论数）和分类信息。
 *
 * 使用场景：
 *
 * - 商品管理（CRUD 操作）
 * - 商品浏览和搜索
 * - 商品详情展示
 * - 购物车和订单关联
 *
 * @author agrimall
 * @see com.agrismart.agrimallbackend.entity.common.ProductCategory
 * @since 1.0
 */
@Data
@NoArgsConstructor
public class Product {
    /**
     * 商品 ID。
     * 主键，自增。
     */
    private Long id;

    /**
     * 商品名称。
     * 必填字段，长度范围：1-16 字符。
     */
    @ValidateEntity(required = true, requiredMaxLength = true, requiredMinLength = true, maxLength = 16, minLength = 1, errorRequiredMsg = "商品名称不能为空！", errorMaxLengthMsg = "商品名称长度不能大于16！", errorMinLengthMsg = "商品名称长度不能小于1！")
    private String productName;

    /**
     * 商品详情。
     * 必填字段，长度范围：1-32 字符。支持富文本内容。
     */
    @ValidateEntity(required = true, requiredMaxLength = true, requiredMinLength = true, maxLength = 32, minLength = 1, errorRequiredMsg = "商品详情不能为空！", errorMaxLengthMsg = "商品详情长度不能大于32！", errorMinLengthMsg = "商品详情长度不能小于1！")
    private String info;

    /**
     * 商品图片路径。
     * 可选字段，最大长度 256 字符。
     */
    @ValidateEntity(requiredMaxLength = true, maxLength = 256, errorMaxLengthMsg = "商品图片路径长度不能大于256！")
    private String productPic;

    /**
     * 商品价格。
     * 必填字段，取值范围：0.00-100000000.00 元。
     */
    @ValidateEntity(required = true, requiredMinValue = true, requiredMaxValue = true, maxValue = 100000000.00, minValue = 0.00, errorRequiredMsg = "商品价格不能为空！", errorMaxValueMsg = "商品价格不合理，请调低价格！", errorMinValueMsg = "商品价格不能低于0.00元！")
    private BigDecimal price;

    /**
     * 商品库存数量。
     * 必填字段，取值范围：0-100000000。
     */
    @ValidateEntity(required = true, requiredMinValue = true, requiredMaxValue = true, maxValue = 100000000, minValue = 0, errorRequiredMsg = "商品库存不能为空！", errorMaxValueMsg = "商品库存不合理，请调低库存量！", errorMinValueMsg = "商品库存不能低于0个！")
    private Integer stock;

    /**
     * 商品销售数量。
     * 系统自动统计，用于商品排行和推荐。
     */
    private Integer sellNum;

    /**
     * 商品评论数量。
     * 系统自动统计，用于商品详情展示。
     */
    private Integer commentNum;

    /**
     * 商品所属的分类 ID。
     * 必填字段，关联到 {@link com.agrismart.agrimallbackend.entity.common.ProductCategory} 表。
     */
    @ValidateEntity(required = true, errorRequiredMsg = "该商品对应的商品种类不能为空！")
    private Long categoryId;

    /**
     * 商品所属的商品分类对象。
     * 关联对象，用于查询时关联分类信息。
     */
    private ProductCategory productCategory;

    /**
     * 商品创建时间。
     * 系统自动设置。
     */
    private Date createTime;

    /**
     * 商品更新时间。
     * 系统自动更新。
     */
    private Date updateTime;

    public void setProductName(String productName) {
        this.productName = productName == null ? null : productName.trim();
    }

    public void setInfo(String info) {
        this.info = info == null ? null : info.trim();
    }
}

