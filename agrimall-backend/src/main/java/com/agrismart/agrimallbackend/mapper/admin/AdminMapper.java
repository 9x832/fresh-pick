package com.agrismart.agrimallbackend.mapper.admin;

import com.agrismart.agrimallbackend.entity.admin.Admin;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

/**
 * 管理员mapper接口
 */
@Mapper
@Repository
public interface AdminMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Admin record);

    int insertSelective(Admin record);

    Admin selectByPrimaryKey(Integer id);

    Admin selectByNameAndPassword(@Param("name") String name, @Param("password") String password);

    List<Admin> selectByRoleId(Integer id);

    int updateByPrimaryKeySelective(Admin record);

    int updateByPrimaryKeySetSelective(@Param("adminIdSet") Set<Integer> adminIdSet, @Param("admin") Admin admin);

    int updateByPrimaryKey(Admin record);

    List<Admin> selectAll();

    List<Admin> selectBySearchName(String name);

    Admin selectByName(String name);

    int getTotal();

}

