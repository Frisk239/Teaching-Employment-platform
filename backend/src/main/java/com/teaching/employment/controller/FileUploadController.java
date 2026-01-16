package com.teaching.employment.controller;

import com.teaching.employment.common.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 文件上传控制器
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-12
 */
@Slf4j
@RestController
@RequestMapping("/file")
@Api(tags = "文件上传管理")
@RequiredArgsConstructor
public class FileUploadController {

    @Value("${file.upload.path:uploads}")
    private String uploadPath;

    @Value("${file.upload.domain:http://localhost:8083}")
    private String domain;

    @Value("${server.servlet.context-path:}")
    private String contextPath;

    /**
     * 允许的文件类型
     */
    private static final Set<String> ALLOWED_FILE_TYPES = new HashSet<>(Arrays.asList(
            "jpg", "jpeg", "png", "gif", "bmp", "webp",  // 图片
            "pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx", "txt",  // 文档
            "zip", "rar", "7z",  // 压缩文件
            "mp4", "avi", "mov", "wmv"  // 视频
    ));

    /**
     * 最大文件大小（10MB）
     */
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;

    /**
     * 单文件上传
     */
    @PostMapping("/upload")
    @ApiOperation("单文件上传")
    public Result<Map<String, String>> uploadFile(
            @ApiParam("文件") @RequestParam("file") MultipartFile file,
            @ApiParam("文件类型目录") @RequestParam(required = false, defaultValue = "common") String type) {

        try {
            // 验证文件
            validateFile(file);

            // 生成文件路径
            String relativePath = generateFilePath(type, file.getOriginalFilename());

            // 创建目录
            Path targetPath = Paths.get(uploadPath, relativePath);
            Files.createDirectories(targetPath.getParent());

            // 保存文件
            file.transferTo(targetPath.toFile());

            // 构建访问URL
            String fileUrl = buildFileUrl(relativePath);

            Map<String, String> data = new HashMap<>();
            data.put("url", fileUrl);
            data.put("relativePath", relativePath);
            data.put("fileName", file.getOriginalFilename());
            data.put("fileSize", String.valueOf(file.getSize()));

            log.info("文件上传成功: {}", relativePath);
            return Result.ok("上传成功", data);

        } catch (IOException e) {
            log.error("文件上传失败", e);
            return Result.error("文件上传失败: " + e.getMessage());
        }
    }

    /**
     * 多文件上传
     */
    @PostMapping("/upload/multiple")
    @ApiOperation("多文件上传")
    public Result<List<Map<String, String>>> uploadMultipleFiles(
            @ApiParam("文件列表") @RequestParam("files") MultipartFile[] files,
            @ApiParam("文件类型目录") @RequestParam(required = false, defaultValue = "common") String type) {

        List<Map<String, String>> results = new ArrayList<>();

        for (MultipartFile file : files) {
            try {
                // 验证文件
                validateFile(file);

                // 生成文件路径
                String relativePath = generateFilePath(type, file.getOriginalFilename());

                // 创建目录
                Path targetPath = Paths.get(uploadPath, relativePath);
                Files.createDirectories(targetPath.getParent());

                // 保存文件
                file.transferTo(targetPath.toFile());

                // 构建访问URL
                String fileUrl = buildFileUrl(relativePath);

                Map<String, String> data = new HashMap<>();
                data.put("url", fileUrl);
                data.put("relativePath", relativePath);
                data.put("fileName", file.getOriginalFilename());
                data.put("fileSize", String.valueOf(file.getSize()));

                results.add(data);

                log.info("文件上传成功: {}", relativePath);

            } catch (IOException e) {
                log.error("文件上传失败: {}", file.getOriginalFilename(), e);
                Map<String, String> errorData = new HashMap<>();
                errorData.put("fileName", file.getOriginalFilename());
                errorData.put("error", e.getMessage());
                results.add(errorData);
            }
        }

        return Result.ok("上传完成", results);
    }

