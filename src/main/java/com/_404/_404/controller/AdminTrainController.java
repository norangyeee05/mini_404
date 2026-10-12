package com._404._404.controller;

import com._404._404.dto.DashboardResponse.TrainRow;
import com._404._404.dto.TransitAdminDto.TrainListResponse;
import com._404._404.dto.TransitAdminDto.TrainRequest;
import com._404._404.service.TrainService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/api/trains")
@RequiredArgsConstructor
public class AdminTrainController {

	private final TrainService trainService;

	@GetMapping
	public TrainListResponse list(@RequestParam(defaultValue = "1") int page) {
		return trainService.list(page);
	}

	@GetMapping("/{id}")
	public TrainRow get(@PathVariable Long id) {
		return trainService.get(id);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public TrainRow create(@RequestBody TrainRequest request) {
		return trainService.create(request);
	}

	@PutMapping("/{id}")
	public TrainRow update(@PathVariable Long id, @RequestBody TrainRequest request) {
		return trainService.update(id, request);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable Long id) {
		trainService.delete(id);
	}
}
