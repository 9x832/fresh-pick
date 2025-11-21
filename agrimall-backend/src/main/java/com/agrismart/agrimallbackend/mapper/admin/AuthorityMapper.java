package com.agrismart.agrimallbackend.mapper.admin;

import com.agrismart.agrimallbackend.entity.admin.Authority;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 权限mapper接口
 */
@Mapper
@Repository
public interface AuthorityMapper {
    int deleteByPrimaryKey(Integer id);

    int deleteByRoleId(Integer id);

    int insert(Authority record);

    int insertSelective(Authority record);

    Authority selectByPrimaryKey(Integer id);

    List<Authority> selectByRoleId(Integer id);

    int updateByPrimaryKeySelective(Authority record);

    int updateByPrimaryKey(Authority record);

    //一次插入多条数据
    int batchInsert(@Param("authorityList") List<Authority> authorityList);
}

