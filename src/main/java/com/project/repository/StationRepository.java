package com.project.repository;

import com.project.domain.Station;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StationRepository extends JpaRepository<Station, Long> {

	List<Station> findTop3ByOrderByIdDesc();
}
