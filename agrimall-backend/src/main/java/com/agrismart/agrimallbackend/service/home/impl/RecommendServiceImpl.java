package com.agrismart.agrimallbackend.service.home.impl;

import com.agrismart.agrimallbackend.dto.response.ResponseVo;
import com.agrismart.agrimallbackend.entity.common.Order;
import com.agrismart.agrimallbackend.entity.common.OrderItem;
import com.agrismart.agrimallbackend.entity.common.Product;
import com.agrismart.agrimallbackend.entity.home.Collect;
import com.agrismart.agrimallbackend.mapper.common.OrderItemMapper;
import com.agrismart.agrimallbackend.mapper.common.OrderMapper;
import com.agrismart.agrimallbackend.mapper.common.ProductMapper;
import com.agrismart.agrimallbackend.mapper.home.CollectMapper;
import com.agrismart.agrimallbackend.service.home.IRecommendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 商品推荐服务实现类（基于协同过滤算法）。
 *
 * 该类实现了 {@link IRecommendService} 接口，提供商品推荐相关的业务逻辑实现。
 * 使用基于物品的协同过滤（Item-Based Collaborative Filtering）算法进行个性化推荐。
 *
 * 推荐算法原理：
 *
 * - 用户偏好计算：根据用户的购买行为（权重 1.0）和收藏行为（权重 0.5）计算用户对商品的偏好分数
 * - 共现矩阵构建：统计商品之间的共现次数，构建商品共现矩阵
 * - 相似度计算：使用余弦相似度计算商品之间的相似度（考虑商品频率）
 * - 推荐分数计算：根据用户对已购买/收藏商品的偏好和商品相似度，计算推荐分数
 * - 兜底策略：如果用户没有行为数据或推荐结果不足，则使用热销推荐作为兜底
 *
 * 推荐流程：
 *
 * - 构建推荐上下文（用户偏好、商品频率、共现矩阵）
 * - 计算候选商品的推荐分数
 * - 按分数排序，返回 Top N 商品
 * - 如果结果不足，使用热销推荐补充
 *
 * @author agrimall
 * @see com.agrismart.agrimallbackend.service.home.IRecommendService
 * @since 1.0
 */
@Service
public class RecommendServiceImpl implements IRecommendService {

    /**
     * 购买行为权重。
     * 用于计算用户偏好，购买行为的权重为 1.0。
     */
    private static final double PURCHASE_WEIGHT = 1.0D;

    /**
     * 收藏行为权重。
     * 用于计算用户偏好，收藏行为的权重为 0.5。
     */
    private static final double COLLECT_WEIGHT = 0.5D;

    /**
     * 默认推荐条数。
     * 当 limit 参数小于等于 0 时使用此默认值。
     */
    private static final int DEFAULT_LIMIT = 6;

    /**
     * 订单数据访问对象。
     * 用于查询用户的购买行为。
     */
    private final OrderMapper orderMapper;

    /**
     * 订单项数据访问对象。
     * 用于查询订单中的商品信息。
     */
    private final OrderItemMapper orderItemMapper;

    /**
     * 收藏数据访问对象。
     * 用于查询用户的收藏行为。
     */
    private final CollectMapper collectMapper;

    /**
     * 商品数据访问对象。
     * 用于查询商品信息和热销商品。
     */
    private final ProductMapper productMapper;

    /**
     * 构造函数，注入依赖。
     *
     * @param orderMapper      订单数据访问对象
     * @param orderItemMapper  订单项数据访问对象
     * @param collectMapper    收藏数据访问对象
     * @param productMapper    商品数据访问对象
     */
    @Autowired
    public RecommendServiceImpl(OrderMapper orderMapper,
                                OrderItemMapper orderItemMapper,
                                CollectMapper collectMapper,
                                ProductMapper productMapper) {
        this.orderMapper = orderMapper;
        this.orderItemMapper = orderItemMapper;
        this.collectMapper = collectMapper;
        this.productMapper = productMapper;
    }

