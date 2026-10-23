package com.project.dto;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


public final class ResourceDto {

    private ResourceDto() {
    }


    public record FileInfo(Long fileId, String fileName) {
    }





    public record PostForm(
            @NotBlank(message = "제목을 입력해주세요.")
            @Size(max = 200, message = "제목은 200자 이내로 입력해주세요.")
            String title,

            @NotBlank(message = "내용을 입력해주세요.")
            String content,

            List<Long> deleteFileIds
    ) {
    }


    public record ListItem(Long resourceId, String title, int viewCount, String writer, LocalDateTime createdAt) {
    }


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
