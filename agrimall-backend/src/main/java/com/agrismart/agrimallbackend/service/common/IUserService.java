package com.agrismart.agrimallbackend.service.common;

import com.github.pagehelper.PageInfo;
import com.agrismart.agrimallbackend.dto.response.ResponseVo;
import com.agrismart.agrimallbackend.entity.common.User;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 用户服务接口。
 *
 * 该接口定义了前台用户相关的业务操作方法，包括：
 *
 * - 用户登录和注册
 * - 用户信息更新和密码修改
 * - 用户列表查询（支持分页和搜索）
 * - 用户管理（重置密码、删除用户）
 *
 * 注意：
 *
 * - 用户登录使用 JWT Token 认证，返回 Token 供前端使用
 * - 用户注册需要验证码验证
 * - 用户信息更新后会重新生成 JWT Token
 *
 * @author agrimall
 * @see com.agrismart.agrimallbackend.service.common.impl.UserServiceImpl
 * @see com.agrismart.agrimallbackend.entity.common.User
 * @since 1.0
 */
public interface IUserService {

    /**
     * 检查用户名是否已存在。
     *
     * @param user 用户对象（包含用户名）
     * @param id   当前用户 ID（编辑时使用，新增时传 0）
     * @return true 表示用户名已存在，false 表示不存在
     */
    ResponseVo<Boolean> isUsernameExist(User user, Long id);

    /**
     * 用户登录。
     * 验证用户名和密码，登录成功后返回 JWT Token。
     *
     * @param username 用户名
     * @param password 密码
     * @return 操作结果，成功时返回 JWT Token（在 msg 字段中）
     */
    ResponseVo<Boolean> login(String username, String password);

    /**
     * 用户注册。
     * 注册流程：验证验证码 -> 验证密码一致性 -> 检查用户名是否已存在 -> 创建用户 -> 发送注册邮件
     *
     * @param user      用户对象
     * @param repassword 确认密码
     * @param cpacha    验证码
     * @param request   HTTP 请求对象（用于获取 Session 中的验证码）
     * @return 操作结果
     */
    ResponseVo<Boolean> register(User user, String repassword, String cpacha, HttpServletRequest request);

    /**
     * 更新用户信息。
     * 更新用户信息后，会重新生成 JWT Token 并返回。
     *
     * @param user 用户对象（包含 ID 和要更新的字段）
     * @return 操作结果，成功时返回新的 JWT Token（在 data 字段中）
     */
    ResponseVo<String> updateInfo(User user);

    /**
     * 修改用户密码。
     * 需要验证原密码，并确认新密码和确认密码一致。
     *
     * @param prePassword   原密码
     * @param newPassword   新密码
     * @param reNewPassword 确认新密码
     * @param request       HTTP 请求对象（用于获取当前登录用户 ID）
     * @return 操作结果
     */
    ResponseVo<Boolean> updatePasswd(String prePassword, String newPassword, String reNewPassword, HttpServletRequest request);

    /**
     * 分页查询用户列表。
     * 用于后台管理系统。
     *
     * @param pageNum  页码，从 1 开始
     * @param pageSize 每页大小
     * @return 包含分页信息的用户列表
     */
    ResponseVo<PageInfo> getUserByPage(Integer pageNum, Integer pageSize);

    /**
     * 按内容分页查询用户列表。
     * 用于后台管理系统，支持按用户名、邮箱、手机号等搜索。
     *
     * @param pageNum  页码，从 1 开始
     * @param pageSize 每页大小
     * @param content  搜索内容（支持模糊搜索）
     * @return 包含分页信息的用户列表
     */
    ResponseVo<PageInfo> getUserByPageAndContent(Integer pageNum, Integer pageSize, String content);

    /**
     * 根据主键查询用户。
     *
     * @param id 用户 ID
     * @return 用户对象，如果不存在则返回 null
     */
    User selectByPrimaryKey(Long id);

    /**
     * 重置用户密码。
     * 用于后台管理系统，管理员重置用户密码。
     *
     * @param passwd  新密码
     * @param userId  用户 ID
     * @return 操作结果
     */
    ResponseVo<Boolean> updateUserPasswd(String passwd, Long userId);

    /**
     * 删除用户。
     * 用于后台管理系统，删除用户账号。
     *
     * @param userId 用户 ID
     * @return 操作结果
     */
    ResponseVo<Boolean> deleteUser(Long userId);
}

