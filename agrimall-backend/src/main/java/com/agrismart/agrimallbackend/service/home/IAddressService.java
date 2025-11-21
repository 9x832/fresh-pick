package com.agrismart.agrimallbackend.service.home;

import com.agrismart.agrimallbackend.dto.response.ResponseVo;
import com.agrismart.agrimallbackend.entity.home.Address;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 收货地址服务接口。
 *
 * 该接口定义了用户收货地址相关的业务操作方法，包括：
 *
 * - 地址添加和更新
 * - 地址列表查询
 * - 设置首选地址
 * - 地址删除
 *
 * 注意：
 *
 * - 每个用户最多可以添加 3 个收货地址
 * - 每个用户只能有一个首选地址，设置新的首选地址时会自动取消其他地址的首选状态
 * - 首选地址用于订单默认配送地址
 *
 * @author agrimall
 * @see com.agrismart.agrimallbackend.service.home.impl.AddressServiceImpl
 * @see com.agrismart.agrimallbackend.entity.home.Address
 * @see com.agrismart.agrimallbackend.common.enums.AddressFirstSelectedEnum
 * @since 1.0
 */
public interface IAddressService {

    /**
     * 添加收货地址。
     * 每个用户最多可以添加 3 个收货地址。
     *
     * @param address 地址对象（包含收货人姓名、地址、手机号等）
     * @param request  HTTP 请求对象（用于获取当前登录用户 ID）
     * @return 操作结果
     */
    ResponseVo<Boolean> add(Address address, HttpServletRequest request);

    /**
     * 更新收货地址信息。
     * 只能更新当前登录用户的地址，会验证地址所有权。
     *
     * @param address 地址对象（包含 ID 和要更新的字段）
     * @param request HTTP 请求对象（用于获取当前登录用户 ID）
     * @return 操作结果
     */
    ResponseVo<Boolean> update(Address address, HttpServletRequest request);

    /**
     * 根据用户 ID 查询地址列表。
     *
     * @param userId 用户 ID
     * @return 地址列表
     */
    List<Address> findAddressByUserId(Long userId);

    /**
     * 设置首选地址。
     * 设置新的首选地址时，会自动取消该用户其他地址的首选状态。
     *
     * @param id      地址 ID
     * @param request HTTP 请求对象（用于获取当前登录用户 ID）
     * @return 操作结果
     */
    ResponseVo<Boolean> setFirstSelected(Long id, HttpServletRequest request);

    /**
     * 删除收货地址。
     *
     * @param id 地址 ID
     * @return 操作结果
     */
    ResponseVo<Boolean> delete(Long id);

    /**
     * 根据用户 ID 和首选状态查询地址。
     * 用于查询用户的首选地址。
     *
     * @param userId       用户 ID
     * @param firstSelected 首选状态（0-不是，1-是）
     * @return 地址对象，如果不存在则返回 null
     */
    Address selectByUserIdAndFirstSelected(Long userId, Integer firstSelected);
}

