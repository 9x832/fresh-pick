package com.agrismart.agrimallbackend.service.common.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.agrismart.agrimallbackend.common.bean.CodeMsg;
import com.agrismart.agrimallbackend.common.enums.MailTypeEnum;
import com.agrismart.agrimallbackend.common.util.JwtUtil;
import com.agrismart.agrimallbackend.common.util.MailUtil;
import com.agrismart.agrimallbackend.common.util.StringUtil;
import com.agrismart.agrimallbackend.common.util.ValidateEntityUtil;
import com.agrismart.agrimallbackend.dto.response.ResponseVo;
import com.agrismart.agrimallbackend.entity.common.User;
import com.agrismart.agrimallbackend.mapper.common.UserMapper;
import com.agrismart.agrimallbackend.service.common.IUserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户服务实现类。
 *
 * 该类实现了 {@link IUserService} 接口，提供用户相关的业务逻辑实现。
 * 包括用户登录、注册、信息更新、密码修改等功能，使用 JWT Token 进行身份认证。
 *
 * @author agrimall
 * @see com.agrismart.agrimallbackend.service.common.IUserService
 * @since 1.0
 */
@Service
public class UserServiceImpl implements IUserService {

    /**
     * 用户数据访问对象。
     */
    @Autowired
    private UserMapper userMapper;

    /**
     * 邮件工具类。
     * 用于发送用户注册邮件。
     */
    @Autowired
    private MailUtil mailUtil;

    @Override
    public ResponseVo<Boolean> isUsernameExist(User user, Long id) {
        User findByUsername = userMapper.findUserByUsername(user.getUsername());
        if (findByUsername != null) {
            if (!findByUsername.getId().equals(id)) {
                return ResponseVo.success(true);
            }
        }
        return ResponseVo.success(false);
    }

    @Override
    public ResponseVo<Boolean> login(String username, String password) {
        if (StringUtil.isEmpty(username)) {
            return ResponseVo.errorByMsg(CodeMsg.USER_USERNAME_EMPTY);
        }
        if (StringUtil.isEmpty(password)) {
            return ResponseVo.errorByMsg(CodeMsg.USER_PASSWORD_EMPTY);
        }
        User findUser = userMapper.findUserByUsername(username);
        if (findUser == null) {
            return ResponseVo.errorByMsg(CodeMsg.USER_NOT_EXIST);
        }
        if (!password.equals(findUser.getPassword())) {
            return ResponseVo.errorByMsg(CodeMsg.USERNAME_OR_PASSWORD_ERROR);
        }
        Map<String, String> map = new HashMap<>();
        map.put("id", findUser.getId().toString());
        map.put("username", findUser.getUsername());
        map.put("email", findUser.getEmail());
        map.put("phone", findUser.getPhone());
        map.put("headPic", findUser.getHeadPic());
        String token = JwtUtil.getToken(map);

        return ResponseVo.successByMsg(true, token);
    }

    @Override
    public ResponseVo<Boolean> register(User user, String repassword, String cpacha, HttpServletRequest request) {
        if (user == null) {
            return ResponseVo.errorByMsg(CodeMsg.DATA_ERROR);
        }
        CodeMsg validate = ValidateEntityUtil.validate(user);
        if (!validate.getCode().equals(CodeMsg.SUCCESS.getCode())) {
            return ResponseVo.errorByMsg(validate);
        }
        if (StringUtil.isEmpty(repassword)) {
            return ResponseVo.errorByMsg(CodeMsg.USER_REPASSWORD_EMPTY);
        }
        if (StringUtil.isEmpty(cpacha)) {
            return ResponseVo.errorByMsg(CodeMsg.CPACHA_EMPTY);
        }
        if (!user.getPassword().equals(repassword)) {
            return ResponseVo.errorByMsg(CodeMsg.USER_REPASSWORD_ERROR);
        }
        String collectCpacha = (String) request.getSession().getAttribute("user_register");
        if (StringUtil.isEmpty(collectCpacha)) {
            return ResponseVo.errorByMsg(CodeMsg.CPACHA_EXPIRE);
        }
        if (!cpacha.equalsIgnoreCase(collectCpacha)) {
            return ResponseVo.errorByMsg(CodeMsg.CPACHA_ERROR);
        }
        if (isUsernameExist(user, 0L).getData()) {
            return ResponseVo.errorByMsg(CodeMsg.USER_USERNAME_ALREADY_EXIST);
        }
        if (userMapper.insertSelective(user) <= 0) {
            return ResponseVo.errorByMsg(CodeMsg.USER_ADD_ERROR);
        }
        mailUtil.sendMailAsync(MailTypeEnum.USER_REGISTER.getCode(), user.getEmail(), "");
        return ResponseVo.successByMsg(true, "注册成功！快去登录体验吧！");
    }

