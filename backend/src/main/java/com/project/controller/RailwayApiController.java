package com.project.controller;

import com.project.dto.RailwayDto.LineResponse;
import com.project.dto.RailwayDto.StationDetail;
import com.project.service.RailwayService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/railway")
@RequiredArgsConstructor
public class RailwayApiController {
	private final RailwayService railwayService;

	@GetMapping("/lines")
	public List<LineResponse> lines() { return railwayService.getLines(); }

	@GetMapping("/lines/{id}")
	public LineResponse line(@PathVariable Long id) { return railwayService.getLine(id); }

	@GetMapping("/stations/{id}")
	public StationDetail station(@PathVariable Long id) { return railwayService.getStation(id); }
}
