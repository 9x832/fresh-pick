package com.agrismart.agrimallbackend.controller.home;

import com.agrismart.agrimallbackend.common.bean.CodeMsg;
import com.agrismart.agrimallbackend.common.enums.AddressFirstSelectedEnum;
import com.agrismart.agrimallbackend.dto.response.ResponseVo;
import com.agrismart.agrimallbackend.entity.common.Order;
import com.agrismart.agrimallbackend.service.common.IOrderService;
import com.agrismart.agrimallbackend.service.home.IAddressService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 前台订单管理控制器。
 *
 * 该控制器提供前台用户订单相关的功能接口，包括：
 *
 * - 订单详情查询
 * - 订单生成（从购物车生成订单）
 * - 订单提交（确认下单）
 * - 订单状态更新（如确认收货、取消订单等）
 * - 订单删除（软删除）
 *
 * 接口路径：{@code /api/home/order}
 *
 * 权限要求：所有接口都需要用户登录（通过 {@link com.agrismart.agrimallbackend.interceptor.JwtInterceptor} 验证）。
 *
 * @author agrimall
 * @see com.agrismart.agrimallbackend.service.common.IOrderService
 * @see com.agrismart.agrimallbackend.service.home.IAddressService
 * @since 1.0
 */
@RestController
@RequestMapping("/api/home/order")
public class HomeOrderController {

    /**
     * 订单服务接口。
     */
    private final IOrderService orderService;

    /**
     * 地址服务接口。
     */
    private final IAddressService addressService;

    /**
     * 构造函数，注入依赖。
     *
     * @param orderService   订单服务接口
     * @param addressService 地址服务接口
     */
    @Autowired
    public HomeOrderController(IOrderService orderService, IAddressService addressService) {
        this.orderService = orderService;
        this.addressService = addressService;
    }

    /**
     * 查询订单详情。
     *
     * 根据订单 ID 查询订单的详细信息，包括订单基本信息和用户的首选收货地址。
     * 只能查询当前登录用户自己的订单。
     *
     * @param orderId 订单 ID（路径变量）
     * @param uid     用户 ID（从请求属性中获取，由拦截器设置）
     * @return 包含订单信息和地址信息的响应对象
     *
     * - order：订单详细信息
     * - address：用户的首选收货地址
     *
     */
    @GetMapping("/{orderId}")
    public ResponseVo<Map<String, Object>> detail(@PathVariable("orderId") Long orderId,
                                                  @RequestAttribute("id") Long uid) {
        Order order = orderService.selectByOrderIdAndUserId(uid, orderId);
        if (order == null) {
            return ResponseVo.errorByMsg(CodeMsg.ORDER_NOT_EXIST);
        }
        Map<String, Object> payload = new HashMap<>(2);
        payload.put("order", order);
        payload.put("address", addressService.selectByUserIdAndFirstSelected(uid, AddressFirstSelectedEnum.YES.getCode()));
        return ResponseVo.success(payload);
    }

    /**
     * 生成订单。
     *
     * 从购物车中根据商品 ID 列表生成订单。订单生成后处于待提交状态，需要调用提交接口才能完成下单。
     *
     * @param ids     购物车商品 ID 列表（逗号分隔的字符串）
     * @param request HTTP 请求对象，用于获取当前登录用户信息
     * @return 生成的订单 ID
     */
    @PostMapping("/generate")
    public ResponseVo<Long> generate(@RequestParam("ids") String ids, HttpServletRequest request) {
        return orderService.generate(ids, request);
    }

    /**
     * 提交订单。
     *
     * 确认提交订单，完成下单流程。提交成功后会发送订单确认邮件给用户。
     *
     * @param remark  订单备注
     * @param orderId 订单 ID
     * @param request HTTP 请求对象，用于获取当前登录用户信息（ID 和邮箱）
     * @return 操作结果，true 表示订单提交成功
     */
    @PostMapping("/submit")
    public ResponseVo<Boolean> submit(@RequestParam("remark") String remark,
                                      @RequestParam("orderId") Long orderId,
                                      HttpServletRequest request) {
        Object attr = request.getAttribute("id");
        Long uid = attr instanceof Long ? (Long) attr : Long.valueOf(String.valueOf(attr));
        String email = (String) request.getAttribute("email");
        return orderService.submit(remark, orderId, uid, email);
    }

    /**
     * 更新订单状态。
     *
     * 更新订单的状态，用于订单流程管理（如确认收货、取消订单等）。
     *
     * @param orderId 订单 ID
     * @param state   新的订单状态，对应 {@link com.agrismart.agrimallbackend.common.enums.OrderStateEnum} 中的值
     * @return 操作结果，true 表示状态更新成功
     */
    @PostMapping("/state")
    public ResponseVo<Boolean> updateOrderState(@RequestParam("orderId") Long orderId,
                                                @RequestParam("state") Integer state) {
        return orderService.updateOrderState(orderId, state);
    }

    /**
     * 用户删除订单（软删除）。
     *
     * 用户删除自己的订单，采用软删除机制，不会真正删除订单数据。
     *
     * @param orderId   订单 ID
     * @param isDeleted 删除状态，1 表示已删除，0 表示未删除
     * @return 操作结果，true 表示删除成功
     */
    @PostMapping("/delete")
    public ResponseVo<Boolean> userDelete(@RequestParam("orderId") Long orderId,
                                          @RequestParam("isDeleted") Integer isDeleted) {
        return orderService.userDelete(orderId, isDeleted);
    }
}

