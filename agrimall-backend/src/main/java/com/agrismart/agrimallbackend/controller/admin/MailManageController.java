package com.agrismart.agrimallbackend.controller.admin;

import com.agrismart.agrimallbackend.common.bean.CodeMsg;
import com.agrismart.agrimallbackend.common.constant.SessionConstant;
import com.agrismart.agrimallbackend.dto.request.MailSendRequest;
import com.agrismart.agrimallbackend.dto.response.ResponseVo;
import com.agrismart.agrimallbackend.entity.admin.Admin;
import com.agrismart.agrimallbackend.entity.admin.Mail;
import com.agrismart.agrimallbackend.mapper.admin.AdminMapper;
import com.agrismart.agrimallbackend.mapper.admin.AttachmentMapper;
import com.agrismart.agrimallbackend.mapper.admin.MailMapper;
import com.agrismart.agrimallbackend.service.admin.IMailService;
import com.github.pagehelper.PageInfo;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 邮件管理控制器。
 *
 * 该控制器提供后台管理系统对管理员之间邮件的管理接口，包括：
 *
 * - 收件箱查询（支持分页和按标题搜索）
 * - 发件箱查询（支持分页和按标题搜索）
 * - 邮件详情查询
 * - 发送邮件
 * - 删除附件
 * - 删除邮件（软删除）
 *
 * 接口路径：{@code /api/admin/mails}
 *
 * 权限要求：
 *
 * - 需要管理员登录（通过 {@link com.agrismart.agrimallbackend.interceptor.AdminInterceptor} 验证）
 * - 需要相应的菜单权限
 *
 * @author agrimall
 * @see com.agrismart.agrimallbackend.service.admin.IMailService
 * @see com.agrismart.agrimallbackend.entity.admin.Mail
 * @since 1.0
 */
@RestController
@RequestMapping("/api/admin/mails")
public class MailManageController {

    /**
     * 邮件服务接口。
     */
    private final IMailService mailService;

    /**
     * 管理员数据访问接口。
     */
    private final AdminMapper adminMapper;

    /**
     * 附件数据访问接口。
     */
    private final AttachmentMapper attachmentMapper;

    /**
     * 邮件数据访问接口。
     */
    private final MailMapper mailMapper;

    /**
     * 构造函数，注入依赖。
     *
     * @param mailService        邮件服务接口
     * @param adminMapper         管理员数据访问接口
     * @param attachmentMapper    附件数据访问接口
     * @param mailMapper          邮件数据访问接口
     */
    @Autowired
    public MailManageController(IMailService mailService,
                                AdminMapper adminMapper,
                                AttachmentMapper attachmentMapper,
                                MailMapper mailMapper) {
        this.mailService = mailService;
        this.adminMapper = adminMapper;
        this.attachmentMapper = attachmentMapper;
        this.mailMapper = mailMapper;
    }

    /**
     * 分页查询收件箱。
     *
     * 查询当前登录管理员的收件箱，支持按标题搜索。
     * 返回结果包含分页信息和所有管理员列表（用于发送邮件时选择收件人）。
     *
     * @param pageNum  页码，从 1 开始，默认为 1
     * @param pageSize 每页大小，默认为 5
     * @param title    邮件标题（可选），用于模糊搜索
     * @param request  HTTP 请求对象，用于获取当前登录的管理员信息
     * @return 包含分页信息和管理员列表的响应对象
     *
     * - pageInfo：分页信息，包含邮件列表
     * - admins：所有管理员列表，用于发送邮件时选择收件人
     *
     */
    @GetMapping("/receive")
    public ResponseVo<Map<String, Object>> receive(@RequestParam(defaultValue = "1") Integer pageNum,
                                                   @RequestParam(defaultValue = "5") Integer pageSize,
                                                   @RequestParam(required = false) String title,
                                                   HttpServletRequest request) {
        Admin logined = (Admin) request.getSession().getAttribute(SessionConstant.SESSION_ADMIN_LOGIN_KEY);
        if (logined == null) {
            return ResponseVo.errorByMsg(CodeMsg.USER_SESSION_EXPIRED);
        }
        ResponseVo<PageInfo<Mail>> pageInfo = (title == null || title.trim().isEmpty())
                ? mailService.getReceiveMailsByPage(pageNum, pageSize, logined.getId())
                : mailService.getReceiveMailsByPageAndTitle(pageNum, pageSize, logined.getId(), title);
        if (!CodeMsg.SUCCESS.getCode().equals(pageInfo.getCode())) {
            return ResponseVo.errorByMsg(CodeMsg.SYSTEM_ERROR);
        }
        Map<String, Object> payload = new HashMap<>(2);
        payload.put("pageInfo", pageInfo.getData());
        payload.put("admins", adminMapper.selectAll());
        return ResponseVo.success(payload);
    }

