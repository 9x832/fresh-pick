package com.agrismart.agrimallbackend.controller.common;

import com.agrismart.agrimallbackend.common.util.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

/**
 * UEditor 富文本编辑器集成控制器。
 *
 * 该控制器提供 UEditor 富文本编辑器的后端支持接口，包括：
 *
 * - UEditor 配置信息
 * - 图片上传功能
 * - 图片查看功能
 *
 * 接口路径：{@code /api/common/ueditor}
 *
 * UEditor 是一个开源的富文本编辑器，该控制器实现了 UEditor 所需的后端接口。
 *
 * 使用场景：
 *
 * - 后台管理系统的富文本编辑（如公告内容、商品详情等）
 *
 * @author agrimall
 * @since 1.0
 */
@RestController
@RequestMapping("/api/common/ueditor")
public class UeditorController {

    /**
     * 图片上传路径。
     * 从配置文件 {@code xqx.upload.photo.path} 中读取。
     */
    @Value("${xqx.upload.photo.path}")
    private String uploadPhotoPath;

    /**
     * 初始化方法，处理相对路径。
     * 将相对路径转换为基于工作目录的绝对路径。
     */
    @PostConstruct
    public void initPath() {
        if (uploadPhotoPath != null && !new File(uploadPhotoPath).isAbsolute()) {
            String baseDir = System.getProperty("user.dir");
            uploadPhotoPath = new File(baseDir, uploadPhotoPath).getAbsolutePath();
        }
    }

    /**
     * 获取 UEditor 配置信息。
     *
     * 返回 UEditor 编辑器的配置 JSON，包括图片上传参数、允许的文件类型、最大文件大小等。
     *
     * @return UEditor 配置 JSON 字符串
     */
    @GetMapping(value = "/config", produces = MediaType.APPLICATION_JSON_VALUE)
    public String config() {
        return "{\n" +
                "            \"imageActionName\": \"uploadimage\",\n" +
                "                \"imageFieldName\": \"file\", \n" +
                "                \"imageMaxSize\": 2048000, \n" +
                "                \"imageAllowFiles\": [\".png\", \".jpg\", \".jpeg\", \".gif\", \".bmp\"], \n" +
                "                \"imageCompressEnable\": true, \n" +
                "                \"imageCompressBorder\": 1600, \n" +
                "                \"imageInsertAlign\": \"none\", \n" +
                "                \"imageUrlPrefix\": \"\",\n" +
                "                \"imagePathFormat\": \"/ueditor/jsp/upload/image/{yyyy}{mm}{dd}/{time}{rand:6}\" }";
    }

    /**
     * 上传图片（UEditor 图片上传接口）。
     *
     * 处理 UEditor 编辑器的图片上传请求，将图片保存到服务器并按日期分目录存储。
     * 返回 UEditor 标准格式的 JSON 响应。
     *
     * @param file    上传的图片文件
     * @param request HTTP 请求对象
     * @return UEditor 标准格式的 JSON 响应字符串
     *
     * - state: "SUCCESS" 表示成功
     * - url: 图片访问 URL
     * - title: 文件名
     * - original: 原始文件名
     *
     * @throws IOException 文件保存失败时抛出
     */
    @PostMapping("/image")
    public String uploadImage(MultipartFile file, HttpServletRequest request) throws IOException {
        if (file == null || file.isEmpty()) {
            return "error";
        }
        String fileName = file.getOriginalFilename();
        String suffixName = "";
        if (fileName != null && fileName.lastIndexOf(".") > 0) {
            suffixName = fileName.substring(fileName.lastIndexOf("."));
        }
        // 使用时间戳作为新文件名，避免文件名冲突
        fileName = Calendar.getInstance().getTimeInMillis() + suffixName;
        // 按日期创建目录（格式：yyyyMMdd）
        String directory = StringUtil.getFormatterDate(new Date(), "yyyyMMdd");
        File dest = new File(uploadPhotoPath + "/" + directory + "/" + fileName);
        if (!dest.getParentFile().exists()) {
            dest.getParentFile().mkdirs();
        }
        file.transferTo(dest);
        // 返回 UEditor 标准格式的 JSON
        return "{\"state\": \"SUCCESS\"," +
                "\"url\": \"" + "/api/common/ueditor/images/" + directory + "/" + fileName + "\"," +
                "\"title\": \"" + fileName + "\"," +
                "\"original\": \"" + fileName + "\"}";
    }

    /**
     * 查看 UEditor 上传的图片。
     *
     * 根据日期目录和文件名返回对应的图片文件。
     *
     * @param date     日期目录（格式：yyyyMMdd），例如 "20231201"
     * @param fileName 图片文件名
     * @return 图片资源响应，如果文件不存在则返回 404
     */
    @GetMapping("/images/{date}/{fileName}")
    public ResponseEntity<FileSystemResource> image(@PathVariable("date") String date,
                                                    @PathVariable("fileName") String fileName) {
        if (StringUtils.isEmpty(fileName)) {
            return ResponseEntity.notFound().build();
        }
        File file = new File(uploadPhotoPath + "/" + date + "/" + fileName);
        if (!file.exists()) {
            return ResponseEntity.notFound().build();
        }
        MediaType mediaType = MediaTypeFactory.getMediaType(fileName).orElse(MediaType.APPLICATION_OCTET_STREAM);
        return ResponseEntity.ok()
                .contentType(mediaType)
                .body(new FileSystemResource(file));
    }
}

