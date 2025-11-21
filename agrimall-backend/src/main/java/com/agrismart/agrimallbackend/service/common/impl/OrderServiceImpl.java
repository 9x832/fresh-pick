package com.agrismart.agrimallbackend.service.common.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.gson.Gson;
import com.agrismart.agrimallbackend.common.bean.CodeMsg;
import com.agrismart.agrimallbackend.common.enums.AddressFirstSelectedEnum;
import com.agrismart.agrimallbackend.common.enums.MailTypeEnum;
import com.agrismart.agrimallbackend.common.enums.OrderStateEnum;
import com.agrismart.agrimallbackend.common.util.MailUtil;
import com.agrismart.agrimallbackend.common.util.SnowFlake;
import com.agrismart.agrimallbackend.common.util.StringUtil;
import com.agrismart.agrimallbackend.dto.response.ResponseVo;
import com.agrismart.agrimallbackend.entity.common.Order;
import com.agrismart.agrimallbackend.entity.common.OrderItem;
import com.agrismart.agrimallbackend.entity.common.Product;
import com.agrismart.agrimallbackend.entity.home.Address;
import com.agrismart.agrimallbackend.entity.home.Cart;
import com.agrismart.agrimallbackend.mapper.common.OrderItemMapper;
import com.agrismart.agrimallbackend.mapper.common.OrderMapper;
import com.agrismart.agrimallbackend.mapper.common.ProductMapper;
import com.agrismart.agrimallbackend.mapper.home.AddressMapper;
import com.agrismart.agrimallbackend.service.common.IOrderService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;

/**
 * 订单服务实现类。
 *
 * 该类实现了 {@link IOrderService} 接口，提供订单相关的业务逻辑实现。
 * 包括订单生成、提交、状态管理、查询统计等功能。
 * 使用 Redis 存储购物车数据，使用事务保证订单提交和状态更新的数据一致性。
 *
 * 订单流程：
 *
 * - 从 Redis 购物车生成订单（未支付状态）
 * - 提交订单时验证库存、扣减库存、更新状态、发送邮件
 * - 订单状态流转由管理员操作
 *
 * @author agrimall
 * @see com.agrismart.agrimallbackend.service.common.IOrderService
 * @since 1.0
 */
@Service
public class OrderServiceImpl implements IOrderService {

    /**
     * 商品数据访问对象。
     * 用于查询商品信息和更新库存、销量。
     */
    @Autowired
    private ProductMapper productMapper;

    /**
     * 地址数据访问对象。
     * 用于查询用户的首选地址。
     */
    @Autowired
    private AddressMapper addressMapper;

    /**
     * 订单数据访问对象。
     */
    @Autowired
    private OrderMapper orderMapper;

    /**
     * 订单项数据访问对象。
     */
    @Autowired
    private OrderItemMapper orderItemMapper;

    /**
     * 邮件工具类。
     * 用于发送订单提交通知邮件。
     */
    @Autowired
    private MailUtil mailUtil;

    /**
     * 购物车 Redis Key 模板。
     * 格式：cart_{userId}，用于存储用户的购物车数据。
     */
    private static final String CART_REDIS_KEY_TEMPLATE = "cart_%d";

    /**
     * 订单库存扣减标记 Redis Key 模板。
     * 格式：order_stock_{orderId}，用于标记订单是否已扣减库存，防止重复扣减。
     */
    private static final String ORDER_STOCK_KEY_TEMPLATE = "order_stock_%d";

    /**
     * Gson 对象。
     * 用于序列化和反序列化购物车数据。
     */
    private final Gson gson = new Gson();

