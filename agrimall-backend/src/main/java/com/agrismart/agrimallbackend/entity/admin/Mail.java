package com.agrismart.agrimallbackend.entity.admin;

import com.agrismart.agrimallbackend.annotation.ValidateEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 邮件实体类。
 *
 * 该实体类对应数据库中的邮件表，用于存储后台管理员之间发送的邮件信息。
 * 支持多个附件（最多 3 个），采用软删除机制，支持发送者和接收者分别删除。
 *
 * 使用场景：
 *
 * - 管理员之间发送邮件
 * - 收件箱和发件箱管理
 * - 邮件查看和删除
 *
 * 相关枚举：
 *
 * - {@link com.agrismart.agrimallbackend.common.enums.MailDeleteStateEnum}：邮件删除状态枚举
 * - {@link com.agrismart.agrimallbackend.common.enums.MailTypeEnum}：邮件类型枚举（系统自动发送的邮件）
 *
 * @author agrimall
 * @see com.agrismart.agrimallbackend.entity.admin.Admin
 * @see com.agrismart.agrimallbackend.entity.admin.Attachment
 * @see com.agrismart.agrimallbackend.common.enums.MailDeleteStateEnum
 * @see com.agrismart.agrimallbackend.common.enums.MailTypeEnum
 * @since 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Mail {
    /**
     * 邮件 ID。
     * 主键，自增。
     */
    private Integer id;

    /**
     * 邮件发件人管理员 ID。
     * 必填字段，关联到 {@link com.agrismart.agrimallbackend.entity.admin.Admin} 表。
     */
    @ValidateEntity(required = true, errorRequiredMsg = "发件人不能为空！")
    private Integer senderId;

    /**
     * 邮件收件人管理员 ID。
     * 必填字段，关联到 {@link com.agrismart.agrimallbackend.entity.admin.Admin} 表。
     * 注意：当前实现中，一封邮件只能有一个收件人。如需支持多个收件人，需要修改表结构。
     */
    @ValidateEntity(required = true, errorRequiredMsg = "收件人不能为空！")
    private Integer receiverId;

    /**
     * 邮件主题。
     * 必填字段，长度范围：1-64 字符。
     */
    @ValidateEntity(required = true, requiredMaxLength = true, requiredMinLength = true, maxLength = 64, minLength = 1, errorRequiredMsg = "主题不能为空！", errorMaxLengthMsg = "主题长度不能大于64！", errorMinLengthMsg = "主题长度不能小于1！")
    private String title;

    /**
     * 邮件附件 1 ID。
     * 可选字段，关联到 {@link com.agrismart.agrimallbackend.entity.admin.Attachment} 表。
     */
    private Integer attachmentOne;

    /**
     * 邮件附件 2 ID。
     * 可选字段，关联到 {@link com.agrismart.agrimallbackend.entity.admin.Attachment} 表。
     */
    private Integer attachmentTwo;

    /**
     * 邮件附件 3 ID。
     * 可选字段，关联到 {@link com.agrismart.agrimallbackend.entity.admin.Attachment} 表。
     */
    private Integer attachmentThree;

    /**
     * 邮件删除状态。
     * 可选字段，取值：1-双方均未删除，2-发送者删除，3-接收者删除。默认为 1。
     * 对应 {@link com.agrismart.agrimallbackend.common.enums.MailDeleteStateEnum}。
     */
    private Integer deleteState;

    /**
     * 邮件创建时间。
     * 系统自动设置。
     */
    private Date createTime;

    /**
     * 邮件更新时间。
     * 系统自动更新。
     */
    private Date updateTime;

    /**
     * 邮件正文内容。
     * 可选字段，支持富文本内容。
     */
    private String content;

    public void setTitle(String title) {
        this.title = title == null ? null : title.trim();
    }

    public void setContent(String content) {
        this.content = content == null ? null : content.trim();
    }
}
