package com.project.controller;

import com.project.common.ApiResponse;
import com.project.dto.Line7StationDto;
import com.project.service.RailwayService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/line7/stations")
@RequiredArgsConstructor
public class Line7StationController {

    private final RailwayService railwayService;

    @GetMapping
    public ApiResponse<List<Line7StationDto>> list() {
        return ApiResponse.ok(railwayService.getLine7Stations());
    }

    @GetMapping("/{id}")
    public ApiResponse<com.project.dto.RailwayDto.StationDetail> detail(@PathVariable Long id) {
        return ApiResponse.ok(railwayService.getStation(id));
    }

    @GetMapping("/{id}/transfer")
    public ApiResponse<Map<String, List<Map<String, String>>>> transfer(@PathVariable Long id) {
        return ApiResponse.ok(railwayService.getTransfers(id));
    }
}
