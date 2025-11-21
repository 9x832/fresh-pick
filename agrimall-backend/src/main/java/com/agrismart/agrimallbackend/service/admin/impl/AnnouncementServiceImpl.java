package com.agrismart.agrimallbackend.service.admin.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.agrismart.agrimallbackend.common.bean.CodeMsg;
import com.agrismart.agrimallbackend.common.util.ValidateEntityUtil;
import com.agrismart.agrimallbackend.dto.response.ResponseVo;
import com.agrismart.agrimallbackend.entity.admin.Announcement;
import com.agrismart.agrimallbackend.mapper.admin.AnnouncementMapper;
import com.agrismart.agrimallbackend.service.admin.IAnnouncementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 公告服务实现类。
 *
 * 该类实现了 {@link IAnnouncementService} 接口，提供公告相关的业务逻辑实现。
 *
 * @author agrimall
 * @see com.agrismart.agrimallbackend.service.admin.IAnnouncementService
 * @since 1.0
 */
@Service
public class AnnouncementServiceImpl implements IAnnouncementService {

    /**
     * 公告数据访问对象。
     */
    @Autowired
    private AnnouncementMapper announcementMapper;

    @Override
    public ResponseVo<PageInfo> getAnnouncementByPage(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Announcement> allAnnouncements = announcementMapper.selectAll();
        PageInfo<Announcement> pageInfo = new PageInfo<>(allAnnouncements);
        pageInfo.setList(allAnnouncements);
        return ResponseVo.success(pageInfo);
    }

    @Override
    public ResponseVo<PageInfo> getAnnouncementByPageAndContent(Integer pageNum, Integer pageSize, String content) {
        PageHelper.startPage(pageNum, pageSize);
        List<Announcement> selectBySearchContent = announcementMapper.selectBySearchContent(content);
        PageInfo<Announcement> pageInfo = new PageInfo<>(selectBySearchContent);
        pageInfo.setList(selectBySearchContent);
        return ResponseVo.success(pageInfo);
    }

    @Override
    public ResponseVo<Boolean> add(Announcement announcement, Integer loginedId) {
        if (announcement == null) {
            return ResponseVo.errorByMsg(CodeMsg.DATA_ERROR);
        }
        announcement.setAdminId(loginedId);
        CodeMsg validate = ValidateEntityUtil.validate(announcement);
        if (!validate.getCode().equals(CodeMsg.SUCCESS.getCode())) {
            return ResponseVo.errorByMsg(validate);
        }
        if (announcementMapper.insertSelective(announcement) <= 0) {
            return ResponseVo.errorByMsg(CodeMsg.ANNOUNCEMENT_ADD_ERROR);
        }
        return ResponseVo.successByMsg(true, "发布成功！");
    }

    @Override
    public ResponseVo<Boolean> delete(Integer id) {
        if (announcementMapper.selectByPrimaryKey(id) == null) {
            return ResponseVo.errorByMsg(CodeMsg.ANNOUNCEMENT_NOT_EXIST);
        }
        if (announcementMapper.deleteByPrimaryKey(id) <= 0) {
            return ResponseVo.errorByMsg(CodeMsg.ANNOUNCEMENT_DELETE_ERROR);
        }
        return ResponseVo.successByMsg(true, "删除成功！");
    }
}

