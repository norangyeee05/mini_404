package com.project.dto;

import java.time.LocalDateTime;
import java.util.List;
import com.project.dto.ResourceDto.FileInfo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public final class NoticeDto {
    private NoticeDto() { }

    public record NoticeForm(
            @NotBlank(message = "제목을 입력해주세요.")
            @Size(max = 200, message = "제목은 200자 이내로 입력해주세요.")
            String title,
            @NotBlank(message = "내용을 입력해주세요.")
            String content
    ) { }

    public record ListItem(Long noticeId, String title, int viewCount, String writer, LocalDateTime createdAt) { }

    public record Detail(Long noticeId, String title, int viewCount, String writer,
                         LocalDateTime createdAt, String content, List<FileInfo> files) { }
}
