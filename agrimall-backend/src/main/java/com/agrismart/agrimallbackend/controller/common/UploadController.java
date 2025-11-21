package com.agrismart.agrimallbackend.controller.common;

import com.agrismart.agrimallbackend.common.bean.CodeMsg;
import com.agrismart.agrimallbackend.common.util.StringUtil;
import com.agrismart.agrimallbackend.common.util.ValidateEntityUtil;
import com.agrismart.agrimallbackend.dto.response.ResponseVo;
import com.agrismart.agrimallbackend.entity.admin.Attachment;
import com.agrismart.agrimallbackend.mapper.admin.AttachmentMapper;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * 通用文件上传控制器。
 * 该控制器提供文件上传功能，包括：
 * - 图片上传（支持格式验证和大小限制）
 * - 附件上传（保存到数据库，支持下载）
 * - 附件下载
 * 接口路径：{@code /api/common/upload}
 * 文件存储：
 * - 图片和附件都按日期分目录存储（格式：yyyyMMdd）
 * - 文件名使用时间戳，避免文件名冲突
 *
 * @author agrimall
 * @see com.agrismart.agrimallbackend.entity.admin.Attachment
 * @since 1.0
 */
@RestController
@RequestMapping("/api/common/upload")
public class UploadController {

    /**
     * 允许的图片文件后缀名。
     * 从配置文件 {@code xqx.upload.photo.sufix} 中读取。
     */
    @Value("${xqx.upload.photo.sufix}")
    private String uploadPhotoSufix;

    /**
     * 图片最大大小（KB）。
     * 从配置文件 {@code xqx.upload.photo.maxsize} 中读取。
     */
    @Value("${xqx.upload.photo.maxsize}")
    private long uploadPhotoMaxSize;

    /**
     * 图片上传路径。
     * 从配置文件 {@code xqx.upload.photo.path} 中读取。
     */
    @Value("${xqx.upload.photo.path}")
    private String uploadPhotoPath;

    /**
     * 附件最大大小（KB）。
     * 从配置文件 {@code xqx.upload.attachment.maxsize} 中读取。
     */
    @Value("${xqx.upload.attachment.maxsize}")
    private long uploadAttachmentMaxSize;

    /**
     * 附件上传路径。
     * 从配置文件 {@code xqx.upload.attachment.path} 中读取。
     */
    @Value("${xqx.upload.attachment.path}")
    private String uploadAttachmentPath;

    /**
     * 附件数据访问接口。
     */
    private final AttachmentMapper attachmentMapper;

    /**
     * 构造函数，注入依赖。
     *
     * @param attachmentMapper 附件数据访问接口
     */
    @Autowired
    public UploadController(AttachmentMapper attachmentMapper) {
        this.attachmentMapper = attachmentMapper;
    }

    /**
     * 初始化方法，处理相对路径。
     * 将相对路径转换为基于工作目录的绝对路径。
     */
    @PostConstruct
    public void initPaths() {
        // 处理图片路径：如果是相对路径，转换为绝对路径
        if (uploadPhotoPath != null && !new File(uploadPhotoPath).isAbsolute()) {
            String baseDir = System.getProperty("user.dir");
            uploadPhotoPath = new File(baseDir, uploadPhotoPath).getAbsolutePath();
        }
        // 处理附件路径：如果是相对路径，转换为绝对路径
        if (uploadAttachmentPath != null && !new File(uploadAttachmentPath).isAbsolute()) {
            String baseDir = System.getProperty("user.dir");
            uploadAttachmentPath = new File(baseDir, uploadAttachmentPath).getAbsolutePath();
        }
    }

    /**
     * 上传图片。
     * 上传图片文件，支持格式验证和大小限制。上传成功后返回图片的相对路径。
     * 验证规则：
     * - 检查文件后缀名是否在允许的列表中
     * - 检查文件大小是否超过限制
     *
     * @param photo 上传的图片文件
     * @return 图片相对路径（格式：yyyyMMdd/时间戳.后缀），例如 "20231201/1234567890.jpg"
     */
    @PostMapping("/photo")
    public ResponseVo<String> uploadPhoto(@RequestParam("photo") MultipartFile photo) {
        String originalFilename = photo.getOriginalFilename();
        if (originalFilename == null) {
            return ResponseVo.errorByMsg(CodeMsg.UPLOAD_PHOTO_ERROR);
        }
        
        // 检查文件后缀名
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        if (!uploadPhotoSufix.contains(suffix.toLowerCase())) {
            return ResponseVo.errorByMsg(CodeMsg.UPLOAD_PHOTO_SUFFIX_ERROR);
        }
        
        // 检查文件大小（转换为 KB）
        if (photo.getSize() / 1024 > uploadPhotoMaxSize) {
            CodeMsg codeMsg = CodeMsg.UPLOAD_PHOTO_ERROR;
            codeMsg.setMsg("图片大小不能超过" + (uploadPhotoMaxSize / 1024) + "M");
            return ResponseVo.errorByMsg(codeMsg);
        }
        
        // 创建上传目录
        File filePath = new File(uploadPhotoPath);
        if (!filePath.exists()) {
            filePath.mkdirs();
        }
        
        // 按日期创建子目录
        String dateFolder = StringUtil.getFormatterDate(new Date(), "yyyyMMdd");
        filePath = new File(uploadPhotoPath + "/" + dateFolder);
        if (!filePath.exists()) {
            filePath.mkdirs();
        }
        
        // 生成文件名：日期目录/时间戳.后缀
        String filename = dateFolder + "/" + System.currentTimeMillis() + suffix;
        try {
            photo.transferTo(new File(uploadPhotoPath + "/" + filename));
        } catch (IOException e) {
            return ResponseVo.errorByMsg(CodeMsg.UPLOAD_PHOTO_ERROR);
        }
        return ResponseVo.success(filename);
    }

