package com.agrismart.agrimallbackend.entity.home;

import com.agrismart.agrimallbackend.annotation.ValidateEntity;
import com.agrismart.agrimallbackend.entity.common.Product;
import com.agrismart.agrimallbackend.entity.common.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 商品收藏实体类。
 *
 * 该实体类对应数据库中的收藏表，用于存储用户收藏的商品信息。
 * 用户可以将感兴趣的商品添加到收藏列表，方便后续查看和购买。
 *
 * 使用场景：
 *
 * - 用户收藏商品
 * - 收藏列表查询
 * - 取消收藏
 * - 个性化推荐（基于收藏行为）
 *
 * @author agrimall
 * @see com.agrismart.agrimallbackend.entity.common.User
 * @see com.agrismart.agrimallbackend.entity.common.Product
 * @since 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Collect {
    /**
     * 收藏 ID。
     * 主键，自增。
     */
    private Long id;

    /**
     * 收藏对应的用户 ID。
     * 必填字段，关联到 {@link com.agrismart.agrimallbackend.entity.common.User} 表。
     */
    @ValidateEntity(required = true, errorRequiredMsg = "该收藏对应的用户不能为空！")
    private Long userId;

    /**
     * 收藏对应的用户对象。
     * 关联对象，用于查询时关联用户信息。
     */
    private User user;

    /**
     * 收藏对应的商品 ID。
     * 必填字段，关联到 {@link com.agrismart.agrimallbackend.entity.common.Product} 表。
     */
    @ValidateEntity(required = true, errorRequiredMsg = "该收藏对应的商品不能为空！")
    private Long productId;

    /**
     * 收藏对应的商品对象。
     * 关联对象，用于查询时关联商品信息。
     */
    private Product product;

    /**
     * 收藏创建时间。
     * 系统自动设置。
     */
    private Date createTime;

    /**
     * 收藏更新时间。
     * 系统自动更新。
     */
    private Date updateTime;

    /**
     * 构造函数（用于创建收藏记录）。
     *
     * @param userId    用户 ID
     * @param productId 商品 ID
     */
    public Collect(Long userId, Long productId) {
        this.userId = userId;
        this.productId = productId;
    }
}

