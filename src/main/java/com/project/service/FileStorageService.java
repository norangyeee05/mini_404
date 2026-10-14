package com.project.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.project.common.BusinessException;
import com.project.domain.Attachment;
import com.project.domain.AttachmentTarget;

/** 로컬 디스크 파일 저장. 저장 파일명은 UUID(+확장자)로 바꿔 경로 조작/중복을 방지 */
@Service
public class FileStorageService {

    private final Path root;

    public FileStorageService(@Value("${app.upload-dir:uploads}") String uploadDir) {
        this.root = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.root);
        } catch (IOException e) {
            throw new IllegalStateException("업로드 폴더를 만들 수 없습니다: " + this.root, e);
        }
    }

    public Attachment store(MultipartFile file, AttachmentTarget targetType, Long targetId) {
        String original = sanitizeName(file.getOriginalFilename());
        String storedName = UUID.randomUUID() + extensionOf(original);

        Path target = root.resolve(storedName).normalize();
        if (!target.startsWith(root)) {
            throw BusinessException.badRequest("잘못된 파일 이름입니다.");
        }
        try (InputStream in = file.getInputStream()) {
            Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new BusinessException(HttpStatus.INTERNAL_SERVER_ERROR, "파일 저장에 실패했습니다.");
        }
        return Attachment.of(targetType, targetId, original, storedName, file.getContentType(), file.getSize());
    }

    public Path resolve(Attachment attachment) {
        Path p = root.resolve(attachment.getStoredName()).normalize();
        if (!p.startsWith(root) || !Files.isRegularFile(p)) {
            throw BusinessException.notFound("파일을 찾을 수 없습니다.");
        }
        return p;
    }

    public void deleteQuietly(Attachment attachment) {
        try {
            Path p = root.resolve(attachment.getStoredName()).normalize();
            if (p.startsWith(root)) {
                Files.deleteIfExists(p);
            }
        } catch (IOException ignored) {
            // 파일이 이미 없거나 삭제 실패 시에도 DB 처리는 계속 진행
        }
    }

    private String sanitizeName(String raw) {
        String n = raw == null ? "file" : raw.replace('\\', '/');
        n = n.substring(n.lastIndexOf('/') + 1).trim();
        if (n.isEmpty()) {
            n = "file";
        }
        return n.length() > 255 ? n.substring(n.length() - 255) : n;
    }

    private String extensionOf(String name) {
        int idx = name.lastIndexOf('.');
        if (idx < 0) {
            return "";
        }
        String ext = name.substring(idx).toLowerCase();
        return ext.matches("\\.[a-z0-9]{1,10}") ? ext : "";
    }
}
