package com.agrismart.agrimallbackend.controller.admin;

import com.agrismart.agrimallbackend.common.bean.CodeMsg;
import com.agrismart.agrimallbackend.common.constant.SessionConstant;
import com.agrismart.agrimallbackend.common.enums.MenuStateEnum;
import com.agrismart.agrimallbackend.dto.request.AdminLoginRequest;
import com.agrismart.agrimallbackend.dto.response.ResponseVo;
import com.agrismart.agrimallbackend.entity.admin.Admin;
import com.agrismart.agrimallbackend.entity.admin.Authority;
import com.agrismart.agrimallbackend.entity.admin.Menu;
import com.agrismart.agrimallbackend.mapper.admin.AdminMapper;
import com.agrismart.agrimallbackend.mapper.admin.AnnouncementMapper;
import com.agrismart.agrimallbackend.mapper.admin.AttachmentMapper;
import com.agrismart.agrimallbackend.mapper.admin.AuthorityMapper;
import com.agrismart.agrimallbackend.mapper.admin.MailMapper;
import com.agrismart.agrimallbackend.mapper.admin.MenuMapper;
import com.agrismart.agrimallbackend.service.admin.IAdminService;
import com.agrismart.agrimallbackend.service.admin.IAnnouncementService;
import com.agrismart.agrimallbackend.service.admin.IMenuService;
import com.agrismart.agrimallbackend.service.common.IOrderService;
import com.github.pagehelper.PageInfo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 后台系统管理控制器。
 *
 * 该控制器提供后台管理系统的核心功能接口，包括：
 *
 * - 管理员登录和登出
 * - 管理员个人信息管理
 * - 当前登录管理员的菜单权限查询
 * - 系统仪表盘数据统计
 *
 * 接口路径：{@code /api/admin}
 *
 * 权限要求：
 *
 * - 登录和登出接口不需要认证
 * - 其他接口需要管理员登录（通过 {@link com.agrismart.agrimallbackend.interceptor.AdminInterceptor} 验证）
 *
 * @author agrimall
 * @see com.agrismart.agrimallbackend.service.admin.IAdminService
 * @see com.agrismart.agrimallbackend.service.admin.IMenuService
 * @since 1.0
 */
@RestController
@RequestMapping("/api/admin")
public class AdminSystemController {

    /**
     * 管理员服务接口。
     */
    private final IAdminService adminService;

    /**
     * 菜单服务接口。
     */
    private final IMenuService menuService;

    /**
     * 公告服务接口。
     */
    private final IAnnouncementService announcementService;

    /**
     * 订单服务接口。
     */
    private final IOrderService orderService;

    /**
     * 菜单数据访问接口。
     */
    private final MenuMapper menuMapper;

    /**
     * 权限数据访问接口。
     */
    private final AuthorityMapper authorityMapper;

    /**
     * 公告数据访问接口。
     */
    private final AnnouncementMapper announcementMapper;

    /**
     * 邮件数据访问接口。
     */
    private final MailMapper mailMapper;

    /**
     * 附件数据访问接口。
     */
    private final AttachmentMapper attachmentMapper;

    /**
     * 管理员数据访问接口。
     */
    private final AdminMapper adminMapper;

    /**
     * 商品数据访问接口。
     */
    private final com.agrismart.agrimallbackend.mapper.common.ProductMapper productMapper;

    /**
     * 用户数据访问接口。
     */
    private final com.agrismart.agrimallbackend.mapper.common.UserMapper userMapper;

    /**
     * 订单数据访问接口。
     */
    private final com.agrismart.agrimallbackend.mapper.common.OrderMapper orderMapper;

    /**
     * 评论数据访问接口。
     */
    private final com.agrismart.agrimallbackend.mapper.common.CommentMapper commentMapper;

    /**
     * 商品分类数据访问接口。
     */
    private final com.agrismart.agrimallbackend.mapper.common.ProductCategoryMapper productCategoryMapper;

    /**
     * 收藏数据访问接口。
     */
    private final com.agrismart.agrimallbackend.mapper.home.CollectMapper collectMapper;

