package com.agrismart.agrimallbackend.service.admin.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.agrismart.agrimallbackend.common.bean.CodeMsg;
import com.agrismart.agrimallbackend.common.constant.SessionConstant;
import com.agrismart.agrimallbackend.common.enums.AdminStateEnum;
import com.agrismart.agrimallbackend.common.util.StringUtil;
import com.agrismart.agrimallbackend.common.util.ValidateEntityUtil;
import com.agrismart.agrimallbackend.dto.response.ResponseVo;
import com.agrismart.agrimallbackend.entity.admin.Admin;
import com.agrismart.agrimallbackend.entity.admin.Authority;
import com.agrismart.agrimallbackend.mapper.admin.AdminMapper;
import com.agrismart.agrimallbackend.mapper.admin.AnnouncementMapper;
import com.agrismart.agrimallbackend.mapper.admin.AuthorityMapper;
import com.agrismart.agrimallbackend.service.admin.IAdminService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 管理员服务实现类。
 *
 * 该类实现了 {@link IAdminService} 接口，提供管理员相关的业务逻辑实现。
 *
 * @author agrimall
 * @see com.agrismart.agrimallbackend.service.admin.IAdminService
 * @since 1.0
 */
@Service
public class AdminServiceImpl implements IAdminService {

    /**
     * 管理员数据访问对象。
     */
    @Autowired
    private AdminMapper adminMapper;

    /**
     * 公告数据访问对象。
     * 用于删除管理员时，同时删除该管理员发布的所有公告。
     */
    @Autowired
    private AnnouncementMapper announcementMapper;

    /**
     * 权限数据访问对象。
     * 用于登录时检查管理员是否有权限。
     */
    @Autowired
    private AuthorityMapper authorityMapper;

    @Override
    public ResponseVo<PageInfo> getAdminListByPage(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Admin> allAdmins = adminMapper.selectAll();
        PageInfo<Admin> pageInfo = new PageInfo<>(allAdmins);
        pageInfo.setList(allAdmins);
        return ResponseVo.success(pageInfo);
    }

    @Override
    public ResponseVo<PageInfo> getAdminListByPageAndName(Integer pageNum, Integer pageSize, String name) {
        PageHelper.startPage(pageNum, pageSize);
        List<Admin> selectBySearchName = adminMapper.selectBySearchName(name);
        PageInfo<Admin> pageInfo = new PageInfo<>(selectBySearchName);
        pageInfo.setList(selectBySearchName);
        return ResponseVo.success(pageInfo);
    }

    @Override
    public ResponseVo<Boolean> isNameExist(Admin admin, Integer id) {
        Admin selectByName = adminMapper.selectByName(admin.getName());
        if (selectByName != null) {
            if (!selectByName.getId().equals(id)) {
                return ResponseVo.success(true);
            }
        }
        return ResponseVo.success(false);
    }

    @Override
    public ResponseVo<Boolean> add(Admin admin) {
        if (admin == null) {
            return ResponseVo.errorByMsg(CodeMsg.DATA_ERROR);
        }
        admin.setPassword("123456");
        CodeMsg validate = ValidateEntityUtil.validate(admin);
        if (!validate.getCode().equals(CodeMsg.SUCCESS.getCode())) {
            return ResponseVo.errorByMsg(validate);
        }
        if (admin.getMobile().toString().length() != 11) {
            return ResponseVo.errorByMsg(CodeMsg.ADMIN_MOBILE_LENGTH_ERROR);
        }
        if (isNameExist(admin, 0).getData()) {
            return ResponseVo.errorByMsg(CodeMsg.ADMIN_NAME_EXIST);
        }
        if (adminMapper.insertSelective(admin) <= 0) {
            return ResponseVo.errorByMsg(CodeMsg.ADMIN_ADD_ERROR);
        }
        return ResponseVo.successByMsg(true, "添加成功！");
    }

    @Override
    public ResponseVo<Admin> edit(Admin admin) {
        if (admin == null) {
            return ResponseVo.errorByMsg(CodeMsg.DATA_ERROR);
        }
        Admin dbAdmin = adminMapper.selectByPrimaryKey(admin.getId());
        if (dbAdmin == null) {
            return ResponseVo.errorByMsg(CodeMsg.DATA_ERROR);
        }
        if (admin.getPassword() == null) {
            admin.setPassword(dbAdmin.getPassword());
        }
        if (admin.getMobile() == null) {
            admin.setMobile(dbAdmin.getMobile());
        }
        CodeMsg validate = ValidateEntityUtil.validate(admin);
        if (!validate.getCode().equals(CodeMsg.SUCCESS.getCode())) {
            return ResponseVo.errorByMsg(validate);
        }
        if (admin.getMobile() == null || admin.getMobile().toString().length() != 11) {
            return ResponseVo.errorByMsg(CodeMsg.ADMIN_MOBILE_LENGTH_ERROR);
        }
        if (isNameExist(admin, admin.getId()).getData()) {
            return ResponseVo.errorByMsg(CodeMsg.ADMIN_NAME_EXIST);
        }
        if (adminMapper.updateByPrimaryKeySelective(admin) <= 0) {
            return ResponseVo.errorByMsg(CodeMsg.ADMIN_EDIT_ERROR);
        }
        return ResponseVo.success(admin);
    }

