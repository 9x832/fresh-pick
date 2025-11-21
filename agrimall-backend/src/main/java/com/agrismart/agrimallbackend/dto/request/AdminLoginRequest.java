package com.agrismart.agrimallbackend.dto.request;

import jakarta.validation.constraints.NotBlank;

/**
 * 管理员登录请求数据传输对象。
 *
 * 该 DTO 用于接收后台管理员登录请求的参数，包含管理员名称、密码和验证码。
 * 使用 Jakarta Bean Validation 进行参数校验。
 *
 * 使用场景：
 *
 * - 在 {@link com.agrismart.agrimallbackend.controller.admin.AdminSystemController#login} 中接收登录请求
 *
 * @author agrimall
 * @see com.agrismart.agrimallbackend.controller.admin.AdminSystemController
 * @since 1.0
 */
public class AdminLoginRequest {

    /**
     * 管理员名称。
     * 必填字段，不能为空。
     */
    @NotBlank(message = "用户名不能为空")
    private String name;

    /**
     * 管理员密码。
     * 必填字段，不能为空。
     */
    @NotBlank(message = "密码不能为空")
    private String password;

    /**
     * 验证码。
     * 必填字段，不能为空。用于防止暴力破解。
     */
    @NotBlank(message = "验证码不能为空")
    private String captcha;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

