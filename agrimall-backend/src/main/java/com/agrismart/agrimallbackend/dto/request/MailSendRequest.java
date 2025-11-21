package com.agrismart.agrimallbackend.dto.request;

import com.agrismart.agrimallbackend.entity.admin.Mail;

import java.util.List;

/**
 * 后台发送邮件请求数据传输对象。
 *
 * 该 DTO 用于接收后台管理员发送邮件的请求参数，包含邮件信息和收件人 ID 列表。
 *
 * 使用场景：
 *
 * - 在 {@link com.agrismart.agrimallbackend.controller.admin.MailManageController#send} 中接收发送邮件请求
 *
 * @author agrimall
 * @see com.agrismart.agrimallbackend.controller.admin.MailManageController
 * @see com.agrismart.agrimallbackend.entity.admin.Mail
 * @since 1.0
 */
public class MailSendRequest {

    /**
     * 邮件实体对象。
     * 包含邮件的标题、内容、附件等信息。
     */
    private Mail mail;

    /**
     * 收件人 ID 列表。
     * 支持多个收件人，每个 ID 对应一个管理员。
     */
    private List<Integer> receiverIds;

    public Mail getMail() {
        return mail;
    }

    public void setMail(Mail mail) {
        this.mail = mail;
    }

    public List<Integer> getReceiverIds() {
        return receiverIds;
    }

    public void setReceiverIds(List<Integer> receiverIds) {
        this.receiverIds = receiverIds;
    }
}