    /**
     * 构造函数，注入依赖。
     *
     * @param adminService         管理员服务接口
     * @param menuService          菜单服务接口
     * @param announcementService  公告服务接口
     * @param orderService         订单服务接口
     * @param menuMapper           菜单数据访问接口
     * @param authorityMapper      权限数据访问接口
     * @param announcementMapper   公告数据访问接口
     * @param mailMapper           邮件数据访问接口
     * @param attachmentMapper     附件数据访问接口
     * @param adminMapper          管理员数据访问接口
     * @param productMapper        商品数据访问接口
     * @param userMapper           用户数据访问接口
     * @param orderMapper          订单数据访问接口
     * @param commentMapper        评论数据访问接口
     * @param productCategoryMapper 商品分类数据访问接口
     * @param collectMapper        收藏数据访问接口
     */
    @Autowired
    public AdminSystemController(IAdminService adminService,
                                 IMenuService menuService,
                                 IAnnouncementService announcementService,
                                 IOrderService orderService,
                                 MenuMapper menuMapper,
                                 AuthorityMapper authorityMapper,
                                 AnnouncementMapper announcementMapper,
                                 MailMapper mailMapper,
                                 AttachmentMapper attachmentMapper,
                                 AdminMapper adminMapper,
                                 com.agrismart.agrimallbackend.mapper.common.ProductMapper productMapper,
                                 com.agrismart.agrimallbackend.mapper.common.UserMapper userMapper,
                                 com.agrismart.agrimallbackend.mapper.common.OrderMapper orderMapper,
                                 com.agrismart.agrimallbackend.mapper.common.CommentMapper commentMapper,
                                 com.agrismart.agrimallbackend.mapper.common.ProductCategoryMapper productCategoryMapper,
                                 com.agrismart.agrimallbackend.mapper.home.CollectMapper collectMapper) {
        this.adminService = adminService;
        this.menuService = menuService;
        this.announcementService = announcementService;
        this.orderService = orderService;
        this.menuMapper = menuMapper;
        this.authorityMapper = authorityMapper;
        this.announcementMapper = announcementMapper;
        this.mailMapper = mailMapper;
        this.attachmentMapper = attachmentMapper;
        this.adminMapper = adminMapper;
        this.productMapper = productMapper;
        this.userMapper = userMapper;
        this.orderMapper = orderMapper;
        this.commentMapper = commentMapper;
        this.productCategoryMapper = productCategoryMapper;
        this.collectMapper = collectMapper;
    }

    /**
     * 管理员登录接口。
     *
     * 验证管理员用户名、密码和验证码，登录成功后会将管理员信息存储到 Session 中。
     *
     * @param requestBody 登录请求对象，包含管理员名称、密码和验证码
     * @param request     HTTP 请求对象，用于存储 Session
     * @return 操作结果，true 表示登录成功
     */
    @PostMapping("/login")
    public ResponseVo<Boolean> login(@Valid @RequestBody AdminLoginRequest requestBody,
                                     HttpServletRequest request) {
        return adminService.login(requestBody.getName(), requestBody.getPassword(), requestBody.getCaptcha(), request);
    }

    /**
     * 管理员登出接口。
     *
     * 清除 Session 中的管理员登录信息。
     *
     * @param request HTTP 请求对象，用于清除 Session
     * @return 操作结果，true 表示登出成功
     */
    @PostMapping("/logout")
    public ResponseVo<Boolean> logout(HttpServletRequest request) {
        return adminService.logout(request);
    }

    /**
     * 获取当前登录管理员的个人信息。
     *
     * 从 Session 中获取当前登录的管理员信息，并清除密码字段后返回。
     *
     * @param request HTTP 请求对象，用于获取 Session 中的管理员信息
     * @return 管理员信息（不包含密码）
     */
    @GetMapping("/profile")
    public ResponseVo<Admin> profile(HttpServletRequest request) {
        Admin logined = (Admin) request.getSession().getAttribute(SessionConstant.SESSION_ADMIN_LOGIN_KEY);
        if (logined == null) {
            return ResponseVo.errorByMsg(CodeMsg.USER_SESSION_EXPIRED);
        }
        Admin result = new Admin();
        BeanUtils.copyProperties(logined, result);
        result.setPassword(null);  // 清除密码，不返回给前端
        return ResponseVo.success(result);
    }

    /**
     * 更新当前登录管理员的个人信息。
     *
     * 更新当前登录管理员的个人信息，更新成功后会自动更新 Session 中的管理员信息。
     *
     * @param admin   管理员实体对象，包含要更新的信息
     * @param request HTTP 请求对象，用于更新 Session
     * @return 操作结果，true 表示更新成功
     */
    @PutMapping("/profile")
    public ResponseVo<Boolean> updateProfile(@RequestBody Admin admin, HttpServletRequest request) {
        return adminService.savePersonInfo(admin, request);
    }

