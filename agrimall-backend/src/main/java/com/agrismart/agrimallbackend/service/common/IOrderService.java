package com.agrismart.agrimallbackend.service.common;

import com.github.pagehelper.PageInfo;
import com.agrismart.agrimallbackend.dto.response.ResponseVo;
import com.agrismart.agrimallbackend.entity.common.Order;
import com.agrismart.agrimallbackend.entity.common.OrderItem;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * 订单服务接口。
 *
 * 该接口定义了订单相关的业务操作方法，包括：
 *
 * - 订单生成和提交（从购物车生成订单）
 * - 订单查询（支持按用户、状态、删除状态筛选）
 * - 订单状态管理
 * - 订单删除（用户软删除和管理员删除）
 * - 订单统计（按日、周、月统计）
 *
 * 订单流程：
 *
 * - 从购物车生成订单（未支付状态）
 * - 提交订单（验证库存、扣减库存、更新为已支付状态、发送邮件通知）
 * - 订单状态流转：已支付 -> 已发货 -> 已送达 -> 已签收
 *
 * @author agrimall
 * @see com.agrismart.agrimallbackend.service.common.impl.OrderServiceImpl
 * @see com.agrismart.agrimallbackend.entity.common.Order
 * @see com.agrismart.agrimallbackend.entity.common.OrderItem
 * @see com.agrismart.agrimallbackend.common.enums.OrderStateEnum
 * @since 1.0
 */
public interface IOrderService {


    /**
     * 从购物车生成订单。
     * 从 Redis 购物车中获取选中的商品，创建订单和订单项，并从购物车中移除已生成订单的商品。
     *
     * @param ids     商品 ID 列表（逗号分隔的字符串，如 "1,2,3"）
     * @param request HTTP 请求对象（用于获取当前登录用户 ID）
     * @return 操作结果，成功时返回订单 ID（在 data 字段中）
     */
    ResponseVo<Long> generate(String ids, HttpServletRequest request);

    /**
     * 根据订单 ID 和用户 ID 查询订单。
     * 用于验证订单是否属于当前用户。
     *
     * @param userId  用户 ID
     * @param orderId 订单 ID
     * @return 订单对象，如果不存在或不属于该用户则返回 null
     */
    Order selectByOrderIdAndUserId(Long userId, Long orderId);

    /**
     * 提交订单。
     * 提交流程：验证地址 -> 验证库存 -> 扣减库存 -> 更新订单状态为已支付 -> 从购物车移除 -> 发送邮件通知
     * 使用事务保证数据一致性。
     *
     * @param remark  订单留言（可选，最大长度 50 字符）
     * @param orderId 订单 ID
     * @param uid     用户 ID
     * @param email   用户邮箱（用于发送订单通知邮件）
     * @return 操作结果
     */
    ResponseVo<Boolean> submit(String remark, Long orderId, Long uid, String email);

    /**
     * 按用户和删除状态分页查询订单列表。
     * 用于前台用户订单查询。
     *
     * @param userId    用户 ID
     * @param isDeleted 删除状态（0-未删除，1-已删除）
     * @param pageNum   页码，从 1 开始
     * @param pageSize  每页大小
     * @return 包含分页信息的订单列表
     */
    ResponseVo<PageInfo> selectByPageAndUserIdAndIsDeleted(Long userId, Integer isDeleted, Integer pageNum, Integer pageSize);

    /**
     * 按用户、删除状态和订单状态分页查询订单列表。
     * 用于前台用户订单查询，支持按订单状态筛选。
     *
     * @param userId    用户 ID
     * @param isDeleted 删除状态（0-未删除，1-已删除）
     * @param state     订单状态
     * @param pageNum   页码，从 1 开始
     * @param pageSize  每页大小
     * @return 包含分页信息的订单列表
     */
    ResponseVo<PageInfo> selectByPageAndUserIdAndState(Long userId, Integer isDeleted, Integer state, Integer pageNum, Integer pageSize);

    /**
     * 按订单状态、用户和删除状态查询订单列表。
     * 用于订单统计和查询。
     *
     * @param state     订单状态
     * @param userId    用户 ID
     * @param isDeleted 删除状态（0-未删除，1-已删除）
     * @return 订单列表
     */
    List<Order> selectByOrderStateAndUserIdAndIsDeleted(Integer state, Long userId, Integer isDeleted);

    /**
     * 更新订单状态。
     * 用于后台管理系统，管理员更新订单状态（如发货、取消等）。
     * 如果状态为"已发货"且库存未扣减，则先扣减库存再更新状态。
     * 使用事务保证数据一致性。
     *
     * @param orderId 订单 ID
     * @param state   订单状态
     * @return 操作结果
     */
    ResponseVo<Boolean> updateOrderState(Long orderId, Integer state);

    /**
     * 用户删除订单（软删除）。
     * 用户删除订单不会真正删除数据，只是标记为已删除。
     *
     * @param orderId   订单 ID
     * @param isDeleted 删除状态（1-已删除）
     * @return 操作结果
     */
    ResponseVo<Boolean> userDelete(Long orderId, Integer isDeleted);

    /**
     * 分页查询订单列表。
     * 用于后台管理系统。
     *
     * @param pageNum  页码，从 1 开始
     * @param pageSize 每页大小
     * @return 包含分页信息的订单列表
     */
    ResponseVo<PageInfo> selectByPage(Integer pageNum, Integer pageSize);

    /**
     * 按订单号分页查询订单列表。
     * 用于后台管理系统，支持按订单号搜索。
     *
     * @param orderNo  订单号
     * @param pageNum  页码，从 1 开始
     * @param pageSize 每页大小
     * @return 包含分页信息的订单列表
     */
    ResponseVo<PageInfo> selectByPageAndContent(Long orderNo, Integer pageNum, Integer pageSize);

    /**
     * 根据主键查询订单。
     *
     * @param id 订单 ID
     * @return 订单对象，如果不存在则返回 null
     */
    Order selectByPrimaryKey(Long id);

    /**
     * 根据订单 ID 查询订单项列表。
     *
     * @param orderId 订单 ID
     * @return 订单项列表
     */
    List<OrderItem> getOrderItemByOrderId(Long orderId);

    /**
     * 删除订单（物理删除）。
     * 用于后台管理系统，真正删除订单记录。
     *
     * @param orderId 订单 ID
     * @return 操作结果
     */
    ResponseVo<Boolean> deleteOrder(Long orderId);

    /**
     * 查询当天的订单列表。
     * 用于订单统计。
     *
     * @return 订单列表
     */
    List<Order> getOrderByDay();

    /**
     * 查询本周的订单列表。
     * 用于订单统计。
     *
     * @return 订单列表
     */
    List<Order> getOrderByWeek();

    /**
     * 查询本月的订单列表。
     * 用于订单统计。
     *
     * @return 订单列表
     */
    List<Order> getOrderByMonth();

    /**
     * 获取本年度每个月的订单数量统计。
     *
     * @return 月度订单统计数据列表
     */
    List<Map<String, Object>> getOrderCountByMonth();

    /**
     * 获取本周订单按星期分布统计。
     *
     * @return 星期订单统计数据列表
     */
    List<Map<String, Object>> getOrderCountByDayOfWeek();
}

