package com.agrismart.agrimallbackend.entity.common;

import com.agrismart.agrimallbackend.annotation.ValidateEntity;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 商品分类实体类。
 *
 * 该实体类对应数据库中的商品分类表，用于存储商品的分类信息。
 * 商品通过分类进行组织和管理，方便用户浏览和搜索。
 *
 * 使用场景：
 *
 * - 商品分类管理（CRUD 操作）
 * - 商品按分类筛选和展示
 * - 商品创建和编辑时的分类选择
 *
 * @author agrimall
 * @see com.agrismart.agrimallbackend.entity.common.Product
 * @since 1.0
 */
@Data
@NoArgsConstructor
public class ProductCategory {

    /**
     * 商品分类 ID。
     * 主键，自增。
     */
    private Long id;

    /**
     * 商品分类名称。
     * 必填字段，长度范围：1-8 字符。
     */
    @ValidateEntity(required = true, requiredMaxLength = true, requiredMinLength = true, maxLength = 8, minLength = 1, errorRequiredMsg = "商品种类名称不能为空！", errorMaxLengthMsg = "商品种类名称长度不能大于8！", errorMinLengthMsg = "商品种类名称长度不能小于1！")
    private String categoryName;

    /**
     * 商品分类创建时间。
     * 系统自动设置。
     */
    private Date createTime;

    /**
     * 商品分类更新时间。
     * 系统自动更新。
     */
    private Date updateTime;

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName == null ? null : categoryName.trim();
    }

}

