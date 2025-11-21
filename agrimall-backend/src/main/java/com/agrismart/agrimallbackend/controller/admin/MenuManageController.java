package com.agrismart.agrimallbackend.controller.admin;

import com.agrismart.agrimallbackend.dto.response.ResponseVo;
import com.agrismart.agrimallbackend.entity.admin.Menu;
import com.agrismart.agrimallbackend.mapper.admin.MenuMapper;
import com.agrismart.agrimallbackend.service.admin.IMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 菜单管理控制器。
 *
 * 该控制器提供后台管理系统对菜单的管理接口，包括：
 *
 * - 菜单列表查询（按层级分类）
 * - 菜单创建
 * - 菜单编辑
 * - 菜单删除
 * - 菜单层级查询
 * - 菜单状态切换（启用/禁用）
 *
 * 接口路径：{@code /api/admin/menus}
 *
 * 权限要求：
 *
 * - 需要管理员登录（通过 {@link com.agrismart.agrimallbackend.interceptor.AdminInterceptor} 验证）
 * - 需要相应的菜单权限
 *
 * @author agrimall
 * @see com.agrismart.agrimallbackend.service.admin.IMenuService
 * @see com.agrismart.agrimallbackend.entity.admin.Menu
 * @since 1.0
 */
@RestController
@RequestMapping("/api/admin/menus")
public class MenuManageController {

    /**
     * 菜单服务接口。
     */
    private final IMenuService menuService;

    /**
     * 菜单数据访问接口。
     */
    private final MenuMapper menuMapper;

    /**
     * 构造函数，注入依赖。
     *
     * @param menuService 菜单服务接口
     * @param menuMapper  菜单数据访问接口
     */
    @Autowired
    public MenuManageController(IMenuService menuService,
                                MenuMapper menuMapper) {
        this.menuService = menuService;
        this.menuMapper = menuMapper;
    }

    /**
     * 查询所有菜单列表（按层级分类）。
     *
     * 返回所有菜单，并按层级分类为一级菜单、二级菜单、三级菜单。
     *
     * @return 包含菜单层级结构的响应对象
     *
     * - firstMenus：一级菜单列表
     * - secondMenus：二级菜单列表
     * - thirdMenus：三级菜单列表
     *
     */
    @GetMapping
    public ResponseVo<Map<String, List<Menu>>> list() {
        List<Menu> allMenus = menuMapper.selectAll();
        Map<String, List<Menu>> payload = new HashMap<>(3);
        payload.put("firstMenus", menuService.getFirstMenus(allMenus).getData());
        payload.put("secondMenus", menuService.getSecondMenus(allMenus).getData());
        payload.put("thirdMenus", menuService.getThirdMenus(allMenus).getData());
        return ResponseVo.success(payload);
    }

    /**
     * 创建菜单。
     *
     * 创建新的菜单项，需要提供菜单的基本信息（名称、URL、图标、父菜单ID等）。
     *
     * @param menu 菜单实体对象，包含菜单的基本信息
     * @return 操作结果，true 表示创建成功
     */
    @PostMapping
    public ResponseVo<Boolean> create(@RequestBody Menu menu) {
        return menuService.add(menu);
    }

    /**
     * 更新菜单信息。
     *
     * 更新指定 ID 的菜单信息。
     *
     * @param id   菜单 ID（路径变量）
     * @param menu 菜单实体对象，包含要更新的信息
     * @return 操作结果，true 表示更新成功
     */
    @PutMapping("/{id}")
    public ResponseVo<Boolean> update(@PathVariable("id") Integer id,
                                      @RequestBody Menu menu) {
        menu.setId(id);
        return menuService.edit(menu);
    }

    /**
     * 删除菜单。
     *
     * 根据菜单 ID 删除指定的菜单项。
     *
     * @param id 菜单 ID（路径变量）
     * @return 操作结果，true 表示删除成功
     */
    @DeleteMapping("/{id}")
    public ResponseVo<Boolean> delete(@PathVariable("id") Integer id) {
        return menuService.delete(id);
    }

    /**
     * 查询菜单层级。
     *
     * 根据菜单 ID 查询该菜单所在的层级（1、2 或 3）。
     *
     * @param id 菜单 ID（路径变量）
     * @return 菜单层级（1、2 或 3）
     */
    @GetMapping("/{id}/level")
    public ResponseVo<Integer> level(@PathVariable("id") Integer id) {
        return menuService.level(id);
    }

    /**
     * 切换菜单状态。
     *
     * 切换指定菜单的状态（启用 ↔ 禁用）。
     * 如果当前状态是启用，则切换为禁用；如果当前状态是禁用，则切换为启用。
     *
     * @param id 菜单 ID（路径变量）
     * @return 操作结果，true 表示状态切换成功
     */
    @PostMapping("/{id}/state")
    public ResponseVo<Boolean> changeState(@PathVariable("id") Integer id) {
        return menuService.chageState(id);
    }
}

