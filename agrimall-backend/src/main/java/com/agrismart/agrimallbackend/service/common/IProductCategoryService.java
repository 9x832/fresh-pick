package com.agrismart.agrimallbackend.service.common;

import com.github.pagehelper.PageInfo;
import com.agrismart.agrimallbackend.dto.response.ResponseVo;
import com.agrismart.agrimallbackend.entity.common.ProductCategory;

/**
 * 商品分类服务接口。
 *
 * 该接口定义了商品分类相关的业务操作方法，包括：
 *
 * - 商品分类列表查询（支持分页和按名称搜索）
 * - 商品分类管理（添加、编辑、删除）
 *
 * @author agrimall
 * @see com.agrismart.agrimallbackend.service.common.impl.ProductCategoryServiceImpl
 * @see com.agrismart.agrimallbackend.entity.common.ProductCategory
 * @since 1.0
 */
public interface IProductCategoryService {

    /**
     * 分页查询商品分类列表。
     *
     * @param pageNum  页码，从 1 开始
     * @param pageSize 每页大小
     * @return 包含分页信息的商品分类列表
     */
    ResponseVo<PageInfo> getProductCategoryByPage(Integer pageNum, Integer pageSize);

    /**
     * 按内容分页查询商品分类列表。
     *
     * @param pageNum  页码，从 1 开始
     * @param pageSize 每页大小
     * @param content  搜索内容（支持模糊搜索）
     * @return 包含分页信息的商品分类列表
     */
    ResponseVo<PageInfo> getProductCategoryByPageAndContent(Integer pageNum, Integer pageSize, String content);

    /**
     * 添加商品分类。
     *
     * @param productCategory 商品分类对象
     * @return 操作结果
     */
    ResponseVo<Boolean> add(ProductCategory productCategory);

    /**
     * 编辑商品分类信息。
     *
     * @param productCategory 商品分类对象（包含 ID）
     * @return 操作结果
     */
    ResponseVo<Boolean> edit(ProductCategory productCategory);

    /**
     * 删除商品分类。
     *
     * @param id 商品分类 ID
     * @return 操作结果
     */
    ResponseVo<Boolean> delete(Long id);
}

