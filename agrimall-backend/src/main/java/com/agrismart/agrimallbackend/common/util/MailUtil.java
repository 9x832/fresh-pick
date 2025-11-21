package com.agrismart.agrimallbackend.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

/**
 * 邮件发送工具类。
 *
 * 该类提供了系统自动发送邮件的功能，支持异步和同步两种发送方式。
 * 根据不同的业务场景，系统会自动发送不同类型的邮件通知用户。
 *
 * 支持的邮件类型（通过 code 参数指定）：
 *
 * - code: 1 - 用户注册邮件，用户注册成功后自动发送欢迎邮件
 * - code: 2 - 订单提交邮件，用户提交订单后自动发送订单确认邮件
 *
 * 使用场景：
 *
 * - 在 {@link com.agrismart.agrimallbackend.service.common.impl.UserServiceImpl} 中发送用户注册邮件
 * - 在 {@link com.agrismart.agrimallbackend.service.common.impl.OrderServiceImpl} 中发送订单提交邮件
 *
 * 使用示例：
 * <pre>
 * {@code
 * // 异步发送用户注册邮件
 * mailUtil.sendMailAsync(MailTypeEnum.USER_REGISTER.getCode(), userEmail, "");
 *
 * // 异步发送订单提交邮件
 * mailUtil.sendMailAsync(MailTypeEnum.ORDER_SUBMIT.getCode(), userEmail, orderId);
 * }
 * </pre>
 *
 * 注意：
 *
 * - 邮件发送采用异步方式，不会阻塞主业务流程
 * - 邮件发送失败时会记录错误日志，但不会抛出异常影响主业务
 * - 邮件配置信息（发件人、SMTP服务器等）在 application.yml 中配置
 *
 * @author agrimall
 * @see com.agrismart.agrimallbackend.common.enums.MailTypeEnum
 * @see com.agrismart.agrimallbackend.service.common.impl.UserServiceImpl
 * @see com.agrismart.agrimallbackend.service.common.impl.OrderServiceImpl
 * @since 1.0
 */
@Component
public class MailUtil {

    /**
     * 日志记录器。
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(MailUtil.class);

    /**
     * Spring Mail 发送器。
     * 用于实际发送邮件。
     */
    private final JavaMailSender mailSender;

    /**
     * 发件人邮箱地址。
     * 从配置文件 spring.mail.username 中读取。
     */
    private final String from;

    /**
     * 构造函数，注入邮件发送器和发件人地址。
     *
     * @param mailSender Spring Mail 发送器
     * @param from       发件人邮箱地址，从配置文件中读取
     */
    @Autowired
    public MailUtil(JavaMailSender mailSender, @Value("${spring.mail.username}") String from) {
        this.mailSender = mailSender;
        this.from = from;
    }

    /**
     * 异步发送邮件。
     *
     * 该方法使用异步方式发送邮件，不会阻塞调用线程。
     * 邮件发送在独立的线程池中执行，通过 {@code mailTaskExecutor} 线程池执行。
     *
     * 推荐使用此方法发送邮件，避免邮件发送延迟影响主业务流程。
     *
     * @param code    邮件模板编码，对应 {@link com.agrismart.agrimallbackend.common.enums.MailTypeEnum} 中的值
     *                1: 用户注册邮件，2: 订单提交邮件
     * @param to      收件人邮箱地址
     * @param context 附加内容，对于订单提交邮件，此参数为订单编号
     */
    @org.springframework.scheduling.annotation.Async("mailTaskExecutor")
    public void sendMailAsync(Integer code, String to, String context) {
        sendMail(code, to, context);
    }

    /**
     * 同步发送邮件。
     *
     * 该方法同步发送邮件，会阻塞调用线程直到邮件发送完成。
     * 主要用于异步方法的内部实现，也可在必要时直接调用。
     *
     * 邮件模板说明：
     *
     * - code = 1：用户注册邮件模板，欢迎用户注册成功
     * - code = 2：订单提交邮件模板，通知用户订单已提交，context 参数为订单编号
     *
     * 如果邮件发送失败，会记录错误日志但不会抛出异常，确保不影响主业务流程。
     *
     * @param code    邮件模板编码，对应 {@link com.agrismart.agrimallbackend.common.enums.MailTypeEnum} 中的值
     * @param to      收件人邮箱地址
     * @param context 附加内容，对于订单提交邮件，此参数为订单编号；对于用户注册邮件，此参数可为空
     */
    public void sendMail(Integer code, String to, String context) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject("鲜采直送服务中心");
        
        // 根据邮件类型选择对应的邮件模板
        switch (code) {
            case 1:
                // 用户注册邮件模板
                message.setText("""
亲爱的用户，您好！

欢迎加入鲜采直送，您已注册成功。现在可以随时挑选新鲜果蔬，我们也会定期为您推送优惠活动。

祝您购物愉快！
鲜采直送·用心为家人准备的好食材
""");
                break;
            case 2:
                // 订单提交邮件模板，context 参数为订单编号
                message.setText("亲爱的用户，您好！\n\n我们已收到您的订单（编号：" + context + "）。仓库正在为您备货，配送进度也会同步到\"我的订单\"中，敬请留意。\n\n感谢您的信赖，祝您生活愉快！\n鲜采直送·让新鲜更快到家");
                break;
            default:
                LOGGER.info("无匹配的邮件模板 code={}", code);
                return;
        }
        
        // 发送邮件，捕获异常但不抛出，避免影响主业务流程
        try {
            mailSender.send(message);
        } catch (MailException e) {
            LOGGER.error("邮件发送失败", e);
        }
    }
}

