package com.agrismart.agrimallbackend.entity.admin;

import com.agrismart.agrimallbackend.annotation.ValidateEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 权限实体类。
 *
 * 该实体类对应数据库中的权限表，用于存储角色和菜单的关联关系。
 * 权限表是角色（Role）和菜单（Menu）之间的中间表，实现多对多关系。
 * 通过权限表，可以为不同角色分配不同的菜单访问权限。
 *
 * 使用场景：
 *
 * - 角色权限分配
 * - 权限查询（根据角色查询可访问的菜单）
 * - 权限验证（判断管理员是否有权限访问某个菜单）
 *
 * @author agrimall
 * @see com.agrismart.agrimallbackend.entity.admin.Role
 * @see com.agrismart.agrimallbackend.entity.admin.Menu
 * @since 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Authority {
    /**
     * 权限 ID。
     * 主键，自增。
     */
    private Integer id;

    /**
     * 权限对应的菜单 ID。
     * 必填字段，关联到 {@link com.agrismart.agrimallbackend.entity.admin.Menu} 表。
     */
    @ValidateEntity(required = true, errorRequiredMsg = "权限对应菜单ID不能为空！")
    private Integer menuId;

    /**
     * 权限对应的角色 ID。
     * 必填字段，关联到 {@link com.agrismart.agrimallbackend.entity.admin.Role} 表。
     */
    @ValidateEntity(required = true, errorRequiredMsg = "权限对应角色ID不能为空！")
    private Integer roleId;

    /**
     * 权限创建时间。
     * 系统自动设置。
     */
    private Date createTime;

    /**
     * 权限更新时间。
     * 系统自动更新。
     */
    private Date updateTime;
}
