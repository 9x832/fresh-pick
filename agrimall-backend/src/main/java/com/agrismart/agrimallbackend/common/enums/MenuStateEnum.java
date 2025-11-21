package com.agrismart.agrimallbackend.common.enums;

/**
 * 菜单状态枚举类。
 *
 * 该枚举用于标识后台管理系统菜单的状态。
 * 菜单可以被开启或关闭，关闭状态的菜单不会在后台管理界面中显示。
 *
 * 枚举值说明：
 *
 * - {@link #OPEN}：开启状态（code: 1），菜单在后台管理界面中显示
 * - {@link #STOP}：关闭状态（code: 2），菜单在后台管理界面中隐藏
 *
 * 使用场景：
 *
 * - 在 {@link com.agrismart.agrimallbackend.entity.admin.Menu} 实体中标识菜单状态
 * - 在 {@link com.agrismart.agrimallbackend.service.admin.impl.MenuServiceImpl} 中切换菜单状态（开启/关闭）
 * - 在 {@link com.agrismart.agrimallbackend.controller.admin.AdminSystemController} 中查询菜单时，只返回开启状态的菜单
 * - 在权限管理中，只对开启状态的菜单进行权限控制
 *
 * 使用示例：
 * <pre>
 * {@code
 * // 设置菜单为开启状态
 * menu.setState(MenuStateEnum.OPEN.getCode());
 *
 * // 设置菜单为关闭状态
 * menu.setState(MenuStateEnum.STOP.getCode());
 *
 * // 查询开启状态的菜单
 * List<Menu> menus = menuMapper.selectByState(MenuStateEnum.OPEN.getCode());
 *
 * // 切换菜单状态
 * if (menu.getState().intValue() == MenuStateEnum.OPEN.getCode()) {
 *     menu.setState(MenuStateEnum.STOP.getCode()); // 从开启切换到关闭
 * } else {
 *     menu.setState(MenuStateEnum.OPEN.getCode()); // 从关闭切换到开启
 * }
 * }
 * </pre>
 *
 * @author agrimall
 * @see com.agrismart.agrimallbackend.entity.admin.Menu
 * @see com.agrismart.agrimallbackend.service.admin.impl.MenuServiceImpl
 * @see com.agrismart.agrimallbackend.controller.admin.AdminSystemController
 * @since 1.0
 */
public enum MenuStateEnum {

    /**
     * 开启状态。
     * code: 1，表示菜单处于开启状态，会在后台管理界面中显示。
     */
    OPEN(1, "开启"),

    /**
     * 关闭状态。
     * code: 2，表示菜单处于关闭状态，不会在后台管理界面中显示。通常用于临时隐藏某个菜单功能。
     */
    STOP(2, "关闭"),

    ;

    /**
     * 枚举值对应的代码。
     * 用于在数据库和业务逻辑中存储和判断菜单状态。
     */
    Integer code;

    /**
     * 枚举值的描述信息。
     * 用于显示和说明该枚举值的含义。
     */
    String desc;

    /**
     * 枚举构造函数。
     *
     * @param code 枚举值对应的代码
     * @param desc 枚举值的描述信息
     */
    MenuStateEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 获取枚举值对应的代码。
     *
     * @return 枚举值对应的代码
     */
    public Integer getCode() {
        return code;
    }

    /**
     * 设置枚举值对应的代码。
     * 注意：通常不建议修改枚举值的 code，该方法主要用于兼容性考虑。
     *
     * @param code 枚举值对应的代码
     */
    public void setCode(Integer code) {
        this.code = code;
    }

    /**
     * 获取枚举值的描述信息。
     *
     * @return 枚举值的描述信息
     */
    public String getDesc() {
        return desc;
    }

    /**
     * 设置枚举值的描述信息。
     * 注意：通常不建议修改枚举值的 desc，该方法主要用于兼容性考虑。
     *
     * @param desc 枚举值的描述信息
     */
    public void setDesc(String desc) {
        this.desc = desc;
    }
}

