package com.project.dto;

import com.project.domain.Station;
import com.project.domain.Train;

import java.util.List;
import java.time.LocalDate;

public record DashboardResponse(
		String now,
		Cards cards,
		ShareChart serviceStatus,
		List<TrendPoint> weeklyTrend,
		ShareChart memberStatus,
		List<TrainRow> trains,
		List<StationRow> stations) {

	public record Cards(CountCard members, CountCard notices, CountCard qnaPending, ServiceCard services) {
	}

	public record CountCard(long count, long diffFromYesterday) {
	}

	public record ServiceCard(long total, long normal, long runningTrains) {
	}

	public record ShareChart(long total, List<Share> shares) {
	}

	public record Share(String code, String label, long count, double percent) {
	}

	public record TrendPoint(String date, long normal, long maintenance, long fault) {
	}

	public record TrainRow(Long id, String status, String statusLabel, String route,
						   String trainNo, String driver, String direction, String directionLabel, boolean active,
						   LocalDate registeredAt, LocalDate inspectionDate) {

		public static TrainRow from(Train t) {
			return new TrainRow(t.getId(), t.getStatus().name(), t.getStatus().getOperationLabel(),
					t.getRoute(), t.getTrainNo(), t.getDriver(),
					 t.getDirection().name(), t.getDirection().getLabel(), t.isActive(),
					 t.getRegisteredAt(), t.getInspectionDate());
		}
	}

	public record StationRow(Long id, String status, String statusLabel, String name,
							 String stationNo, String staff) {

		public static StationRow from(Station s) {
			return new StationRow(s.getId(), s.getStatus().name(), s.getStatus().getOperationLabel(),
					s.getName(), s.getStationNo(), s.getStaff());
		}
	}
}
