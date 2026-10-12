package com._404._404.repository;

import com._404._404.domain.Qna;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QnaRepository extends JpaRepository<Qna, Long> {

	List<Qna> findTop3ByOrderByIdDesc();

	long countByAnswered(boolean answered);
}