    /**
     * Redis 模板。
     * 用于操作 Redis 中的购物车数据和订单库存标记。
     */
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    @Transactional
    public ResponseVo<Long> generate(String ids, HttpServletRequest request) {
        if (StringUtil.isEmpty(ids)) {
            CodeMsg codeMsg = CodeMsg.DATA_ERROR;
            codeMsg.setMsg("请勾选你要购买的商品！");
            return ResponseVo.errorByMsg(codeMsg);
        }
        String[] split = ids.split(",");
        Set<Long> productIdSet = new HashSet<>();
        for (String id : split) {
            productIdSet.add(Long.valueOf(id));
        }
        List<Product> productList = productMapper.selectByProductIdSet(productIdSet);
        List<OrderItem> orderItemList = new ArrayList<>();
        OrderItem orderItem;
        HashOperations<String, String, String> opsForHash = redisTemplate.opsForHash();
        Object attr = request.getAttribute("id");
        Long uid = attr instanceof Long ? (Long) attr : Long.valueOf(String.valueOf(attr));
        String redisKey = String.format(CART_REDIS_KEY_TEMPLATE, uid);
        Map<String, String> entries = opsForHash.entries(redisKey);
        BigDecimal orderTotalPrice = BigDecimal.ZERO;
        for (Map.Entry<String, String> entry : entries.entrySet()) {
            Cart cart = gson.fromJson(entry.getValue(), Cart.class);
            for (Product product : productList) {
                if (product.getId().equals(cart.getProductId())) {
                    orderItem = new OrderItem(product.getId(), cart.getQuantity(),
                            product.getProductName(),
                            product.getProductPic(),
                            product.getPrice(),
                            product.getPrice().multiply(BigDecimal.valueOf(cart.getQuantity())));
                    orderItemList.add(orderItem);
                    orderTotalPrice = orderTotalPrice.add(product.getPrice().multiply(BigDecimal.valueOf(cart.getQuantity())));
                }
            }
        }
        Order order = new Order(new SnowFlake(2, 3).nextId(),
                uid,
                OrderStateEnum.NO_PAY.getCode(),
                orderTotalPrice);
        if (orderMapper.insertSelective(order) <= 0) {
            return ResponseVo.errorByMsg(CodeMsg.ORDER_ADD_ERROR);
        }
        for (OrderItem or : orderItemList) {
            or.setOrderId(order.getId());
        }
        if (orderItemMapper.batchInsert(orderItemList) <= 0) {
            return ResponseVo.errorByMsg(CodeMsg.ORDER_ITEM_ADD_ERROR);
        }
        for (Long productId : productIdSet) {
            opsForHash.delete(redisKey, String.valueOf(productId));
        }
        return ResponseVo.success(order.getId());
    }

    @Override
    public Order selectByOrderIdAndUserId(Long userId, Long orderId) {
        return orderMapper.selectByOrderIdAndUserId(userId, orderId);
    }

    @Override
    @Transactional
    public ResponseVo<Boolean> submit(String remark, Long orderId, Long uid, String email) {
        Order order = orderMapper.selectByOrderIdAndUserId(uid, orderId);
        if (order == null) {
            return ResponseVo.errorByMsg(CodeMsg.DATA_ERROR);
        }
        Address address = addressMapper.selectByUserIdAndFirstSelected(uid, AddressFirstSelectedEnum.YES.getCode());
        if (address == null) {
            return ResponseVo.errorByMsg(CodeMsg.ORDER_ADDRESS_EMPTY);
        }
        if (remark.length() > 50) {
            return ResponseVo.errorByMsg(CodeMsg.ORDER_REMARK_EXCEED_LENGTH);
        }
        CodeMsg codeMsg = CodeMsg.ORDER_ERROR;
        Set<Long> productIdSet = new HashSet<>();
        for (OrderItem orderItem : order.getOrderItemList()) {
            productIdSet.add(orderItem.getProductId());
        }
        List<Product> productList = productMapper.selectByProductIdSet(productIdSet);
        for (OrderItem orderItem : order.getOrderItemList()) {
            for (Product product : productList) {
                if (orderItem.getProductId().equals(product.getId())) {
                    if (orderItem.getQuantity() > product.getStock()) {
                        codeMsg.setMsg("商品<" + product.getProductName() + ">库存不足了，请减少购买数量！");
                        return ResponseVo.errorByMsg(codeMsg);
                    }
                }
            }
        }
        for (OrderItem orderItem : order.getOrderItemList()) {
            for (Product product : productList) {
                if (orderItem.getProductId().equals(product.getId())) {
                    product.setStock(product.getStock() - orderItem.getQuantity());
                    product.setSellNum(product.getSellNum() + orderItem.getQuantity());
                }
            }
        }
        for (Product product : productList) {
            if (productMapper.updateByPrimaryKeySelective(product) <= 0) {
                throw new RuntimeException("商品库存信息更新失败！");
            }
        }
        redisTemplate.opsForValue().set(String.format(ORDER_STOCK_KEY_TEMPLATE, order.getId()), "1");
        order.setAddressId(address.getId());
        order.setRemark(remark);
        order.setState(OrderStateEnum.PAYED.getCode());
        if (orderMapper.updateByPrimaryKeySelective(order) <= 0) {
            throw new RuntimeException("订单提交失败！");
        }
        HashOperations<String, String, String> opsForHash = redisTemplate.opsForHash();
        String redisKey = String.format(CART_REDIS_KEY_TEMPLATE, uid);
        for (OrderItem orderItem : order.getOrderItemList()) {
            opsForHash.delete(redisKey, String.valueOf(orderItem.getProductId()));
        }
        mailUtil.sendMailAsync(MailTypeEnum.ORDER_SUBMIT.getCode(), email, String.valueOf(order.getOrderNo()));
        return ResponseVo.successByMsg(true, "订单提交成功,交易完成！");
    }