    /**
     * 上传附件。
     * 上传附件文件，保存到服务器并记录到数据库。附件信息包括文件名、大小、上传者等。
     * 上传成功后返回附件实体对象。
     * 验证规则：
     * - 检查文件大小是否超过限制
     * - 使用 {@link com.agrismart.agrimallbackend.common.util.ValidateEntityUtil} 验证附件实体
     * 注意：该接口需要管理员登录，管理员 ID 从请求属性中获取（由拦截器设置）。
     *
     * @param attachment 上传的附件文件
     * @param adminId    管理员 ID（从请求属性中获取，由拦截器设置）
     * @return 附件实体对象，包含附件 ID、文件名、大小等信息
     */
    @PostMapping("/attachment")
    public ResponseVo<Attachment> uploadAttachment(@RequestParam("attachment") MultipartFile attachment,
                                                   @RequestAttribute("aid") Integer adminId) {
        String originalFilename = attachment.getOriginalFilename();
        if (originalFilename == null) {
            return ResponseVo.errorByMsg(CodeMsg.UPLOAD_ATTACHMENT_ERROR);
        }
        
        // 检查文件大小（转换为 KB）
        if (attachment.getSize() / 1024 > uploadAttachmentMaxSize) {
            CodeMsg codeMsg = CodeMsg.UPLOAD_ATTACHMENT_ERROR;
            codeMsg.setMsg("附件大小不能超过" + (uploadAttachmentMaxSize / 1024) + "M");
            return ResponseVo.errorByMsg(codeMsg);
        }
        
        // 创建上传目录
        File filePath = new File(uploadAttachmentPath);
        if (!filePath.exists()) {
            filePath.mkdirs();
        }
        
        // 按日期创建子目录
        String dateFolder = StringUtil.getFormatterDate(new Date(), "yyyyMMdd");
        filePath = new File(uploadAttachmentPath + "/" + dateFolder);
        if (!filePath.exists()) {
            filePath.mkdirs();
        }
        
        // 生成文件名：日期目录/时间戳.后缀
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        String filename = dateFolder + "/" + System.currentTimeMillis() + suffix;
        try {
            attachment.transferTo(new File(uploadAttachmentPath + "/" + filename));
        } catch (IOException e) {
            return ResponseVo.errorByMsg(CodeMsg.UPLOAD_ATTACHMENT_ERROR);
        }
        
        // 计算文件大小（KB），保留两位小数
        BigDecimal size = BigDecimal.valueOf((double) attachment.getSize() / 1024).setScale(2, BigDecimal.ROUND_HALF_EVEN);
        
        // 创建附件实体并验证
        Attachment saveAttachment = new Attachment(null, adminId, filename, originalFilename, size);
        CodeMsg validate = ValidateEntityUtil.validate(saveAttachment);
        if (!CodeMsg.SUCCESS.getCode().equals(validate.getCode())) {
            return ResponseVo.errorByMsg(validate);
        }
        
        // 保存到数据库
        if (attachmentMapper.insertSelective(saveAttachment) <= 0) {
            return ResponseVo.errorByMsg(CodeMsg.UPLOAD_ATTACHMENT_ERROR);
        }
        return ResponseVo.success(saveAttachment);
    }

    /**
     * 下载附件。
     * 根据附件 ID 从数据库查询附件信息，然后从服务器读取文件并返回给客户端下载。
     * 响应头设置：
     * - Content-Disposition: attachment，提示浏览器下载文件
     * - Content-Type: application/octet-stream，二进制流
     *
     * @param id 附件 ID（路径变量）
     * @return 附件文件字节数组响应，如果附件不存在则返回 404
     * @throws IOException 文件读取失败时抛出
     */
    @GetMapping("/attachment/{id}")
    public ResponseEntity<byte[]> downloadAttachment(@PathVariable("id") Integer id) throws IOException {
        // 从数据库查询附件信息
        Attachment attachment = attachmentMapper.selectByPrimaryKey(id);
        if (attachment == null) {
            return ResponseEntity.notFound().build();
        }
        
        // 读取文件
        File file = new File(uploadAttachmentPath, attachment.getUrl());
        if (!file.exists()) {
            return ResponseEntity.notFound().build();
        }
        
        try (FileInputStream inputStream = new FileInputStream(file)) {
            byte[] data = IOUtils.toByteArray(inputStream);
            String filename = attachment.getName();
            
            // 设置响应头，支持中文文件名
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" +
                            new String(filename.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1))
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(data);
        }
    }
}

