package com.agrismart.agrimallbackend.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * 前台用户注册请求数据传输对象。
 *
 * 该 DTO 用于接收前台用户注册请求的参数，包含用户名、密码、确认密码、邮箱和验证码。
 * 使用 Jakarta Bean Validation 进行参数校验。
 *
 * 使用场景：
 *
 * - 在用户注册接口中接收注册请求参数
 *
 * @author agrimall
 * @since 1.0
 */
public class RegisterRequest {

    /**
     * 用户名。
     * 必填字段，不能为空。
     */
    @NotBlank(message = "用户名不能为空")
    private String username;

    /**
     * 密码。
     * 必填字段，不能为空。
     */
    @NotBlank(message = "密码不能为空")
    private String password;

    /**
     * 确认密码。
     * 必填字段，不能为空。需要与密码字段一致。
     */
    @NotBlank(message = "确认密码不能为空")
    private String confirmPassword;

    /**
     * 邮箱地址。
     * 必填字段，需要符合邮箱格式。用于接收系统通知邮件。
     */
    @Email(message = "邮箱格式不正确")
    private String email;

    /**
     * 验证码。
     * 可选字段，用于防止恶意注册。
     */
    private String captcha;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCaptcha() {
        return captcha;
    }

    public void setCaptcha(String captcha) {
        this.captcha = captcha;
    }
}

