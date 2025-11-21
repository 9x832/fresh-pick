package com.agrismart.agrimallbackend.service.admin.impl;

import com.agrismart.agrimallbackend.common.bean.CodeMsg;
import com.agrismart.agrimallbackend.common.enums.MenuStateEnum;
import com.agrismart.agrimallbackend.common.util.ValidateEntityUtil;
import com.agrismart.agrimallbackend.dto.response.ResponseVo;
import com.agrismart.agrimallbackend.entity.admin.Menu;
import com.agrismart.agrimallbackend.mapper.admin.MenuMapper;
import com.agrismart.agrimallbackend.service.admin.IMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 菜单服务实现类。
 *
 * 该类实现了 {@link IMenuService} 接口，提供菜单相关的业务逻辑实现。
 * 菜单采用树形结构，支持最多三级菜单的层级查询和判断。
 *
 * @author agrimall
 * @see com.agrismart.agrimallbackend.service.admin.IMenuService
 * @since 1.0
 */
@Service
public class MenuServiceImpl implements IMenuService {

    /**
     * 菜单数据访问对象。
     */
    @Autowired
    private MenuMapper menuMapper;

    @Override
    public ResponseVo<List<Menu>> getFirstMenus(List<Menu> allMenus) {
        List<Menu> firstMenus = allMenus.stream().filter(e -> e.getParentId().intValue() == 0)
                .sorted(Comparator.comparing(Menu::getSort).reversed())
                .collect(Collectors.toList());

        return ResponseVo.success(firstMenus);
    }

    @Override
    public ResponseVo<List<Menu>> getSecondMenus(List<Menu> allMenus) {
        List<Menu> secondMenus = new ArrayList<>();
        ResponseVo<List<Menu>> responseFirstMenus = getFirstMenus(allMenus);
        for (Menu firstMenu : responseFirstMenus.getData()) {
            for (Menu menu : allMenus) {
                if (menu.getParentId().equals(firstMenu.getId())) {
                    secondMenus.add(menu);
                }
            }
        }
        secondMenus = secondMenus.stream()
                .sorted(Comparator.comparing(Menu::getSort).reversed())
                .collect(Collectors.toList());

        return ResponseVo.success(secondMenus);
    }

    @Override
    public ResponseVo<List<Menu>> getThirdMenus(List<Menu> allMenus) {
        List<Menu> thirdMenus = new ArrayList<>();
        ResponseVo<List<Menu>> responseSecondMenus = getSecondMenus(allMenus);
        for (Menu secondMenu : responseSecondMenus.getData()) {
            for (Menu menu : allMenus) {
                if (menu.getParentId().equals(secondMenu.getId())) {
                    thirdMenus.add(menu);
                }
            }
        }
        thirdMenus = thirdMenus.stream()
                .sorted(Comparator.comparing(Menu::getSort).reversed())
                .collect(Collectors.toList());

        return ResponseVo.success(thirdMenus);
    }

    @Override
    public ResponseVo<Boolean> isSecondMenu(Menu menu) {
        if (menu.getParentId() != 0) {
            Menu parentMenu = menuMapper.selectByPrimaryKey(menu.getParentId());
            if (parentMenu.getParentId() == 0) {
                return ResponseVo.success(true);
            } else {
                return ResponseVo.success(false);
            }
        } else {
            return ResponseVo.success(false);
        }
    }

    @Override
    public ResponseVo<Boolean> isFirstMenu(Menu menu) {
        if (menu.getParentId() == 0) {
            return ResponseVo.success(true);
        } else {
            return ResponseVo.success(false);
        }
    }