    /**
     * 分页查询发件箱。
     *
     * 查询当前登录管理员的发件箱，支持按标题搜索。
     * 返回结果包含分页信息和所有管理员列表（用于发送邮件时选择收件人）。
     *
     * @param pageNum  页码，从 1 开始，默认为 1
     * @param pageSize 每页大小，默认为 5
     * @param title    邮件标题（可选），用于模糊搜索
     * @param request  HTTP 请求对象，用于获取当前登录的管理员信息
     * @return 包含分页信息和管理员列表的响应对象
     *
     * - pageInfo：分页信息，包含邮件列表
     * - admins：所有管理员列表，用于发送邮件时选择收件人
     *
     */
    @GetMapping("/sent")
    public ResponseVo<Map<String, Object>> sent(@RequestParam(defaultValue = "1") Integer pageNum,
                                                @RequestParam(defaultValue = "5") Integer pageSize,
                                                @RequestParam(required = false) String title,
                                                HttpServletRequest request) {
        Admin logined = (Admin) request.getSession().getAttribute(SessionConstant.SESSION_ADMIN_LOGIN_KEY);
        if (logined == null) {
            return ResponseVo.errorByMsg(CodeMsg.USER_SESSION_EXPIRED);
        }
        ResponseVo<PageInfo<Mail>> pageInfo = (title == null || title.trim().isEmpty())
                ? mailService.getSendMailsByPage(pageNum, pageSize, logined.getId())
                : mailService.getSendMailsByPageAndTitle(pageNum, pageSize, logined.getId(), title);
        if (!CodeMsg.SUCCESS.getCode().equals(pageInfo.getCode())) {
            return ResponseVo.errorByMsg(CodeMsg.SYSTEM_ERROR);
        }
        Map<String, Object> payload = new HashMap<>(2);
        payload.put("pageInfo", pageInfo.getData());
        payload.put("admins", adminMapper.selectAll());
        return ResponseVo.success(payload);
    }

    /**
     * 查询邮件详情。
     *
     * 根据邮件 ID 查询邮件的详细信息。只有邮件的发送者或接收者才能查看邮件详情。
     * 返回结果包含邮件信息、所有管理员列表和所有附件列表。
     *
     * @param id      邮件 ID（路径变量）
     * @param request HTTP 请求对象，用于获取当前登录的管理员信息
     * @return 包含邮件信息、管理员列表和附件列表的响应对象
     *
     * - mail：邮件详细信息
     * - admins：所有管理员列表
     * - attachments：所有附件列表
     *
     */
    @GetMapping("/{id}")
    public ResponseVo<Map<String, Object>> detail(@PathVariable("id") Integer id, HttpServletRequest request) {
        Admin logined = (Admin) request.getSession().getAttribute(SessionConstant.SESSION_ADMIN_LOGIN_KEY);
        if (logined == null) {
            return ResponseVo.errorByMsg(CodeMsg.USER_SESSION_EXPIRED);
        }
        Mail mail = mailMapper.selectByPrimaryKey(id);
        // 只有邮件的发送者或接收者才能查看邮件详情
        if (mail == null || (!logined.getId().equals(mail.getSenderId()) && !logined.getId().equals(mail.getReceiverId()))) {
            return ResponseVo.errorByMsg(CodeMsg.DATA_ERROR);
        }
        Map<String, Object> payload = new HashMap<>(3);
        payload.put("mail", mail);
        payload.put("admins", adminMapper.selectAll());
        payload.put("attachments", attachmentMapper.selectAll());
        return ResponseVo.success(payload);
    }

    /**
     * 发送邮件。
     *
     * 当前登录的管理员向其他管理员发送邮件，支持多个收件人。
     * 邮件的发送者会自动设置为当前登录的管理员。
     *
     * @param requestBody 邮件发送请求对象，包含邮件信息和收件人 ID 列表
     * @param request     HTTP 请求对象，用于获取当前登录的管理员信息
     * @return 操作结果，true 表示发送成功
     */
    @PostMapping
    public ResponseVo<Boolean> send(@RequestBody MailSendRequest requestBody, HttpServletRequest request) {
        Admin logined = (Admin) request.getSession().getAttribute(SessionConstant.SESSION_ADMIN_LOGIN_KEY);
        if (logined == null) {
            return ResponseVo.errorByMsg(CodeMsg.USER_SESSION_EXPIRED);
        }
        if (requestBody == null || requestBody.getMail() == null) {
            return ResponseVo.errorByMsg(CodeMsg.DATA_ERROR);
        }
        List<Integer> receiverIds = requestBody.getReceiverIds();
        if (CollectionUtils.isEmpty(receiverIds)) {
            return ResponseVo.errorByMsg(CodeMsg.MAIL_RECEIVER_GET_ERROR);
        }
        // 将收件人 ID 列表转换为逗号分隔的字符串
        String receivers = receiverIds.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
        return mailService.sendMail(requestBody.getMail(), receivers, logined.getId());
    }

    /**
     * 删除附件。
     *
     * 根据附件 ID 删除指定的附件。
     *
     * @param id 附件 ID（路径变量）
     * @return 操作结果，true 表示删除成功
     */
    @DeleteMapping("/attachments/{id}")
    public ResponseVo<Boolean> removeAttachment(@PathVariable("id") Integer id) {
        return mailService.removeAttachment(id);
    }

    /**
     * 删除邮件（软删除）。
     *
     * 根据邮件 ID 删除指定的邮件。采用软删除机制，根据当前登录管理员是发送者还是接收者，
     * 分别标记为发送者删除或接收者删除。
     *
     * @param id      邮件 ID（路径变量）
     * @param request HTTP 请求对象，用于获取当前登录的管理员信息
     * @return 操作结果，true 表示删除成功
     */
    @DeleteMapping("/{id}")
    public ResponseVo<Boolean> deleteMail(@PathVariable("id") Integer id, HttpServletRequest request) {
        Admin logined = (Admin) request.getSession().getAttribute(SessionConstant.SESSION_ADMIN_LOGIN_KEY);
        if (logined == null) {
            return ResponseVo.errorByMsg(CodeMsg.USER_SESSION_EXPIRED);
        }
        return mailService.delete(id, logined.getId());
    }

}

