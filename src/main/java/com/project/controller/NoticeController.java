package com.project.controller;

import com.project.common.ApiResponse;
import com.project.common.PageResponse;
import com.project.dto.NoticeDto.Detail;
import com.project.dto.NoticeDto.ListItem;
import com.project.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** Public notice list and detail API. */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notices")
public class NoticeController {
    private final NoticeService noticeService;

    @GetMapping
    public ApiResponse<PageResponse<ListItem>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String searchType,
            @RequestParam(required = false) String keyword) {
        return ApiResponse.ok(noticeService.list(searchType, keyword, page, size));
    }

    @GetMapping("/recent")
    public ApiResponse<PageResponse<ListItem>> recent(
            @RequestParam(defaultValue = "5") int size) {
        return ApiResponse.ok(noticeService.list(null, null, 1, size));
    }

    @GetMapping("/{noticeId}")
    public ApiResponse<Detail> detail(@PathVariable Long noticeId) {
        return ApiResponse.ok(noticeService.detail(noticeId));
    }

    @GetMapping("/{noticeId}/files/{fileId}")
    public ResponseEntity<Resource> file(@PathVariable Long noticeId, @PathVariable Long fileId) {
        return FileHelper.download(noticeService.file(noticeId, fileId));
    }
}
