package com._404._404.controller;

import com._404._404.dto.DashboardResponse.StationRow;
import com._404._404.dto.TransitAdminDto.StationListResponse;
import com._404._404.dto.TransitAdminDto.StationRequest;
import com._404._404.service.StationService;
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
@RequestMapping("/admin/api/stations")
@RequiredArgsConstructor
public class AdminStationController {

	private final StationService stationService;

	@GetMapping
	public StationListResponse list(@RequestParam(defaultValue = "1") int page) {
		return stationService.list(page);
	}

	@GetMapping("/{id}")
	public StationRow get(@PathVariable Long id) {
		return stationService.get(id);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public StationRow create(@RequestBody StationRequest request) {
		return stationService.create(request);
	}

	@PutMapping("/{id}")
	public StationRow update(@PathVariable Long id, @RequestBody StationRequest request) {
		return stationService.update(id, request);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable Long id) {
		stationService.delete(id);
	}
}
