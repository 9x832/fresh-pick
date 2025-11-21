package com.agrismart.agrimallbackend.controller.home;

import com.agrismart.agrimallbackend.dto.response.ResponseVo;
import com.agrismart.agrimallbackend.entity.common.Product;
import com.agrismart.agrimallbackend.service.home.IRecommendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 前台商品推荐控制器。
 *
 * 该控制器提供商品推荐功能接口，包括：
 *
 * - 个性化推荐（基于用户行为）
 * - 热销推荐（基于销量）
 *
 * 接口路径：{@code /api/home/recommend}
 *
 * 权限要求：
 *
 * - 个性化推荐需要用户登录（通过 {@link com.agrismart.agrimallbackend.interceptor.JwtInterceptor} 验证）
 * - 热销推荐为公开接口，不需要登录
 *
 * @author agrimall
 * @see com.agrismart.agrimallbackend.service.home.IRecommendService
 * @since 1.0
 */
@RestController
@RequestMapping("/api/home/recommend")
public class HomeRecommendController {

    /**
     * 推荐服务接口。
     */
    private final IRecommendService recommendService;

    /**
     * 构造函数，注入依赖。
     *
     * @param recommendService 推荐服务接口
     */
    @Autowired
    public HomeRecommendController(IRecommendService recommendService) {
        this.recommendService = recommendService;
    }

    /**
     * 个性化商品推荐。
     *
     * 基于当前登录用户的浏览、购买、收藏等行为，推荐个性化的商品列表。
     * 需要用户登录才能使用。
     *
     * @param userId 用户 ID（从请求属性中获取，由拦截器设置）
     * @param size   返回的商品数量，默认为 6
     * @param page   页码，从 0 开始，默认为 0
     * @return 推荐的商品列表
     */
    @GetMapping("/personal")
    public ResponseVo<List<Product>> personal(@RequestAttribute("id") Long userId,
                                              @RequestParam(value = "size", required = false, defaultValue = "6") Integer size,
                                              @RequestParam(value = "page", required = false, defaultValue = "0") Integer page) {
        return recommendService.recommendForUser(userId, size, page);
    }

    /**
     * 热销商品推荐。
     *
     * 返回按销量排序的热销商品列表，作为兜底推荐方案。
     * 该接口为公开接口，不需要用户登录。
     *
     * @param size 返回的商品数量，默认为 6
     * @param page 页码，从 0 开始，默认为 0
     * @return 热销商品列表，按销量从高到低排序
     */
    @GetMapping("/popular")
    public ResponseVo<List<Product>> popular(@RequestParam(value = "size", required = false, defaultValue = "6") Integer size,
                                             @RequestParam(value = "page", required = false, defaultValue = "0") Integer page) {
        return recommendService.popular(size, page);
    }
}


