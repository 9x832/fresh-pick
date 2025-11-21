package com.agrismart.agrimallbackend.controller.admin;

import com.github.pagehelper.PageInfo;
import com.agrismart.agrimallbackend.common.bean.CodeMsg;
import com.agrismart.agrimallbackend.common.constant.SessionConstant;
import com.agrismart.agrimallbackend.dto.response.ResponseVo;
import com.agrismart.agrimallbackend.entity.admin.Admin;
import com.agrismart.agrimallbackend.mapper.admin.RoleMapper;
import com.agrismart.agrimallbackend.service.admin.IAdminService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 管理员账号管理控制器。
 *
 * 该控制器提供后台管理员账号的 CRUD 操作接口，包括：
 *
 * - 管理员列表查询（支持分页和按名称搜索）
 * - 管理员账号创建
 * - 管理员账号编辑
 * - 管理员账号删除
 * - 管理员状态切换（启用/冻结）
 *
 * 接口路径：{@code /api/admin/admins}
 *
 * 权限要求：
 *
 * - 需要管理员登录（通过 {@link com.agrismart.agrimallbackend.interceptor.AdminInterceptor} 验证）
 * - 需要相应的菜单权限
 *
 * @author agrimall
 * @see com.agrismart.agrimallbackend.service.admin.IAdminService
 * @see com.agrismart.agrimallbackend.entity.admin.Admin
 * @since 1.0
 */
@RestController
@RequestMapping("/api/admin/admins")
public class AdminManageController {

    /**
     * 管理员服务接口。
     */
    private final IAdminService adminService;

    /**
     * 角色数据访问接口。
     * 用于查询所有角色列表，供管理员创建和编辑时选择。
     */
    private final RoleMapper roleMapper;

    /**
     * 构造函数，注入依赖。
     *
     * @param adminService 管理员服务接口
     * @param roleMapper   角色数据访问接口
     */
    @Autowired
    public AdminManageController(IAdminService adminService,
                                 RoleMapper roleMapper) {
        this.adminService = adminService;
        this.roleMapper = roleMapper;
    }

    /**
     * 分页查询管理员列表。
     *
     * 支持按管理员名称搜索，如果不提供名称参数则查询所有管理员。
     * 返回结果包含分页信息和所有角色列表（用于下拉选择）。
     *
     * @param pageNum  页码，从 1 开始，默认为 1
     * @param pageSize 每页大小，默认为 5
     * @param name     管理员名称（可选），用于模糊搜索
     * @return 包含分页信息和角色列表的响应对象
     *
     * - pageInfo：分页信息，包含管理员列表
     * - roles：所有角色列表，用于创建/编辑管理员时选择角色
     *
     */
    @GetMapping
    public ResponseVo<Map<String, Object>> list(@RequestParam(defaultValue = "1") Integer pageNum,
                                                @RequestParam(defaultValue = "5") Integer pageSize,
                                                @RequestParam(required = false) String name) {
        // 根据是否提供名称参数，选择不同的查询方法
        ResponseVo<PageInfo> pageInfoVo = (name == null || name.trim().isEmpty())
                ? adminService.getAdminListByPage(pageNum, pageSize)
                : adminService.getAdminListByPageAndName(pageNum, pageSize, name);
        
        // 检查查询是否成功
        if (!CodeMsg.SUCCESS.getCode().equals(pageInfoVo.getCode())) {
            return ResponseVo.errorByMsg(CodeMsg.SYSTEM_ERROR);
        }
        
        // 构建返回数据，包含分页信息和角色列表
        Map<String, Object> payload = new HashMap<>(2);
        payload.put("pageInfo", pageInfoVo.getData());
        payload.put("roles", roleMapper.selectAll());
        return ResponseVo.success(payload);
    }

    /**
     * 创建管理员账号。
     *
     * 创建新的管理员账号，需要提供管理员的基本信息（名称、密码、角色等）。
     *
     * @param admin 管理员实体对象，包含管理员的基本信息
     * @return 操作结果，true 表示创建成功
     */
    @PostMapping
    public ResponseVo<Boolean> create(@RequestBody Admin admin) {
        return adminService.add(admin);
    }

    /**
     * 更新管理员信息。
     *
     * 更新指定 ID 的管理员信息。如果当前登录的管理员编辑的是自己的信息，
     * 会同步更新 Session 中的管理员信息，确保信息一致性。
     *
     * @param id      管理员 ID（路径变量）
     * @param admin   管理员实体对象，包含要更新的信息
     * @param request HTTP 请求对象，用于获取当前登录的管理员信息
     * @return 操作结果，true 表示更新成功
     */
    @PutMapping("/{id}")
    public ResponseVo<Boolean> update(@PathVariable("id") Integer id,
                                      @RequestBody Admin admin,
                                      HttpServletRequest request) {
        admin.setId(id);
        ResponseVo<Admin> editResult = adminService.edit(admin);
        
        // 检查编辑是否成功
        if (!CodeMsg.SUCCESS.getCode().equals(editResult.getCode())) {
            CodeMsg codeMsg = new CodeMsg();
            codeMsg.setCode(editResult.getCode());
            codeMsg.setMsg(editResult.getMsg());
            return ResponseVo.errorByMsg(codeMsg);
        }
        
        // 如果当前登录的管理员编辑的是自己的信息，同步更新 Session
        Admin loginedAdmin = (Admin) request.getSession().getAttribute(SessionConstant.SESSION_ADMIN_LOGIN_KEY);
        if (loginedAdmin != null && loginedAdmin.getId().equals(editResult.getData().getId())) {
            request.getSession().setAttribute(SessionConstant.SESSION_ADMIN_LOGIN_KEY, editResult.getData());
        }
        return ResponseVo.successByMsg(true, "编辑成功！");
    }

    /**
     * 删除管理员账号。
     *
     * 根据管理员 ID 删除指定的管理员账号。
     *
     * @param id 管理员 ID（路径变量）
     * @return 操作结果，true 表示删除成功
     */
    @DeleteMapping("/{id}")
    public ResponseVo<Boolean> delete(@PathVariable("id") Integer id) {
        return adminService.delete(id);
    }

    /**
     * 切换管理员状态。
     *
     * 切换指定管理员的状态（启用 ↔ 冻结）。
     * 如果当前状态是启用，则切换为冻结；如果当前状态是冻结，则切换为启用。
     *
     * @param id 管理员 ID（路径变量）
     * @return 操作结果，true 表示状态切换成功
     */
    @PostMapping("/{id}/state")
    public ResponseVo<Boolean> changeState(@PathVariable("id") Integer id) {
        return adminService.chageState(id);
    }
}

