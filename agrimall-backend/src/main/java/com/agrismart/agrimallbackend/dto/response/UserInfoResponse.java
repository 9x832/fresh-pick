package com.agrismart.agrimallbackend.dto.response;

import java.io.Serializable;

/**
 * 用户信息响应数据传输对象。
 *
 * 该 DTO 用于封装返回给前端的用户信息，仅包含必要的公开字段，不包含敏感信息（如密码）。
 *
 * 使用场景：
 *
 * - 在用户信息查询接口中返回用户基本信息
 * - 在登录成功后返回用户信息
 *
 * @author agrimall
 * @since 1.0
 */
public class UserInfoResponse implements Serializable {

    /**
     * 用户 ID。
     */
    private Long id;

    /**
     * 用户名。
     */
    private String username;

    /**
     * 昵称。
     */
    private String nickname;

    /**
     * 头像 URL。
     */
    private String avatarUrl;

    /**
     * 邮箱地址。
     */
    private String email;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

