package com._404._404.repository;

import com._404._404.domain.Train;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TrainRepository extends JpaRepository<Train, Long> {

	List<Train> findTop3ByOrderByIdDesc();
}
