package com.agrismart.agrimallbackend.dto.request;

import jakarta.validation.constraints.Min;

/**
 * 商品分页查询请求数据传输对象。
 *
 * 该 DTO 用于接收商品分页查询请求的参数，支持按分类和关键词搜索。
 * 使用 Jakarta Bean Validation 进行参数校验。
 *
 * 使用场景：
 *
 * - 在商品列表查询接口中接收分页和搜索参数
 *
 * @author agrimall
 * @since 1.0
 */
public class ProductPageRequest {

    /**
     * 页码。
     * 从 1 开始，默认值为 1。最小值为 1。
     */
    @Min(value = 1, message = "页码最小为1")
    private int pageNum = 1;

    /**
     * 每页大小。
     * 默认值为 10。最小值为 1。
     */
    @Min(value = 1, message = "分页大小最小为1")
    private int pageSize = 10;

    /**
     * 商品分类 ID。
     * 可选字段，用于筛选指定分类的商品。
     */
    private Long categoryId;

    /**
     * 搜索关键词。
     * 可选字段，用于模糊搜索商品名称、详情等。
     */
    private String keyword;

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
}

