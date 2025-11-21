package com.agrismart.agrimallbackend.mapper.admin;

import com.agrismart.agrimallbackend.entity.admin.Attachment;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 附件mapper接口
 */
@Mapper
@Repository
public interface AttachmentMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Attachment record);

    int insertSelective(Attachment record);

    Attachment selectByPrimaryKey(Integer id);

    List<Attachment> selectAll();

    int updateByPrimaryKeySelective(Attachment record);

    int updateByPrimaryKey(Attachment record);

    int getTotal();
}

