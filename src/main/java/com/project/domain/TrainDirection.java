package com.project.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TrainDirection {

	UP("상행"),
	DOWN("하행");

	private final String label;
}
