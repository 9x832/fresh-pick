package com.agrismart.agrimallbackend.controller.common;

import com.agrismart.agrimallbackend.common.util.CpachaUtil;
import javax.imageio.ImageIO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * 验证码生成控制器。
 *
 * 该控制器提供图形验证码生成功能，用于登录、注册等场景的安全验证。
 * 生成的验证码会存储到 Session 中，供后续验证使用。
 *
 * 接口路径：{@code /api/common/captcha}
 *
 * 特性：
 *
 * - 支持自定义验证码长度、字体大小、图片尺寸
 * - 生成带旋转效果的验证码图片
 * - 验证码值存储在 Session 中，通过 method 参数指定存储键名
 *
 * 使用场景：
 *
 * - 管理员登录验证码（method: "admin_login_captcha"）
 * - 用户注册验证码（method: "user_register_captcha"）
 * - 其他需要验证码的场景
 *
 * @author agrimall
 * @see com.agrismart.agrimallbackend.common.util.CpachaUtil
 * @since 1.0
 */
@RestController
@RequestMapping("/api/common/captcha")
public class CaptchaController {

    /**
     * 日志记录器。
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(CaptchaController.class);

    /**
     * 生成验证码图片。
     *
     * 根据参数生成指定样式的验证码图片，并将验证码值存储到 Session 中。
     * 生成的图片以 GIF 格式返回给客户端。
     *
     * @param vcodeLength 验证码长度，默认为 4
     * @param fontSize    字体大小，默认为 21
     * @param width       图片宽度（像素），默认为 98
     * @param height      图片高度（像素），默认为 33
     * @param method      Session 存储键名，用于后续验证时从 Session 中获取验证码值（必填）
     * @param request     HTTP 请求对象，用于存储验证码到 Session
     * @param response    HTTP 响应对象，用于输出验证码图片
     */
    @GetMapping
    public void generate(@RequestParam(name = "vl", defaultValue = "4") Integer vcodeLength,
                         @RequestParam(name = "fs", defaultValue = "21") Integer fontSize,
                         @RequestParam(name = "w", defaultValue = "98") Integer width,
                         @RequestParam(name = "h", defaultValue = "33") Integer height,
                         @RequestParam(name = "method") String method,
                         HttpServletRequest request,
                         HttpServletResponse response) {
        // 创建验证码工具实例
        CpachaUtil cpachaUtil = new CpachaUtil(vcodeLength, fontSize, width, height);
        
        // 生成验证码字符串
        String generatorVCode = cpachaUtil.generatorVCode();
        
        // 将验证码存储到 Session 中，使用 method 参数作为键名
        request.getSession().setAttribute(method, generatorVCode);
        LOGGER.info("验证码生成成功，method={}, value={}", method, generatorVCode);
        
        // 设置响应类型为 GIF 图片
        response.setContentType("image/gif");
        try {
            // 生成带旋转效果的验证码图片并输出
            ImageIO.write(cpachaUtil.generatorRotateVCodeImage(generatorVCode, true),
                    "gif", response.getOutputStream());
        } catch (IOException e) {
            LOGGER.error("验证码生成失败", e);
        }
    }
}

