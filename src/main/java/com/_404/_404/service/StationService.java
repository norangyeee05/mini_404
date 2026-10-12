package com._404._404.service;

import com._404._404.domain.Station;
import com._404._404.dto.DashboardResponse.StationRow;
import com._404._404.dto.PageInfo;
import com._404._404.dto.TransitAdminDto.StationListResponse;
import com._404._404.dto.TransitAdminDto.StationRequest;
import com._404._404.repository.StationRepository;
import com._404._404.util.Validators;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StationService {

	private static final int PAGE_SIZE = 10;

	private final StationRepository stationRepository;

	public StationListResponse list(int page) {
		Page<Station> result = stationRepository.findAll(
				PageRequest.of(Math.max(page, 1) - 1, PAGE_SIZE, Sort.by(Sort.Direction.DESC, "id")));
		return new StationListResponse(result.getContent().stream().map(StationRow::from).toList(), PageInfo.from(result));
	}

	public StationRow get(Long id) {
		return StationRow.from(find(id));
	}

	@Transactional
	public StationRow create(StationRequest request) {
		Station station = Station.builder()
				.name(Validators.requireText(request.name(), "역 이름", 100))
				.stationNo(Validators.requireText(request.stationNo(), "역 번호", 20))
				.staff(Validators.requireText(request.staff(), "역무원", 50))
				.status(Validators.requireNotNull(request.status(), "시설 상태"))
				.build();
		return StationRow.from(stationRepository.save(station));
	}

	@Transactional
	public StationRow update(Long id, StationRequest request) {
		Station station = find(id);
		station.update(
				Validators.requireText(request.name(), "역 이름", 100),
				Validators.requireText(request.stationNo(), "역 번호", 20),
				Validators.requireText(request.staff(), "역무원", 50),
				Validators.requireNotNull(request.status(), "시설 상태"));
		return StationRow.from(stationRepository.saveAndFlush(station));
	}

	@Transactional
	public void delete(Long id) {
		stationRepository.delete(find(id));
	}

	private Station find(Long id) {
		return stationRepository.findById(id)
				.orElseThrow(() -> new NoSuchElementException("역을 찾을 수 없습니다."));
	}
}
