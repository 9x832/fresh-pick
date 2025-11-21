package com.agrismart.agrimallbackend.mapper.common;

import com.agrismart.agrimallbackend.entity.common.OrderItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 订单详情mapper接口
 */
@Mapper
@Repository
public interface OrderItemMapper {
    int deleteByPrimaryKey(Long id);

    int insert(OrderItem record);

    int insertSelective(OrderItem record);

    OrderItem selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(OrderItem record);

    int updateByPrimaryKey(OrderItem record);

    int batchInsert(@Param("orderItemList") List<OrderItem> orderItemList);

    /**
     * 批量按订单ID查询订单明细。
     *
     * @param orderIds 订单ID集合
     * @return 订单明细列表
     */
    List<OrderItem> selectByOrderIds(@Param("orderIds") List<Long> orderIds);
}

