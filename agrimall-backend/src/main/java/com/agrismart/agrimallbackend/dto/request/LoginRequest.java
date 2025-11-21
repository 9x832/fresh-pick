package com.agrismart.agrimallbackend.dto.request;

import jakarta.validation.constraints.NotBlank;

/**
 * 前台用户登录请求数据传输对象。
 *
 * 该 DTO 用于接收前台用户登录请求的参数，包含用户名、密码和可选的验证码。
 * 使用 Jakarta Bean Validation 进行参数校验。
 *
 * 使用场景：
 *
 * - 在用户登录接口中接收登录请求参数
 *
 * @author agrimall
 * @since 1.0
 */
public class LoginRequest {

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
     * 验证码。
     * 可选字段，用于防止暴力破解。
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

    public String getCaptcha() {
        return captcha;
    }

    public void setCaptcha(String captcha) {
        this.captcha = captcha;
    }
}

