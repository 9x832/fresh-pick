package com.agrismart.agrimallbackend.controller.admin;

import com.agrismart.agrimallbackend.common.util.StringUtil;
import com.agrismart.agrimallbackend.dto.response.ResponseVo;
import com.agrismart.agrimallbackend.entity.common.Order;
import com.agrismart.agrimallbackend.service.common.IOrderService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 订单管理控制器。
 *
 * 该控制器提供后台管理系统对订单的管理接口，包括：
 *
 * - 订单列表查询（支持分页和按订单号搜索）
 * - 订单详情查询（包含订单项）
 * - 订单状态更新
 * - 订单删除
 *
 * 接口路径：{@code /api/admin/orders}
 *
 * 权限要求：
 *
 * - 需要管理员登录（通过 {@link com.agrismart.agrimallbackend.interceptor.AdminInterceptor} 验证）
 * - 需要相应的菜单权限
 *
 * @author agrimall
 * @see com.agrismart.agrimallbackend.service.common.IOrderService
 * @see com.agrismart.agrimallbackend.entity.common.Order
 * @since 1.0
 */
@RestController
@RequestMapping("/api/admin/orders")
public class OrderManageController {

    /**
     * 订单服务接口。
     */
    private final IOrderService orderService;

    /**
     * 构造函数，注入依赖。
     *
     * @param orderService 订单服务接口
     */
    @Autowired
    public OrderManageController(IOrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * 分页查询订单列表。
     *
     * 支持按订单号搜索，如果不提供订单号或订单号格式错误则查询所有订单。
     *
     * @param pageNum 页码，从 1 开始，默认为 1
     * @param pageSize 每页大小，默认为 5
     * @param orderNo 订单号（可选），用于精确搜索订单
     * @return 分页信息，包含订单列表
     */
    @GetMapping
    public ResponseVo<PageInfo> list(@RequestParam(defaultValue = "1") Integer pageNum,
                                     @RequestParam(defaultValue = "5") Integer pageSize,
                                     @RequestParam(required = false) String orderNo) {
        if (StringUtil.isEmpty(orderNo)) {
            return orderService.selectByPage(pageNum, pageSize);
        }
        try {
            // 尝试将订单号转换为 Long 类型进行精确查询
            Long orderNoLong = Long.valueOf(orderNo);
            return orderService.selectByPageAndContent(orderNoLong, pageNum, pageSize);
        } catch (NumberFormatException e) {
            // 如果订单号格式错误，返回所有订单列表
            return orderService.selectByPage(pageNum, pageSize);
        }
    }

    /**
     * 查询订单详情。
     *
     * 根据订单 ID 查询订单的详细信息，包括订单基本信息和订单项列表。
     *
     * @param orderId 订单 ID（路径变量）
     * @return 包含订单信息和订单项列表的响应对象
     *
     * - order：订单基本信息
     * - items：订单项列表，包含商品信息和购买数量
     *
     */
    @GetMapping("/{orderId}")
    public ResponseVo<Map<String, Object>> detail(@PathVariable("orderId") Long orderId) {
        Order order = orderService.selectByPrimaryKey(orderId);
        if (order == null) {
            return ResponseVo.errorByMsg(com.agrismart.agrimallbackend.common.bean.CodeMsg.ORDER_NOT_EXIST);
        }
        Map<String, Object> payload = new HashMap<>(2);
        payload.put("order", order);
        payload.put("items", orderService.getOrderItemByOrderId(orderId));
        return ResponseVo.success(payload);
    }

    /**
     * 更新订单状态。
     *
     * 更新指定订单的状态，用于订单流程管理（如发货、送达、签收等）。
     *
     * @param orderId 订单 ID（路径变量）
     * @param state   新的订单状态，对应 {@link com.agrismart.agrimallbackend.common.enums.OrderStateEnum} 中的值
     * @return 操作结果，true 表示状态更新成功
     */
    @PostMapping("/{orderId}/state")
    public ResponseVo<Boolean> updateState(@PathVariable("orderId") Long orderId,
                                           @RequestParam("state") Integer state) {
        return orderService.updateOrderState(orderId, state);
    }

    /**
     * 删除订单。
     *
     * 根据订单 ID 删除指定的订单。
     *
     * @param orderId 订单 ID（路径变量）
     * @return 操作结果，true 表示删除成功
     */
    @DeleteMapping("/{orderId}")
    public ResponseVo<Boolean> delete(@PathVariable("orderId") Long orderId) {
        return orderService.deleteOrder(orderId);
    }
}

