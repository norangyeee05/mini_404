package com.project.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.project.domain.RequestStatus;
import com.project.dto.ResourceDto.FileInfo;

import jakarta.validation.constraints.NotNull;

/** 자료실 요청(회원이 올린 추가 요청을 관리자가 처리) DTO 모음 */
public final class ResourceRequestDto {

    private ResourceRequestDto() {
    }

    /** 요청 목록 항목 */
    public record ListItem(
            Long requestId,
            String title,
            String requester,
            RequestStatus status,
            LocalDateTime createdAt
    ) {
    }

    /** 요청 상세 (목록 항목 + 내용, 첨부파일) */
    public record Detail(
            Long requestId,
            String title,
            String requester,
            RequestStatus status,
            LocalDateTime createdAt,
            String content,
            List<FileInfo> files
    ) {
    }

    /** 처리 상태 변경 요청 */
    public record StatusUpdate(@NotNull(message = "처리 상태를 선택해주세요.") RequestStatus status) {
    }
}
