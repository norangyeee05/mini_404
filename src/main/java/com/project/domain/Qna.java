package com.project.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "qna")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Qna {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, length = 50)
    private String writer;

    /** 기존 Q&A 행은 작성자 정보가 없을 수 있어 nullable로 둡니다. */
    @Column(name = "author_id")
    private Long authorId;

    @Enumerated(EnumType.STRING)
    @Column(name = "inquiry_type", length = 30)
    private InquiryType inquiryType;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(columnDefinition = "TEXT")
    private String answer;

    @Column(name = "view_count", nullable = false)
    private int viewCount;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean answered;

    @Column(name = "is_public", nullable = false, columnDefinition = "boolean default true")
    private boolean isPublic = true;

    public static Qna create(String title, String content, InquiryType inquiryType, Long authorId, String writer, boolean isPublic) {
        Qna qna = new Qna();
        qna.title = title;
        qna.content = content;
        qna.inquiryType = inquiryType;
        qna.authorId = authorId;
        qna.writer = writer;
        qna.viewCount = 0;
        qna.answered = false;
        qna.isPublic = isPublic;
        return qna;
    }

    @PrePersist
    void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    public void update(String title, String content, InquiryType inquiryType, boolean isPublic) {
        this.title = title;
        this.content = content;
        this.inquiryType = inquiryType;
        this.isPublic = isPublic;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
        this.answered = true;
    }

}
