package com.project.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "notice")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notice {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 200)
	private String title;

	@Column(nullable = false, length = 50)
	private String writer;

	@Column(name = "view_count", nullable = false)
	private int viewCount;

	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;

	@Builder
	public Notice(String title, String writer, int viewCount, LocalDateTime createdAt) {
		this.title = title;
		this.writer = writer;
		this.viewCount = viewCount;
		this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
	}
}
