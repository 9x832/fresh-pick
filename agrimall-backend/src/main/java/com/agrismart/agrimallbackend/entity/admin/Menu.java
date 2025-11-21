package com.agrismart.agrimallbackend.entity.admin;

import com.agrismart.agrimallbackend.annotation.ValidateEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 菜单实体类。
 *
 * 该实体类对应数据库中的菜单表，用于存储后台管理系统的菜单信息。
 * 菜单采用树形结构，通过 parentId 字段实现多级菜单（最多三级）。
 * 菜单通过权限（Authority）关联到角色（Role），实现基于菜单的权限控制。
 *
 * 使用场景：
 *
 * - 菜单管理（CRUD 操作）
 * - 菜单权限分配
 * - 动态菜单生成（根据角色权限）
 *
 * 相关枚举：
 *
 * - {@link com.agrismart.agrimallbackend.common.enums.MenuStateEnum}：菜单状态枚举
 *
 * @author agrimall
 * @see com.agrismart.agrimallbackend.entity.admin.Authority
 * @see com.agrismart.agrimallbackend.entity.admin.Role
 * @see com.agrismart.agrimallbackend.common.enums.MenuStateEnum
 * @since 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Menu {
    /**
     * 菜单 ID。
     * 主键，自增。
     */
    private Integer id;

    /**
     * 上级菜单 ID。
     * 必填字段，默认为 0（表示一级菜单）。用于构建菜单树形结构。
     */
    @ValidateEntity(required = true, errorRequiredMsg = "上级菜单不能为空！")
    private Integer parentId;

    /**
     * 菜单名称。
     * 必填字段，长度范围：1-32 字符。
     */
    @ValidateEntity(required = true, requiredMaxLength = true, requiredMinLength = true, minLength = 1, maxLength = 32, errorRequiredMsg = "菜单名称不能为空！", errorMinLengthMsg = "菜单名称长度至少为1!", errorMaxLengthMsg = "菜单名称长度不能大于32!")
    private String name;

    /**
     * 菜单路径（URL）。
     * 可选字段，最大长度 256 字符。用于前端路由跳转。
     */
    @ValidateEntity(requiredMaxLength = true, maxLength = 256, errorMaxLengthMsg = "菜单路径长度不能大于256！")
    private String url;

    /**
     * 菜单排序值。
     * 必填字段，取值范围：0-1024。值越大，在同级别菜单中越优先显示。默认为 0。
     */
    @ValidateEntity(requiredMaxValue = true, requiredMinValue = true, required = true, maxValue = 1024, minValue = 0, errorMaxValueMsg = "菜单排序最大值为1024！", errorMinValueMsg = "菜单排序最小值为0！", errorRequiredMsg = "菜单排序不能为空！")
    private Integer sort;

    /**
     * 菜单图标。
     * 必填字段，最大长度 64 字符。用于前端菜单图标显示。
     */
    @ValidateEntity(required = true, requiredMaxLength = true, maxLength = 64, errorRequiredMsg = "菜单图标不能为空！", errorMaxLengthMsg = "菜单图标长度不能大于64！")
    private String icon;

    /**
     * 菜单状态。
     * 必填字段，取值：1-开启，2-停用。对应 {@link com.agrismart.agrimallbackend.common.enums.MenuStateEnum}。
     */
    @ValidateEntity(required = true, errorRequiredMsg = "菜单状态不能为空！")
    private Integer state;

    /**
     * 菜单创建时间。
     * 系统自动设置。
     */
    private Date createTime;

    /**
     * 菜单更新时间。
     * 系统自动更新。
     */
    private Date updateTime;

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public void setUrl(String url) {
        this.url = url == null ? null : url.trim();
    }

    public void setIcon(String icon) {
        this.icon = icon == null ? null : icon.trim();
    }
}
