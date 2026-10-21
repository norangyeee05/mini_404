package com.project.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "train")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Train {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "line_id")
	private RailwayLine line;

	@Column(name = "train_no", nullable = false, length = 20)
	private String trainNo;

	@Column(nullable = false, length = 100)
	private String route;

	@Column(nullable = false, length = 50)
	private String driver;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 10)
	private TrainDirection direction;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private OperationStatus status;

	@Column(nullable = false, columnDefinition = "boolean default true")
	private boolean active = true;

	@Column(name = "registered_at")
	private LocalDate registeredAt;

	@Column(name = "inspection_date")
	private LocalDate inspectionDate;

	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

	@Builder
	public Train(String trainNo, String route, String driver, TrainDirection direction, OperationStatus status,
			 LocalDate registeredAt, LocalDate inspectionDate) {
		this.trainNo = trainNo;
		this.route = route;
		this.driver = driver;
		this.direction = direction;
		this.status = status;
		this.registeredAt = registeredAt;
		this.inspectionDate = inspectionDate;
	}

	public void update(String trainNo, String route, String driver, TrainDirection direction, OperationStatus status,
			   LocalDate registeredAt, LocalDate inspectionDate) {
		this.trainNo = trainNo;
		this.route = route;
		this.driver = driver;
		this.direction = direction;
		this.status = status;
		this.registeredAt = registeredAt;
		this.inspectionDate = inspectionDate;
	}

	public void setActive(boolean active) { this.active = active; }
	public void setLine(RailwayLine line) { this.line = line; }

	@PrePersist
	@PreUpdate
	void touchUpdatedAt() { this.updatedAt = LocalDateTime.now(); }
}
