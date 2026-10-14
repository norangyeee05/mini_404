package com.project.repository;

import com.project.domain.Train;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TrainRepository extends JpaRepository<Train, Long> {

	List<Train> findTop3ByOrderByIdDesc();
}
