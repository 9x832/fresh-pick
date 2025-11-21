package com.agrismart.agrimallbackend.service.admin;

import com.github.pagehelper.PageInfo;
import com.agrismart.agrimallbackend.dto.response.ResponseVo;
import com.agrismart.agrimallbackend.entity.admin.Mail;

/**
 * 邮件服务接口。
 *
 * 该接口定义了后台邮件相关的业务操作方法，包括：
 *
 * - 收件箱查询（支持分页和按标题搜索）
 * - 发件箱查询（支持分页和按标题搜索）
 * - 发送邮件（支持多个收件人）
 * - 删除邮件（软删除，区分发送者和接收者）
 * - 删除附件
 *
 * 邮件删除采用软删除机制：
 *
 * - 发送者删除：标记为发送者删除，接收者仍可查看
 * - 接收者删除：标记为接收者删除，发送者仍可查看
 * - 双方都删除：真正删除邮件记录
 *
 * @author agrimall
 * @see com.agrismart.agrimallbackend.service.admin.impl.MailServiceImpl
 * @see com.agrismart.agrimallbackend.entity.admin.Mail
 * @see com.agrismart.agrimallbackend.common.enums.MailDeleteStateEnum
 * @since 1.0
 */
public interface IMailService {

    /**
     * 分页查询收件箱。
     * 查询指定管理员接收的、未被接收者删除的邮件。
     *
     * @param pageNum  页码，从 1 开始
     * @param pageSize 每页大小
     * @param id       管理员 ID（收件人 ID）
     * @return 包含分页信息的邮件列表
     */
    ResponseVo<PageInfo<Mail>> getReceiveMailsByPage(Integer pageNum, Integer pageSize, Integer id);

    /**
     * 按标题分页查询收件箱。
     *
     * @param pageNum  页码，从 1 开始
     * @param pageSize 每页大小
     * @param id       管理员 ID（收件人 ID）
     * @param title    邮件标题（支持模糊搜索）
     * @return 包含分页信息的邮件列表
     */
    ResponseVo<PageInfo<Mail>> getReceiveMailsByPageAndTitle(Integer pageNum, Integer pageSize, Integer id, String title);

    /**
     * 分页查询发件箱。
     * 查询指定管理员发送的、未被发送者删除的邮件。
     *
     * @param pageNum  页码，从 1 开始
     * @param pageSize 每页大小
     * @param id       管理员 ID（发件人 ID）
     * @return 包含分页信息的邮件列表
     */
    ResponseVo<PageInfo<Mail>> getSendMailsByPage(Integer pageNum, Integer pageSize, Integer id);

    /**
     * 按标题分页查询发件箱。
     *
     * @param pageNum  页码，从 1 开始
     * @param pageSize 每页大小
     * @param id       管理员 ID（发件人 ID）
     * @param title    邮件标题（支持模糊搜索）
     * @return 包含分页信息的邮件列表
     */
    ResponseVo<PageInfo<Mail>> getSendMailsByPageAndTitle(Integer pageNum, Integer pageSize, Integer id, String title);

    /**
     * 删除附件。
     *
     * @param id 附件 ID
     * @return 操作结果
     */
    ResponseVo<Boolean> removeAttachment(Integer id);

    /**
     * 发送邮件。
     * 支持向多个收件人发送邮件，每个收件人创建一条邮件记录。
     *
     * @param mail      邮件对象（包含标题、内容、附件等）
     * @param receivers 收件人 ID 列表（逗号分隔的字符串，如 "1,2,3"）
     * @param loginedId 当前登录的管理员 ID（发件人 ID）
     * @return 操作结果
     */
    ResponseVo<Boolean> sendMail(Mail mail, String receivers, Integer loginedId);

    /**
     * 删除邮件（软删除）。
     * 根据当前登录管理员的身份（发送者或接收者）进行相应的删除操作。
     * 如果双方都已删除，则真正删除邮件记录。
     *
     * @param id        邮件 ID
     * @param loginedId 当前登录的管理员 ID
     * @return 操作结果
     */
    ResponseVo<Boolean> delete(Integer id, Integer loginedId);
}

