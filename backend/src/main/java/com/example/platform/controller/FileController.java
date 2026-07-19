package com.example.platform.controller;

import com.example.platform.common.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/file")
public class FileController {
    @Value("${platform.upload-path}")
    private String uploadPath;

    @PostMapping("/upload")
    public Result<Map<String, Object>> upload(@RequestParam("file") MultipartFile file,
                                              @RequestParam(value = "type", defaultValue = "file") String type) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("上传文件不能为空");
        }
        String originalName = file.getOriginalFilename() == null ? "file" : file.getOriginalFilename();
        String suffix = "";
        int dotIndex = originalName.lastIndexOf('.');
        if (dotIndex >= 0) {
            suffix = originalName.substring(dotIndex);
        }
        String storedName = UUID.randomUUID().toString().replace("-", "") + suffix;
        File targetDir = Paths.get(uploadPath, type).toFile();
        if (!targetDir.exists() && !targetDir.mkdirs()) {
            throw new IOException("上传目录创建失败");
        }
        File targetFile = new File(targetDir, storedName);
        file.transferTo(targetFile);

        Map<String, Object> data = new HashMap<>();
        data.put("filename", originalName);
        data.put("storedName", storedName);
        data.put("url", "/upload/" + type + "/" + storedName);
        data.put("downloadUrl", "/api/file/download?type=" + type + "&name=" + storedName);
        return Result.success(data);
    }

    @GetMapping("/download")
    public ResponseEntity<FileSystemResource> download(@RequestParam("type") String type,
                                                       @RequestParam("name") String name) throws IOException {
        File file = resolveUploadFile(type, name);
        String encodedName = URLEncoder.encode(file.getName(), StandardCharsets.UTF_8.name()).replace("+", "%20");
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encodedName)
                .body(new FileSystemResource(file));
    }

    @GetMapping("/preview/{type}/{name:.+}")
    public ResponseEntity<FileSystemResource> preview(@PathVariable String type,
                                                      @PathVariable String name) {
        File file = resolveUploadFile(type, name);
        return ResponseEntity.ok()
                .contentType(detectMediaType(file.getName()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + file.getName() + "\"")
                .body(new FileSystemResource(file));
    }

    private File resolveUploadFile(String type, String name) {
        Path basePath = Paths.get(uploadPath).toAbsolutePath().normalize();
        Path filePath = basePath.resolve(type).resolve(name).normalize();
        if (!filePath.startsWith(basePath)) {
            throw new IllegalArgumentException("文件路径不合法");
        }
        File file = filePath.toFile();
        if (!file.exists() || !file.isFile()) {
            throw new IllegalArgumentException("文件不存在");
        }
        return file;
    }

    private MediaType detectMediaType(String filename) {
        String lowerName = filename.toLowerCase();
        if (lowerName.endsWith(".pdf")) {
            return MediaType.APPLICATION_PDF;
        }
        if (lowerName.endsWith(".png")) {
            return MediaType.IMAGE_PNG;
        }
        if (lowerName.endsWith(".jpg") || lowerName.endsWith(".jpeg")) {
            return MediaType.IMAGE_JPEG;
        }
        if (lowerName.endsWith(".gif")) {
            return MediaType.IMAGE_GIF;
        }
        if (lowerName.endsWith(".txt")) {
            return MediaType.TEXT_PLAIN;
        }
        return MediaType.APPLICATION_OCTET_STREAM;
    }
}
