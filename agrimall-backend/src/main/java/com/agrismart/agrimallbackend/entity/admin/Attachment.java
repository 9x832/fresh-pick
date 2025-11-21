package com.agrismart.agrimallbackend.entity.admin;

import com.agrismart.agrimallbackend.annotation.ValidateEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 附件实体类。
 *
 * 该实体类对应数据库中的附件表，用于存储后台管理系统上传的附件信息。
 * 附件主要用于邮件系统，管理员可以在发送邮件时附加文件。
 *
 * 使用场景：
 *
 * - 附件上传和管理
 * - 邮件附件关联
 * - 附件下载
 *
 * @author agrimall
 * @see com.agrismart.agrimallbackend.entity.admin.Admin
 * @see com.agrismart.agrimallbackend.entity.admin.Mail
 * @since 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Attachment {
    /**
     * 附件 ID。
     * 主键，自增。
     */
    private Integer id;

    /**
     * 附件上传者管理员 ID。
     * 必填字段，关联到 {@link com.agrismart.agrimallbackend.entity.admin.Admin} 表。
     */
    @ValidateEntity(required = true, errorRequiredMsg = "发件人不能为空！")
    private Integer senderId;

    /**
     * 附件保存路径。
     * 必填字段，长度范围：1-256 字符。相对于附件上传根目录的路径。
     */
    @ValidateEntity(required = true, requiredMinLength = true, requiredMaxLength = true, maxLength = 256, minLength = 1, errorMaxLengthMsg = "附件保存路径长度不能大于256！", errorMinLengthMsg = "附件保存路径长度不能小于1！", errorRequiredMsg = "附件保存路径不能为空！")
    private String url;

    /**
     * 附件名称（原始文件名）。
     * 必填字段，长度范围：1-256 字符。用户上传时的原始文件名。
     */
    @ValidateEntity(required = true, requiredMinLength = true, requiredMaxLength = true, maxLength = 256, minLength = 1, errorMaxLengthMsg = "附件名称长度不能大于256！", errorMinLengthMsg = "附件名称长度不能小于1！", errorRequiredMsg = "附件名称不能为空！")
    private String name;

    /**
     * 附件大小。
     * 必填字段，单位为 KB。默认值为 0.00。
     */
    @ValidateEntity(required = true, errorRequiredMsg = "附件大小不能为空！")
    private BigDecimal size;

    /**
     * 附件创建时间。
     * 系统自动设置。
     */
    private Date createTime;

    /**
     * 附件更新时间。
     * 系统自动更新。
     */
    private Date updateTime;

    /**
     * 构造函数（用于附件上传）。
     *
     * @param o                 未使用（保留参数）
     * @param adminId           管理员 ID
     * @param filename          附件保存路径
     * @param originalFilename  原始文件名
     * @param size              附件大小（KB）
     */
    public Attachment(Object o, Integer adminId, String filename, String originalFilename, BigDecimal size) {
        this.senderId = adminId;
        this.url = filename;
        this.name = originalFilename;
        this.size = size;
    }

    public void setUrl(String url) {
        this.url = url == null ? null : url.trim();
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }
}