    @Override
    public ResponseVo<PageInfo> selectByPageAndUserIdAndIsDeleted(Long userId, Integer isDeleted, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Order> orderList = orderMapper.selectByUserIdAndIsDeleted(userId, isDeleted);
        PageInfo<Order> pageInfo = new PageInfo<>(orderList);
        pageInfo.setList(orderList);
        return ResponseVo.success(pageInfo);
    }

    @Override
    public ResponseVo<PageInfo> selectByPageAndUserIdAndState(Long userId, Integer isDeleted, Integer state, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Order> orderList = orderMapper.selectByOrderStateAndUserIdAndIsDeleted(state, userId, isDeleted);
        PageInfo<Order> pageInfo = new PageInfo<>(orderList);
        pageInfo.setList(orderList);
        return ResponseVo.success(pageInfo);
    }

    @Override
    public ResponseVo<PageInfo> selectByPage(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Order> orderList = orderMapper.selectAll();
        PageInfo<Order> pageInfo = new PageInfo<>(orderList);
        pageInfo.setList(orderList);
        return ResponseVo.success(pageInfo);
    }

    @Override
    public ResponseVo<PageInfo> selectByPageAndContent(Long orderNo, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Order> orderList = orderMapper.selectBySearchContent(orderNo);
        PageInfo<Order> pageInfo = new PageInfo<>(orderList);
        pageInfo.setList(orderList);
        return ResponseVo.success(pageInfo);
    }

    @Override
    public List<Order> selectByOrderStateAndUserIdAndIsDeleted(Integer state, Long userId, Integer isDeleted) {
        return orderMapper.selectByOrderStateAndUserIdAndIsDeleted(state, userId, isDeleted);
    }

