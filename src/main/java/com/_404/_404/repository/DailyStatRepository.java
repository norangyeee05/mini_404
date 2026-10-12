package com._404._404.repository;

import com._404._404.domain.DailyStat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DailyStatRepository extends JpaRepository<DailyStat, Long> {

	Optional<DailyStat> findByStatDate(LocalDate statDate);

	List<DailyStat> findByStatDateBetweenOrderByStatDateAsc(LocalDate from, LocalDate to);
}
