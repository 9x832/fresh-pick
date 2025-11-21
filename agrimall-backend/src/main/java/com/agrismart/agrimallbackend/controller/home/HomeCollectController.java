package com.agrismart.agrimallbackend.controller.home;

import com.agrismart.agrimallbackend.dto.response.ResponseVo;
import com.agrismart.agrimallbackend.service.home.ICollectService;
import com.github.pagehelper.PageInfo;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 前台商品收藏管理控制器。
 *
 * 该控制器提供前台用户商品收藏的管理接口，包括：
 *
 * - 收藏列表查询（支持分页）
 * - 添加商品收藏
 * - 取消商品收藏
 *
 * 接口路径：{@code /api/home/collect}
 *
 * 权限要求：所有接口都需要用户登录（通过 {@link com.agrismart.agrimallbackend.interceptor.JwtInterceptor} 验证）。
 *
 * @author agrimall
 * @see com.agrismart.agrimallbackend.service.home.ICollectService
 * @since 1.0
 */
@RestController
@RequestMapping("/api/home/collect")
public class HomeCollectController {

    /**
     * 收藏服务接口。
     */
    private final ICollectService collectService;

    /**
     * 构造函数，注入依赖。
     *
     * @param collectService 收藏服务接口
     */
    @Autowired
    public HomeCollectController(ICollectService collectService) {
        this.collectService = collectService;
    }

    /**
     * 分页查询当前用户的收藏列表。
     *
     * 返回当前登录用户收藏的商品列表。
     *
     * @param uid      用户 ID（从请求属性中获取，由拦截器设置）
     * @param pageNum  页码，从 1 开始，默认为 1
     * @param pageSize 每页大小，默认为 4
     * @return 分页信息，包含收藏列表
     */
    @GetMapping
    public ResponseVo<PageInfo> list(@RequestAttribute("id") Long uid,
                                     @RequestParam(defaultValue = "1") Integer pageNum,
                                     @RequestParam(defaultValue = "4") Integer pageSize) {
        return collectService.getCollectByPageAndUserId(pageNum, pageSize, uid);
    }

    /**
     * 添加商品收藏。
     *
     * 将指定商品添加到当前登录用户的收藏列表中。
     *
     * @param productId 商品 ID（路径变量）
     * @param request    HTTP 请求对象，用于获取当前登录用户信息
     * @return 操作结果，true 表示添加成功
     */
    @PostMapping("/{productId}")
    public ResponseVo<Boolean> add(@PathVariable("productId") Long productId, HttpServletRequest request) {
        return collectService.add(productId, request);
    }

    /**
     * 取消商品收藏。
     *
     * 根据收藏 ID 删除指定的收藏记录。
     *
     * @param collectId 收藏 ID（路径变量）
     * @return 操作结果，true 表示取消成功
     */
    @DeleteMapping("/{collectId}")
    public ResponseVo<Boolean> delete(@PathVariable("collectId") Long collectId) {
        return collectService.delete(collectId);
    }
}

