package com.example.platform.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class UploadBootstrap implements CommandLineRunner {
    private static final String MINIMAL_PDF =
            "%PDF-1.4\n" +
            "1 0 obj<< /Type /Catalog /Pages 2 0 R >>endobj\n" +
            "2 0 obj<< /Type /Pages /Kids [3 0 R] /Count 1 >>endobj\n" +
            "3 0 obj<< /Type /Page /Parent 2 0 R /MediaBox [0 0 300 144] /Contents 4 0 R >>endobj\n" +
            "4 0 obj<< /Length 55 >>stream\n" +
            "BT /F1 18 Tf 36 96 Td (Online Teaching Demo File) Tj ET\n" +
            "endstream endobj\n" +
            "xref\n" +
            "0 5\n" +
            "0000000000 65535 f \n" +
            "0000000009 00000 n \n" +
            "0000000058 00000 n \n" +
            "0000000115 00000 n \n" +
            "0000000202 00000 n \n" +
            "trailer<< /Root 1 0 R /Size 5 >>\n" +
            "startxref\n" +
            "308\n" +
            "%%EOF\n";

    private static final byte[] PNG_BYTES = Base64.getDecoder().decode(
            "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mP8/x8AAwMCAO7Z0mQAAAAASUVORK5CYII=");

    private static final byte[] JPG_BYTES = Base64.getDecoder().decode(
            "/9j/4AAQSkZJRgABAQAAAQABAAD/2wCEAAkGBxAQEBAQEA8QDw8PEA8PEA8QDw8PDw8QFREWFhURFRUYHSggGBolGxUVITEhJSkrLi4uFx8zODMsNygtLisBCgoKDg0OGxAQGi0fHyUtLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLf/AABEIAAEAAgMBIgACEQEDEQH/xAAXAAEBAQEAAAAAAAAAAAAAAAABAgME/8QAFhEBAQEAAAAAAAAAAAAAAAAAAQAC/9oADAMBAAIQAxAAAAG3wD//xAAXEAADAQAAAAAAAAAAAAAAAAAAAREh/9oACAEBAAEFAmP/xAAVEQEBAAAAAAAAAAAAAAAAAAABAP/aAAgBAwEBPwGn/8QAFBEBAAAAAAAAAAAAAAAAAAAAAP/aAAgBAgEBPwCf/8QAFxABAQEBAAAAAAAAAAAAAAAAAQARIf/aAAgBAQAGPwJaf//EABcQAAMBAAAAAAAAAAAAAAAAAAABETH/2gAIAQEAAT8hY8//2gAMAwEAAgADAAAAED//xAAVEQEBAAAAAAAAAAAAAAAAAAABEP/aAAgBAwEBPxCF/8QAFBEBAAAAAAAAAAAAAAAAAAAAEP/aAAgBAgEBPxB//8QAFxABAQEBAAAAAAAAAAAAAAAAAQARIf/aAAgBAQABPxB1k7f/2Q==");

    @Value("${platform.upload-path}")
    private String uploadPath;

    @Override
    public void run(String... args) throws Exception {
        Path basePath = Paths.get(uploadPath);
        ensureDirectory(basePath);
        ensureDirectory(basePath.resolve("file"));
        ensureDirectory(basePath.resolve("video"));
        ensureDirectory(basePath.resolve("cover"));
        ensureDirectory(basePath.resolve("banner"));

        Map<Path, byte[]> binaryFiles = new LinkedHashMap<>();
        binaryFiles.put(basePath.resolve("cover").resolve("java.png"), PNG_BYTES);
        binaryFiles.put(basePath.resolve("cover").resolve("mysql.png"), PNG_BYTES);
        binaryFiles.put(basePath.resolve("cover").resolve("vue.png"), PNG_BYTES);
        binaryFiles.put(basePath.resolve("banner").resolve("banner1.jpg"), JPG_BYTES);
        binaryFiles.put(basePath.resolve("banner").resolve("banner2.jpg"), JPG_BYTES);

        for (Map.Entry<Path, byte[]> entry : binaryFiles.entrySet()) {
            ensureBinaryFile(entry.getKey(), entry.getValue());
        }

        ensureTextFile(basePath.resolve("file").resolve("java-basic.pdf"), MINIMAL_PDF);
        ensureTextFile(basePath.resolve("file").resolve("mysql-design.pdf"), MINIMAL_PDF);
        ensureTextFile(basePath.resolve("file").resolve("vue-basic.pdf"), MINIMAL_PDF);
    }

    private void ensureDirectory(Path path) throws IOException {
        Files.createDirectories(path);
    }

    private void ensureBinaryFile(Path path, byte[] content) throws IOException {
        if (!Files.exists(path)) {
            Files.write(path, content);
        }
    }

    private void ensureTextFile(Path path, String content) throws IOException {
        if (!Files.exists(path)) {
            Files.write(path, content.getBytes(StandardCharsets.UTF_8));
        }
    }
}
