package com.project.dto;

import com.project.domain.RailwayLine;
import com.project.domain.RailwayStation;

import java.util.List;

public final class RailwayDto {
	private RailwayDto() {}

	public record LineResponse(Long id, String name, String color, List<StationSummary> stations) {
		public static LineResponse from(RailwayLine line, List<RailwayStation> stations) {
			return new LineResponse(line.getId(), line.getName(), line.getDisplayColor(),
					stations.stream().map(StationSummary::from).toList());
		}
	}

	public record StationSummary(Long id, String stationNo, String name, String nameEn, int sequence,
			double latitude, double longitude) {
		public static StationSummary from(RailwayStation station) {
			return new StationSummary(station.getId(), station.getStationNo(), station.getName(), station.getNameEn(),
					station.getSequenceNo(), station.getLatitude(), station.getLongitude());
		}
	}

	public record StationDetail(Long id, Long lineId, String lineName, String stationNo, String stationName, String name,
			String nameEn, double latitude, double longitude, String phone, String address, String usageInfo,
			String prevStation, String nextStation, List<String> facilities, String transferLine, List<String> transferInfo) {
		public static StationDetail from(RailwayStation station, String prevStation, String nextStation) {
			return new StationDetail(station.getId(), station.getLine().getId(), station.getLine().getName(),
					station.getStationNo(), station.getName(), station.getName(), station.getNameEn(), station.getLatitude(), station.getLongitude(),
					station.getContact(), station.getAddress(), station.getUsageInfo(), prevStation, nextStation,
					split(station.getFacilities()), station.getTransferLine(), split(station.getTransferInfo()));
		}
		private static List<String> split(String value) {
			return value == null || value.isBlank() ? List.of() : java.util.Arrays.stream(value.split("\\|"))
					.map(String::trim).filter(s -> !s.isEmpty()).toList();
		}
	}
}