    @Override
    public ResponseVo<Boolean> delete(Integer id) {
        if (id == null) {
            return ResponseVo.errorByMsg(CodeMsg.DATA_ERROR);
        }
        if (announcementMapper.deleteByAdminId(id) < 0) {
            return ResponseVo.errorByMsg(CodeMsg.ADMIN_DELETE_ERROR);
        }
        if (adminMapper.deleteByPrimaryKey(id) <= 0) {
            return ResponseVo.errorByMsg(CodeMsg.ADMIN_DELETE_ERROR);
        }
        return ResponseVo.successByMsg(true, "删除成功！");
    }

    @Override
    public ResponseVo<Boolean> chageState(Integer id) {
        Admin selectedAdmin = adminMapper.selectByPrimaryKey(id);
        if (selectedAdmin == null) {
            return ResponseVo.errorByMsg(CodeMsg.DATA_ERROR);
        }
        if (selectedAdmin.getState().intValue() == AdminStateEnum.OPEN.getCode()) {
            selectedAdmin.setState(AdminStateEnum.STOP.getCode());
            if (adminMapper.updateByPrimaryKeySelective(selectedAdmin) <= 0) {
                return ResponseVo.errorByMsg(CodeMsg.ADMIN_STATE_CHANGE_ERROR);
            }
        } else {
            selectedAdmin.setState(AdminStateEnum.OPEN.getCode());
            if (adminMapper.updateByPrimaryKeySelective(selectedAdmin) <= 0) {
                return ResponseVo.errorByMsg(CodeMsg.ADMIN_STATE_CHANGE_ERROR);
            }
        }

        return ResponseVo.success(true);
    }

    @Override
    public ResponseVo<Boolean> login(String name, String password, String captcha, HttpServletRequest request) {
        if (StringUtil.isEmpty(captcha)) {
            return ResponseVo.errorByMsg(CodeMsg.CPACHA_EMPTY);
        }
        String correctCpacha = (String) request.getSession().getAttribute("admin_login");
        if (StringUtil.isEmpty(correctCpacha)) {
            return ResponseVo.errorByMsg(CodeMsg.CPACHA_EXPIRE);
        }
        if (!captcha.equalsIgnoreCase(correctCpacha)) {
            return ResponseVo.errorByMsg(CodeMsg.CPACHA_ERROR);
        }
        Admin admin = adminMapper.selectByNameAndPassword(name, password);
        if (admin == null) {
            return ResponseVo.errorByMsg(CodeMsg.USERNAME_OR_PASSWORD_ERROR);
        }
        if (admin.getState().intValue() == AdminStateEnum.STOP.getCode()) {
            return ResponseVo.errorByMsg(CodeMsg.USER_STATE_ERROR);
        }
        List<Authority> authorities = authorityMapper.selectByRoleId(admin.getRoleId());
        if (authorities == null || authorities.isEmpty()) {
            return ResponseVo.errorByMsg(CodeMsg.USER_AUTHORITY_ERROR);
        }
        request.getSession().setAttribute(SessionConstant.SESSION_ADMIN_LOGIN_KEY, admin);
        return ResponseVo.successByMsg(true, "登录成功！");
    }

    @Override
    public ResponseVo<Boolean> logout(HttpServletRequest request) {
        request.getSession().setAttribute(SessionConstant.SESSION_ADMIN_LOGIN_KEY, null);
        return ResponseVo.success(true);
    }

    @Override
    public ResponseVo<Boolean> savePersonInfo(Admin admin, HttpServletRequest request) {
        if (admin == null) {
            return ResponseVo.errorByMsg(CodeMsg.DATA_ERROR);
        }
        Admin loginedAdmin = (Admin) request.getSession().getAttribute(SessionConstant.SESSION_ADMIN_LOGIN_KEY);
        if (loginedAdmin == null) {
            return ResponseVo.errorByMsg(CodeMsg.USER_SESSION_EXPIRED);
        }
        BeanUtils.copyProperties(admin, loginedAdmin, "id", "createTime", "updateTime", "roleId", "state", "password");
        if (adminMapper.updateByPrimaryKeySelective(loginedAdmin) <= 0) {
            return ResponseVo.errorByMsg(CodeMsg.PERSON_INFO_SAVE_ERROR);
        }
        request.getSession().setAttribute(SessionConstant.SESSION_ADMIN_LOGIN_KEY, loginedAdmin);
        return ResponseVo.successByMsg(true, "保存个人信息成功！");
    }
}

