package com.agrismart.agrimallbackend.mapper.admin;

import com.agrismart.agrimallbackend.entity.admin.Announcement;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 公告mapper接口
 */
@Mapper
@Repository
public interface AnnouncementMapper {
    int deleteByPrimaryKey(Integer id);

    int deleteByAdminId(Integer adminId);

    int insert(Announcement record);

    int insertSelective(Announcement record);

    Announcement selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Announcement record);

    int updateByPrimaryKey(Announcement record);

    List<Announcement> selectAll();

    List<Announcement> selectBySearchContent(String content);

    int getTotal();
}

