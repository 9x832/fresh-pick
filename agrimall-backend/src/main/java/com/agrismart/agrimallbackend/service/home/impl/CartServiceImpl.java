package com.agrismart.agrimallbackend.service.home.impl;

import com.google.gson.Gson;
import com.agrismart.agrimallbackend.common.bean.CodeMsg;
import com.agrismart.agrimallbackend.common.util.StringUtil;
import com.agrismart.agrimallbackend.common.util.ValidateEntityUtil;
import com.agrismart.agrimallbackend.dto.response.ResponseVo;
import com.agrismart.agrimallbackend.entity.common.Product;
import com.agrismart.agrimallbackend.entity.home.Cart;
import com.agrismart.agrimallbackend.mapper.common.ProductMapper;
import com.agrismart.agrimallbackend.service.home.ICartService;
import com.agrismart.agrimallbackend.vo.CartProductVo;
import com.agrismart.agrimallbackend.vo.CartVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

/**
 * 购物车服务实现类。
 *
 * 该类实现了 {@link ICartService} 接口，提供购物车相关的业务逻辑实现。
 * 购物车数据存储在 Redis 中，使用 Hash 结构存储，Key 格式：cart_{userId}。
 *
 * 购物车特性：
 *
 * - 同一商品在购物车中只存在一条记录，添加相同商品时会累加数量
 * - 添加和更新商品时会验证库存，不能超过商品库存数量
 * - 购物车列表查询时会关联商品信息，计算商品小计和购物车总价
 *
 * @author agrimall
 * @see com.agrismart.agrimallbackend.service.home.ICartService
 * @since 1.0
 */
@Service
public class CartServiceImpl implements ICartService {

    /**
     * 购物车 Redis Key 模板。
     * 格式：cart_{userId}，用于存储用户的购物车数据。
     */
    private static final String CART_REDIS_KEY_TEMPLATE = "cart_%d";

    /**
     * Redis 模板。
     * 用于操作 Redis 中的购物车数据。
     */
    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 商品数据访问对象。
     * 用于查询商品信息和验证库存。
     */
    @Autowired
    private ProductMapper productMapper;

    /**
     * Gson 对象。
     * 用于序列化和反序列化购物车数据。
     */
    private final Gson gson = new Gson();

    @Override
    public ResponseVo<Boolean> add(Long uid, Cart addCart) {
        if (addCart == null) {
            return ResponseVo.errorByMsg(CodeMsg.DATA_ERROR);
        }
        if (addCart.getQuantity() == null) {
            addCart.setQuantity(1);
        }
        CodeMsg validate = ValidateEntityUtil.validate(addCart);
        if (!validate.getCode().equals(CodeMsg.SUCCESS.getCode())) {
            return ResponseVo.errorByMsg(validate);
        }
        Product product = productMapper.selectByPrimaryKey(addCart.getProductId());
        if (product == null) {
            return ResponseVo.errorByMsg(CodeMsg.PRODUCT_NOT_EXIST);
        }
        if (addCart.getQuantity() > product.getStock()) {
            return ResponseVo.errorByMsg(CodeMsg.PRODUCT_STOCK_ERROR);
        }
        HashOperations<String, String, String> opsForHash = redisTemplate.opsForHash();
        String redisKey = String.format(CART_REDIS_KEY_TEMPLATE, uid);

        Cart cart;
        String value = opsForHash.get(redisKey, String.valueOf(product.getId()));
        if (StringUtil.isEmpty(value)) {
            cart = new Cart(product.getId(), addCart.getQuantity());
        } else {
            cart = gson.fromJson(value, Cart.class);
            cart.setQuantity(cart.getQuantity() + addCart.getQuantity());
        }
        opsForHash.put(redisKey,
                String.valueOf(product.getId()),
                gson.toJson(cart));
        return ResponseVo.successByMsg(true, "添加购物车成功！");
    }

