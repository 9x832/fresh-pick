package com.agrismart.agrimallbackend.controller.admin;

import com.agrismart.agrimallbackend.common.bean.CodeMsg;
import com.agrismart.agrimallbackend.common.constant.SessionConstant;
import com.agrismart.agrimallbackend.dto.response.ResponseVo;
import com.agrismart.agrimallbackend.entity.admin.Admin;
import com.agrismart.agrimallbackend.entity.admin.Announcement;
import com.agrismart.agrimallbackend.service.admin.IAnnouncementService;
import com.github.pagehelper.PageInfo;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 公告管理控制器。
 *
 * 该控制器提供后台管理系统对公告的管理接口，包括：
 *
 * - 公告列表查询（支持分页和搜索）
 * - 公告创建
 * - 公告删除
 *
 * 接口路径：{@code /api/admin/announcements}
 *
 * 权限要求：
 *
 * - 需要管理员登录（通过 {@link com.agrismart.agrimallbackend.interceptor.AdminInterceptor} 验证）
 * - 需要相应的菜单权限
 *
 * @author agrimall
 * @see com.agrismart.agrimallbackend.service.admin.IAnnouncementService
 * @see com.agrismart.agrimallbackend.entity.admin.Announcement
 * @since 1.0
 */
@RestController
@RequestMapping("/api/admin/announcements")
public class AnnouncementManageController {

    /**
     * 公告服务接口。
     */
    private final IAnnouncementService announcementService;

    /**
     * 构造函数，注入依赖。
     *
     * @param announcementService 公告服务接口
     */
    @Autowired
    public AnnouncementManageController(IAnnouncementService announcementService) {
        this.announcementService = announcementService;
    }

    /**
     * 分页查询公告列表。
     *
     * 支持按内容（公告标题、内容等）搜索，如果不提供搜索内容则查询所有公告。
     *
     * @param pageNum  页码，从 1 开始，默认为 1
     * @param pageSize 每页大小，默认为 5
     * @param content  搜索内容（可选），用于模糊搜索公告标题、内容等
     * @return 分页信息，包含公告列表
     */
    @GetMapping
    public ResponseVo<PageInfo> list(@RequestParam(defaultValue = "1") Integer pageNum,
                                     @RequestParam(defaultValue = "5") Integer pageSize,
                                     @RequestParam(required = false) String content) {
        return (content == null || content.trim().isEmpty())
                ? announcementService.getAnnouncementByPage(pageNum, pageSize)
                : announcementService.getAnnouncementByPageAndContent(pageNum, pageSize, content);
    }

    /**
     * 创建公告。
     *
     * 创建新的公告，需要提供公告的基本信息（标题、内容等）。
     * 公告的创建者会自动设置为当前登录的管理员。
     *
     * @param announcement 公告实体对象，包含公告的基本信息
     * @param request      HTTP 请求对象，用于获取当前登录的管理员信息
     * @return 操作结果，true 表示创建成功
     */
    @PostMapping
    public ResponseVo<Boolean> create(@RequestBody Announcement announcement, HttpServletRequest request) {
        Admin admin = (Admin) request.getSession().getAttribute(SessionConstant.SESSION_ADMIN_LOGIN_KEY);
        if (admin == null) {
            return ResponseVo.errorByMsg(CodeMsg.USER_SESSION_EXPIRED);
        }
        return announcementService.add(announcement, admin.getId());
    }

    /**
     * 删除公告。
     *
     * 根据公告 ID 删除指定的公告。
     *
     * @param id 公告 ID（路径变量）
     * @return 操作结果，true 表示删除成功
     */
    @DeleteMapping("/{id}")
    public ResponseVo<Boolean> delete(@PathVariable("id") Integer id) {
        return announcementService.delete(id);
    }
}


