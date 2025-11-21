package com.agrismart.agrimallbackend.service.admin.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.agrismart.agrimallbackend.common.bean.CodeMsg;
import com.agrismart.agrimallbackend.common.enums.MailDeleteStateEnum;
import com.agrismart.agrimallbackend.common.util.ValidateEntityUtil;
import com.agrismart.agrimallbackend.dto.response.ResponseVo;
import com.agrismart.agrimallbackend.entity.admin.Mail;
import com.agrismart.agrimallbackend.mapper.admin.AttachmentMapper;
import com.agrismart.agrimallbackend.mapper.admin.MailMapper;
import com.agrismart.agrimallbackend.service.admin.IMailService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 邮件服务实现类。
 *
 * 该类实现了 {@link IMailService} 接口，提供邮件相关的业务逻辑实现。
 * 支持收件箱、发件箱查询，邮件发送（支持多个收件人），以及软删除机制。
 *
 * @author agrimall
 * @see com.agrismart.agrimallbackend.service.admin.IMailService
 * @since 1.0
 */
@Service
public class MailServiceImpl implements IMailService {

    /**
     * 邮件数据访问对象。
     */
    @Autowired
    private MailMapper mailMapper;

    /**
     * 附件数据访问对象。
     * 用于删除邮件附件。
     */
    @Autowired
    private AttachmentMapper attachmentMapper;

    @Override
    public ResponseVo<PageInfo<Mail>> getReceiveMailsByPage(Integer pageNum, Integer pageSize, Integer id) {
        PageHelper.startPage(pageNum, pageSize);
        List<Mail> receiveMails = mailMapper.selectByReceiverIdAndDeleteState(id, MailDeleteStateEnum.RECEIVER_DELETE.getCode());
        PageInfo<Mail> pageInfo = new PageInfo<>(receiveMails);
        pageInfo.setList(receiveMails);
        return ResponseVo.success(pageInfo);
    }

    @Override
    public ResponseVo<PageInfo<Mail>> getReceiveMailsByPageAndTitle(Integer pageNum, Integer pageSize, Integer id, String title) {
        PageHelper.startPage(pageNum, pageSize);
        List<Mail> selectBySearchTitle = mailMapper.selectBySearchTitleAndReceiverIdAndDeleteState(title, id, MailDeleteStateEnum.RECEIVER_DELETE.getCode());
        PageInfo<Mail> pageInfo = new PageInfo<>(selectBySearchTitle);
        pageInfo.setList(selectBySearchTitle);
        return ResponseVo.success(pageInfo);
    }

    @Override
    public ResponseVo<PageInfo<Mail>> getSendMailsByPage(Integer pageNum, Integer pageSize, Integer id) {
        PageHelper.startPage(pageNum, pageSize);
        List<Mail> sendMails = mailMapper.selectBySenderIdAndDeleteState(id, MailDeleteStateEnum.SENDER_DELETE.getCode());
        PageInfo<Mail> pageInfo = new PageInfo<>(sendMails);
        pageInfo.setList(sendMails);
        return ResponseVo.success(pageInfo);
    }

    @Override
    public ResponseVo<PageInfo<Mail>> getSendMailsByPageAndTitle(Integer pageNum, Integer pageSize, Integer id, String title) {
        PageHelper.startPage(pageNum, pageSize);
        List<Mail> selectBySearchTitle = mailMapper.selectBySearchTitleAndSenderIdAndDeleteState(title, id, MailDeleteStateEnum.SENDER_DELETE.getCode());
        PageInfo<Mail> pageInfo = new PageInfo<>(selectBySearchTitle);
        pageInfo.setList(selectBySearchTitle);
        return ResponseVo.success(pageInfo);
    }

    @Override
    public ResponseVo<Boolean> removeAttachment(Integer id) {
        if (attachmentMapper.selectByPrimaryKey(id) == null) {
            return ResponseVo.errorByMsg(CodeMsg.MAIL_ATTACHMENT_NO_EXIST);
        }
        if (attachmentMapper.deleteByPrimaryKey(id) <= 0) {
            return ResponseVo.errorByMsg(CodeMsg.MAIL_ATTACHMENT_DELETE_ERROR);
        }
        return ResponseVo.successByMsg(true, "删除成功！");
    }

    @Override
    public ResponseVo<Boolean> sendMail(Mail mail, String receivers, Integer loginedId) {
        if (mail == null || receivers == null) {
            return ResponseVo.errorByMsg(CodeMsg.DATA_ERROR);
        }
        mail.setSenderId(loginedId);
        String[] split = receivers.split(",");
        List<Mail> mailList = new ArrayList<>();
        for (String receiver : split) {
            Mail saveMail = new Mail();
            BeanUtils.copyProperties(mail, saveMail, "id", "createTime", "updateTime");
            try {
                saveMail.setReceiverId(Integer.parseInt(receiver));
            } catch (Exception e) {
                return ResponseVo.errorByMsg(CodeMsg.MAIL_RECEIVER_GET_ERROR);
            }
            CodeMsg validate = ValidateEntityUtil.validate(saveMail);
            if (!validate.getCode().equals(CodeMsg.SUCCESS.getCode())) {
                return ResponseVo.errorByMsg(validate);
            }
            mailList.add(saveMail);
        }
        mailMapper.batchInsert(mailList);
        return ResponseVo.successByMsg(true, "发送成功！");
    }

    @Override
    public ResponseVo<Boolean> delete(Integer id, Integer loginedId) {
        Mail selectByPrimaryKey = mailMapper.selectByPrimaryKey(id);
        if (selectByPrimaryKey == null) {
            return ResponseVo.errorByMsg(CodeMsg.MAIL_NO_EXIST);
        }
        if (selectByPrimaryKey.getReceiverId().equals(loginedId) && selectByPrimaryKey.getSenderId().equals(loginedId)) {
            if (mailMapper.deleteByPrimaryKey(id) <= 0) {
                return ResponseVo.errorByMsg(CodeMsg.MAIL_DELETE_ERROR);
            }
        } else if (selectByPrimaryKey.getReceiverId().equals(loginedId)) {
            if (selectByPrimaryKey.getDeleteState().intValue() == MailDeleteStateEnum.SENDER_DELETE.getCode().intValue()) {
                if (mailMapper.deleteByPrimaryKey(id) <= 0) {
                    return ResponseVo.errorByMsg(CodeMsg.MAIL_DELETE_ERROR);
                }
            } else {
                selectByPrimaryKey.setDeleteState(MailDeleteStateEnum.RECEIVER_DELETE.getCode());
                if (mailMapper.updateByPrimaryKeySelective(selectByPrimaryKey) <= 0) {
                    return ResponseVo.errorByMsg(CodeMsg.MAIL_DELETE_ERROR);
                }
            }
        } else if (selectByPrimaryKey.getSenderId().equals(loginedId)) {
            if (selectByPrimaryKey.getDeleteState().intValue() == MailDeleteStateEnum.RECEIVER_DELETE.getCode().intValue()) {
                if (mailMapper.deleteByPrimaryKey(id) <= 0) {
                    return ResponseVo.errorByMsg(CodeMsg.MAIL_DELETE_ERROR);
                }
            } else {
                selectByPrimaryKey.setDeleteState(MailDeleteStateEnum.SENDER_DELETE.getCode());
                if (mailMapper.updateByPrimaryKeySelective(selectByPrimaryKey) <= 0) {
                    return ResponseVo.errorByMsg(CodeMsg.MAIL_DELETE_ERROR);
                }
            }
        }

        return ResponseVo.successByMsg(true, "删除成功！");
    }
}

