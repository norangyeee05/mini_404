package com.project.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Entity
@Table(name = "railway_station", uniqueConstraints = @UniqueConstraint(columnNames = {"line_id", "station_no"}))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RailwayStation {
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "line_id", nullable = false)
	private RailwayLine line;

	@Column(name = "station_no", nullable = false, length = 20)
	private String stationNo;

	@Column(nullable = false, length = 100)
	private String name;

	@Column(name = "name_en", length = 100)
	private String nameEn;

	@Column(nullable = false)
	private int sequenceNo;

	@Column(nullable = false)
	private double latitude;

	@Column(nullable = false)
	private double longitude;

	@Column(length = 300)
	private String address;

	@Column(length = 500)
	private String facilities;

	@Column(name = "transfer_line", length = 100)
	private String transferLine;

	@Column(name = "transfer_info", length = 500)
	private String transferInfo;

	@Column(name = "staff_name", length = 50)
	private String staffName;

	@Column(length = 30)
	private String contact;

	@Column(name = "usage_info", length = 500)
	private String usageInfo;

	@Column(name = "registered_at")
	private LocalDate registeredAt;

	@Column(name = "inspection_date")
	private LocalDate inspectionDate;

	@Column(name = "facility_status", length = 30)
	private String facilityStatus = "INSPECTED";

	@Column(name = "last_inspection_date")
	private LocalDate lastInspectionDate;

	@Column(name = "next_inspection_due_date")
	private LocalDate nextInspectionDueDate;

	@Column(nullable = false, columnDefinition = "boolean default true")
	private boolean active = true;

	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

	public RailwayStation(RailwayLine line, String stationNo, String name, String nameEn, int sequenceNo,
			double latitude, double longitude, String address, String facilities, String transferLine, String transferInfo) {
		this.line = line;
		this.stationNo = stationNo;
		this.name = name;
		this.nameEn = nameEn;
		this.sequenceNo = sequenceNo;
		this.latitude = latitude;
		this.longitude = longitude;
		this.address = address;
		this.facilities = facilities;
		this.transferLine = transferLine;
		this.transferInfo = transferInfo;
		touchUpdatedAt();
	}

	public void update(String stationNo, String name, String nameEn, int sequenceNo, double latitude,
			double longitude, String address, String facilities, String transferLine, String transferInfo) {
		this.stationNo = stationNo;
		this.name = name;
		this.nameEn = nameEn;
		this.sequenceNo = sequenceNo;
		this.latitude = latitude;
		this.longitude = longitude;
		this.address = address;
		this.facilities = facilities;
		this.transferLine = transferLine;
		this.transferInfo = transferInfo;
		touchUpdatedAt();
	}

	public void setActive(boolean active) { this.active = active; touchUpdatedAt(); }

	public void updateAdminDetails(String staffName, String contact, String usageInfo,
								   LocalDate registeredAt, LocalDate inspectionDate, String facilityStatus,
								   LocalDate lastInspectionDate, LocalDate nextInspectionDueDate) {
		this.staffName = staffName;
		this.contact = contact;
		this.usageInfo = usageInfo;
		this.registeredAt = registeredAt;
		this.inspectionDate = inspectionDate;
		this.facilityStatus = facilityStatus == null ? "INSPECTED" : facilityStatus;
		this.lastInspectionDate = lastInspectionDate;
		this.nextInspectionDueDate = nextInspectionDueDate;
		touchUpdatedAt();
	}

	@PrePersist
	private void touchOnCreate() { touchUpdatedAt(); }

	private void touchUpdatedAt() { this.updatedAt = LocalDateTime.now(ZoneId.of("Asia/Seoul")); }
}
