package com.agrismart.agrimallbackend.controller.admin;

import com.agrismart.agrimallbackend.common.util.StringUtil;
import com.agrismart.agrimallbackend.dto.response.ResponseVo;
import com.agrismart.agrimallbackend.entity.common.Product;
import com.agrismart.agrimallbackend.entity.common.ProductCategory;
import com.agrismart.agrimallbackend.mapper.common.ProductCategoryMapper;
import com.agrismart.agrimallbackend.mapper.common.ProductMapper;
import com.agrismart.agrimallbackend.service.common.IProductService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 商品管理控制器。
 *
 * 该控制器提供后台管理系统对商品的管理接口，包括：
 *
 * - 商品列表查询（支持分页和搜索）
 * - 商品详情查询
 * - 商品创建
 * - 商品编辑
 * - 商品删除
 * - 商品分类列表查询
 *
 * 接口路径：{@code /api/admin/products}
 *
 * 权限要求：
 *
 * - 需要管理员登录（通过 {@link com.agrismart.agrimallbackend.interceptor.AdminInterceptor} 验证）
 * - 需要相应的菜单权限
 *
 * @author agrimall
 * @see com.agrismart.agrimallbackend.service.common.IProductService
 * @see com.agrismart.agrimallbackend.entity.common.Product
 * @since 1.0
 */
@RestController
@RequestMapping("/api/admin/products")
public class ProductManageController {

    /**
     * 商品服务接口。
     */
    private final IProductService productService;

    /**
     * 商品数据访问接口。
     */
    private final ProductMapper productMapper;

    /**
     * 商品分类数据访问接口。
     */
    private final ProductCategoryMapper productCategoryMapper;

    /**
     * 构造函数，注入依赖。
     *
     * @param productService         商品服务接口
     * @param productMapper          商品数据访问接口
     * @param productCategoryMapper 商品分类数据访问接口
     */
    @Autowired
    public ProductManageController(IProductService productService,
                                   ProductMapper productMapper,
                                   ProductCategoryMapper productCategoryMapper) {
        this.productService = productService;
        this.productMapper = productMapper;
        this.productCategoryMapper = productCategoryMapper;
    }

    /**
     * 分页查询商品列表。
     *
     * 支持按内容（商品名称、详情等）搜索，如果不提供搜索内容则查询所有商品。
     * 返回结果包含分页信息和所有商品分类列表（用于下拉选择）。
     *
     * @param pageNum  页码，从 1 开始，默认为 1
     * @param pageSize 每页大小，默认为 5
     * @param content  搜索内容（可选），用于模糊搜索商品名称、详情等
     * @return 包含分页信息和商品分类列表的响应对象
     *
     * - pageInfo：分页信息，包含商品列表
     * - categories：所有商品分类列表，用于创建/编辑商品时选择分类
     *
     */
    @GetMapping
    public ResponseVo<Map<String, Object>> list(@RequestParam(defaultValue = "1") Integer pageNum,
                                                @RequestParam(defaultValue = "5") Integer pageSize,
                                                @RequestParam(required = false) String content) {
        ResponseVo<PageInfo> pageInfo = (StringUtil.isEmpty(content))
                ? productService.getProductByPage(pageNum, pageSize)
                : productService.getProductByPageAndContent(pageNum, pageSize, content);
        Map<String, Object> payload = new HashMap<>(2);
        payload.put("pageInfo", pageInfo.getData());
        payload.put("categories", productCategoryMapper.selectAll());
        return ResponseVo.success(payload);
    }

    /**
     * 查询商品详情。
     *
     * 根据商品 ID 查询商品的详细信息。
     *
     * @param id 商品 ID（路径变量）
     * @return 商品实体对象
     */
    @GetMapping("/{id}")
    public ResponseVo<Product> detail(@PathVariable("id") Long id) {
        return ResponseVo.success(productMapper.selectByPrimaryKey(id));
    }

    /**
     * 创建商品。
     *
     * 创建新的商品，需要提供商品的基本信息（名称、价格、库存、分类等）。
     *
     * @param product 商品实体对象，包含商品的基本信息
     * @return 操作结果，true 表示创建成功
     */
    @PostMapping
    public ResponseVo<Boolean> create(@RequestBody Product product) {
        return productService.add(product);
    }

    /**
     * 更新商品信息。
     *
     * 更新指定 ID 的商品信息。
     *
     * @param id      商品 ID（路径变量）
     * @param product 商品实体对象，包含要更新的信息
     * @return 操作结果，true 表示更新成功
     */
    @PutMapping("/{id}")
    public ResponseVo<Boolean> update(@PathVariable("id") Long id,
                                      @RequestBody Product product) {
        product.setId(id);
        return productService.edit(product);
    }

    /**
     * 删除商品。
     *
     * 根据商品 ID 删除指定的商品。
     *
     * @param id 商品 ID（路径变量）
     * @return 操作结果，true 表示删除成功
     */
    @DeleteMapping("/{id}")
    public ResponseVo<Boolean> delete(@PathVariable("id") Long id) {
        return productService.delete(id);
    }

    /**
     * 查询所有商品分类列表。
     *
     * 返回系统中所有的商品分类，用于商品创建和编辑时的分类选择。
     *
     * @return 商品分类列表
     */
    @GetMapping("/categories")
    public ResponseVo<List<ProductCategory>> categories() {
        return ResponseVo.success(productCategoryMapper.selectAll());
    }
}

