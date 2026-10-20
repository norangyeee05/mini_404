package com.project.service;

import com.project.domain.RailwayLine;
import com.project.domain.RailwayStation;
import com.project.dto.RailwayDto.LineResponse;
import com.project.dto.RailwayDto.StationDetail;
import com.project.dto.Line7StationDto;
import com.project.repository.RailwayLineRepository;
import com.project.repository.RailwayStationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RailwayService {
	private final RailwayLineRepository lineRepository;
	private final RailwayStationRepository stationRepository;

	public List<LineResponse> getLines() {
		return lineRepository.findByActiveTrueOrderByIdAsc().stream()
				.map(line -> LineResponse.from(line, stationRepository.findByLineIdOrderBySequenceNoAsc(line.getId())))
				.toList();
	}

	public List<Line7StationDto> getLine7Stations() {
		RailwayLine line = lineRepository.findByName("7호선")
				.orElseThrow(() -> new NoSuchElementException("7호선 정보를 찾을 수 없습니다."));
		return stationRepository.findByLineIdOrderBySequenceNoAsc(line.getId()).stream().filter(RailwayStation::isActive)
				.map(s -> new Line7StationDto(s.getId(), s.getName(), s.getSequenceNo())).toList();
	}

	public Map<String, List<Map<String, String>>> getTransfers(Long id) {
		RailwayStation station = stationRepository.findById(id).filter(RailwayStation::isActive)
				.orElseThrow(() -> new NoSuchElementException("역을 찾을 수 없습니다."));
		if (station.getTransferLine() == null || station.getTransferLine().isBlank()
				|| station.getTransferInfo() == null || station.getTransferInfo().isBlank()) {
			return Map.of("transfers", List.of());
		}
		List<Map<String, String>> transfers = java.util.Arrays.stream(station.getTransferInfo().split("\\|"))
				.map(String::trim).filter(s -> !s.isEmpty())
				.map(info -> Map.of("transferLineName", station.getTransferLine(), "transferInfo", info)).toList();
		return Map.of("transfers", transfers);
	}

	public LineResponse getLine(Long id) {
		RailwayLine line = lineRepository.findById(id).filter(RailwayLine::isActive)
				.orElseThrow(() -> new NoSuchElementException("노선을 찾을 수 없습니다."));
		return LineResponse.from(line, stationRepository.findByLineIdOrderBySequenceNoAsc(id));
	}

	public StationDetail getStation(Long id) {
		RailwayStation station = stationRepository.findById(id).filter(RailwayStation::isActive)
				.orElseThrow(() -> new NoSuchElementException("역을 찾을 수 없습니다."));
		List<RailwayStation> activeStations = stationRepository.findByLineIdOrderBySequenceNoAsc(station.getLine().getId())
				.stream().filter(RailwayStation::isActive).toList();
		int index = java.util.stream.IntStream.range(0, activeStations.size())
				.filter(i -> activeStations.get(i).getId().equals(station.getId())).findFirst().orElse(-1);
		String previous = index > 0 ? activeStations.get(index - 1).getName() : null;
		String next = index >= 0 && index + 1 < activeStations.size() ? activeStations.get(index + 1).getName() : null;
		return StationDetail.from(station, previous, next);
	}
}
