package com.agrismart.agrimallbackend.controller.admin;

import com.agrismart.agrimallbackend.dto.response.ResponseVo;
import com.agrismart.agrimallbackend.entity.admin.Menu;
import com.agrismart.agrimallbackend.entity.admin.Role;
import com.agrismart.agrimallbackend.mapper.admin.AuthorityMapper;
import com.agrismart.agrimallbackend.mapper.admin.MenuMapper;
import com.agrismart.agrimallbackend.service.admin.IMenuService;
import com.agrismart.agrimallbackend.service.admin.IRoleService;
import com.github.pagehelper.PageInfo;
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
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 角色管理控制器。
 *
 * 该控制器提供后台管理系统对角色的管理接口，包括：
 *
 * - 角色列表查询（支持分页和搜索）
 * - 角色创建
 * - 角色编辑
 * - 角色删除
 * - 角色权限查询
 * - 角色权限保存
 *
 * 接口路径：{@code /api/admin/roles}
 *
 * 权限要求：
 *
 * - 需要管理员登录（通过 {@link com.agrismart.agrimallbackend.interceptor.AdminInterceptor} 验证）
 * - 需要相应的菜单权限
 *
 * @author agrimall
 * @see com.agrismart.agrimallbackend.service.admin.IRoleService
 * @see com.agrismart.agrimallbackend.entity.admin.Role
 * @since 1.0
 */
@RestController
@RequestMapping("/api/admin/roles")
public class RoleManageController {

    /**
     * 角色服务接口。
     */
    private final IRoleService roleService;

    /**
     * 菜单服务接口。
     */
    private final IMenuService menuService;

    /**
     * 菜单数据访问接口。
     */
    private final MenuMapper menuMapper;

    /**
     * 权限数据访问接口。
     */
    private final AuthorityMapper authorityMapper;

    /**
     * 构造函数，注入依赖。
     *
     * @param roleService      角色服务接口
     * @param menuService      菜单服务接口
     * @param menuMapper       菜单数据访问接口
     * @param authorityMapper  权限数据访问接口
     */
    @Autowired
    public RoleManageController(IRoleService roleService,
                                IMenuService menuService,
                                MenuMapper menuMapper,
                                AuthorityMapper authorityMapper) {
        this.roleService = roleService;
        this.menuService = menuService;
        this.menuMapper = menuMapper;
        this.authorityMapper = authorityMapper;
    }

    /**
     * 分页查询角色列表。
     *
     * 支持按角色名称搜索，如果不提供名称参数则查询所有角色。
     *
     * @param pageNum  页码，从 1 开始，默认为 1
     * @param pageSize 每页大小，默认为 5
     * @param name     角色名称（可选），用于模糊搜索
     * @return 分页信息，包含角色列表
     */
    @GetMapping
    public ResponseVo<PageInfo> list(@RequestParam(defaultValue = "1") Integer pageNum,
                                     @RequestParam(defaultValue = "5") Integer pageSize,
                                     @RequestParam(required = false) String name) {
        return (name == null || name.trim().isEmpty())
                ? roleService.getRoleListByPage(pageNum, pageSize)
                : roleService.getRoleListByPageAndName(pageNum, pageSize, name);
    }

    /**
     * 创建角色。
     *
     * 创建新的角色，需要提供角色的基本信息（名称等）。
     *
     * @param role 角色实体对象，包含角色的基本信息
     * @return 操作结果，true 表示创建成功
     */
    @PostMapping
    public ResponseVo<Boolean> create(@RequestBody Role role) {
        return roleService.add(role);
    }

    /**
     * 更新角色信息。
     *
     * 更新指定 ID 的角色信息。
     *
     * @param id   角色 ID（路径变量）
     * @param role 角色实体对象，包含要更新的信息
     * @return 操作结果，true 表示更新成功
     */
    @PutMapping("/{id}")
    public ResponseVo<Boolean> update(@PathVariable("id") Integer id,
                                      @RequestBody Role role) {
        role.setId(id);
        return roleService.edit(role);
    }

    /**
     * 删除角色。
     *
     * 根据角色 ID 删除指定的角色。
     *
     * @param id 角色 ID（路径变量）
     * @return 操作结果，true 表示删除成功
     */
    @DeleteMapping("/{id}")
    public ResponseVo<Boolean> delete(@PathVariable("id") Integer id) {
        return roleService.delete(id);
    }

    /**
     * 查询角色的权限配置。
     *
     * 查询指定角色的权限配置，返回所有菜单列表（按层级分类）和该角色已拥有的权限列表。
     *
     * @param id 角色 ID（路径变量）
     * @return 包含菜单层级结构和角色权限列表的响应对象
     *
     * - firstMenus：一级菜单列表
     * - secondMenus：二级菜单列表
     * - thirdMenus：三级菜单列表
     * - authorityList：该角色已拥有的权限列表
     *
     */
    @GetMapping("/{id}/authority")
    public ResponseVo<Map<String, Object>> authority(@PathVariable("id") Integer id) {
        List<Menu> allMenus = menuMapper.selectAll();
        Map<String, Object> payload = new HashMap<>(4);
        payload.put("firstMenus", menuService.getFirstMenus(allMenus).getData());
        payload.put("secondMenus", menuService.getSecondMenus(allMenus).getData());
        payload.put("thirdMenus", menuService.getThirdMenus(allMenus).getData());
        payload.put("authorityList", authorityMapper.selectByRoleId(id));
        return ResponseVo.success(payload);
    }

    /**
     * 保存角色的权限配置。
     *
     * 为指定角色保存菜单权限配置，将菜单 ID 列表转换为逗号分隔的字符串后保存。
     *
     * @param id      角色 ID（路径变量）
     * @param menuIds 菜单 ID 列表，表示该角色拥有的菜单权限
     * @return 操作结果，true 表示权限保存成功
     */
    @PostMapping("/{id}/authority")
    public ResponseVo<Boolean> saveAuthority(@PathVariable("id") Integer id,
                                             @RequestBody List<Integer> menuIds) {
        String ids = menuIds == null ? "" : menuIds.stream().map(String::valueOf).collect(Collectors.joining(","));
        return roleService.saveAuthority(ids, id);
    }
}

