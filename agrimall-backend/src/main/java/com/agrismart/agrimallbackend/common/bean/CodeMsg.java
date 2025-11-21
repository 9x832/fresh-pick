package com.agrismart.agrimallbackend.common.bean;

/**
 * 错误码统一处理类。
 *
 * 该类用于统一管理系统中的所有错误码和错误消息。
 * 采用静态常量的方式定义错误码，便于统一管理和维护。
 *
 * 错误码编码规则：
 *
 * - 0：成功
 * - 负数：错误码，按模块划分范围
 * - -1 到 -99：通用错误码
 * - -1000 到 -1999：菜单管理错误码
 * - -2000 到 -2999：管理员管理错误码
 * - -3000 到 -3999：角色管理错误码
 * - -4000 到 -4999：邮件管理错误码
 * - -5000 到 -5999：系统管理错误码
 * - -6000 到 -6999：公告管理错误码
 * - -7000 到 -7999：用户管理错误码
 * - -8000 到 -8999：地址管理错误码
 * - -9000 到 -9999：商品管理错误码
 * - -10000 到 -10999：收藏管理错误码
 * - -11000 到 -11999：订单管理错误码
 * - -12000 到 -12999：评论管理错误码
 *
 * 使用示例：
 * <pre>
 * {@code
 * // 返回成功
 * return ResponseVo.success(data);
 *
 * // 返回错误
 * return ResponseVo.errorByMsg(CodeMsg.USER_NOT_EXIST);
 *
 * // 动态设置错误消息
 * CodeMsg codeMsg = CodeMsg.VALIDATE_ENTITY_ERROR;
 * codeMsg.setMsg("用户名长度不符合要求");
 * return ResponseVo.errorByMsg(codeMsg);
 * }
 * </pre>
 *
 * @author agrimall
 * @see com.agrismart.agrimallbackend.dto.response.ResponseVo
 * @since 1.0
 */
public class CodeMsg {

    /**
     * 错误码。
     * 0 表示成功，负数表示各种错误类型。
     */
    private Integer code;

    /**
     * 错误消息。
     * 用于向用户展示的错误提示信息。
     */
    private String msg;

