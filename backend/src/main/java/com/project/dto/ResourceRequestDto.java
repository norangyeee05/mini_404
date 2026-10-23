package com.project.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.project.domain.RequestStatus;
import com.project.dto.ResourceDto.FileInfo;

import jakarta.validation.constraints.NotNull;


public final class ResourceRequestDto {

    private ResourceRequestDto() {
    }


    public record ListItem(
            Long requestId,
            String title,
            String requester,
            RequestStatus status,
            LocalDateTime createdAt
    ) {
    }


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


    public record StatusUpdate(@NotNull(message = "처리 상태를 선택해주세요.") RequestStatus status) {
    }
}
