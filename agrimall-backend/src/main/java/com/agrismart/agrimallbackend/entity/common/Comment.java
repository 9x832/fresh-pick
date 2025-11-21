package com.agrismart.agrimallbackend.entity.common;

import com.agrismart.agrimallbackend.annotation.ValidateEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 商品评论实体类。
 *
 * 该实体类对应数据库中的评论表，用于存储用户对商品的评论信息。
 * 评论由用户创建，关联到具体的商品，用于展示商品评价和用户反馈。
 *
 * 使用场景：
 *
 * - 用户对商品进行评论
 * - 商品详情页展示评论列表
 * - 评论管理和统计
 *
 * @author agrimall
 * @see com.agrismart.agrimallbackend.entity.common.Product
 * @see com.agrismart.agrimallbackend.entity.common.User
 * @since 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Comment {
    /**
     * 评论 ID。
     * 主键，自增。
     */
    private Long id;

    /**
     * 评论对应的商品 ID。
     * 必填字段，关联到 {@link com.agrismart.agrimallbackend.entity.common.Product} 表。
     */
    @ValidateEntity(required = true, errorRequiredMsg = "评论对应的商品不能为空！")
    private Long productId;

    /**
     * 评论对应的商品对象。
     * 关联对象，用于查询时关联商品信息。
     */
    private Product product;

    /**
     * 评论对应的用户 ID。
     * 必填字段，关联到 {@link com.agrismart.agrimallbackend.entity.common.User} 表。
     */
    @ValidateEntity(required = true, errorRequiredMsg = "评论对应的用户不能为空！")
    private Long userId;

    /**
     * 评论对应的用户对象。
     * 关联对象，用于查询时关联用户信息。
     */
    private User user;

    /**
     * 评论内容。
     * 必填字段，长度范围：1-100 字符。
     */
    @ValidateEntity(required = true, requiredMaxLength = true, requiredMinLength = true, maxLength = 100, minLength = 1, errorRequiredMsg = "评论内容不能为空！", errorMinLengthMsg = "评论内容的长度不能小于1", errorMaxLengthMsg = "评论内容的长度不能大于100！")
    private String content;

    /**
     * 评论创建时间。
     * 系统自动设置。
     */
    private Date createTime;

    /**
     * 评论更新时间。
     * 系统自动更新。
     */
    private Date updateTime;

    public void setContent(String content) {
        this.content = content == null ? null : content.trim();
    }
}
