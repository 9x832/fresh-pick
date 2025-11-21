package com.agrismart.agrimallbackend.controller.home;

import com.agrismart.agrimallbackend.dto.response.ResponseVo;
import com.agrismart.agrimallbackend.entity.home.Address;
import com.agrismart.agrimallbackend.service.home.IAddressService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 前台收货地址管理控制器。
 *
 * 该控制器提供前台用户收货地址的管理接口，包括：
 *
 * - 地址列表查询
 * - 地址添加
 * - 地址编辑
 * - 设置首选地址
 * - 地址删除
 *
 * 接口路径：{@code /api/home/address}
 *
 * 权限要求：所有接口都需要用户登录（通过 {@link com.agrismart.agrimallbackend.interceptor.JwtInterceptor} 验证）。
 *
 * @author agrimall
 * @see com.agrismart.agrimallbackend.service.home.IAddressService
 * @see com.agrismart.agrimallbackend.entity.home.Address
 * @since 1.0
 */
@RestController
@RequestMapping("/api/home/address")
public class HomeAddressController {

    /**
     * 地址服务接口。
     */
    private final IAddressService addressService;

    /**
     * 构造函数，注入依赖。
     *
     * @param addressService 地址服务接口
     */
    @Autowired
    public HomeAddressController(IAddressService addressService) {
        this.addressService = addressService;
    }

    /**
     * 查询当前用户的所有收货地址列表。
     *
     * 返回当前登录用户的所有收货地址。
     *
     * @param uid 用户 ID（从请求属性中获取，由拦截器设置）
     * @return 地址列表
     */
    @GetMapping
    public ResponseVo<List<Address>> list(@RequestAttribute("id") Long uid) {
        List<Address> addressList = addressService.findAddressByUserId(uid);
        return ResponseVo.success(addressList);
    }

    /**
     * 添加收货地址。
     *
     * 为当前登录用户添加新的收货地址。
     *
     * @param address 地址实体对象，包含地址的详细信息（收货人、电话、地址等）
     * @param request HTTP 请求对象，用于获取当前登录用户信息
     * @return 操作结果，true 表示添加成功
     */
    @PostMapping
    public ResponseVo<Boolean> add(@RequestBody Address address,
                                   HttpServletRequest request) {
        return addressService.add(address, request);
    }

    /**
     * 更新收货地址信息。
     *
     * 更新指定 ID 的收货地址信息。
     *
     * @param id      地址 ID（路径变量）
     * @param address 地址实体对象，包含要更新的信息
     * @param request HTTP 请求对象，用于获取当前登录用户信息
     * @return 操作结果，true 表示更新成功
     */
    @PutMapping("/{id}")
    public ResponseVo<Boolean> update(@PathVariable("id") Long id,
                                      @RequestBody Address address,
                                      HttpServletRequest request) {
        address.setId(id);
        return addressService.update(address, request);
    }

    /**
     * 设置首选地址。
     *
     * 将指定地址设置为当前用户的首选收货地址。
     * 设置成功后，该地址会被标记为首选，其他地址的首选标记会被清除。
     *
     * @param id      地址 ID（路径变量）
     * @param request HTTP 请求对象，用于获取当前登录用户信息
     * @return 操作结果，true 表示设置成功
     */
    @PostMapping("/{id}/first")
    public ResponseVo<Boolean> setFirstSelected(@PathVariable("id") Long id, HttpServletRequest request) {
        return addressService.setFirstSelected(id, request);
    }

    /**
     * 删除收货地址。
     *
     * 根据地址 ID 删除指定的收货地址。
     *
     * @param id 地址 ID（路径变量）
     * @return 操作结果，true 表示删除成功
     */
    @DeleteMapping("/{id}")
    public ResponseVo<Boolean> delete(@PathVariable("id") Long id) {
        return addressService.delete(id);
    }
}

