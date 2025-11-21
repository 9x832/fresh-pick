package com.agrismart.agrimallbackend.controller.home;

import com.agrismart.agrimallbackend.dto.response.ResponseVo;
import com.agrismart.agrimallbackend.entity.common.Product;
import com.agrismart.agrimallbackend.entity.common.ProductCategory;
import com.agrismart.agrimallbackend.service.common.ICommentService;
import com.agrismart.agrimallbackend.service.common.IProductService;
import com.agrismart.agrimallbackend.mapper.common.ProductCategoryMapper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 前台商品浏览控制器。
 *
 * 该控制器提供前台商品浏览和查询功能接口，包括：
 *
 * - 商品列表查询（支持按分类和内容搜索）
 * - 热销商品排行
 * - 商品分类列表
 * - 商品详情查询（包含评论）
 *
 * 接口路径：{@code /api/home/products}
 *
 * 权限要求：所有接口都不需要用户登录，公开访问。
 *
 * @author agrimall
 * @see com.agrismart.agrimallbackend.service.common.IProductService
 * @see com.agrismart.agrimallbackend.service.common.ICommentService
 * @since 1.0
 */
@RestController
@RequestMapping("/api/home/products")
public class HomeProductController {

    /**
     * 商品服务接口。
     */
    private final IProductService productService;

    /**
     * 评论服务接口。
     */
    private final ICommentService commentService;

    /**
     * 商品分类数据访问接口。
     */
    private final ProductCategoryMapper productCategoryMapper;

    /**
     * 构造函数，注入依赖。
     *
     * @param productService         商品服务接口
     * @param commentService          评论服务接口
     * @param productCategoryMapper  商品分类数据访问接口
     */
    @Autowired
    public HomeProductController(IProductService productService,
                                 ICommentService commentService,
                                 ProductCategoryMapper productCategoryMapper) {
        this.productService = productService;
        this.commentService = commentService;
        this.productCategoryMapper = productCategoryMapper;
    }

    /**
     * 分页查询商品列表。
     *
     * 支持按商品分类和内容（商品名称、详情等）搜索。
     * 如果不提供分类 ID，则查询所有分类的商品。
     *
     * @param categoryId 商品分类 ID（可选），用于筛选指定分类的商品
     * @param content    搜索内容（可选），用于模糊搜索商品名称、详情等
     * @param pageNum    页码，从 1 开始，默认为 1
     * @param pageSize   每页大小，默认为 5
     * @return 分页信息，包含商品列表
     */
    @GetMapping
    public ResponseVo<PageInfo> list(@RequestParam(value = "categoryId", required = false) Long categoryId,
                                     @RequestParam(value = "content", required = false) String content,
                                     @RequestParam(defaultValue = "1") Integer pageNum,
                                     @RequestParam(defaultValue = "5") Integer pageSize) {
        if (categoryId == null) {
            return productService.getProductByPageAndContent(pageNum, pageSize, content);
        }
        return productService.getProductByPageAndCategoryIdAndContent(pageNum, pageSize, categoryId, content);
    }

    /**
     * 查询热销商品排行。
     *
     * 返回按销量排序的所有商品列表。
     *
     * @return 商品列表，按销量从高到低排序
     */
    @GetMapping("/top")
    public ResponseVo<List<Product>> top() {
        return ResponseVo.success(productService.selectBySellNumber());
    }

    /**
     * 查询热销商品排行榜（限制数量）。
     *
     * 返回按销量排序的前 N 个商品。
     *
     * @param size 返回的商品数量，默认为 20
     * @return 商品列表，按销量从高到低排序
     */
    @GetMapping("/hot-rank")
    public ResponseVo<List<Product>> hotRank(@RequestParam(value = "size", defaultValue = "20") Integer size) {
        int limit = size == null ? 20 : size;
        return ResponseVo.success(productService.selectBySellNumberLimit(limit));
    }

    /**
     * 查询所有商品分类列表。
     *
     * 返回系统中所有的商品分类，用于商品筛选和导航。
     *
     * @return 商品分类列表
     */
    @GetMapping("/categories")
    public ResponseVo<List<ProductCategory>> categories() {
        return ResponseVo.success(productCategoryMapper.selectAll());
    }

    /**
     * 查询商品详情。
     *
     * 根据商品 ID 查询商品的详细信息，包括商品基本信息、评论总数、销量排行和评论列表。
     *
     * @param id       商品 ID（路径变量）
     * @param pageNum  评论页码，从 1 开始，默认为 1
     * @param pageSize 评论每页大小，默认为 5
     * @return 包含商品详情和相关信息的响应对象
     *
     * - product：商品详细信息
     * - totalComment：评论总数
     * - sellRank：销量排行榜（所有商品）
     * - pageInfo：评论分页信息
     *
     */
    @GetMapping("/{id}")
    public ResponseVo<Map<String, Object>> detail(@PathVariable("id") Long id,
                                                  @RequestParam(defaultValue = "1") Integer pageNum,
                                                  @RequestParam(defaultValue = "5") Integer pageSize) {
        Map<String, Object> payload = new HashMap<>(4);
        payload.put("product", productService.selectByPrimaryKey(id));
        payload.put("totalComment", commentService.selectByProductId(id).size());
        payload.put("sellRank", productService.selectBySellNumber());
        payload.put("pageInfo", commentService.selectByProductIdAndPage(id, pageNum, pageSize).getData());
        return ResponseVo.success(payload);
    }
}

