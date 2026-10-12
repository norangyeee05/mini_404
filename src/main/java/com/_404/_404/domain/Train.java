package com._404._404.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "train")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Train {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

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

	@Builder
	public Train(String trainNo, String route, String driver, TrainDirection direction, OperationStatus status) {
		this.trainNo = trainNo;
		this.route = route;
		this.driver = driver;
		this.direction = direction;
		this.status = status;
	}

	public void update(String trainNo, String route, String driver, TrainDirection direction, OperationStatus status) {
		this.trainNo = trainNo;
		this.route = route;
		this.driver = driver;
		this.direction = direction;
		this.status = status;
	}
}
