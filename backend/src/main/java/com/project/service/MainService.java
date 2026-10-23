package com.project.service;

import com.project.domain.Notice;
import com.project.domain.Qna;
import com.project.repository.NoticeRepository;
import com.project.repository.QnaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MainService {


	private static final List<String> STATIONS = List.of(
			"석남", "산곡", "부평구청", "굴포천", "삼산체육관", "상동",
			"부천시청", "신중동", "춘의", "부천종합운동장", "까치울");

	private final NoticeRepository noticeRepository;
	private final QnaRepository qnaRepository;

	public List<String> getStations() {
		return STATIONS;
	}

	public List<Notice> getLatestNotices() {
		return noticeRepository.findTop5ByOrderByCreatedAtDescIdDesc();
	}

	public List<Qna> getLatestQnas() {
		return qnaRepository.findTop3ByOrderByCreatedAtDescIdDesc();
	}
}