    @Override
    public ResponseVo<Boolean> haveChildrenMenu(Menu menu) {
        List<Menu> allMenus = menuMapper.selectAll();
        ResponseVo<List<Menu>> thirdMenus = getThirdMenus(allMenus);
        ResponseVo<List<Menu>> secondMenus = getSecondMenus(allMenus);
        List<Menu> collectThirdMenus = thirdMenus.getData().stream().filter(e -> e.getParentId() == menu.getId())
                .collect(Collectors.toList());
        List<Menu> collectSecondMenus = secondMenus.getData().stream().filter(e -> e.getParentId() == menu.getId())
                .collect(Collectors.toList());
        if (collectThirdMenus.size() > 0 || collectSecondMenus.size() > 0) {
            return ResponseVo.success(true);
        } else {
            return ResponseVo.success(false);
        }
    }

    @Override
    public ResponseVo<Boolean> add(Menu menu) {
        if (menu == null) {
            return ResponseVo.errorByMsg(CodeMsg.DATA_ERROR);
        }
        CodeMsg validate = ValidateEntityUtil.validate(menu);
        if (!validate.getCode().equals(CodeMsg.SUCCESS.getCode())) {
            return ResponseVo.errorByMsg(validate);
        }
        if (menuMapper.insertSelective(menu) <= 0) {
            return ResponseVo.errorByMsg(CodeMsg.MENU_ADD_ERROR);
        }
        return ResponseVo.successByMsg(true, "添加成功！");
    }

    @Override
    public ResponseVo<Boolean> edit(Menu menu) {
        if (menu == null) {
            return ResponseVo.errorByMsg(CodeMsg.DATA_ERROR);
        }
        CodeMsg validate = ValidateEntityUtil.validate(menu);
        if (!validate.getCode().equals(CodeMsg.SUCCESS.getCode())) {
            return ResponseVo.errorByMsg(validate);
        }
        if (menuMapper.updateByPrimaryKeySelective(menu) <= 0) {
            return ResponseVo.errorByMsg(CodeMsg.MENU_EDIT_ERROR);
        }
        return ResponseVo.successByMsg(true, "编辑成功！");
    }

    @Override
    public ResponseVo<Boolean> delete(Integer id) {
        Menu selectByPrimaryKey = menuMapper.selectByPrimaryKey(id);
        if (selectByPrimaryKey == null) {
            return ResponseVo.errorByMsg(CodeMsg.DATA_ERROR);
        }
        if (haveChildrenMenu(selectByPrimaryKey).getData()) {
            return ResponseVo.errorByMsg(CodeMsg.MENU_CHILDREN_EXIST);
        }
        if (menuMapper.deleteByPrimaryKey(id) <= 0) {
            return ResponseVo.errorByMsg(CodeMsg.MENU_DELETE_ERROR);
        }
        return ResponseVo.successByMsg(true, "删除成功！");
    }

    @Override
    public ResponseVo<Integer> level(Integer id) {
        Menu selectByPrimaryKey = menuMapper.selectByPrimaryKey(id);
        if (selectByPrimaryKey == null) {
            return ResponseVo.errorByMsg(CodeMsg.DATA_ERROR);
        }
        ResponseVo<Boolean> secondMenu = isSecondMenu(selectByPrimaryKey);
        if (secondMenu.getData()) {
            return ResponseVo.success(2);
        }
        return ResponseVo.success(0);
    }

    @Override
    public ResponseVo<Boolean> chageState(Integer id) {
        Menu selectedMenu = menuMapper.selectByPrimaryKey(id);
        if (selectedMenu == null) {
            return ResponseVo.errorByMsg(CodeMsg.DATA_ERROR);
        }
        if (selectedMenu.getState().intValue() == MenuStateEnum.OPEN.getCode()) {
            selectedMenu.setState(MenuStateEnum.STOP.getCode());
            if (menuMapper.updateByPrimaryKeySelective(selectedMenu) <= 0) {
                return ResponseVo.errorByMsg(CodeMsg.MENU_STATE_CHANGE_ERROR);
            }
        } else {
            selectedMenu.setState(MenuStateEnum.OPEN.getCode());
            if (menuMapper.updateByPrimaryKeySelective(selectedMenu) <= 0) {
                return ResponseVo.errorByMsg(CodeMsg.MENU_STATE_CHANGE_ERROR);
            }
        }

        return ResponseVo.success(true);
    }
}

