package com.project.repository;

import com.project.domain.RailwayLine;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface RailwayLineRepository extends JpaRepository<RailwayLine, Long> {
	List<RailwayLine> findByActiveTrueOrderByIdAsc();
	Optional<RailwayLine> findByName(String name);
	boolean existsByName(String name);
}
