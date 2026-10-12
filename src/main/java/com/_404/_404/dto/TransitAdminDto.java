package com._404._404.dto;

import com._404._404.dto.DashboardResponse.StationRow;
import com._404._404.dto.DashboardResponse.TrainRow;

import java.util.List;

public final class TransitAdminDto {

	private TransitAdminDto() {
	}

	public record TrainRequest(String trainNo, String route, String driver,
							   com._404._404.domain.TrainDirection direction,
							   com._404._404.domain.OperationStatus status) {
	}

	public record StationRequest(String name, String stationNo, String staff,
								 com._404._404.domain.OperationStatus status) {
	}

	public record TrainListResponse(List<TrainRow> items, PageInfo pageInfo) {
	}

	public record StationListResponse(List<StationRow> items, PageInfo pageInfo) {
	}
}
