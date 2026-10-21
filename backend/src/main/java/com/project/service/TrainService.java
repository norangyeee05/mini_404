package com.project.service;

import com.project.domain.Train;
import com.project.common.SearchUtil;
import com.project.dto.AdminTrainDto.Row;
import com.project.dto.AdminTrainDto.ListResponse;
import com.project.dto.AdminTrainDto.Request;
import com.project.repository.TrainRepository;
import com.project.repository.RailwayLineRepository;
import com.project.util.Validators;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TrainService {

	private final TrainRepository trainRepository;
	private final RailwayLineRepository lineRepository;

	public ListResponse list(int page, int size, String searchType, String keyword) {
		Specification<Train> spec = (root, query, cb) -> {
			if (SearchUtil.isBlank(keyword)) return cb.conjunction();
			String like = SearchUtil.likePattern(keyword);
			return switch (searchType == null ? "" : searchType) {
				case "trainNo" -> cb.like(cb.lower(root.get("trainNo")), like, '\\');
				case "driver" -> cb.like(cb.lower(root.get("driver")), like, '\\');
				case "section", "route" -> cb.like(cb.lower(root.get("route")), like, '\\');
				default -> cb.or(cb.like(cb.lower(root.get("trainNo")), like, '\\'),
						cb.like(cb.lower(root.get("driver")), like, '\\'), cb.like(cb.lower(root.get("route")), like, '\\'));
			};
		};
		Page<Train> result = trainRepository.findAll(spec,
				PageRequest.of(Math.max(page, 1) - 1, Math.min(Math.max(size, 1), 100), Sort.by(Sort.Direction.DESC, "id")));
		return new ListResponse(result.getTotalElements(), result.getNumber() + 1, result.getSize(), result.getContent().stream().map(Row::from).toList());
	}

	public Row get(Long id) {
		return Row.from(find(id));
	}

	@Transactional
	public Row create(Request request) {
		Train train = Train.builder()
				.trainNo(requireTrainNo(request.trainNo()))
				.route(Validators.requireText(request.section(), "운행 구간", 100))
				.driver(Validators.requireText(request.driver(), "기관사", 50))
				.direction(Validators.requireNotNull(request.direction(), "상행/하행"))
				.status(parseStatus(request.status()))
				.registeredAt(request.registeredAt() == null ? LocalDate.now() : request.registeredAt())
				.inspectionDate(request.inspectionDate())
				.build();
		train.setLine(lineRepository.findById(request.lineId()).orElseThrow(() -> new NoSuchElementException("노선을 찾을 수 없습니다.")));
		train.setActive(!Boolean.FALSE.equals(request.active()));
		return Row.from(trainRepository.saveAndFlush(train));
	}

	@Transactional
	public Row update(Long id, Request request) {
		Train train = find(id);
		train.update(
				requireTrainNo(request.trainNo()),
				Validators.requireText(request.section(), "운행 구간", 100),
				Validators.requireText(request.driver(), "기관사", 50),
				Validators.requireNotNull(request.direction(), "상행/하행"),
				parseStatus(request.status()),
				request.registeredAt() == null ? train.getRegisteredAt() : request.registeredAt(),
				request.inspectionDate() == null ? train.getInspectionDate() : request.inspectionDate());
		train.setLine(lineRepository.findById(request.lineId()).orElseThrow(() -> new NoSuchElementException("노선을 찾을 수 없습니다.")));
		train.setActive(!Boolean.FALSE.equals(request.active()));
		return Row.from(trainRepository.saveAndFlush(train));
	}

	@Transactional
	public void delete(Long id) {
		find(id).setActive(false);
	}

	@Transactional
	public Row setActive(Long id, boolean active) {
		Train train = find(id);
		train.setActive(active);
		return Row.from(train);
	}

	private String requireTrainNo(String trainNo) {
		if (trainNo == null || !trainNo.matches("7L-[0-9]{4}")) {
			throw com.project.common.BusinessException.badRequest("열차 번호는 7L- 뒤에 숫자 4자리 형식이어야 합니다.");
		}
		return trainNo;
	}

	private com.project.domain.OperationStatus parseStatus(String status) {
		return switch (status == null ? "" : status.toUpperCase()) {
			case "RUNNING", "NORMAL" -> com.project.domain.OperationStatus.NORMAL;
			case "MAINTENANCE" -> com.project.domain.OperationStatus.MAINTENANCE;
			case "BROKEN", "FAULT" -> com.project.domain.OperationStatus.FAULT;
			default -> throw com.project.common.BusinessException.badRequest("운행 상태 값이 올바르지 않습니다.");
		};
	}

	private Train find(Long id) {
		return trainRepository.findById(id)
				.orElseThrow(() -> new NoSuchElementException("열차를 찾을 수 없습니다."));
	}
}
