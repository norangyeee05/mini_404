package com.project.dto;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** 자료실 DTO 모음 (FileInfo, PostForm은 자료실 요청에서도 함께 사용) */
public final class ResourceDto {

    private ResourceDto() {
    }

    /** 첨부파일 정보 (다운로드용 fileId, 원본 파일명) */
    public record FileInfo(Long fileId, String fileName) {
    }

    /**
     * 자료 등록/수정, 자료실 추가 요청 공용 multipart 폼 (title, content + 파일은 별도 파트).
     * deleteFileIds: 수정 시 삭제할 기존 첨부파일 ID (선택)
     */
    public record PostForm(
            @NotBlank(message = "제목을 입력해주세요.")
            @Size(max = 200, message = "제목은 200자 이내로 입력해주세요.")
            String title,

            @NotBlank(message = "내용을 입력해주세요.")
            String content,

            List<Long> deleteFileIds
    ) {
    }

    /** 자료 목록 항목 */
    public record ListItem(Long resourceId, String title, int viewCount, String writer, LocalDateTime createdAt) {
    }

    /** 자료 상세 (목록 항목 + 내용, 첨부파일) */
    public record Detail(
            Long resourceId,
            String title,
            int viewCount,
            String writer,
            LocalDateTime createdAt,
            String content,
            List<FileInfo> files
    ) {
    }
}
