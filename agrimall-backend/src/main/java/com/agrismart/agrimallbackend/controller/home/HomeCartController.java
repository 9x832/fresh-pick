package com.agrismart.agrimallbackend.controller.home;

import com.agrismart.agrimallbackend.dto.response.ResponseVo;
import com.agrismart.agrimallbackend.entity.home.Cart;
import com.agrismart.agrimallbackend.service.home.ICartService;
import com.agrismart.agrimallbackend.vo.CartVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 前台购物车管理控制器。
 *
 * 该控制器提供前台用户购物车的管理接口，包括：
 *
 * - 购物车列表查询
 * - 商品添加到购物车
 * - 购物车商品数量更新
 * - 购物车商品删除
 * - 购物车商品总数查询
 *
 * 接口路径：{@code /api/home/cart}
 *
 * 权限要求：所有接口都需要用户登录（通过 {@link com.agrismart.agrimallbackend.interceptor.JwtInterceptor} 验证）。
 *
 * @author agrimall
 * @see com.agrismart.agrimallbackend.service.home.ICartService
 * @see com.agrismart.agrimallbackend.entity.home.Cart
 * @see com.agrismart.agrimallbackend.vo.CartVo
 * @since 1.0
 */
@RestController
@RequestMapping("/api/home/cart")
public class HomeCartController {

    /**
     * 购物车服务接口。
     */
    private final ICartService cartService;

    /**
     * 构造函数，注入依赖。
     *
     * @param cartService 购物车服务接口
     */
    @Autowired
    public HomeCartController(ICartService cartService) {
        this.cartService = cartService;
    }

    /**
     * 查询购物车列表。
     *
     * 返回当前登录用户的购物车信息，包括商品列表、总价等。
     *
     * @param uid 用户 ID（从请求属性中获取，由拦截器设置）
     * @return 购物车视图对象，包含商品列表和总价等信息
     */
    @GetMapping
    public ResponseVo<CartVo> list(@RequestAttribute("id") Long uid) {
        return cartService.list(uid);
    }

    /**
     * 添加商品到购物车。
     *
     * 将商品添加到当前登录用户的购物车中。如果商品已存在，则更新数量。
     *
     * @param uid    用户 ID（从请求属性中获取，由拦截器设置）
     * @param cart   购物车实体对象，包含商品 ID 和数量等信息
     * @return 操作结果，true 表示添加成功
     */
    @PostMapping
    public ResponseVo<Boolean> add(@RequestAttribute("id") Long uid,
                                   @org.springframework.web.bind.annotation.RequestBody Cart cart) {
        return cartService.add(uid, cart);
    }

    /**
     * 更新购物车商品数量。
     *
     * 更新购物车中指定商品的数量。method 参数指定更新方式：
     *
     * - "add"：增加数量
     * - "sub"：减少数量
     * - 其他：直接设置为指定数量
     *
     * @param uid       用户 ID（从请求属性中获取，由拦截器设置）
     * @param productId 商品 ID（路径变量）
     * @param method    更新方式（"add"、"sub" 或其他）
     * @return 操作结果，true 表示更新成功
     */
    @PutMapping("/{productId}")
    public ResponseVo<Boolean> update(@RequestAttribute("id") Long uid,
                                      @PathVariable("productId") Long productId,
                                      @RequestParam("method") String method) {
        return cartService.update(uid, productId, method);
    }

    /**
     * 删除购物车商品。
     *
     * 从购物车中删除指定的商品。
     *
     * @param uid       用户 ID（从请求属性中获取，由拦截器设置）
     * @param productId 商品 ID（路径变量）
     * @return 操作结果，true 表示删除成功
     */
    @DeleteMapping("/{productId}")
    public ResponseVo<Boolean> delete(@RequestAttribute("id") Long uid,
                                      @PathVariable("productId") Long productId) {
        return cartService.delete(uid, productId);
    }

    /**
     * 查询购物车商品总数。
     *
     * 返回当前登录用户购物车中商品的总数量（所有商品的数量之和）。
     *
     * @param uid 用户 ID（从请求属性中获取，由拦截器设置）
     * @return 购物车商品总数
     */
    @GetMapping("/total")
    public ResponseVo<Integer> total(@RequestAttribute("id") Long uid) {
        return cartService.total(uid);
    }
}

