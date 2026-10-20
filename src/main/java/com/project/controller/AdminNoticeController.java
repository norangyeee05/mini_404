package com.project.controller;

import java.util.Map;

import com.project.common.ApiResponse;
import com.project.common.PageResponse;
import com.project.dto.NoticeDto.Detail;
import com.project.dto.NoticeDto.ListItem;
import com.project.common.LoginUser;
import com.project.common.SessionKeys;
import com.project.dto.NoticeDto.NoticeForm;
import com.project.service.NoticeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.validation.annotation.Validated;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/notices")
public class AdminNoticeController {
    private final NoticeService noticeService;

    @GetMapping
    public ApiResponse<PageResponse<ListItem>> list(@RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size, @RequestParam(required = false) String searchType,
            @RequestParam(required = false) String keyword) {
        return ApiResponse.ok(noticeService.list(searchType, keyword, page, size));
    }

    @GetMapping("/{noticeId}")
    public ApiResponse<Detail> detail(@PathVariable Long noticeId) {
        return ApiResponse.ok(noticeService.adminDetail(noticeId));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Map<String, Long>>> create(
            @SessionAttribute(name = SessionKeys.LOGIN_USER) LoginUser admin,
            @Valid @RequestBody NoticeForm form) {
        Long noticeId = noticeService.create(admin.id(), form, List.of());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(Map.of("noticeId", noticeId), "공지사항이 등록되었습니다."));
    }

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<ApiResponse<Map<String, Long>>> createMultipart(
            @SessionAttribute(name = SessionKeys.LOGIN_USER) LoginUser admin,
            @Validated @ModelAttribute NoticeForm form,
            @RequestParam(value = "files", required = false) List<MultipartFile> files,
            @RequestParam(value = "files[]", required = false) List<MultipartFile> bracketFiles) {
        Long noticeId = noticeService.create(admin.id(), form, FileHelper.merge(files, bracketFiles));
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(Map.of("noticeId", noticeId), "공지사항이 등록되었습니다."));
    }

    @PutMapping("/{noticeId}")
    public ApiResponse<Map<String, Long>> update(@PathVariable Long noticeId,
                                                  @Valid @RequestBody NoticeForm form) {
        return ApiResponse.ok(Map.of("noticeId", noticeService.update(noticeId, form, List.of())));
    }

    @PutMapping(value = "/{noticeId}", consumes = "multipart/form-data")
    public ApiResponse<Map<String, Long>> updateMultipart(@PathVariable Long noticeId,
            @Validated @ModelAttribute NoticeForm form,
            @RequestParam(value = "files", required = false) List<MultipartFile> files,
            @RequestParam(value = "files[]", required = false) List<MultipartFile> bracketFiles) {
        return ApiResponse.ok(Map.of("noticeId", noticeService.update(noticeId, form, FileHelper.merge(files, bracketFiles))));
    }

    @DeleteMapping("/{noticeId}")
    public ApiResponse<Void> delete(@PathVariable Long noticeId) {
        noticeService.delete(noticeId);
        return ApiResponse.ok(null, "공지사항이 삭제되었습니다.");
    }
}
