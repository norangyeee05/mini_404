package com.project.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.project.domain.InquiryType;
import com.project.domain.QnaStatus;
import com.project.dto.ResourceDto.FileInfo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public final class QnaDto {
    private QnaDto() { }

    public record QuestionForm(
            @NotBlank(message = "제목을 입력해주세요.")
            @Size(max = 200, message = "제목은 200자 이내로 입력해주세요.")
            String title,
            @NotNull(message = "문의 유형을 선택해주세요.")
            InquiryType inquiryType,
            Boolean isPrivate,
            @NotBlank(message = "내용을 입력해주세요.")
            String content
    ) { }

    public record AnswerForm(
            @NotBlank(message = "답변을 입력해주세요.")
            String answer
    ) { }

    public record AdminEditForm(
            @NotBlank(message = "제목을 입력해주세요.")
            @Size(max = 200, message = "제목은 200자 이내로 입력해주세요.")
            String title,
            @NotNull(message = "문의 유형을 선택해주세요.")
            InquiryType inquiryType,
            Boolean isPrivate,
            @NotBlank(message = "내용을 입력해주세요.")
            String content,
            String answer,
            @NotNull(message = "답변 상태를 선택해주세요.")
            QnaStatus status
    ) { }

    public record AdminAnswerForm(
            @NotBlank(message = "답변을 입력해주세요.")
            String answer
    ) { }

    public record ListItem(Long qnaId, String title, InquiryType inquiryType, String writer, int viewCount,
                           boolean isPrivate, QnaStatus status, LocalDateTime createdAt) { }

    public record Detail(Long qnaId, String title, InquiryType inquiryType, String writer,
                         boolean isPrivate, QnaStatus status, LocalDateTime createdAt, String content,
                         List<FileInfo> files, String answer, boolean isOwner) { }

    public record StatusResult(Long qnaId, QnaStatus status) { }
}
