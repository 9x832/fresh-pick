package com.agrismart.agrimallbackend.service.admin.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.util.StringUtil;
import com.agrismart.agrimallbackend.common.bean.CodeMsg;
import com.agrismart.agrimallbackend.common.util.ValidateEntityUtil;
import com.agrismart.agrimallbackend.dto.response.ResponseVo;
import com.agrismart.agrimallbackend.entity.admin.Admin;
import com.agrismart.agrimallbackend.entity.admin.Authority;
import com.agrismart.agrimallbackend.entity.admin.Role;
import com.agrismart.agrimallbackend.mapper.admin.AdminMapper;
import com.agrismart.agrimallbackend.mapper.admin.AuthorityMapper;
import com.agrismart.agrimallbackend.mapper.admin.RoleMapper;
import com.agrismart.agrimallbackend.service.admin.IRoleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 角色服务实现类。
 *
 * 该类实现了 {@link IRoleService} 接口，提供角色相关的业务逻辑实现。
 *
 * @author agrimall
 * @see com.agrismart.agrimallbackend.service.admin.IRoleService
 * @since 1.0
 */
@Service
public class RoleServiceImpl implements IRoleService {

    /**
     * 角色数据访问对象。
     */
    @Autowired
    private RoleMapper roleMapper;

    /**
     * 管理员数据访问对象。
     * 用于删除角色时，更新使用该角色的管理员的角色 ID。
     */
    @Autowired
    private AdminMapper adminMapper;

    /**
     * 权限数据访问对象。
     * 用于角色权限的分配和删除。
     */
    @Autowired
    private AuthorityMapper authorityMapper;

    /**
     * 日志记录器。
     */
    private final Logger log = LoggerFactory.getLogger(RoleServiceImpl.class);

    @Override
    public ResponseVo<PageInfo> getRoleListByPage(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Role> allRoles = roleMapper.selectAll();
        PageInfo<Role> pageInfo = new PageInfo<>(allRoles);
        pageInfo.setList(allRoles);
        return ResponseVo.success(pageInfo);
    }

    @Override
    public ResponseVo<PageInfo> getRoleListByPageAndName(Integer pageNum, Integer pageSize, String name) {
        PageHelper.startPage(pageNum, pageSize);
        List<Role> selectBySearchName = roleMapper.selectBySearchName(name);
        PageInfo<Role> pageInfo = new PageInfo<>(selectBySearchName);
        pageInfo.setList(selectBySearchName);
        return ResponseVo.success(pageInfo);
    }

    @Override
    public ResponseVo<Boolean> add(Role role) {
        if (role == null) {
            return ResponseVo.errorByMsg(CodeMsg.DATA_ERROR);
        }
        CodeMsg validate = ValidateEntityUtil.validate(role);
        if (!validate.getCode().equals(CodeMsg.SUCCESS.getCode())) {
            return ResponseVo.errorByMsg(validate);
        }
        if (isNameExist(role, 0).getData()) {
            return ResponseVo.errorByMsg(CodeMsg.ROLE_NAME_EXIST);
        }
        if (roleMapper.insertSelective(role) <= 0) {
            return ResponseVo.errorByMsg(CodeMsg.ROLE_ADD_ERROR);
        }
        return ResponseVo.successByMsg(true, "添加成功！");
    }

    @Override
    public ResponseVo<Boolean> edit(Role role) {
        if (role == null) {
            return ResponseVo.errorByMsg(CodeMsg.DATA_ERROR);
        }
        CodeMsg validate = ValidateEntityUtil.validate(role);
        if (!validate.getCode().equals(CodeMsg.SUCCESS.getCode())) {
            return ResponseVo.errorByMsg(validate);
        }
        if (isNameExist(role, role.getId()).getData()) {
            return ResponseVo.errorByMsg(CodeMsg.ROLE_NAME_EXIST);
        }
        if (roleMapper.updateByPrimaryKeySelective(role) <= 0) {
            return ResponseVo.errorByMsg(CodeMsg.ROLE_EDIT_ERROR);
        }
        return ResponseVo.successByMsg(true, "编辑成功！");
    }

    @Override
    public ResponseVo<Boolean> delete(Integer id) {
        if (id == null) {
            return ResponseVo.errorByMsg(CodeMsg.DATA_ERROR);
        }
        List<Admin> selectByRoleId = adminMapper.selectByRoleId(id);
        Admin admin = new Admin();
        admin.setRoleId(0);
        Set<Integer> adminIdSet = selectByRoleId.stream().map(Admin::getId).collect(Collectors.toSet());
        if (adminIdSet.size() > 0) {
            if (adminMapper.updateByPrimaryKeySetSelective(adminIdSet, admin) <= 0) {
                return ResponseVo.errorByMsg(CodeMsg.ADMIN_ROLE_EDIT_ERROR);
            }
        }
        if (authorityMapper.deleteByRoleId(id) < 0) {
            return ResponseVo.errorByMsg(CodeMsg.ROLE_AUTHORITY_DELETE_ERROR);
        }
        if (roleMapper.deleteByPrimaryKey(id) <= 0) {
            return ResponseVo.errorByMsg(CodeMsg.ROLE_DELETE_ERROR);
        }
        return ResponseVo.successByMsg(true, "删除成功！");
    }

    @Override
    public ResponseVo<Boolean> isNameExist(Role role, Integer id) {
        Role selectByName = roleMapper.selectByName(role.getName());
        if (selectByName != null) {
            if (!selectByName.getId().equals(id)) {
                return ResponseVo.success(true);
            }
        }
        return ResponseVo.success(false);
    }

    @Override
    public ResponseVo<Boolean> saveAuthority(String ids, Integer roleId) {
        if (roleMapper.selectByPrimaryKey(roleId) == null) {
            return ResponseVo.errorByMsg(CodeMsg.DATA_ERROR);
        }
        if (authorityMapper.deleteByRoleId(roleId) < 0) {
            return ResponseVo.errorByMsg(CodeMsg.ROLE_AUTHORITY_DELETE_ERROR);
        }
        if (StringUtil.isEmpty(ids)) {
            return ResponseVo.successByMsg(true, "角色权限保存成功！");
        }
        List<Authority> authorityList = new ArrayList<>();
        String[] split = ids.split(",");
        for (String id : split) {
            Authority authority = new Authority();
            authority.setRoleId(roleId);
            try {
                authority.setMenuId(Integer.valueOf(id));
            } catch (Exception e) {
                log.info("当前权限为空，强转异常...");
            }
            authorityList.add(authority);
        }
        if (authorityMapper.batchInsert(authorityList) < 0) {
            return ResponseVo.errorByMsg(CodeMsg.ROLE_AUTHORITY_UPDATE_ERROR);
        }
        return ResponseVo.successByMsg(true, "角色权限保存成功！");
    }
}

