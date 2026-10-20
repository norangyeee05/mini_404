package com.project.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "railway_line")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RailwayLine {
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true, length = 40)
	private String name;

	@Column(name = "display_color", nullable = false, length = 20)
	private String displayColor;

	@Column(nullable = false)
	private boolean active;

	public RailwayLine(String name, String displayColor, boolean active) {
		this.name = name;
		this.displayColor = displayColor;
		this.active = active;
	}
}
