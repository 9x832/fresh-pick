package com.agrismart.agrimallbackend.mapper.common;

import com.agrismart.agrimallbackend.entity.common.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

/**
 * 商品mapper接口
 */
@Mapper
@Repository
public interface ProductMapper {
    int deleteByPrimaryKey(Long id);

    int insert(Product record);

    int insertSelective(Product record);

    Product selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Product record);

    int updateByPrimaryKey(Product record);

    //获取所有商品
    List<Product> selectAll();

    //根据搜索内容获取符合条件的商品
    List<Product> selectBySearchContent(String content);

    //根据商品种类id和搜索内容获取商品
    List<Product> selectByCategoryIdAndSearchContent(@Param("categoryId") Long categoryId, @Param("content") String content);

    //根据商品id集合获取商品
    List<Product> selectByProductIdSet(@Param("productIdSet") Set<Long> productIdSet);

    //获取销量排行前5个商品
    List<Product> selectBySellNumber();

    /**
     * 获取销量排行TopN商品。
     *
     * @param limit 限制数量
     * @return 商品列表
     */
    List<Product> selectBySellNumberLimit(@Param("limit") int limit);
}

