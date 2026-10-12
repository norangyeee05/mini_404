package com._404._404.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
}