    /**
     * 删除文件
     */
    @DeleteMapping("/delete")
    @ApiOperation("删除文件")
    public Result<Void> deleteFile(@RequestBody Map<String, String> request) {
        String relativePath = request.get("relativePath");
        if (relativePath == null || relativePath.isEmpty()) {
            return Result.error("文件路径不能为空");
        }

        try {
            Path targetPath = Paths.get(uploadPath, relativePath);
            Files.deleteIfExists(targetPath);
            log.info("文件删除成功: {}", relativePath);
            return Result.ok("删除成功");
        } catch (IOException e) {
            log.error("文件删除失败", e);
            return Result.error("文件删除失败: " + e.getMessage());
        }
    }

    /**
     * 下载文件
     */
    @GetMapping("/download/**")
    @ApiOperation("下载文件")
    public ResponseEntity<Resource> downloadFile(HttpServletRequest request) {
        try {
            // 获取相对路径（去掉 /file/download/ 前缀）
            String requestURI = request.getRequestURI();
            String relativePath = requestURI.substring("/api/file/download/".length());

            if (relativePath.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            // 构建文件路径
            Path targetPath = Paths.get(uploadPath, relativePath);

            // 检查文件是否存在
            if (!Files.exists(targetPath) || !Files.isReadable(targetPath)) {
                log.warn("文件不存在或不可读: {}", relativePath);
                return ResponseEntity.notFound().build();
            }

            // 创建资源
            Resource resource = new FileSystemResource(targetPath);

            // 获取文件名
            String filename = targetPath.getFileName().toString();

            // 确定内容类型
            String contentType = determineContentType(filename);

            // 构建响应
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .body(resource);

        } catch (Exception e) {
            log.error("文件下载失败", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 确定文件内容类型
     */
    private String determineContentType(String filename) {
        String extension = getFileExtension(filename).toLowerCase();

        switch (extension) {
            case "pdf":
                return "application/pdf";
            case "doc":
                return "application/msword";
            case "docx":
                return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            case "xls":
                return "application/vnd.ms-excel";
            case "xlsx":
                return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            case "ppt":
                return "application/vnd.ms-powerpoint";
            case "pptx":
                return "application/vnd.openxmlformats-officedocument.presentationml.presentation";
            case "txt":
                return "text/plain";
            case "jpg":
            case "jpeg":
                return "image/jpeg";
            case "png":
                return "image/png";
            case "gif":
                return "image/gif";
            case "zip":
                return "application/zip";
            case "rar":
                return "application/x-rar-compressed";
            default:
                return "application/octet-stream";
        }
    }

    /**
     * 验证文件
     */
    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("文件不能为空");
        }

        // 检查文件大小
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("文件大小超过限制（最大10MB）");
        }

        // 检查文件类型
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            throw new IllegalArgumentException("文件名不能为空");
        }

        String fileExtension = getFileExtension(originalFilename).toLowerCase();
        if (!ALLOWED_FILE_TYPES.contains(fileExtension)) {
            throw new IllegalArgumentException("不支持的文件类型: " + fileExtension);
        }
    }

    /**
     * 生成文件路径
     */
    private String generateFilePath(String type, String originalFilename) {
        // 按日期分类: type/yyyy/MM/dd/UUID.ext
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String extension = getFileExtension(originalFilename);

        return String.format("%s/%s/%s.%s", type, date, uuid, extension);
    }

    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf(".");
        if (lastDotIndex > 0 && lastDotIndex < filename.length() - 1) {
            return filename.substring(lastDotIndex + 1);
        }
        return "";
    }

    /**
     * 构建文件访问URL
     */
    private String buildFileUrl(String relativePath) {
        // 替换路径分隔符为URL路径
        String urlPath = relativePath.replace("\\", "/");
        return domain + contextPath + "/uploads/" + urlPath;
    }
}
