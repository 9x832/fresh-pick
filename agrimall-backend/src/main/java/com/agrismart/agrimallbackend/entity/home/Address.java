package com.agrismart.agrimallbackend.entity.home;

import com.agrismart.agrimallbackend.annotation.ValidateEntity;
import com.agrismart.agrimallbackend.common.enums.AddressFirstSelectedEnum;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 收货地址实体类。
 *
 * 该实体类对应数据库中的地址表，用于存储用户的收货地址信息。
 * 用户可以有多个收货地址，但只能有一个首选地址。首选地址用于订单默认配送地址。
 *
 * 使用场景：
 *
 * - 用户收货地址管理（添加、编辑、删除）
 * - 设置首选地址
 * - 订单配送地址选择
 *
 * 相关枚举：
 *
 * - {@link com.agrismart.agrimallbackend.common.enums.AddressFirstSelectedEnum}：地址首选状态枚举
 *
 * @author agrimall
 * @see com.agrismart.agrimallbackend.entity.common.User
 * @see com.agrismart.agrimallbackend.entity.common.Order
 * @see com.agrismart.agrimallbackend.common.enums.AddressFirstSelectedEnum
 * @since 1.0
 */
@Data
@NoArgsConstructor
public class Address {
    /**
     * 地址 ID。
     * 主键，自增。
     */
    private Long id;

    /**
     * 收货人姓名。
     * 必填字段，长度范围：1-8 字符。
     */
    @ValidateEntity(required = true, requiredMaxLength = true, requiredMinLength = true, maxLength = 8, minLength = 1, errorRequiredMsg = "收货人姓名不能为空！", errorMaxLengthMsg = "收货人姓名长度不能大于8！", errorMinLengthMsg = "收货人姓名长度不能小于1！")
    private String receiverName;

    /**
     * 收货人地址。
     * 必填字段，长度范围：1-64 字符。包含省市区和详细地址。
     */
    @ValidateEntity(required = true, requiredMaxLength = true, requiredMinLength = true, maxLength = 64, minLength = 1, errorRequiredMsg = "收货人地址不能为空！", errorMaxLengthMsg = "收货人地址长度不能大于64！", errorMinLengthMsg = "收货人地址长度不能小于1！")
    private String receiverAddress;

    /**
     * 收货人手机号码。
     * 必填字段，固定长度 11 位。
     */
    @ValidateEntity(required = true, requiredMaxLength = true, requiredMinLength = true, maxLength = 11, minLength = 11, errorRequiredMsg = "收货人手机号码不能为空！", errorMaxLengthMsg = "请输入11位收货人手机号码！", errorMinLengthMsg = "请输入11位收货人手机号码！")
    private String receiverPhone;

    /**
     * 地址对应的用户 ID。
     * 必填字段，关联到 {@link com.agrismart.agrimallbackend.entity.common.User} 表。
     */
    @ValidateEntity(required = true, errorRequiredMsg = "该地址对应的用户不能为空！")
    private Long userId;

    /**
     * 该地址是否为首选地址。
     * 取值：0-不是，1-是。默认值为 0（不是）。
     * 对应 {@link com.agrismart.agrimallbackend.common.enums.AddressFirstSelectedEnum}。
     * 每个用户只能有一个首选地址，设置新的首选地址时，会自动取消其他地址的首选状态。
     */
    private Integer firstSelected = AddressFirstSelectedEnum.NO.getCode();

    /**
     * 地址创建时间。
     * 系统自动设置。
     */
    private Date createTime;

    /**
     * 地址更新时间。
     * 系统自动更新。
     */
    private Date updateTime;

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName == null ? null : receiverName.trim();
    }

    public void setReceiverAddress(String receiverAddress) {
        this.receiverAddress = receiverAddress == null ? null : receiverAddress.trim();
    }

    public void setReceiverPhone(String receiverPhone) {
        this.receiverPhone = receiverPhone == null ? null : receiverPhone.trim();
    }
}

