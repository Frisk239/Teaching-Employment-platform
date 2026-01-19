package com.teaching.employment.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.teaching.employment.common.Result;
import com.teaching.employment.entity.TeachingMaterial;
import com.teaching.employment.service.TeachingMaterialService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 教学资料管理控制器
 *
 * @author Teaching Employment Platform Team
 * @since 2026-01-17
 */
@RestController
@RequestMapping("/teaching-material")
@Api(tags = "教学资料管理")
@RequiredArgsConstructor
public class TeachingMaterialController {

    private final TeachingMaterialService teachingMaterialService;

    @Value("${file.upload.path:uploads}")
    private String uploadPath;

    /**
     * 分页查询教学资料列表
     */
    @GetMapping("/page")
    @ApiOperation("分页查询教学资料列表")
    public Result<IPage<TeachingMaterial>> getTeachingMaterialPage(
            @ApiParam("当前页") @RequestParam(defaultValue = "1") Integer current,
            @ApiParam("每页大小") @RequestParam(defaultValue = "10") Integer size,
            @ApiParam("教师ID") @RequestParam(required = false) Long teacherId,
            @ApiParam("课程ID") @RequestParam(required = false) Long courseId,
            @ApiParam("资料类型") @RequestParam(required = false) String materialType,
            @ApiParam("分类") @RequestParam(required = false) String category,
            @ApiParam("关键词") @RequestParam(required = false) String keyword) {
        IPage<TeachingMaterial> page = teachingMaterialService.getTeachingMaterialPage(current, size,
                teacherId, courseId, materialType, category, keyword);
        return Result.ok(page);
    }

    /**
     * 上传教学资料
     */
    @PostMapping("/upload")
    @ApiOperation("上传教学资料")
    public Result<Map<String, Object>> uploadMaterial(
            @ApiParam("资料文件") @RequestParam("file") MultipartFile file,
            @ApiParam("资料名称") @RequestParam("title") String title,
            @ApiParam("资料类型") @RequestParam("materialType") String materialType,
            @ApiParam("课程ID") @RequestParam(required = false) Long courseId,
            @ApiParam("教师ID") @RequestParam Long teacherId,
            @ApiParam("分类") @RequestParam(required = false) String category,
            @ApiParam("标签") @RequestParam(required = false) String tags,
            @ApiParam("资料描述") @RequestParam(required = false) String description,
            @ApiParam("是否公开") @RequestParam(defaultValue = "0") Integer isPublic) {
        try {
            // 生成文件路径：materials/yyyyMM/UUID.ext
            String dateFolder = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMM"));
            String uuid = UUID.randomUUID().toString();
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null && originalFilename.contains(".")
                    ? originalFilename.substring(originalFilename.lastIndexOf("."))
                    : "";
            String fileName = uuid + extension;

            // 相对路径：materials/202601/uuid.pdf
            String relativePath = "materials/" + dateFolder + "/" + fileName;

            // 构建完整路径并创建目录
            Path targetPath = Paths.get(uploadPath, relativePath);
            Files.createDirectories(targetPath.getParent());

            // 保存文件
            file.transferTo(targetPath.toFile());

            // 构建文件URL（相对路径，以 /uploads 开头用于访问）
            String fileUrl = "/uploads/" + relativePath.replace("\\", "/");

            // 创建教学资料记录
            TeachingMaterial material = new TeachingMaterial();
            material.setTitle(title);
            material.setMaterialType(materialType);
            material.setFileUrl(fileUrl);
            material.setFileSize(file.getSize());
            material.setCourseId(courseId);
            material.setTeacherId(teacherId);
            material.setCategory(category);
            material.setTags(tags);
            material.setDescription(description);
            material.setIsPublic(isPublic);
            material.setStatus("active");

            boolean success = teachingMaterialService.save(material);

            if (success) {
                Map<String, Object> data = new HashMap<>();
                data.put("id", material.getId());
                data.put("fileName", originalFilename);
                data.put("fileUrl", fileUrl);
                data.put("fileSize", file.getSize());
                return Result.ok("上传成功", data);
            } else {
                return Result.error("保存记录失败");
            }
        } catch (IOException e) {
            return Result.error("文件上传失败：" + e.getMessage());
        }
    }

    /**
     * 更新教学资料信息
     */
    @PutMapping
    @ApiOperation("更新教学资料信息")
    public Result<Void> updateMaterial(@RequestBody TeachingMaterial material) {
        boolean success = teachingMaterialService.updateById(material);
        return success ? Result.ok("更新成功") : Result.error("更新失败");
    }

    /**
     * 删除教学资料
     */
    @DeleteMapping("/{id}")
    @ApiOperation("删除教学资料")
    public Result<Void> deleteMaterial(@PathVariable Long id) {
        boolean success = teachingMaterialService.removeById(id);
        return success ? Result.ok("删除成功") : Result.error("删除失败");
    }

    /**
     * 批量删除教学资料
     */
    @DeleteMapping("/batch")
    @ApiOperation("批量删除教学资料")
    public Result<Void> batchDelete(@RequestBody Long[] ids) {
        boolean success = teachingMaterialService.removeByIds(java.util.Arrays.asList(ids));
        return success ? Result.ok("删除成功") : Result.error("删除失败");
    }

    /**
     * 记录下载
     */
    @PostMapping("/{id}/download")
    @ApiOperation("记录下载")
    public Result<Void> recordDownload(@PathVariable Long id) {
        teachingMaterialService.incrementDownloadCount(id);
        return Result.ok();
    }

    /**
     * 记录查看
     */
    @PostMapping("/{id}/view")
    @ApiOperation("记录查看")
    public Result<Void> recordView(@PathVariable Long id) {
        teachingMaterialService.incrementViewCount(id);
        return Result.ok();
    }

    /**
     * 根据ID获取教学资料详情
     */
    @GetMapping("/{id}")
    @ApiOperation("根据ID获取教学资料详情")
    public Result<TeachingMaterial> getMaterialById(@PathVariable Long id) {
        TeachingMaterial material = teachingMaterialService.getById(id);
        return Result.ok(material);
    }
}