    @Override
    @Transactional
    public ResponseVo<Boolean> updateOrderState(Long orderId, Integer state) {
        if (orderId == null || state == null) {
            return ResponseVo.errorByMsg(CodeMsg.DATA_ERROR);
        }
        Order order = orderMapper.selectByPrimaryKey(orderId);
        if (order == null) {
            return ResponseVo.errorByMsg(CodeMsg.ORDER_NOT_EXIST);
        }
        if (OrderStateEnum.SEND.getCode().equals(state)) {
            String stockKey = String.format(ORDER_STOCK_KEY_TEMPLATE, orderId);
            boolean alreadyDeducted = Boolean.TRUE.equals(redisTemplate.hasKey(stockKey));
            if (!alreadyDeducted) {
                List<OrderItem> orderItems = orderMapper.getOrderItemByOrderId(orderId);
                if (!CollectionUtils.isEmpty(orderItems)) {
                    Set<Long> productIds = new HashSet<>();
                    for (OrderItem item : orderItems) {
                        if (item.getProductId() != null) {
                            productIds.add(item.getProductId());
                        }
                    }
                    if (!productIds.isEmpty()) {
                        List<Product> products = productMapper.selectByProductIdSet(productIds);
                        Map<Long, Product> productMap = new HashMap<>();
                        for (Product product : products) {
                            productMap.put(product.getId(), product);
                        }
                        CodeMsg codeMsg = CodeMsg.ORDER_ERROR;
                        for (OrderItem item : orderItems) {
                            Product product = productMap.get(item.getProductId());
                            if (product == null) {
                                codeMsg.setMsg("商品信息异常，无法发货，请联系管理员！");
                                return ResponseVo.errorByMsg(codeMsg);
                            }
                            Integer stock = product.getStock();
                            if (stock == null || stock < item.getQuantity()) {
                                codeMsg.setMsg("商品<" + product.getProductName() + ">库存不足，无法发货！");
                                return ResponseVo.errorByMsg(codeMsg);
                            }
                        }
                        for (OrderItem item : orderItems) {
                            Product product = productMap.get(item.getProductId());
                            product.setStock(product.getStock() - item.getQuantity());
                            Integer sellNum = product.getSellNum() == null ? 0 : product.getSellNum();
                            product.setSellNum(sellNum + item.getQuantity());
                        }
                        for (Product product : productMap.values()) {
                            if (productMapper.updateByPrimaryKeySelective(product) <= 0) {
                                throw new RuntimeException("商品库存信息更新失败！");
                            }
                        }
                        redisTemplate.opsForValue().set(stockKey, "1");
                    }
                }
            }
        }
        if (orderMapper.updateStateByOrderId(orderId, state) <= 0) {
            return ResponseVo.errorByMsg(CodeMsg.ORDER_STATE_EDIT_ERROR);
        }
        return ResponseVo.successByMsg(true, "成功修改订单状态！");
    }

    @Override
    public ResponseVo<Boolean> userDelete(Long orderId, Integer isDeleted) {
        if (orderId == null || isDeleted == null) {
            return ResponseVo.errorByMsg(CodeMsg.DATA_ERROR);
        }
        Order order = orderMapper.selectByPrimaryKey(orderId);
        if (order == null) {
            return ResponseVo.errorByMsg(CodeMsg.ORDER_NOT_EXIST);
        }
        if (orderMapper.updateIsDeletedByOrderId(orderId, isDeleted) <= 0) {
            return ResponseVo.errorByMsg(CodeMsg.ORDER_DELETE_ERROR);
        }
        return ResponseVo.successByMsg(true, "成功删除该订单！");
    }

    @Override
    public Order selectByPrimaryKey(Long id) {
        return orderMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<OrderItem> getOrderItemByOrderId(Long orderId) {
        return orderMapper.getOrderItemByOrderId(orderId);
    }

    @Override
    public ResponseVo<Boolean> deleteOrder(Long orderId) {
        if (orderId == null) {
            return ResponseVo.errorByMsg(CodeMsg.DATA_ERROR);
        }
        if (orderMapper.deleteByPrimaryKey(orderId) <= 0) {
            return ResponseVo.errorByMsg(CodeMsg.ORDER_DELETE_ERROR);
        }
        return ResponseVo.successByMsg(true, "订单删除成功！");
    }

    @Override
    public List<Order> getOrderByDay() {
        return orderMapper.getOrderByDay();
    }

    @Override
    public List<Order> getOrderByWeek() {
        return orderMapper.getOrderByWeek();
    }

    @Override
    public List<Order> getOrderByMonth() {
        return orderMapper.getOrderByMonth();
    }

    @Override
    public List<Map<String, Object>> getOrderCountByMonth() {
        return orderMapper.getOrderCountByMonth();
    }

    @Override
    public List<Map<String, Object>> getOrderCountByDayOfWeek() {
        return orderMapper.getOrderCountByDayOfWeek();
    }
}

