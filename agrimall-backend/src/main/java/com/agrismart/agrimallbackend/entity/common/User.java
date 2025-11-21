package com.agrismart.agrimallbackend.entity.common;

import com.agrismart.agrimallbackend.annotation.ValidateEntity;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 前台用户实体类。
 *
 * 该实体类对应数据库中的用户表，用于存储前台用户的账号信息。
 * 用户通过 JWT Token 进行身份认证，可以购买商品、下单、评论等。
 *
 * 使用场景：
 *
 * - 用户注册和登录
 * - 用户信息管理
 * - 订单、购物车、收藏等业务关联
 *
 * @author agrimall
 * @since 1.0
 */
@Data
@NoArgsConstructor
public class User {
    /**
     * 用户 ID。
     * 主键，自增。
     */
    private Long id;

    /**
     * 用户名。
     * 必填字段，长度范围：1-8 字符。用于登录和显示。
     */
    @ValidateEntity(required = true, requiredMaxLength = true, requiredMinLength = true, maxLength = 8, minLength = 1, errorRequiredMsg = "用户名称不能为空！", errorMaxLengthMsg = "用户名称长度不能大于8！", errorMinLengthMsg = "用户名称长度不能小于1！")
    private String username;

    /**
     * 用户密码。
     * 必填字段，长度范围：6-16 字符。存储时通常需要加密处理。
     */
    @ValidateEntity(required = true, requiredMaxLength = true, requiredMinLength = true, maxLength = 16, minLength = 6, errorRequiredMsg = "密码不能为空！", errorMaxLengthMsg = "密码长度不能大于16！", errorMinLengthMsg = "密码长度不能小于6！")
    private String password;

    /**
     * 用户电子邮箱。
     * 必填字段，长度范围：1-64 字符。用于接收系统通知邮件。
     */
    @ValidateEntity(required = true, requiredMaxLength = true, requiredMinLength = true, maxLength = 64, minLength = 1, errorRequiredMsg = "电子邮箱不能为空！", errorMaxLengthMsg = "电子邮箱长度不能大于64！", errorMinLengthMsg = "电子邮箱长度不能小于1！")
    private String email;

    /**
     * 用户手机号码。
     * 必填字段，固定长度 11 位。用于联系和验证。
     */
    @ValidateEntity(required = true, requiredMaxLength = true, requiredMinLength = true, maxLength = 11, minLength = 11, errorRequiredMsg = "手机号码不能为空！", errorMaxLengthMsg = "请输入11位手机号码！", errorMinLengthMsg = "请输入11位手机号码！")
    private String phone;

    /**
     * 用户头像路径。
     * 可选字段，最大长度 256 字符。
     */
    @ValidateEntity(requiredMaxLength = true, maxLength = 256, errorMaxLengthMsg = "头像路径长度不能大于256！")
    private String headPic;

    /**
     * 用户信息创建时间。
     * 系统自动设置。
     */
    private Date createTime;

    /**
     * 用户信息更新时间。
     * 系统自动更新。
     */
    private Date updateTime;


    public void setUsername(String username) {
        this.username = username == null ? null : username.trim();
    }

    public void setPassword(String password) {
        this.password = password == null ? null : password.trim();
    }


    public void setPhone(String phone) {
        this.phone = phone == null ? null : phone.trim();
    }


}

