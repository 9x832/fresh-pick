package com.agrismart.agrimallbackend.entity.admin;

import com.agrismart.agrimallbackend.annotation.ValidateEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 角色实体类。
 *
 * 该实体类对应数据库中的角色表，用于存储后台管理系统的角色信息。
 * 角色用于权限管理，通过权限（Authority）关联菜单（Menu），实现基于角色的访问控制（RBAC）。
 *
 * 使用场景：
 *
 * - 角色管理（CRUD 操作）
 * - 权限分配（为角色分配菜单权限）
 * - 管理员角色关联
 *
 * @author agrimall
 * @see com.agrismart.agrimallbackend.entity.admin.Authority
 * @see com.agrismart.agrimallbackend.entity.admin.Menu
 * @see com.agrismart.agrimallbackend.entity.admin.Admin
 * @since 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Role {
    /**
     * 角色 ID。
     * 主键，自增。
     */
    private Integer id;

    /**
     * 角色名称。
     * 必填字段，长度范围：1-32 字符。
     */
    @ValidateEntity(required = true, requiredMaxLength = true, requiredMinLength = true, maxLength = 32, minLength = 1, errorRequiredMsg = "角色名称不能为空！", errorMaxLengthMsg = "角色名称长度不能大于32！", errorMinLengthMsg = "角色名称长度不能小于1!")
    private String name;

    /**
     * 角色描述。
     * 可选字段，最大长度 128 字符。
     */
    @ValidateEntity(requiredMaxLength = true, maxLength = 128, errorMaxLengthMsg = "角色描述长度不能大于128！")
    private String description;

    /**
     * 角色创建时间。
     * 系统自动设置。
     */
    private Date createTime;

    /**
     * 角色更新时间。
     * 系统自动更新。
     */
    private Date updateTime;

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }
}
