package com.agrismart.agrimallbackend.service.common;

import com.github.pagehelper.PageInfo;
import com.agrismart.agrimallbackend.dto.response.ResponseVo;
import com.agrismart.agrimallbackend.entity.common.Product;

import java.util.List;

/**
 * 商品服务接口。
 *
 * 该接口定义了商品相关的业务操作方法，包括：
 *
 * - 商品列表查询（支持分页、按分类和内容搜索）
 * - 商品管理（添加、编辑、删除）
 * - 商品详情查询
 * - 热销商品查询（按销量排序）
 *
 * @author agrimall
 * @see com.agrismart.agrimallbackend.service.common.impl.ProductServiceImpl
 * @see com.agrismart.agrimallbackend.entity.common.Product
 * @since 1.0
 */
public interface IProductService {

    /**
     * 分页查询商品列表。
     * 用于后台管理系统。
     *
     * @param pageNum  页码，从 1 开始
     * @param pageSize 每页大小
     * @return 包含分页信息的商品列表
     */
    ResponseVo<PageInfo> getProductByPage(Integer pageNum, Integer pageSize);

    /**
     * 按内容分页查询商品列表。
     * 用于后台管理系统，支持按商品名称、详情等搜索。
     *
     * @param pageNum  页码，从 1 开始
     * @param pageSize 每页大小
     * @param content  搜索内容（支持模糊搜索）
     * @return 包含分页信息的商品列表
     */
    ResponseVo<PageInfo> getProductByPageAndContent(Integer pageNum, Integer pageSize, String content);

    /**
     * 添加商品。
     *
     * @param product 商品对象
     * @return 操作结果
     */
    ResponseVo<Boolean> add(Product product);

    /**
     * 编辑商品信息。
     *
     * @param product 商品对象（包含 ID）
     * @return 操作结果
     */
    ResponseVo<Boolean> edit(Product product);

    /**
     * 删除商品。
     *
     * @param id 商品 ID
     * @return 操作结果
     */
    ResponseVo<Boolean> delete(Long id);

    /**
     * 按分类和内容分页查询商品列表。
     * 用于前台商品浏览，支持按分类筛选和关键词搜索。
     *
     * @param pageNum    页码，从 1 开始
     * @param pageSize   每页大小
     * @param categoryId 商品分类 ID（可选，为 null 时查询所有分类）
     * @param content    搜索内容（可选，支持模糊搜索）
     * @return 包含分页信息的商品列表
     */
    ResponseVo<PageInfo> getProductByPageAndCategoryIdAndContent(Integer pageNum, Integer pageSize, Long categoryId, String content);

    /**
     * 根据主键查询商品。
     *
     * @param id 商品 ID
     * @return 商品对象，如果不存在则返回 null
     */
    Product selectByPrimaryKey(Long id);

    /**
     * 按销量查询商品列表。
     * 返回所有商品，按销量降序排序。
     *
     * @return 商品列表（按销量降序）
     */
    List<Product> selectBySellNumber();

    /**
     * 按销量获取前 N 个商品。
     * 用于热销商品推荐。
     *
     * @param limit 限制数量（如果小于等于 0，则默认返回 5 个）
     * @return 商品列表（按销量降序，最多返回 limit 个）
     */
    List<Product> selectBySellNumberLimit(int limit);
}

