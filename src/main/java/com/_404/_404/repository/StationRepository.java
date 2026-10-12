package com._404._404.repository;

import com._404._404.domain.Station;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StationRepository extends JpaRepository<Station, Long> {

	List<Station> findTop3ByOrderByIdDesc();
}
