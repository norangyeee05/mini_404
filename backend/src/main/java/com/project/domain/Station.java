package com.project.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "station")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Station {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 100)
	private String name;

	@Column(name = "station_no", nullable = false, length = 20)
	private String stationNo;

	@Column(nullable = false, length = 50)
	private String staff;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private OperationStatus status;

	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

	@Builder
	public Station(String name, String stationNo, String staff, OperationStatus status) {
		this.name = name;
		this.stationNo = stationNo;
		this.staff = staff;
		this.status = status;
	}

	public void update(String name, String stationNo, String staff, OperationStatus status) {
		this.name = name;
		this.stationNo = stationNo;
		this.staff = staff;
		this.status = status;
	}

	@PrePersist
	@PreUpdate
	void touchUpdatedAt() { this.updatedAt = LocalDateTime.now(); }
}
