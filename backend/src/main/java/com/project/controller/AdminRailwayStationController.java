package com.project.controller;

import com.project.common.ApiResponse;
import com.project.dto.AdminRailwayStationDto;
import com.project.dto.AdminRailwayStationDto.Form;
import com.project.dto.AdminRailwayStationDto.ListItem;
import com.project.dto.AdminRailwayStationDto.ListResponse;
import com.project.service.AdminRailwayStationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/line7/stations")
public class AdminRailwayStationController {
    private final AdminRailwayStationService service;

    @GetMapping
    public ApiResponse<ListResponse> list(@RequestParam(defaultValue = "1") int page,
                                          @RequestParam(defaultValue = "10") int size,
                                          @RequestParam(required = false) String searchType,
                                          @RequestParam(required = false) String keyword) {
        return ApiResponse.ok(service.list(page, size, searchType, keyword));
    }

    @GetMapping("/{id}")
    public ApiResponse<ListItem> detail(@PathVariable Long id) { return ApiResponse.ok(service.get(id)); }

    @PostMapping
    public ResponseEntity<ApiResponse<ListItem>> create(@Valid @RequestBody Form form) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(service.create(form), "역 정보가 등록되었습니다."));
    }

    @PutMapping("/{id}")
    public ApiResponse<ListItem> update(@PathVariable Long id, @Valid @RequestBody Form form) {
        return ApiResponse.ok(service.update(id, form), "역 정보가 수정되었습니다.");
    }

    @PatchMapping("/{id}/active")
    public ApiResponse<ListItem> setActive(@PathVariable Long id, @Valid @RequestBody AdminRailwayStationDto.ActiveUpdate body) {
        return ApiResponse.ok(service.setActive(id, body.active()), "역 활성 상태가 변경되었습니다.");
    }
}
