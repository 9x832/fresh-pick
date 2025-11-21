package com.agrismart.agrimallbackend.service.admin;

import com.github.pagehelper.PageInfo;
import com.agrismart.agrimallbackend.dto.response.ResponseVo;
import com.agrismart.agrimallbackend.entity.admin.Admin;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 管理员服务接口。
 *
 * 该接口定义了后台管理员相关的业务操作方法，包括：
 *
 * - 管理员列表查询（支持分页和按名称搜索）
 * - 管理员账号管理（添加、编辑、删除）
 * - 管理员状态管理（启用/冻结）
 * - 管理员登录和登出
 * - 管理员个人信息保存
 *
 * @author agrimall
 * @see com.agrismart.agrimallbackend.service.admin.impl.AdminServiceImpl
 * @see com.agrismart.agrimallbackend.entity.admin.Admin
 * @since 1.0
 */
public interface IAdminService {

    /**
     * 分页查询管理员列表。
     *
     * @param pageNum  页码，从 1 开始
     * @param pageSize 每页大小
     * @return 包含分页信息的管理员列表
     */
    ResponseVo<PageInfo> getAdminListByPage(Integer pageNum, Integer pageSize);

    /**
     * 按名称分页查询管理员列表。
     *
     * @param pageNum  页码，从 1 开始
     * @param pageSize 每页大小
     * @param name     管理员名称（支持模糊搜索）
     * @return 包含分页信息的管理员列表
     */
    ResponseVo<PageInfo> getAdminListByPageAndName(Integer pageNum, Integer pageSize, String name);

    /**
     * 检查管理员名称是否已存在。
     *
     * @param admin 管理员对象（包含名称）
     * @param id    当前管理员 ID（编辑时使用，新增时传 0）
     * @return true 表示名称已存在，false 表示不存在
     */
    ResponseVo<Boolean> isNameExist(Admin admin, Integer id);

    /**
     * 添加管理员。
     *
     * @param admin 管理员对象
     * @return 操作结果
     */
    ResponseVo<Boolean> add(Admin admin);

    /**
     * 编辑管理员信息。
     *
     * @param admin 管理员对象（包含 ID）
     * @return 更新后的管理员对象
     */
    ResponseVo<Admin> edit(Admin admin);

    /**
     * 删除管理员。
     * 删除管理员时，会同时删除该管理员发布的所有公告。
     *
     * @param id 管理员 ID
     * @return 操作结果
     */
    ResponseVo<Boolean> delete(Integer id);

    /**
     * 切换管理员状态（启用/冻结）。
     *
     * @param id 管理员 ID
     * @return 操作结果
     */
    ResponseVo<Boolean> chageState(Integer id);

    /**
     * 管理员登录。
     * 登录流程：验证验证码 -> 验证用户名和密码 -> 检查管理员状态 -> 检查权限 -> 保存到 Session
     *
     * @param name     管理员名称
     * @param password 密码
     * @param captcha  验证码
     * @param request  HTTP 请求对象（用于获取 Session）
     * @return 操作结果
     */
    ResponseVo<Boolean> login(String name, String password, String captcha, HttpServletRequest request);

    /**
     * 管理员登出。
     *
     * @param request HTTP 请求对象（用于清除 Session）
     * @return 操作结果
     */
    ResponseVo<Boolean> logout(HttpServletRequest request);

    /**
     * 保存管理员个人信息。
     * 只能修改非敏感字段（如头像、地址等），不能修改 ID、角色、状态、密码等。
     *
     * @param admin   管理员对象（包含要更新的字段）
     * @param request HTTP 请求对象（用于获取当前登录的管理员）
     * @return 操作结果
     */
    ResponseVo<Boolean> savePersonInfo(Admin admin, HttpServletRequest request);
}