    @Override
    public ResponseVo<String> updateInfo(User user) {
        if (user == null) {
            return ResponseVo.errorByMsg(CodeMsg.DATA_ERROR);
        }
        User findUserById = userMapper.selectByPrimaryKey(user.getId());
        user.setPassword(findUserById.getPassword());
        CodeMsg validate = ValidateEntityUtil.validate(user);
        if (!validate.getCode().equals(CodeMsg.SUCCESS.getCode())) {
            return ResponseVo.errorByMsg(validate);
        }
        if (userMapper.updateByPrimaryKeySelective(user) <= 0) {
            return ResponseVo.errorByMsg(CodeMsg.USER_INFO_EDIT_ERROR);
        }
        Map<String, String> map = new HashMap<>();
        map.put("id", user.getId().toString());
        map.put("username", user.getUsername());
        map.put("email", user.getEmail());
        map.put("phone", user.getPhone());
        map.put("headPic", user.getHeadPic());
        String token = JwtUtil.getToken(map);

        return ResponseVo.successByMsg(token, "修改个人信息成功！");
    }

    @Override
    public ResponseVo<Boolean> updatePasswd(String prePassword, String newPassword, String reNewPassword, HttpServletRequest request) {
        if (StringUtil.isEmpty(prePassword)) {
            return ResponseVo.errorByMsg(CodeMsg.USER_PREPASSWORD_EMPTY);
        }
        if (StringUtil.isEmpty(newPassword)) {
            return ResponseVo.errorByMsg(CodeMsg.USER_NEWPASSWORD_EMPTY);
        }
        if (StringUtil.isEmpty(reNewPassword)) {
            return ResponseVo.errorByMsg(CodeMsg.USER_RENEWPASSWORD_EMPTY);
        }
        Object attr = request.getAttribute("id");
        Long uid = attr instanceof Long ? (Long) attr : Long.valueOf(String.valueOf(attr));
        User user = userMapper.selectByPrimaryKey(uid);
        if (!prePassword.equals(user.getPassword())) {
            return ResponseVo.errorByMsg(CodeMsg.USER_PREPASSWORD_ERROR);
        }
        user.setPassword(newPassword);
        CodeMsg validate = ValidateEntityUtil.validate(user);
        if (!validate.getCode().equals(CodeMsg.SUCCESS.getCode())) {
            return ResponseVo.errorByMsg(validate);
        }
        if (!newPassword.equals(reNewPassword)) {
            return ResponseVo.errorByMsg(CodeMsg.USER_RENEWPASSWORD_ERROR);
        }
        if (userMapper.updateByPrimaryKeySelective(user) <= 0) {
            return ResponseVo.errorByMsg(CodeMsg.USER_PASSWORD_EDIT_ERROR);
        }
        return ResponseVo.successByMsg(true, "修改密码成功！");
    }

    @Override
    public ResponseVo<PageInfo> getUserByPage(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<User> userList = userMapper.selectAll();
        PageInfo<User> pageInfo = new PageInfo<>(userList);
        pageInfo.setList(userList);
        return ResponseVo.success(pageInfo);
    }

    @Override
    public ResponseVo<PageInfo> getUserByPageAndContent(Integer pageNum, Integer pageSize, String content) {
        PageHelper.startPage(pageNum, pageSize);
        List<User> userList = userMapper.selectBySearchContent(content);
        PageInfo<User> pageInfo = new PageInfo<>(userList);
        pageInfo.setList(userList);
        return ResponseVo.success(pageInfo);
    }

    @Override
    public User selectByPrimaryKey(Long id) {
        return userMapper.selectByPrimaryKey(id);
    }

    @Override
    public ResponseVo<Boolean> updateUserPasswd(String passwd, Long userId) {
        if (StringUtil.isEmpty(passwd) || userId == null) {
            return ResponseVo.errorByMsg(CodeMsg.DATA_ERROR);
        }
        User user = userMapper.selectByPrimaryKey(userId);
        user.setPassword(passwd);
        CodeMsg validate = ValidateEntityUtil.validate(user);
        if (!validate.getCode().equals(CodeMsg.SUCCESS.getCode())) {
            return ResponseVo.errorByMsg(validate);
        }
        if (userMapper.updateByPrimaryKeySelective(user) <= 0) {
            return ResponseVo.errorByMsg(CodeMsg.USER_PASSWORD_EDIT_ERROR);
        }
        return ResponseVo.successByMsg(true, "修改用户密码成功！");
    }

    @Override
    public ResponseVo<Boolean> deleteUser(Long userId) {
        if (userId == null) {
            return ResponseVo.errorByMsg(CodeMsg.DATA_ERROR);
        }
        User user = userMapper.selectByPrimaryKey(userId);
        if (user == null) {
            return ResponseVo.errorByMsg(CodeMsg.USER_NOT_EXIST);
        }
        if (userMapper.deleteByPrimaryKey(userId) <= 0) {
            return ResponseVo.errorByMsg(CodeMsg.USER_DELETE_ERROR);
        }
        return ResponseVo.successByMsg(true, "删除用户成功！");
    }
}

