package com.agrismart.agrimallbackend.service.admin;

import com.github.pagehelper.PageInfo;
import com.agrismart.agrimallbackend.dto.response.ResponseVo;
import com.agrismart.agrimallbackend.entity.admin.Role;

/**
 * 角色服务接口。
 *
 * 该接口定义了后台角色相关的业务操作方法，包括：
 *
 * - 角色列表查询（支持分页和按名称搜索）
 * - 角色管理（添加、编辑、删除）
 * - 角色权限分配
 *
 * @author agrimall
 * @see com.agrismart.agrimallbackend.service.admin.impl.RoleServiceImpl
 * @see com.agrismart.agrimallbackend.entity.admin.Role
 * @since 1.0
 */
public interface IRoleService {

    /**
     * 分页查询角色列表。
     *
     * @param pageNum  页码，从 1 开始
     * @param pageSize 每页大小
     * @return 包含分页信息的角色列表
     */
    ResponseVo<PageInfo> getRoleListByPage(Integer pageNum, Integer pageSize);

    /**
     * 按名称分页查询角色列表。
     *
     * @param pageNum  页码，从 1 开始
     * @param pageSize 每页大小
     * @param name     角色名称（支持模糊搜索）
     * @return 包含分页信息的角色列表
     */
    ResponseVo<PageInfo> getRoleListByPageAndName(Integer pageNum, Integer pageSize, String name);

    /**
     * 添加角色。
     *
     * @param role 角色对象
     * @return 操作结果
     */
    ResponseVo<Boolean> add(Role role);

    /**
     * 编辑角色信息。
     *
     * @param role 角色对象（包含 ID）
     * @return 操作结果
     */
    ResponseVo<Boolean> edit(Role role);

    /**
     * 删除角色。
     * 删除角色时，会同时删除该角色的所有权限，并将使用该角色的管理员角色 ID 设置为 0。
     *
     * @param id 角色 ID
     * @return 操作结果
     */
    ResponseVo<Boolean> delete(Integer id);

    /**
     * 检查角色名称是否已存在。
     *
     * @param role 角色对象（包含名称）
     * @param id   当前角色 ID（编辑时使用，新增时传 0）
     * @return true 表示名称已存在，false 表示不存在
     */
    ResponseVo<Boolean> isNameExist(Role role, Integer id);

    /**
     * 保存角色权限。
     * 先删除该角色的所有现有权限，然后批量添加新的权限。
     *
     * @param ids    菜单 ID 列表（逗号分隔的字符串，如 "1,2,3"）
     * @param roleId 角色 ID
     * @return 操作结果
     */
    ResponseVo<Boolean> saveAuthority(String ids, Integer roleId);
}

