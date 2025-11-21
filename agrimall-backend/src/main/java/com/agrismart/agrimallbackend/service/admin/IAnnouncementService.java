package com.agrismart.agrimallbackend.service.admin;

import com.github.pagehelper.PageInfo;
import com.agrismart.agrimallbackend.dto.response.ResponseVo;
import com.agrismart.agrimallbackend.entity.admin.Announcement;

/**
 * 公告服务接口。
 *
 * 该接口定义了后台公告相关的业务操作方法，包括：
 *
 * - 公告列表查询（支持分页和按内容搜索）
 * - 公告发布
 * - 公告删除
 *
 * @author agrimall
 * @see com.agrismart.agrimallbackend.service.admin.impl.AnnouncementServiceImpl
 * @see com.agrismart.agrimallbackend.entity.admin.Announcement
 * @since 1.0
 */
public interface IAnnouncementService {

    /**
     * 分页查询公告列表。
     *
     * @param pageNum  页码，从 1 开始
     * @param pageSize 每页大小
     * @return 包含分页信息的公告列表
     */
    ResponseVo<PageInfo> getAnnouncementByPage(Integer pageNum, Integer pageSize);

    /**
     * 按内容分页查询公告列表。
     *
     * @param pageNum  页码，从 1 开始
     * @param pageSize 每页大小
     * @param content  公告内容（支持模糊搜索）
     * @return 包含分页信息的公告列表
     */
    ResponseVo<PageInfo> getAnnouncementByPageAndContent(Integer pageNum, Integer pageSize, String content);

    /**
     * 发布公告。
     *
     * @param announcement 公告对象（包含内容）
     * @param loginedId    当前登录的管理员 ID
     * @return 操作结果
     */
    ResponseVo<Boolean> add(Announcement announcement, Integer loginedId);

    /**
     * 删除公告。
     *
     * @param id 公告 ID
     * @return 操作结果
     */
    ResponseVo<Boolean> delete(Integer id);
}

