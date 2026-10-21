package com.project.repository;

import com.project.domain.RailwayStation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface RailwayStationRepository extends JpaRepository<RailwayStation, Long>, JpaSpecificationExecutor<RailwayStation> {
	List<RailwayStation> findByLineIdOrderBySequenceNoAsc(Long lineId);
	@Query(value = """
			select * from railway_station
			where line_id = :lineId and (
				:keyword = ''
				or (:searchType = 'stationName' and lower(name) like :pattern escape '\\')
				or (:searchType = 'stationNo' and lower(station_no) like :pattern escape '\\')
				or (:searchType not in ('stationName', 'stationNo') and
					(lower(name) like :pattern escape '\\' or lower(station_no) like :pattern escape '\\'))
			)
			order by cast(station_no as integer) asc, id asc
			""",
		countQuery = """
			select count(*) from railway_station
			where line_id = :lineId and (
				:keyword = ''
				or (:searchType = 'stationName' and lower(name) like :pattern escape '\\')
				or (:searchType = 'stationNo' and lower(station_no) like :pattern escape '\\')
				or (:searchType not in ('stationName', 'stationNo') and
					(lower(name) like :pattern escape '\\' or lower(station_no) like :pattern escape '\\'))
			)
			""", nativeQuery = true)
	Page<RailwayStation> findAdminPage(@Param("lineId") Long lineId,
			@Param("searchType") String searchType, @Param("keyword") String keyword,
			@Param("pattern") String pattern, Pageable pageable);
	@Query(value = "select * from railway_station where line_id = :lineId order by cast(station_no as integer) desc, id asc", nativeQuery = true)
	List<RailwayStation> findByLineIdOrderByNumericStationNoDesc(@Param("lineId") Long lineId);
	Optional<RailwayStation> findByIdAndLineId(Long id, Long lineId);
	boolean existsByLine_IdAndStationNo(Long lineId, String stationNo);
	boolean existsByLine_IdAndStationNoAndIdNot(Long lineId, String stationNo, Long id);
}
