package com._404._404.service;

import com._404._404.domain.Train;
import com._404._404.dto.DashboardResponse.TrainRow;
import com._404._404.dto.PageInfo;
import com._404._404.dto.TransitAdminDto.TrainListResponse;
import com._404._404.dto.TransitAdminDto.TrainRequest;
import com._404._404.repository.TrainRepository;
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
public class TrainService {

	private static final int PAGE_SIZE = 10;

	private final TrainRepository trainRepository;

	public TrainListResponse list(int page) {
		Page<Train> result = trainRepository.findAll(
				PageRequest.of(Math.max(page, 1) - 1, PAGE_SIZE, Sort.by(Sort.Direction.DESC, "id")));
		return new TrainListResponse(result.getContent().stream().map(TrainRow::from).toList(), PageInfo.from(result));
	}

	public TrainRow get(Long id) {
		return TrainRow.from(find(id));
	}

	@Transactional
	public TrainRow create(TrainRequest request) {
		Train train = Train.builder()
				.trainNo(Validators.requireText(request.trainNo(), "열차 번호", 20))
				.route(Validators.requireText(request.route(), "운행 구간", 100))
				.driver(Validators.requireText(request.driver(), "기관사", 50))
				.direction(Validators.requireNotNull(request.direction(), "상행/하행"))
				.status(Validators.requireNotNull(request.status(), "운행 상태"))
				.build();
		return TrainRow.from(trainRepository.save(train));
	}

	@Transactional
	public TrainRow update(Long id, TrainRequest request) {
		Train train = find(id);
		train.update(
				Validators.requireText(request.trainNo(), "열차 번호", 20),
				Validators.requireText(request.route(), "운행 구간", 100),
				Validators.requireText(request.driver(), "기관사", 50),
				Validators.requireNotNull(request.direction(), "상행/하행"),
				Validators.requireNotNull(request.status(), "운행 상태"));
		return TrainRow.from(trainRepository.saveAndFlush(train));
	}

	@Transactional
	public void delete(Long id) {
		trainRepository.delete(find(id));
	}

	private Train find(Long id) {
		return trainRepository.findById(id)
				.orElseThrow(() -> new NoSuchElementException("열차를 찾을 수 없습니다."));
	}
}
