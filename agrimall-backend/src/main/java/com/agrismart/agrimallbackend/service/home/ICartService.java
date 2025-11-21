package com.agrismart.agrimallbackend.service.home;

import com.agrismart.agrimallbackend.entity.home.Cart;
import com.agrismart.agrimallbackend.dto.response.ResponseVo;
import com.agrismart.agrimallbackend.vo.CartVo;

/**
 * 购物车服务接口。
 *
 * 该接口定义了用户购物车相关的业务操作方法，包括：
 *
 * - 商品添加到购物车
 * - 购物车列表查询（包含商品详细信息）
 * - 购物车商品数量更新（增加/减少）
 * - 购物车商品删除
 * - 购物车商品总数查询
 *
 * 注意：
 *
 * - 购物车数据存储在 Redis 中，Key 格式：cart_{userId}
 * - 同一商品在购物车中只存在一条记录，添加相同商品时会累加数量
 * - 添加商品时会验证库存，不能超过商品库存数量
 *
 * @author agrimall
 * @see com.agrismart.agrimallbackend.service.home.impl.CartServiceImpl
 * @see com.agrismart.agrimallbackend.entity.home.Cart
 * @see com.agrismart.agrimallbackend.vo.CartVo
 * @since 1.0
 */
public interface ICartService {

    /**
     * 添加商品到购物车。
     * 如果购物车中已存在该商品，则累加数量；否则创建新记录。
     * 添加时会验证库存，不能超过商品库存数量。
     *
     * @param uid     用户 ID
     * @param addCart 购物车对象（包含商品 ID 和数量）
     * @return 操作结果
     */
    ResponseVo<Boolean> add(Long uid, Cart addCart);

    /**
     * 查询购物车列表。
     * 返回购物车中所有商品的详细信息（名称、图片、价格等）和购物车总价。
     *
     * @param uid 用户 ID
     * @return 购物车对象（包含商品列表和总价）
     */
    ResponseVo<CartVo> list(Long uid);

    /**
     * 更新购物车商品数量。
     * 支持增加（add）和减少（reduce）两种操作。
     * 增加时会验证库存，不能超过商品库存数量。
     *
     * @param uid       用户 ID
     * @param productId 商品 ID
     * @param method    操作方式（"add" 表示增加，"reduce" 表示减少）
     * @return 操作结果
     */
    ResponseVo<Boolean> update(Long uid, Long productId, String method);

    /**
     * 从购物车删除商品。
     *
     * @param uid       用户 ID
     * @param productId 商品 ID
     * @return 操作结果
     */
    ResponseVo<Boolean> delete(Long uid, Long productId);

    /**
     * 查询购物车商品总数。
     * 返回购物车中不同商品的数量（不是商品总件数）。
     *
     * @param uid 用户 ID
     * @return 购物车商品总数
     */
    ResponseVo<Integer> total(Long uid);
}

