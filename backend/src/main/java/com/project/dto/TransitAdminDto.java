package com.project.dto;

import com.project.dto.DashboardResponse.StationRow;
import com.project.dto.DashboardResponse.TrainRow;

import java.util.List;
import java.time.LocalDate;

public final class TransitAdminDto {

	private TransitAdminDto() {
	}

	public record TrainRequest(String trainNo, String route, String driver,
							   com.project.domain.TrainDirection direction,
							   com.project.domain.OperationStatus status, Boolean active,
							   LocalDate registeredAt, LocalDate inspectionDate) {
	}

	public record StationRequest(String name, String stationNo, String staff,
								 com.project.domain.OperationStatus status) {
	}

	public record TrainListResponse(List<TrainRow> items, PageInfo pageInfo) {
	}

	public record StationListResponse(List<StationRow> items, PageInfo pageInfo) {
	}
}
