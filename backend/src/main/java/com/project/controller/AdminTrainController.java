package com.project.controller;

import com.project.common.ApiResponse;
import com.project.dto.AdminTrainDto.ListResponse;
import com.project.dto.AdminTrainDto.Request;
import com.project.dto.AdminTrainDto.Row;
import jakarta.validation.Valid;
import com.project.service.TrainService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/line7/trains")
@RequiredArgsConstructor
public class AdminTrainController {

	private final TrainService trainService;

	@GetMapping
	public ApiResponse<ListResponse> list(@RequestParam(defaultValue = "1") int page,
							  @RequestParam(defaultValue = "10") int size,
							  @RequestParam(required = false) String searchType,
							  @RequestParam(required = false) String keyword) {
		return ApiResponse.ok(trainService.list(page, size, searchType, keyword));
	}

	@GetMapping("/{id}")
	public ApiResponse<Row> get(@PathVariable Long id) {
		return ApiResponse.ok(trainService.get(id));
	}

	@PostMapping
	public ResponseEntity<ApiResponse<Row>> create(@Valid @RequestBody Request request) {
		return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(trainService.create(request)));
	}

	@PutMapping("/{id}")
	public ApiResponse<Row> update(@PathVariable Long id, @Valid @RequestBody Request request) {
		return ApiResponse.ok(trainService.update(id, request));
	}

	@PatchMapping("/{id}/active")
	public ApiResponse<Row> active(@PathVariable Long id, @RequestBody java.util.Map<String, Boolean> body) {
		return ApiResponse.ok(trainService.setActive(id, Boolean.TRUE.equals(body.get("active"))));
	}
}
