package com.agrismart.agrimallbackend.service.home;

import com.github.pagehelper.PageInfo;
import com.agrismart.agrimallbackend.dto.response.ResponseVo;
import com.agrismart.agrimallbackend.entity.home.Collect;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 商品收藏服务接口。
 *
 * 该接口定义了用户商品收藏相关的业务操作方法，包括：
 *
 * - 添加商品收藏
 * - 取消商品收藏
 * - 收藏列表查询（支持分页）
 *
 * 注意：
 *
 * - 同一商品只能收藏一次，重复收藏会返回错误
 * - 收藏数据用于个性化推荐算法
 *
 * @author agrimall
 * @see com.agrismart.agrimallbackend.service.home.impl.CollectServiceImpl
 * @see com.agrismart.agrimallbackend.entity.home.Collect
 * @since 1.0
 */
public interface ICollectService {

    /**
     * 根据用户 ID 查询收藏列表。
     * 返回该用户的所有收藏记录（不分页）。
     *
     * @param userId 用户 ID
     * @return 收藏列表
     */
    List<Collect> findCollectByUserId(Long userId);

    /**
     * 添加商品收藏。
     * 同一商品只能收藏一次，重复收藏会返回错误。
     *
     * @param id      商品 ID
     * @param request HTTP 请求对象（用于获取当前登录用户 ID）
     * @return 操作结果
     */
    ResponseVo<Boolean> add(Long id, HttpServletRequest request);

    /**
     * 取消商品收藏。
     *
     * @param id 收藏 ID
     * @return 操作结果
     */
    ResponseVo<Boolean> delete(Long id);

    /**
     * 按用户 ID 分页查询收藏列表。
     * 用于前台用户查看自己的收藏。
     *
     * @param pageNum  页码，从 1 开始
     * @param pageSize 每页大小
     * @param userId   用户 ID
     * @return 包含分页信息的收藏列表
     */
    ResponseVo<PageInfo> getCollectByPageAndUserId(Integer pageNum, Integer pageSize, Long userId);
}

