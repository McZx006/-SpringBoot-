package com.example.platform.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
public class UploadPreviewController {
    @Value("${platform.upload-path}")
    private String uploadPath;

    @GetMapping("/upload/{type}/{name:.+}")
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
