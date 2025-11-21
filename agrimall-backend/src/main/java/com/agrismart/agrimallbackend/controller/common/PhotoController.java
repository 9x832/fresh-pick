package com.agrismart.agrimallbackend.controller.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.PostConstruct;
import java.io.File;

/**
 * 图片查看控制器。
 *
 * 该控制器提供统一的图片查看接口，用于访问上传的图片文件。
 * 图片文件存储在配置的路径下，通过文件名访问。
 *
 * 接口路径：{@code /api/common/photo}
 *
 * 使用场景：
 *
 * - 用户头像查看
 * - 商品图片查看
 * - 其他上传图片的查看
 *
 * 注意：
 *
 * - 图片路径从配置文件 {@code xqx.upload.photo.path} 中读取
 * - 如果文件不存在，返回 404 状态码
 *
 * @author agrimall
 * @since 1.0
 */
@RestController
@RequestMapping("/api/common/photo")
public class PhotoController {

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
     * 查看图片。
     *
     * 根据文件名返回对应的图片文件。文件名可以是相对路径（如 "20231201/1234567890.jpg"），
     * 系统会在配置的图片上传路径下查找该文件。
     *
     * @param filename 图片文件名（可以是相对路径），例如 "20231201/1234567890.jpg"
     * @return 图片资源响应，如果文件不存在则返回 404
     */
    @GetMapping("/view")
    public ResponseEntity<Resource> view(@RequestParam("filename") String filename) {
        File file = new File(uploadPhotoPath, filename);
        if (!file.exists()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(new FileSystemResource(file));
    }
}

