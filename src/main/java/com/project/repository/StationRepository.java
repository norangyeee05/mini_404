package com.project.repository;

import com.project.domain.Station;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface StationRepository extends JpaRepository<Station, Long>, JpaSpecificationExecutor<Station> {

	List<Station> findTop3ByOrderByIdDesc();
	Optional<Station> findByStationNo(String stationNo);
}
