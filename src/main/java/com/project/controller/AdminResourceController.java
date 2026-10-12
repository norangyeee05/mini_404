package com.project.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.multipart.MultipartFile;

import com.project.common.ApiResponse;
import com.project.common.LoginUser;
import com.project.common.SessionKeys;
import com.project.dto.ResourceDto.PostForm;
import com.project.service.ResourceService;

/**
 * 관리자 자료실 관리 (2-3). 접근 제어는 AuthInterceptor(/api/admin/**)에서 처리.
 * multipart/form-data: title, content, files[] (수정 시 삭제할 기존 파일은 deleteFileIds 로 전달)
 */
@RestController
@RequestMapping("/api/admin/resources")
public class AdminResourceController {

    private final ResourceService resourceService;

    public AdminResourceController(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Map<String, Long>>> create(
            @SessionAttribute(name = SessionKeys.LOGIN_USER) LoginUser admin,
            @Validated @ModelAttribute PostForm form,
            @RequestParam(value = "files", required = false) List<MultipartFile> files,
            @RequestParam(value = "files[]", required = false) List<MultipartFile> bracketFiles) {
        Long id = resourceService.create(admin.id(), form, FileHelper.merge(files, bracketFiles));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(Map.of("resourceId", id), "자료가 등록되었습니다."));
    }

    @PutMapping("/{resourceId}")
    public ApiResponse<Map<String, Long>> update(
            @PathVariable Long resourceId,
            @Validated @ModelAttribute PostForm form,
            @RequestParam(value = "files", required = false) List<MultipartFile> files,
            @RequestParam(value = "files[]", required = false) List<MultipartFile> bracketFiles) {
        Long id = resourceService.update(resourceId, form, FileHelper.merge(files, bracketFiles));
        return ApiResponse.ok(Map.of("resourceId", id), "자료가 수정되었습니다.");
    }

    @DeleteMapping("/{resourceId}")
    public ApiResponse<Void> delete(@PathVariable Long resourceId) {
        resourceService.delete(resourceId);
        return ApiResponse.ok(null, "자료가 삭제되었습니다.");
    }
}
