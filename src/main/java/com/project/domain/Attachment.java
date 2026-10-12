package com.project.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

/** 첨부파일 공용 테이블 (자료실 / 자료실 요청 / QnA). 대상은 target_type + target_id 로 구분 */
@Entity
@Table(name = "attachment", indexes = @Index(name = "idx_attachment_target", columnList = "target_type, target_id"))
public class Attachment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "target_type", nullable = false, length = 30)
    private AttachmentTarget targetType;

    @Column(name = "target_id", nullable = false)
    private Long targetId;

    @Column(name = "original_name", nullable = false, length = 255)
    private String originalName;

    @Column(name = "stored_name", nullable = false, length = 255)
    private String storedName;

    @Column(name = "content_type", length = 150)
    private String contentType;

    @Column(name = "file_size", nullable = false)
    private long fileSize;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    protected Attachment() {
    }

    public static Attachment of(AttachmentTarget targetType, Long targetId, String originalName,
                                String storedName, String contentType, long fileSize) {
        Attachment a = new Attachment();
        a.targetType = targetType;
        a.targetId = targetId;
        a.originalName = originalName;
        a.storedName = storedName;
        a.contentType = contentType;
        a.fileSize = fileSize;
        return a;
    }

    @PrePersist
    void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public AttachmentTarget getTargetType() { return targetType; }
    public Long getTargetId() { return targetId; }
    public String getOriginalName() { return originalName; }
    public String getStoredName() { return storedName; }
    public String getContentType() { return contentType; }
    public long getFileSize() { return fileSize; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
