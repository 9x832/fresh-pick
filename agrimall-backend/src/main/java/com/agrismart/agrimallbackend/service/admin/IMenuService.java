package com.agrismart.agrimallbackend.service.admin;

import com.agrismart.agrimallbackend.dto.response.ResponseVo;
import com.agrismart.agrimallbackend.entity.admin.Menu;

import java.util.List;

/**
 * 菜单服务接口。
 *
 * 该接口定义了后台菜单相关的业务操作方法，包括：
 *
 * - 菜单层级查询（一级、二级、三级菜单）
 * - 菜单类型判断（是否为一级菜单、二级菜单，是否有子菜单）
 * - 菜单管理（添加、编辑、删除）
 * - 菜单状态切换（启用/禁用）
 * - 菜单层级查询
 *
 * 菜单采用树形结构，最多支持三级菜单：
 *
 * - 一级菜单：parentId = 0
 * - 二级菜单：parentId = 一级菜单 ID
 * - 三级菜单：parentId = 二级菜单 ID
 *
 * @author agrimall
 * @see com.agrismart.agrimallbackend.service.admin.impl.MenuServiceImpl
 * @see com.agrismart.agrimallbackend.entity.admin.Menu
 * @since 1.0
 */
public interface IMenuService {

    /**
     * 获取一级菜单列表。
     * 从所有菜单中筛选出 parentId = 0 的菜单，并按 sort 字段降序排序。
     *
     * @param allMenus 所有菜单列表
     * @return 一级菜单列表
     */
    ResponseVo<List<Menu>> getFirstMenus(List<Menu> allMenus);

    /**
     * 获取二级菜单列表。
     * 从所有菜单中筛选出 parentId 等于一级菜单 ID 的菜单，并按 sort 字段降序排序。
     *
     * @param allMenus 所有菜单列表
     * @return 二级菜单列表
     */
    ResponseVo<List<Menu>> getSecondMenus(List<Menu> allMenus);

    /**
     * 获取三级菜单列表。
     * 从所有菜单中筛选出 parentId 等于二级菜单 ID 的菜单，并按 sort 字段降序排序。
     *
     * @param allMenus 所有菜单列表
     * @return 三级菜单列表
     */
    ResponseVo<List<Menu>> getThirdMenus(List<Menu> allMenus);

    /**
     * 判断菜单是否为二级菜单。
     * 判断逻辑：菜单的 parentId 不为 0，且父菜单的 parentId 为 0。
     *
     * @param menu 菜单对象
     * @return true 表示是二级菜单，false 表示不是
     */
    ResponseVo<Boolean> isSecondMenu(Menu menu);

    /**
     * 判断菜单是否为一级菜单。
     * 判断逻辑：菜单的 parentId 为 0。
     *
     * @param menu 菜单对象
     * @return true 表示是一级菜单，false 表示不是
     */
    ResponseVo<Boolean> isFirstMenu(Menu menu);

    /**
     * 判断菜单是否有子菜单。
     * 检查是否存在其他菜单的 parentId 等于该菜单的 ID。
     *
     * @param menu 菜单对象
     * @return true 表示有子菜单，false 表示没有
     */
    ResponseVo<Boolean> haveChildrenMenu(Menu menu);

    /**
     * 添加菜单。
     *
     * @param menu 菜单对象
     * @return 操作结果
     */
    ResponseVo<Boolean> add(Menu menu);

    /**
     * 编辑菜单信息。
     *
     * @param menu 菜单对象（包含 ID）
     * @return 操作结果
     */
    ResponseVo<Boolean> edit(Menu menu);

    /**
     * 删除菜单。
     * 如果菜单有子菜单，则不允许删除。
     *
     * @param id 菜单 ID
     * @return 操作结果
     */
    ResponseVo<Boolean> delete(Integer id);

    /**
     * 查询菜单层级。
     * 返回菜单的层级：0 表示一级菜单，2 表示二级菜单。
     *
     * @param id 菜单 ID
     * @return 菜单层级（0 或 2）
     */
    ResponseVo<Integer> level(Integer id);

    /**
     * 切换菜单状态（启用/禁用）。
     *
     * @param id 菜单 ID
     * @return 操作结果
     */
    ResponseVo<Boolean> chageState(Integer id);
}