    /**
     * 为用户推荐商品（基于物品的协同过滤算法）。
     *
     * 算法流程：
     *
     * - 构建推荐上下文（用户偏好、商品频率、共现矩阵）
     * - 遍历用户已购买/收藏的商品，找到与这些商品相似的其他商品
     * - 使用余弦相似度计算商品相似度
     * - 根据用户偏好和相似度计算推荐分数
     * - 按分数排序，返回 Top N 商品
     * - 如果结果不足，使用热销推荐补充
     *
     * @param userId 用户ID
     * @param limit  推荐数量
     * @param page   页码（用于分页）
     * @return 推荐商品列表
     */
    @Override
    public ResponseVo<List<Product>> recommendForUser(Long userId, int limit, int page) {
        // 1. 参数校验和初始化
        int effectiveLimit = limit > 0 ? limit : DEFAULT_LIMIT;
        int safePage = Math.max(page, 0);
        
        // 如果用户ID为空，直接返回热销推荐
        if (userId == null) {
            return popular(effectiveLimit, safePage);
        }
        
        // 2. 构建推荐上下文（包含用户偏好、商品频率、共现矩阵）
        RecommendContext context = buildContext();
        
        // 3. 获取目标用户的偏好数据（用户对各个商品的偏好分数）
        Map<Long, Double> targetPreference = context.userPreference.get(userId);
        
        // 如果用户没有行为数据（未购买、未收藏），返回热销推荐
        if (CollectionUtils.isEmpty(targetPreference)) {
            return popular(effectiveLimit, safePage);
        }

        // 4. 计算推荐分数
        // scoreMap: 候选商品ID -> 推荐分数
        Map<Long, Double> scoreMap = new HashMap<>();
        
        // 获取用户已拥有（已购买/收藏）的商品ID集合，避免推荐已拥有的商品
        Set<Long> ownedProducts = targetPreference.keySet();
        
        // 遍历用户已购买/收藏的每个商品
        for (Map.Entry<Long, Double> entry : targetPreference.entrySet()) {
            Long itemId = entry.getKey();           // 用户已拥有的商品ID
            Double preference = entry.getValue();   // 用户对该商品的偏好分数
            
            // 获取与该商品共现的其他商品（共现矩阵中的邻居）
            Map<Long, Double> neighbours = context.coMatrix.get(itemId);
            if (CollectionUtils.isEmpty(neighbours)) {
                continue;  // 如果没有共现商品，跳过
            }
            
            // 获取商品A被购买的频率（用于相似度计算）
            Integer freqA = context.itemFrequency.get(itemId);
            if (freqA == null || freqA == 0) {
                continue;  // 如果频率为0，跳过
            }
            
            // 遍历与该商品共现的所有候选商品
            for (Map.Entry<Long, Double> neighbour : neighbours.entrySet()) {
                Long candidateId = neighbour.getKey();  // 候选商品ID
                
                // 跳过用户已拥有的商品
                if (ownedProducts.contains(candidateId)) {
                    continue;
                }
                
                // 获取候选商品B被购买的频率
                Integer freqB = context.itemFrequency.get(candidateId);
                if (freqB == null || freqB == 0) {
                    continue;  // 如果频率为0，跳过
                }
                
                // 5. 计算商品相似度（余弦相似度变种）
                // 相似度公式：sim(A, B) = coMatrix[A][B] / sqrt(freqA × freqB)
                // coMatrix[A][B]: 商品A和B的共现次数
                // freqA: 商品A被购买的次数
                // freqB: 商品B被购买的次数
                // sqrt(freqA × freqB): 归一化因子，避免热门商品主导推荐结果
                double similarity = neighbour.getValue() / (Math.sqrt(freqA * freqB));
                
                // 如果相似度小于等于0，跳过
                if (similarity <= 0) {
                    continue;
                }
                
                // 6. 计算推荐分数
                // 推荐分数 = 用户对商品A的偏好 × 商品A与候选商品B的相似度
                // 分数越高，说明用户越可能喜欢该候选商品
                double score = similarity * preference;
                
                // 累加推荐分数（同一个候选商品可能从多个已拥有商品得到推荐分数）
                scoreMap.merge(candidateId, score, Double::sum);
            }
        }

        // 7. 如果没有任何推荐结果，返回热销推荐作为兜底
        if (scoreMap.isEmpty()) {
            return popular(effectiveLimit, safePage);
        }

        // 8. 按推荐分数降序排序，取 Top N
        List<Long> orderedIds = scoreMap.entrySet().stream()
            .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))  // 按分数降序排序
            .map(Map.Entry::getKey)  // 提取商品ID
            .limit(effectiveLimit)   // 限制数量
            .collect(Collectors.toList());

        // 9. 分页处理（旋转列表，实现分页效果）
        List<Long> rotatedIds = rotateByPage(orderedIds, effectiveLimit, safePage);
        
        // 10. 根据商品ID列表查询商品详细信息
        List<Product> recommendProducts = fetchOrderedProducts(rotatedIds);
        
        // 11. 如果推荐结果不足，使用热销推荐补充
        if (recommendProducts.size() < effectiveLimit) {
            // 获取已推荐的商品ID集合，避免重复
            Set<Long> existed = recommendProducts.stream()
                .map(Product::getId)
                .collect(Collectors.toCollection(LinkedHashSet::new));
            
            // 获取热销商品作为兜底
            List<Product> fallback = fetchPopularProducts(effectiveLimit, safePage);
            
            // 补充推荐结果，直到达到指定数量
            for (Product product : fallback) {
                if (!existed.contains(product.getId())) {
                    recommendProducts.add(product);
                }
                if (recommendProducts.size() >= effectiveLimit) {
                    break;
                }
            }
        }
        
        return ResponseVo.success(recommendProducts);
    }

    @Override
    public ResponseVo<List<Product>> popular(int limit, int page) {
        int effectiveLimit = limit > 0 ? limit : DEFAULT_LIMIT;
        int safePage = Math.max(page, 0);
        List<Product> hotList = fetchPopularProducts(effectiveLimit, safePage);
        return ResponseVo.success(hotList);
    }

    /**
     * 构建推荐上下文（用户偏好、商品频率、共现矩阵）。
     *
     * 这是协同过滤算法的核心数据准备阶段，包括：
     *
     * - 用户偏好计算：根据购买和收藏行为计算用户对商品的偏好分数
     * - 商品频率统计：统计每个商品被购买/收藏的总次数
     * - 共现矩阵构建：统计商品之间的共现次数（同一用户购买/收藏的商品对）
     *
     * @return 推荐上下文对象
     */
    private RecommendContext buildContext() {
        // ========== 第一步：获取订单数据 ==========
        List<Order> orders = orderMapper.selectAll();
        if (CollectionUtils.isEmpty(orders)) {
            return RecommendContext.empty();
        }

        // 构建订单ID到用户ID的映射，方便后续查询
        Map<Long, Long> orderUserMapping = new HashMap<>(orders.size());
        List<Long> orderIds = new ArrayList<>(orders.size());
        for (Order order : orders) {
            orderIds.add(order.getId());
            orderUserMapping.put(order.getId(), order.getUserId());
        }

        // 批量查询订单项（订单中的商品信息）
        List<OrderItem> orderItems = orderIds.isEmpty()
            ? Collections.emptyList()
            : orderItemMapper.selectByOrderIds(orderIds);

        // ========== 第二步：计算用户偏好 ==========
        // userPreference[userId][productId] = 偏好分数
        // 偏好分数 = 购买次数 × 1.0 + 收藏次数 × 0.5
        Map<Long, Map<Long, Double>> userPreference = new HashMap<>();
        
        // 处理购买行为：每次购买增加 1.0 的偏好分数
        for (OrderItem item : orderItems) {
            Long userId = orderUserMapping.get(item.getOrderId());
            if (userId == null || item.getProductId() == null) {
                continue;
            }
            // 累加购买行为的偏好分数
            userPreference
                .computeIfAbsent(userId, key -> new HashMap<>())
                .merge(item.getProductId(), PURCHASE_WEIGHT, Double::sum);
        }

        // 处理收藏行为：每次收藏增加 0.5 的偏好分数
        List<Collect> collects = collectMapper.selectAllSimple();
        for (Collect collect : collects) {
            if (collect.getUserId() == null || collect.getProductId() == null) {
                continue;
            }
            // 累加收藏行为的偏好分数（权重较低）
            userPreference
                .computeIfAbsent(collect.getUserId(), key -> new HashMap<>())
                .merge(collect.getProductId(), COLLECT_WEIGHT, Double::sum);
        }

        // 如果没有任何用户偏好数据，返回空上下文
        if (userPreference.isEmpty()) {
            return RecommendContext.empty();
        }

        // ========== 第三步：统计商品频率和构建共现矩阵 ==========
        // itemFrequency[productId] = 该商品被购买/收藏的总次数
        Map<Long, Integer> itemFrequency = new HashMap<>();
        
        // coMatrix[productA][productB] = 商品A和B的共现次数
        // 共现：指同一用户同时购买/收藏了商品A和B
        Map<Long, Map<Long, Double>> coMatrix = new HashMap<>();

        // 遍历每个用户的偏好数据
        for (Map.Entry<Long, Map<Long, Double>> entry : userPreference.entrySet()) {
            // 获取该用户购买/收藏的所有商品ID
            Set<Long> items = entry.getValue().keySet();
            if (items.isEmpty()) {
                continue;
            }
            
            // 统计商品频率：每个商品被该用户购买/收藏，频率+1
            for (Long itemId : items) {
                itemFrequency.merge(itemId, 1, Integer::sum);
            }
            
            // 构建共现矩阵：统计商品对之间的共现次数
            // 如果用户同时拥有商品A和B，则 coMatrix[A][B] += 1
            List<Long> itemList = new ArrayList<>(items);
            for (int i = 0; i < itemList.size(); i++) {
                Long itemA = itemList.get(i);
                for (int j = 0; j < itemList.size(); j++) {
                    if (i == j) {
                        continue;  // 跳过自己与自己
                    }
                    Long itemB = itemList.get(j);
                    // 累加商品A和B的共现次数
                    coMatrix
                        .computeIfAbsent(itemA, key -> new HashMap<>())
                        .merge(itemB, 1.0D, Double::sum);
                }
            }
        }

        // 返回构建好的推荐上下文
        return new RecommendContext(userPreference, itemFrequency, coMatrix);
    }

    /**
     * 对推荐结果进行分页旋转。
     *
     * 通过旋转列表实现分页效果，每次请求返回不同的商品，避免总是返回相同的推荐结果。
     *
     * @param orderedIds 已排序的商品ID列表
     * @param limit      每页数量
     * @param page       页码
     * @return 分页后的商品ID列表
     */
    private List<Long> rotateByPage(List<Long> orderedIds, int limit, int page) {
        if (CollectionUtils.isEmpty(orderedIds)) {
            return Collections.emptyList();
        }
        List<Long> rotated = new ArrayList<>(orderedIds);
        int size = rotated.size();
        
        // 如果列表长度小于等于每页数量，直接返回全部
        if (size <= limit) {
            return rotated;
        }
        
        // 计算旋转偏移量：offset = (页码 × 每页数量) % 列表长度
        // 例如：列表10个商品，每页6个，第1页offset=0，第2页offset=6，第3页offset=2（循环）
        int offset = (page * limit) % size;
        
        // 如果偏移量大于0，向左旋转列表
        if (offset > 0) {
            Collections.rotate(rotated, -offset);
        }
        
        // 返回前limit个元素
        return rotated.subList(0, Math.min(limit, rotated.size()));
    }

    private List<Product> fetchPopularProducts(int limit, int page) {
        List<Product> popular = productMapper.selectBySellNumberLimit(Math.max(limit * 3, limit));
        if (CollectionUtils.isEmpty(popular)) {
            return popular;
        }
        List<Product> rotated = new ArrayList<>(popular);
        if (rotated.size() > limit) {
            int offset = (page * limit) % rotated.size();
            if (offset > 0) {
                Collections.rotate(rotated, -offset);
            }
        }
        return rotated.subList(0, Math.min(limit, rotated.size()));
    }

    /**
     * 根据商品ID列表查询商品详细信息，并保持原有顺序。
     *
     * 由于数据库查询可能改变顺序，需要根据原始ID列表重新排序。
     *
     * @param productIds 商品ID列表（已按推荐分数排序）
     * @return 商品列表（保持原有顺序）
     */
    private List<Product> fetchOrderedProducts(List<Long> productIds) {
        if (CollectionUtils.isEmpty(productIds)) {
            return new ArrayList<>();
        }
        
        // 去重，保持插入顺序
        Set<Long> uniqueIds = new LinkedHashSet<>(productIds);
        
        // 批量查询商品信息
        List<Product> products = productMapper.selectByProductIdSet(uniqueIds);
        
        // 构建商品ID到商品对象的映射，方便快速查找
        Map<Long, Product> productMap = products.stream()
            .collect(Collectors.toMap(Product::getId, Function.identity(), (a, b) -> a, LinkedHashMap::new));
        
        // 按照原始ID列表的顺序重新排列商品
        List<Product> ordered = new ArrayList<>(productIds.size());
        for (Long id : productIds) {
            Product product = productMap.get(id);
            if (product != null) {
                ordered.add(product);
            }
        }
        return ordered;
    }

    /**
     * 推荐上下文类。
     *
     * 封装协同过滤算法所需的所有数据：
     *
     * - userPreference: 用户偏好矩阵 [userId][productId] = 偏好分数
     * - itemFrequency: 商品频率统计 [productId] = 被购买/收藏的总次数
     * - coMatrix: 商品共现矩阵 [productA][productB] = 共现次数
     *
     */
    private static class RecommendContext {
        /**
         * 用户偏好矩阵。
         * 结构：userPreference[userId][productId] = 偏好分数
         * 偏好分数 = 购买次数 × 1.0 + 收藏次数 × 0.5
         */
        private final Map<Long, Map<Long, Double>> userPreference;

        /**
         * 商品频率统计。
         * 结构：itemFrequency[productId] = 该商品被购买/收藏的总次数
         * 用于相似度计算中的归一化
         */
        private final Map<Long, Integer> itemFrequency;

        /**
         * 商品共现矩阵。
         * 结构：coMatrix[productA][productB] = 商品A和B的共现次数
         * 共现：指同一用户同时购买/收藏了商品A和B
         * 用于计算商品之间的相似度
         */
        private final Map<Long, Map<Long, Double>> coMatrix;

        /**
         * 构造函数。
         *
         * @param userPreference 用户偏好矩阵
         * @param itemFrequency  商品频率统计
         * @param coMatrix       商品共现矩阵
         */
        RecommendContext(Map<Long, Map<Long, Double>> userPreference,
                         Map<Long, Integer> itemFrequency,
                         Map<Long, Map<Long, Double>> coMatrix) {
            this.userPreference = userPreference;
            this.itemFrequency = itemFrequency;
            this.coMatrix = coMatrix;
        }

        /**
         * 创建空的推荐上下文。
         * 当没有数据时返回空上下文。
         *
         * @return 空的推荐上下文
         */
        static RecommendContext empty() {
            return new RecommendContext(Collections.emptyMap(), Collections.emptyMap(), Collections.emptyMap());
        }
    }
}