    /**
     * 获取当前登录管理员的菜单权限。
     *
     * 根据当前登录管理员的角色，查询该角色拥有的菜单权限。
     * 返回的菜单按层级分类：一级菜单、二级菜单、三级菜单。
     * 只返回开启状态的菜单。
     *
     * @param request HTTP 请求对象，用于获取当前登录的管理员信息
     * @return 包含菜单层级结构的响应对象
     *
     * - firstMenus：一级菜单列表
     * - secondMenus：二级菜单列表
     * - thirdMenus：三级菜单列表
     *
     */
    @GetMapping("/menus/current")
    public ResponseVo<Map<String, Object>> menus(HttpServletRequest request) {
        Admin logined = (Admin) request.getSession().getAttribute(SessionConstant.SESSION_ADMIN_LOGIN_KEY);
        if (logined == null) {
            return ResponseVo.errorByMsg(CodeMsg.USER_SESSION_EXPIRED);
        }
        // 查询当前角色拥有的权限
        List<Authority> authorities = authorityMapper.selectByRoleId(logined.getRoleId());
        Set<Integer> menuIds = authorities.stream().map(Authority::getMenuId).collect(Collectors.toSet());
        // 查询开启状态的菜单
        List<Menu> allMenus = menuMapper.selectByStateAndPrimaryKeys(MenuStateEnum.OPEN.getCode(), menuIds);
        
        // 按层级分类菜单
        Map<String, Object> payload = new HashMap<>(3);
        payload.put("firstMenus", menuService.getFirstMenus(allMenus).getData());
        payload.put("secondMenus", menuService.getSecondMenus(allMenus).getData());
        payload.put("thirdMenus", menuService.getThirdMenus(allMenus).getData());
        return ResponseVo.success(payload);
    }

    /**
     * 获取系统仪表盘数据。
     *
     * 返回后台管理系统的统计数据，包括：
     *
     * - 订单统计：今日、本周、本月的订单数量
     * - 系统数据统计：公告总数、邮件总数、附件总数、管理员总数
     * - 最新公告列表：最近 5 条公告
     *
     * @return 包含仪表盘数据的响应对象
     *
     * - todayOrderCount：今日订单数量
     * - weekOrderCount：本周订单数量
     * - monthOrderCount：本月订单数量
     * - announcementTotal：公告总数
     * - mailTotal：邮件总数
     * - attachmentTotal：附件总数
     * - adminTotal：管理员总数
     * - latestAnnouncements：最新公告列表（分页信息）
     *
     */
    @GetMapping("/dashboard")
    public ResponseVo<Map<String, Object>> dashboard() {
        Map<String, Object> payload = new HashMap<>(10);
        payload.put("todayOrderCount", orderService.getOrderByDay().size());
        payload.put("weekOrderCount", orderService.getOrderByWeek().size());
        payload.put("monthOrderCount", orderService.getOrderByMonth().size());
        payload.put("announcementTotal", announcementMapper.getTotal());
        payload.put("mailTotal", mailMapper.getTotal());
        payload.put("attachmentTotal", attachmentMapper.getTotal());
        payload.put("adminTotal", adminMapper.selectAll().size());
        // 添加更有价值的业务数据
        payload.put("productTotal", productMapper.selectAll().size());
        payload.put("userTotal", userMapper.selectAll().size());
        payload.put("orderTotal", orderMapper.selectAll().size());
        payload.put("commentTotal", commentMapper.selectAll().size());
        payload.put("categoryTotal", productCategoryMapper.selectAll().size());
        payload.put("collectTotal", collectMapper.selectAllSimple().size());
        payload.put("totalSalesAmount", orderMapper.getTotalSalesAmount());
        // 添加月度订单统计
        payload.put("orderCountByMonth", orderService.getOrderCountByMonth());
        // 添加按星期分布统计
        payload.put("orderCountByDayOfWeek", orderService.getOrderCountByDayOfWeek());
        PageInfo pageInfo = announcementService.getAnnouncementByPage(1, 5).getData();
        payload.put("latestAnnouncements", pageInfo);
        return ResponseVo.success(payload);
    }
}

