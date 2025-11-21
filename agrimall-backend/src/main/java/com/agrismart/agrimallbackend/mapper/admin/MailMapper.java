package com.agrismart.agrimallbackend.mapper.admin;

import com.agrismart.agrimallbackend.entity.admin.Mail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 邮箱mapper接口
 */
@Mapper
@Repository
public interface MailMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Mail record);

    int insertSelective(Mail record);

    Mail selectByPrimaryKey(Integer id);

    List<Mail> selectByReceiverIdAndDeleteState(@Param("id") Integer id, @Param("deleteState") Integer deleteState);

    List<Mail> selectBySenderIdAndDeleteState(@Param("id") Integer id, @Param("deleteState") Integer deleteState);

    List<Mail> selectBySearchTitleAndReceiverIdAndDeleteState(@Param("title") String title, @Param("id") Integer id, @Param("deleteState") Integer deleteState);

    List<Mail> selectBySearchTitleAndSenderIdAndDeleteState(@Param("title") String title, @Param("id") Integer id, @Param("deleteState") Integer deleteState);

    List<Mail> selectAllByDeleteState(@Param("deleteState") Integer deleteState);

    List<Mail> selectBySearchTitleAndDeleteState(@Param("title") String title, @Param("deleteState") Integer deleteState);

    int updateByPrimaryKeySelective(Mail record);

    int updateByPrimaryKeyWithBLOBs(Mail record);

    int updateByPrimaryKey(Mail record);

    //一次插入多条数据
    int batchInsert(@Param("mailList") List<Mail> mailList);

    int getTotal();
}