    /**
     * 私有构造函数，用于创建错误码常量。
     *
     * @param code 错误码
     * @param msg  错误消息
     */
    private CodeMsg(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    /**
     * 无参构造函数。
     * 用于创建可修改的错误码对象，通常用于动态设置错误消息。
     */
    public CodeMsg() {
    }

    /**
     * 获取错误码。
     *
     * @return 错误码，0 表示成功，负数表示错误
     */
    public Integer getCode() {
        return code;
    }

    /**
     * 设置错误码。
     *
     * @param code 错误码
     */
    public void setCode(Integer code) {
        this.code = code;
    }

    /**
     * 获取错误消息。
     *
     * @return 错误消息
     */
    public String getMsg() {
        return msg;
    }

    /**
     * 设置错误消息。
     *
     * 该方法常用于动态设置错误消息，例如在实体验证时，
     * 使用 {@link #VALIDATE_ENTITY_ERROR} 并动态设置具体的验证错误信息。
     *
     * @param msg 错误消息
     */
    public void setMsg(String msg) {
        this.msg = msg;
    }

    // ==================== 通用错误码定义 (-1 到 -99) ====================

    /** 操作成功 */
    public static CodeMsg SUCCESS = new CodeMsg(0, "success");

    /** 非法数据错误 */
    public static CodeMsg DATA_ERROR = new CodeMsg(-1, "非法数据！");

    /**
     * 实体验证错误。
     * 该错误码的消息通常需要动态设置，用于返回具体的验证错误信息。
     */
    public static CodeMsg VALIDATE_ENTITY_ERROR = new CodeMsg(-2, "");

    /** 验证码为空 */
    public static CodeMsg CPACHA_EMPTY = new CodeMsg(-3, "验证码不能为空!");

    /** 会话已过期 */
    public static CodeMsg SESSION_EXPIRED = new CodeMsg(-4, "会话已失效，请刷新页面重试！");

    /** 验证码错误 */
    public static CodeMsg CPACHA_ERROR = new CodeMsg(-5, "验证码错误！");

    /** 用户会话已过期或未登录 */
    public static CodeMsg USER_SESSION_EXPIRED = new CodeMsg(-6, "还未登录或会话失效，请重新登录！");

    /** 图片格式不正确 */
    public static CodeMsg UPLOAD_PHOTO_SUFFIX_ERROR = new CodeMsg(-7, "图片格式不正确！");

    /** 图片上传失败 */
    public static CodeMsg UPLOAD_PHOTO_ERROR = new CodeMsg(-8, "图片上传失败！");

    /** 附件上传失败 */
    public static CodeMsg UPLOAD_ATTACHMENT_ERROR = new CodeMsg(-9, "附件上传失败！");

    /** 文件下载失败 */
    public static CodeMsg DOWNLOAD_FILE_ERROR = new CodeMsg(-10, "文件下载失败！");

    /** 验证码已过期 */
    public static CodeMsg CPACHA_EXPIRE = new CodeMsg(-11, "验证码已过期，请刷新验证码！");

    /** 系统错误 */
    public static CodeMsg SYSTEM_ERROR = new CodeMsg(-12, "系统出现了错误，请联系管理员！");

    // ==================== 菜单管理错误码 (-1000 到 -1999) ====================

    /** 菜单状态改变失败 */
    public static CodeMsg MENU_STATE_CHANGE_ERROR = new CodeMsg(-1000, "菜单状态改变失败！请联系管理员！");

    /** 菜单添加失败 */
    public static CodeMsg MENU_ADD_ERROR = new CodeMsg(-1001, "菜单添加失败！请联系管理员！");

    /** 菜单编辑失败 */
    public static CodeMsg MENU_EDIT_ERROR = new CodeMsg(-1002, "菜单编辑失败！请联系管理员！");

    /** 菜单删除失败 */
    public static CodeMsg MENU_DELETE_ERROR = new CodeMsg(-1003, "菜单删除失败！请联系管理员！");

    /** 菜单删除失败：存在子菜单 */
    public static CodeMsg MENU_CHILDREN_EXIST = new CodeMsg(-1004, "删除失败！请先删除该菜单下的子菜单！");

    // ==================== 管理员管理错误码 (-2000 到 -2999) ====================

    /** 管理员状态信息改变失败 */
    public static CodeMsg ADMIN_STATE_CHANGE_ERROR = new CodeMsg(-2000, "管理员状态信息改变失败！请联系管理员！");

    /** 管理员手机号长度不正确 */
    public static CodeMsg ADMIN_MOBILE_LENGTH_ERROR = new CodeMsg(-2001, "请输入正确的管理员手机号长度！");

    /** 管理员信息添加失败 */
    public static CodeMsg ADMIN_ADD_ERROR = new CodeMsg(-2002, "管理员信息添加失败！请联系管理员！");

    /** 管理员名字已存在 */
    public static CodeMsg ADMIN_NAME_EXIST = new CodeMsg(-2003, "管理员名字重复！请换一个！");

    /** 管理员信息编辑失败 */
    public static CodeMsg ADMIN_EDIT_ERROR = new CodeMsg(-2004, "管理员信息编辑失败！请联系管理员！");

    /** 管理员信息删除失败 */
    public static CodeMsg ADMIN_DELETE_ERROR = new CodeMsg(-2005, "管理员信息删除失败！请联系管理员！");

    /** 管理员角色信息编辑失败 */
    public static CodeMsg ADMIN_ROLE_EDIT_ERROR = new CodeMsg(-2006, "管理员对应角色信息编辑失败！请联系管理员！");

    // ==================== 角色管理错误码 (-3000 到 -3999) ====================

    /** 角色添加失败 */
    public static CodeMsg ROLE_ADD_ERROR = new CodeMsg(-3000, "角色添加失败！请联系管理员！");

    /** 角色编辑失败 */
    public static CodeMsg ROLE_EDIT_ERROR = new CodeMsg(-3001, "角色编辑失败！请联系管理员！");

    /** 角色删除失败 */
    public static CodeMsg ROLE_DELETE_ERROR = new CodeMsg(-3002, "角色删除失败！请联系管理员！");

    /** 角色名字已存在 */
    public static CodeMsg ROLE_NAME_EXIST = new CodeMsg(-3003, "角色名字重复！请换一个！");

    /** 角色权限保存失败 */
    public static CodeMsg ROLE_AUTHORITY_UPDATE_ERROR = new CodeMsg(-3004, "角色权限保存失败！请联系管理员！");

    /** 角色权限删除失败 */
    public static CodeMsg ROLE_AUTHORITY_DELETE_ERROR = new CodeMsg(-3005, "角色权限删除失败！请联系管理员！");

    // ==================== 邮件管理错误码 (-4000 到 -4999) ====================

    /** 邮件附件不存在 */
    public static CodeMsg MAIL_ATTACHMENT_NO_EXIST = new CodeMsg(-4000, "删除失败！这个附件已经不存在了！");

    /** 邮件附件删除失败 */
    public static CodeMsg MAIL_ATTACHMENT_DELETE_ERROR = new CodeMsg(-4001, "附件删除失败！请联系管理员！");

    /** 邮件收件人获取异常 */
    public static CodeMsg MAIL_RECEIVER_GET_ERROR = new CodeMsg(-4002, "发送失败！收件人获取异常！请联系管理员！");

    /** 邮件不存在 */
    public static CodeMsg MAIL_NO_EXIST = new CodeMsg(-4003, "删除失败！这个邮件已经不存在了！");

    /** 邮件删除失败 */
    public static CodeMsg MAIL_DELETE_ERROR = new CodeMsg(-4004, "删除失败！请联系管理员！");

    // ==================== 系统管理错误码 (-5000 到 -5999) ====================

    /** 用户名或密码错误 */
    public static CodeMsg USERNAME_OR_PASSWORD_ERROR = new CodeMsg(-5000, "用户名或者密码错误！");

    /** 用户已被冻结，无法登录 */
    public static CodeMsg USER_STATE_ERROR = new CodeMsg(-5001, "该用户已被冻结！无法登录！");

    /** 用户没有任何权限，无法登录 */
    public static CodeMsg USER_AUTHORITY_ERROR = new CodeMsg(-5002, "该用户没有任何权限！无法登录！");

    /** 个人信息保存失败 */
    public static CodeMsg PERSON_INFO_SAVE_ERROR = new CodeMsg(-5003, "个人信息保存失败！请联系管理员！");

    // ==================== 公告管理错误码 (-6000 到 -6999) ====================

    /** 公告添加失败 */
    public static CodeMsg ANNOUNCEMENT_ADD_ERROR = new CodeMsg(-6000, "公告添加失败！请联系管理员！");

    /** 公告不存在 */
    public static CodeMsg ANNOUNCEMENT_NOT_EXIST = new CodeMsg(-6001, "删除失败！这个公告已经不存在了！");

    /** 公告删除失败 */
    public static CodeMsg ANNOUNCEMENT_DELETE_ERROR = new CodeMsg(-6002, "公告删除失败！请联系管理员！");

    // ==================== 用户管理错误码 (-7000 到 -7999) ====================

    /** 确认密码为空 */
    public static CodeMsg USER_REPASSWORD_EMPTY = new CodeMsg(-7000, "确认密码不能为空！");

    /** 两次密码输入不一致 */
    public static CodeMsg USER_REPASSWORD_ERROR = new CodeMsg(-7001, "两次密码输入不一致！");

    /** 用户信息添加失败 */
    public static CodeMsg USER_ADD_ERROR = new CodeMsg(-7002, "用户信息添加失败，请联系管理员！");

    /** 用户名已存在 */
    public static CodeMsg USER_USERNAME_ALREADY_EXIST = new CodeMsg(-7003, "用户名称已经存在，请换一个！");

    /** 用户名为空 */
    public static CodeMsg USER_USERNAME_EMPTY = new CodeMsg(-7004, "用户名称不能为空！");

    /** 用户密码为空 */
    public static CodeMsg USER_PASSWORD_EMPTY = new CodeMsg(-7005, "用户密码不能为空！");

    /** 用户不存在 */
    public static CodeMsg USER_NOT_EXIST = new CodeMsg(-7006, "用户不存在！");

    /** 用户个人信息修改失败 */
    public static CodeMsg USER_INFO_EDIT_ERROR = new CodeMsg(-7007, "用户个人信息修改失败，请联系管理员！");

    /** 原密码为空 */
    public static CodeMsg USER_PREPASSWORD_EMPTY = new CodeMsg(-7008, "原密码不能为空！");

    /** 新密码为空 */
    public static CodeMsg USER_NEWPASSWORD_EMPTY = new CodeMsg(-7009, "新密码不能为空！");

    /** 确认新密码为空 */
    public static CodeMsg USER_RENEWPASSWORD_EMPTY = new CodeMsg(-7010, "确认新密码不能为空！");

    /** 原密码错误 */
    public static CodeMsg USER_PREPASSWORD_ERROR = new CodeMsg(-7011, "原密码错误！");

    /** 新密码和确认新密码不一致 */
    public static CodeMsg USER_RENEWPASSWORD_ERROR = new CodeMsg(-7012, "新密码和确认新密码输入不一致！");

    /** 用户密码修改失败 */
    public static CodeMsg USER_PASSWORD_EDIT_ERROR = new CodeMsg(-7013, "用户密码修改失败，请联系管理员！");

    /** 用户删除失败 */
    public static CodeMsg USER_DELETE_ERROR = new CodeMsg(-7014, "用户删除失败，请联系管理员！");

    // ==================== 地址管理错误码 (-8000 到 -8999) ====================

    /** 地址添加失败 */
    public static CodeMsg ADDRESS_ADD_ERROR = new CodeMsg(-8000, "地址添加失败，请联系管理员！");

    /** 地址数量超过限制（最多3个） */
    public static CodeMsg ADDRESS_NUM_EXCEED_LIMIT = new CodeMsg(-8001, "存储的地址数量不能超过3个！");

    /** 地址设置为订单首选失败 */
    public static CodeMsg ADDRESS_SET_FIRST_SELECTED_ERROR = new CodeMsg(-8002, "地址设置为订单首选失败，请联系管理员！");

    /** 地址删除失败 */
    public static CodeMsg ADDRESS_DELETE_ERROR = new CodeMsg(-8003, "地址删除失败，请联系管理员！");

    /** 地址更新失败 */
    public static CodeMsg ADDRESS_EDIT_ERROR = new CodeMsg(-8004, "地址更新失败，请联系管理员！");

    /** 地址不存在或已被删除 */
    public static CodeMsg ADDRESS_NOT_EXIST = new CodeMsg(-8005, "地址不存在或已被删除！");

    /** 无权操作该地址 */
    public static CodeMsg ADDRESS_NO_AUTHORITY = new CodeMsg(-8006, "无权操作该地址！");

    // ==================== 商品管理错误码 (-9000 到 -9999) ====================

    /** 商品种类添加失败 */
    public static CodeMsg PRODUCT_CATEGORY_ADD_ERROR = new CodeMsg(-9000, "商品种类添加失败，请联系管理员！");

    /** 商品种类修改失败 */
    public static CodeMsg PRODUCT_CATEGORY_EDIT_ERROR = new CodeMsg(-9001, "商品种类修改失败，请联系管理员！");

    /** 商品种类删除失败 */
    public static CodeMsg PRODUCT_CATEGORY_DELETE_ERROR = new CodeMsg(-9002, "商品种类删除失败，请联系管理员！");

    /** 商品添加失败 */
    public static CodeMsg PRODUCT_ADD_ERROR = new CodeMsg(-9003, "商品添加失败，请联系管理员！");

    /** 商品修改失败 */
    public static CodeMsg PRODUCT_EDIT_ERROR = new CodeMsg(-9004, "商品修改失败，请联系管理员！");

    /** 商品删除失败 */
    public static CodeMsg PRODUCT_DELETE_ERROR = new CodeMsg(-9005, "商品删除失败，请联系管理员！");

    /** 商品不存在 */
    public static CodeMsg PRODUCT_NOT_EXIST = new CodeMsg(-9006, "该商品已经不存在！");

    /** 商品库存不足 */
    public static CodeMsg PRODUCT_STOCK_ERROR = new CodeMsg(-9007, "该商品库存不够！");

    // ==================== 收藏管理错误码 (-10000 到 -10999) ====================

    /** 收藏添加失败 */
    public static CodeMsg COLLECT_ADD_ERROR = new CodeMsg(-10000, "收藏添加失败，请联系管理员！");

    /** 收藏删除失败 */
    public static CodeMsg COLLECT_DELETE_ERROR = new CodeMsg(-10001, "收藏删除失败，请联系管理员！");

    /** 商品已收藏，不能重复添加 */
    public static CodeMsg COLLECT_ALREADY_EXIST = new CodeMsg(-10002, "该商品已收藏，请勿重复添加！");

    // ==================== 订单管理错误码 (-11000 到 -11999) ====================

    /** 订单信息添加失败 */
    public static CodeMsg ORDER_ADD_ERROR = new CodeMsg(-11000, "订单信息添加失败，请联系管理员！");

    /** 订单详情信息添加失败 */
    public static CodeMsg ORDER_ITEM_ADD_ERROR = new CodeMsg(-11001, "订单详情信息添加失败，请联系管理员！");

    /** 订单留言长度超过限制（最多50个字符） */
    public static CodeMsg ORDER_REMARK_EXCEED_LENGTH = new CodeMsg(-11002, "订单留言不能超过50！");

    /** 订单配送地址为空 */
    public static CodeMsg ORDER_ADDRESS_EMPTY = new CodeMsg(-11003, "订单配送地址不能为空！");

    /** 订单修改失败 */
    public static CodeMsg ORDER_UPDATE_ERROR = new CodeMsg(-11004, "订单修改失败，请联系管理员！");

    /**
     * 订单错误。
     * 该错误码的消息通常需要动态设置，用于返回具体的订单错误信息。
     */
    public static CodeMsg ORDER_ERROR = new CodeMsg(-11005, "");

    /** 订单不存在 */
    public static CodeMsg ORDER_NOT_EXIST = new CodeMsg(-11006, "该订单不存在！");

    /** 订单状态修改失败 */
    public static CodeMsg ORDER_STATE_EDIT_ERROR = new CodeMsg(-11007, "订单状态修改失败，请联系管理员！");

    /** 订单删除失败 */
    public static CodeMsg ORDER_DELETE_ERROR = new CodeMsg(-11008, "订单删除失败，请联系管理员！");

    // ==================== 评论管理错误码 (-12000 到 -12999) ====================

    /** 评论添加失败 */
    public static CodeMsg COMMENT_ADD_ERROR = new CodeMsg(-12000, "评论添加失败，请联系管理员！");

    /** 评论不存在 */
    public static CodeMsg COMMENT_NOT_EXIST = new CodeMsg(-12001, "该评论不存在！");

    /** 评论删除失败 */
    public static CodeMsg COMMENT_DELETE_ERROR = new CodeMsg(-12002, "评论删除失败，请联系管理员！");
}

