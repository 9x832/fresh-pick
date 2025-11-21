package com.agrismart.agrimallbackend.dto.response;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * 分页响应数据传输对象。
 *
 * 该 DTO 用于封装分页查询的响应数据，包含总记录数、当前页码、每页大小和数据列表。
 * 使用泛型支持不同类型的数据列表。
 *
 * 使用场景：
 *
 * - 在分页查询接口中返回分页数据
 *
 * @param <T> 数据列表的元素类型
 * @author agrimall
 * @since 1.0
 */
public class PageResponse<T> implements Serializable {

    /**
     * 总记录数。
     */
    private long total;

    /**
     * 当前页码。
     * 从 1 开始。
     */
    private int pageNum;

    /**
     * 每页大小。
     */
    private int pageSize;

    /**
     * 数据列表。
     * 当前页的数据记录。
     */
    private List<T> list;

    /**
     * 无参构造函数。
     * 初始化数据列表为空列表。
     */
    public PageResponse() {
        this.list = Collections.emptyList();
    }

    /**
     * 全参构造函数。
     *
     * @param total    总记录数
     * @param pageNum  当前页码
     * @param pageSize 每页大小
     * @param list     数据列表
     */
    public PageResponse(long total, int pageNum, int pageSize, List<T> list) {
        this.total = total;
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.list = list;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

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

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }
}

