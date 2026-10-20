package com.project.controller;

import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.multipart.MultipartFile;

import com.project.common.ApiResponse;
import com.project.common.LoginUser;
import com.project.common.PageResponse;
import com.project.common.SessionKeys;
import com.project.dto.ResourceDto.Detail;
import com.project.dto.ResourceDto.ListItem;
import com.project.dto.ResourceDto.PostForm;
import com.project.service.ResourceRequestService;
import com.project.service.ResourceService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/resources")
public class ResourceController {

    private final ResourceService resourceService;
    private final ResourceRequestService requestService;

    @GetMapping
    public ApiResponse<PageResponse<ListItem>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String searchType,
            @RequestParam(required = false) String keyword) {
        return ApiResponse.ok(resourceService.list(searchType, keyword, page, size));
    }

    @GetMapping("/{resourceId}")
    public ApiResponse<Detail> detail(@PathVariable Long resourceId) {
        return ApiResponse.ok(resourceService.detail(resourceId));
    }

    @GetMapping("/{resourceId}/files/{fileId}")
    public ResponseEntity<Resource> download(@PathVariable Long resourceId, @PathVariable Long fileId) {
        return FileHelper.download(resourceService.file(resourceId, fileId));
    }

    @PostMapping("/requests")
    public ResponseEntity<ApiResponse<Map<String, Long>>> createRequest(
            @SessionAttribute(name = SessionKeys.LOGIN_USER) LoginUser user,
            @Validated @ModelAttribute PostForm form,
            @RequestParam(value = "files", required = false) List<MultipartFile> files,
            @RequestParam(value = "files[]", required = false) List<MultipartFile> bracketFiles) {
        user.requireUser();
        Long requestId = requestService.create(user.id(), form, FileHelper.merge(files, bracketFiles));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(Map.of("requestId", requestId), "자료실 추가 요청이 등록되었습니다."));
    }
}
