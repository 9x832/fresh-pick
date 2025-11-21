package com.agrismart.agrimallbackend.controller.admin;

import com.agrismart.agrimallbackend.dto.response.ResponseVo;
import com.agrismart.agrimallbackend.entity.common.ProductCategory;
import com.agrismart.agrimallbackend.service.common.IProductCategoryService;
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

/**
 * 商品分类管理控制器。
 *
 * 该控制器提供后台管理系统对商品分类的管理接口，包括：
 *
 * - 商品分类列表查询（支持分页和搜索）
 * - 商品分类创建
 * - 商品分类编辑
 * - 商品分类删除
 *
 * 接口路径：{@code /api/admin/product-categories}
 *
 * 权限要求：
 *
 * - 需要管理员登录（通过 {@link com.agrismart.agrimallbackend.interceptor.AdminInterceptor} 验证）
 * - 需要相应的菜单权限
 *
 * @author agrimall
 * @see com.agrismart.agrimallbackend.service.common.IProductCategoryService
 * @see com.agrismart.agrimallbackend.entity.common.ProductCategory
 * @since 1.0
 */
@RestController
@RequestMapping("/api/admin/product-categories")
public class ProductCategoryManageController {

    /**
     * 商品分类服务接口。
     */
    private final IProductCategoryService productCategoryService;

    /**
     * 构造函数，注入依赖。
     *
     * @param productCategoryService 商品分类服务接口
     */
    @Autowired
    public ProductCategoryManageController(IProductCategoryService productCategoryService) {
        this.productCategoryService = productCategoryService;
    }

    /**
     * 分页查询商品分类列表。
     *
     * 支持按内容（分类名称等）搜索，如果不提供搜索内容则查询所有分类。
     *
     * @param pageNum  页码，从 1 开始，默认为 1
     * @param pageSize 每页大小，默认为 5
     * @param content  搜索内容（可选），用于模糊搜索分类名称等
     * @return 分页信息，包含商品分类列表
     */
    @GetMapping
    public ResponseVo<PageInfo> list(@RequestParam(defaultValue = "1") Integer pageNum,
                                     @RequestParam(defaultValue = "5") Integer pageSize,
                                     @RequestParam(required = false) String content) {
        return (content == null || content.trim().isEmpty())
                ? productCategoryService.getProductCategoryByPage(pageNum, pageSize)
                : productCategoryService.getProductCategoryByPageAndContent(pageNum, pageSize, content);
    }

    /**
     * 创建商品分类。
     *
     * 创建新的商品分类，需要提供分类的基本信息（名称、图标等）。
     *
     * @param category 商品分类实体对象，包含分类的基本信息
     * @return 操作结果，true 表示创建成功
     */
    @PostMapping
    public ResponseVo<Boolean> create(@RequestBody ProductCategory category) {
        return productCategoryService.add(category);
    }

    /**
     * 更新商品分类信息。
     *
     * 更新指定 ID 的商品分类信息。
     *
     * @param id       商品分类 ID（路径变量）
     * @param category 商品分类实体对象，包含要更新的信息
     * @return 操作结果，true 表示更新成功
     */
    @PutMapping("/{id}")
    public ResponseVo<Boolean> update(@PathVariable("id") Long id,
                                      @RequestBody ProductCategory category) {
        category.setId(id);
        return productCategoryService.edit(category);
    }

    /**
     * 删除商品分类。
     *
     * 根据商品分类 ID 删除指定的商品分类。
     *
     * @param id 商品分类 ID（路径变量）
     * @return 操作结果，true 表示删除成功
     */
    @DeleteMapping("/{id}")
    public ResponseVo<Boolean> delete(@PathVariable("id") Long id) {
        return productCategoryService.delete(id);
    }
}

