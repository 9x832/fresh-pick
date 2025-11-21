package com.agrismart.agrimallbackend.service.home;

import com.agrismart.agrimallbackend.dto.response.ResponseVo;
import com.agrismart.agrimallbackend.entity.common.Product;

import java.util.List;

/**
 * 商品推荐服务接口。
 *
 * 该接口定义了商品推荐相关的业务操作方法，包括：
 *
 * - 个性化推荐（基于协同过滤算法）
 * - 热销推荐（基于销量）
 *
 * 推荐算法：
 *
 * - 个性化推荐：基于用户的购买和收藏行为，使用协同过滤算法计算商品相似度，推荐用户可能感兴趣的商品
 * - 热销推荐：按商品销量降序排序，返回热销商品列表
 * - 如果用户没有行为数据或推荐结果不足，则使用热销推荐作为兜底
 *
 * @author agrimall
 * @see com.agrismart.agrimallbackend.service.home.impl.RecommendServiceImpl
 * @see com.agrismart.agrimallbackend.entity.common.Product
 * @since 1.0
 */
public interface IRecommendService {

    /**
     * 获取为指定用户生成的个性化推荐商品列表。
     * 基于协同过滤算法，根据用户的购买和收藏行为推荐商品。
     * 如果用户没有行为数据或推荐结果不足，则返回热销推荐作为兜底。
     *
     * @param userId 用户 ID（如果为 null，则返回热销推荐）
     * @param limit  推荐条数（如果小于等于 0，则使用默认值 6）
     * @param page   页码（从 0 开始，用于分页推荐）
     * @return 推荐商品列表
     */
    ResponseVo<List<Product>> recommendForUser(Long userId, int limit, int page);

    /**
     * 获取热销商品列表，作为兜底推荐。
     * 按商品销量降序排序，返回热销商品列表。
     *
     * @param limit 推荐条数（如果小于等于 0，则使用默认值 6）
     * @param page  页码（从 0 开始，用于分页推荐）
     * @return 热销商品列表
     */
    ResponseVo<List<Product>> popular(int limit, int page);
}


