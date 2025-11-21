package com.agrismart.agrimallbackend.mapper.home;

import com.agrismart.agrimallbackend.entity.home.Collect;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 收藏mapper接口
 */
@Mapper
@Repository
public interface CollectMapper {
    int deleteByPrimaryKey(Long id);

    int insert(Collect record);

    int insertSelective(Collect record);

    Collect selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Collect record);

    int updateByPrimaryKey(Collect record);

    //通过用户id查找收藏
    List<Collect> findCollectByUserId(Long userId);

    //通过用户id和商品id查找收藏
    Collect findCollectByUserIdAndProductId(@Param("userId") Long userId, @Param("productId") Long productId);

    /**
     * 获取全部收藏记录（不带商品关联，主要用于推荐统计）。
     *
     * @return 收藏列表
     */
    List<Collect> selectAllSimple();
}

