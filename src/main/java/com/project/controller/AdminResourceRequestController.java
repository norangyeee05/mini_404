package com.project.controller;

import java.util.Map;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.common.ApiResponse;
import com.project.common.PageResponse;
import com.project.domain.RequestStatus;
import com.project.domain.ResourceRequest;
import com.project.dto.ResourceRequestDto.Detail;
import com.project.dto.ResourceRequestDto.ListItem;
import com.project.dto.ResourceRequestDto.StatusUpdate;
import com.project.service.ResourceRequestService;

import jakarta.validation.Valid;

/** 관리자 자료실 요청 관리 (2-7). 접근 제어는 AuthInterceptor(/api/admin/**)에서 처리 */
@RestController
@RequestMapping("/api/admin/resource-requests")
public class AdminResourceRequestController {

    private final ResourceRequestService requestService;

    public AdminResourceRequestController(ResourceRequestService requestService) {
        this.requestService = requestService;
    }

    /** 공통 쿼리 + status(PENDING/COMPLETED), searchType: title(기본) / requester */
    @GetMapping
    public ApiResponse<PageResponse<ListItem>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String searchType,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) RequestStatus status) {
        return ApiResponse.ok(requestService.list(searchType, keyword, status, page, size));
    }

    @GetMapping("/{requestId}")
    public ApiResponse<Detail> detail(@PathVariable Long requestId) {
        return ApiResponse.ok(requestService.detail(requestId));
    }

    /** (명세서 외 추가) 요청에 첨부된 파일 내려받기 */
    @GetMapping("/{requestId}/files/{fileId}")
    public ResponseEntity<Resource> download(@PathVariable Long requestId, @PathVariable Long fileId) {
        return FileHelper.download(requestService.file(requestId, fileId));
    }

    @PatchMapping("/{requestId}/status")
    public ApiResponse<Map<String, Object>> changeStatus(@PathVariable Long requestId,
                                                         @Valid @RequestBody StatusUpdate body) {
        ResourceRequest r = requestService.changeStatus(requestId, body.status());
        return ApiResponse.ok(Map.of("requestId", r.getId(), "status", r.getStatus()), "처리 상태가 변경되었습니다.");
    }

    @DeleteMapping("/{requestId}")
    public ApiResponse<Void> delete(@PathVariable Long requestId) {
        requestService.delete(requestId);
        return ApiResponse.ok(null, "요청이 삭제되었습니다.");
    }
}
