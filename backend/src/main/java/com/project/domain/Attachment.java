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
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "attachment", indexes = @Index(name = "idx_attachment_target", columnList = "target_type, target_id"))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

}
