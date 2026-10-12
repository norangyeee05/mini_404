package com._404._404.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "transit_service")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TransitService {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 100)
	private String name;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private OperationStatus status;

	@Builder
	public TransitService(String name, OperationStatus status) {
		this.name = name;
		this.status = status;
	}
}
