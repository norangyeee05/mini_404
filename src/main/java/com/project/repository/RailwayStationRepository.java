package com.project.repository;

import com.project.domain.RailwayStation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.util.List;
import java.util.Optional;

public interface RailwayStationRepository extends JpaRepository<RailwayStation, Long>, JpaSpecificationExecutor<RailwayStation> {
	List<RailwayStation> findByLineIdOrderBySequenceNoAsc(Long lineId);
	Optional<RailwayStation> findByIdAndLineId(Long id, Long lineId);
}
