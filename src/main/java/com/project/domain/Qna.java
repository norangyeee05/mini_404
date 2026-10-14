package com.project.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "qna")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Qna {

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

	@Column(nullable = false, columnDefinition = "boolean default false")
	private boolean answered;

	@Builder
	public Qna(String title, String writer, int viewCount, LocalDateTime createdAt, boolean answered) {
		this.title = title;
		this.writer = writer;
		this.viewCount = viewCount;
		this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
		this.answered = answered;
	}
}
