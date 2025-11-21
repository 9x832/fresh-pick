package com.agrismart.agrimallbackend.entity.admin;

import com.agrismart.agrimallbackend.annotation.ValidateEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 管理员实体类。
 *
 * 该实体类对应数据库中的管理员表，用于存储后台管理系统的管理员账号信息。
 * 管理员通过角色（Role）来分配权限，通过状态字段控制账号的启用和冻结。
 *
 * 使用场景：
 *
 * - 管理员登录和认证
 * - 管理员账号管理（CRUD 操作）
 * - 权限控制（通过角色关联）
 *
 * 相关枚举：
 *
 * - {@link com.agrismart.agrimallbackend.common.enums.AdminStateEnum}：管理员状态枚举
 *
 * 注意：该类实现了 {@link Serializable} 接口，以支持 Spring Session Redis 的序列化存储。
 *
 * @author agrimall
 * @see com.agrismart.agrimallbackend.entity.admin.Role
 * @see com.agrismart.agrimallbackend.common.enums.AdminStateEnum
 * @since 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Admin implements Serializable {

    /**
     * 序列化版本号。
     * 用于确保序列化和反序列化时的版本兼容性。
     */
    private static final long serialVersionUID = 1L;

    /**
     * 管理员 ID。
     * 主键，自增。
     */
    private Integer id;

    /**
     * 管理员对应的角色 ID。
     * 必填字段，关联到 {@link com.agrismart.agrimallbackend.entity.admin.Role} 表。
     */
    @ValidateEntity(required = true, errorRequiredMsg = "管理员对应角色不能为空！")
    private Integer roleId;

    /**
     * 管理员头像路径。
     * 可选字段，最大长度 256 字符。
     */
    @ValidateEntity(requiredMaxLength = true, maxLength = 256, errorMaxLengthMsg = "管理员头像长度不能大于256！")
    private String headPic;

    /**
     * 管理员姓名。
     * 必填字段，长度范围：1-16 字符。
     */
    @ValidateEntity(required = true, requiredMaxLength = true, requiredMinLength = true, maxLength = 16, minLength = 1, errorRequiredMsg = "管理员名称不能为空！", errorMaxLengthMsg = "管理员名称长度不能大于16！", errorMinLengthMsg = "管理员名称长度不能小于1！")
    private String name;

    /**
     * 管理员密码。
     * 必填字段，长度范围：5-16 字符。默认密码：123456。
     * 存储时通常需要加密处理。
     */
    @ValidateEntity(required = true, requiredMaxLength = true, requiredMinLength = true, maxLength = 16, minLength = 5, errorRequiredMsg = "管理员密码不能为空！", errorMaxLengthMsg = "管理员密码长度不能大于16！", errorMinLengthMsg = "管理员密码长度不能小于5！")
    private String password;

    /**
     * 管理员性别。
     * 可选字段，取值：1-男，2-女，3-未知。默认为 3（未知）。
     */
    private Integer sex;

    /**
     * 管理员地址。
     * 可选字段，最大长度 128 字符。
     */
    @ValidateEntity(requiredMaxLength = true, maxLength = 128, errorMaxLengthMsg = "管理员地址长度不能大于128！")
    private String address;

    /**
     * 管理员电话。
     * 可选字段。
     */
    private Long mobile;

    /**
     * 管理员状态。
     * 必填字段，取值：1-启用，2-冻结。对应 {@link com.agrismart.agrimallbackend.common.enums.AdminStateEnum}。
     */
    @ValidateEntity(required = true, errorRequiredMsg = "管理员状态不能为空！")
    private Integer state;

    /**
     * 管理员创建时间。
     * 系统自动设置。
     */
    private Date createTime;

    /**
     * 管理员更新时间。
     * 系统自动更新。
     */
    private Date updateTime;

    public void setHeadPic(String headPic) {
        this.headPic = headPic == null ? null : headPic.trim();
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public void setPassword(String password) {
        this.password = password == null ? null : password.trim();
    }

    public void setAddress(String address) {
        this.address = address == null ? null : address.trim();
    }
}