    @Override
    public ResponseVo<CartVo> list(Long uid) {
        List<CartProductVo> cartProductVoList = new ArrayList<>();
        BigDecimal cartTotalPrice = BigDecimal.ZERO;
        HashOperations<String, String, String> opsForHash = redisTemplate.opsForHash();
        String redisKey = String.format(CART_REDIS_KEY_TEMPLATE, uid);
        Map<String, String> entries = opsForHash.entries(redisKey);
        Set<Long> productIdSet = new HashSet<>();
        CartProductVo cartProductVo;
        for (Map.Entry<String, String> entry : entries.entrySet()) {
            Cart cart = gson.fromJson(entry.getValue(), Cart.class);
            productIdSet.add(cart.getProductId());
            cartProductVo = new CartProductVo(cart.getProductId(), cart.getQuantity());
            cartProductVoList.add(cartProductVo);
        }
        List<Product> productList = productMapper.selectByProductIdSet(productIdSet);
        for (CartProductVo cartProduct : cartProductVoList) {
            for (Product product : productList) {
                if (cartProduct.getProductId().equals(product.getId())) {
                    cartProduct.setPrice(product.getPrice());
                    cartProduct.setProductName(product.getProductName());
                    cartProduct.setProductPic(product.getProductPic());
                    cartProduct.setProductTotalPrice(product.getPrice().multiply(BigDecimal.valueOf(cartProduct.getQuantity())));
                    cartTotalPrice = cartTotalPrice.add(product.getPrice().multiply(BigDecimal.valueOf(cartProduct.getQuantity())));
                }
            }
        }
        CartVo cartVo = new CartVo();
        cartVo.setCartProductVoList(cartProductVoList);
        cartVo.setCartTotalPrice(cartTotalPrice);
        return ResponseVo.success(cartVo);
    }

    @Override
    public ResponseVo<Boolean> update(Long uid, Long productId, String method) {
        HashOperations<String, String, String> opsForHash = redisTemplate.opsForHash();
        String redisKey = String.format(CART_REDIS_KEY_TEMPLATE, uid);
        Product product = productMapper.selectByPrimaryKey(productId);
        String value = opsForHash.get(redisKey, String.valueOf(productId));
        if (StringUtil.isEmpty(value)) {
            return ResponseVo.errorByMsg(CodeMsg.PRODUCT_NOT_EXIST);
        }
        Cart cart = gson.fromJson(value, Cart.class);
        if ("add".equals(method)) {
            if (cart.getQuantity() >= product.getStock()) {
                return ResponseVo.errorByMsg(CodeMsg.PRODUCT_STOCK_ERROR);
            }
            if (cart.getQuantity() != null && cart.getQuantity() >= 0) {
                cart.setQuantity(cart.getQuantity() + 1);
            }
        } else if ("reduce".equals(method)) {
            if (cart.getQuantity() != null && cart.getQuantity() >= 0) {
                cart.setQuantity(cart.getQuantity() - 1);
            }
        }
        opsForHash.put(redisKey, String.valueOf(productId), gson.toJson(cart));
        return ResponseVo.success(true);
    }

    @Override
    public ResponseVo<Boolean> delete(Long uid, Long productId) {
        HashOperations<String, String, String> opsForHash = redisTemplate.opsForHash();
        String redisKey = String.format(CART_REDIS_KEY_TEMPLATE, uid);
        String value = opsForHash.get(redisKey, String.valueOf(productId));
        if (StringUtil.isEmpty(value)) {
            return ResponseVo.errorByMsg(CodeMsg.PRODUCT_NOT_EXIST);
        }
        opsForHash.delete(redisKey, String.valueOf(productId));
        return ResponseVo.success(true);
    }

    @Override
    public ResponseVo<Integer> total(Long uid) {
        Integer total = 0;
        HashOperations<String, String, String> opsForHash = redisTemplate.opsForHash();
        String redisKey = String.format(CART_REDIS_KEY_TEMPLATE, uid);
        Map<String, String> entries = opsForHash.entries(redisKey);
        for (Map.Entry<String, String> entry : entries.entrySet()) {
            total++;
        }
        return ResponseVo.success(total);
    }
}

