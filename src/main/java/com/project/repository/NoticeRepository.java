package com.project.repository;

import com.project.domain.Notice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NoticeRepository extends JpaRepository<Notice, Long> {

	List<Notice> findTop5ByOrderByIdDesc();
}
