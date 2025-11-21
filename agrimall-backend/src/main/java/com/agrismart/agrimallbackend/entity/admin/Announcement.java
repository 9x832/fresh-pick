package com.agrismart.agrimallbackend.entity.admin;

import com.agrismart.agrimallbackend.annotation.ValidateEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 公告实体类。
 *
 * 该实体类对应数据库中的公告表，用于存储后台管理系统发布的公告信息。
 * 公告由管理员创建，用于向用户或管理员发布重要通知。
 *
 * 使用场景：
 *
 * - 公告管理（创建、查询、删除）
 * - 系统通知发布
 * - 仪表盘最新公告展示
 *
 * @author agrimall
 * @see com.agrismart.agrimallbackend.entity.admin.Admin
 * @since 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Announcement {
    /**
     * 公告 ID。
     * 主键，自增。
     */
    private Integer id;

    /**
     * 公告内容。
     * 必填字段，长度范围：1-256 字符。支持富文本内容。
     */
    @ValidateEntity(required = true, requiredMaxLength = true, requiredMinLength = true, maxLength = 256, minLength = 1, errorRequiredMsg = "公告内容不能为空！", errorMaxLengthMsg = "公告内容长度不能大于256！", errorMinLengthMsg = "公告内容长度不能小于1！")
    private String content;

    /**
     * 公告发布者管理员 ID。
     * 必填字段，关联到 {@link com.agrismart.agrimallbackend.entity.admin.Admin} 表。
     */
    @ValidateEntity(required = true, errorRequiredMsg = "公告发布对应管理员角色不能为空！")
    private Integer adminId;

    /**
     * 公告创建时间。
     * 系统自动设置。
     */
    private Date createTime;

    /**
     * 公告更新时间。
     * 系统自动更新。
     */
    private Date updateTime;

    public void setContent(String content) {
        this.content = content == null ? null : content.trim();
    }
}
