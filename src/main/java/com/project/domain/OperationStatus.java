package com.project.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OperationStatus {

	NORMAL("정상", "운행"),
	MAINTENANCE("점검", "점검"),
	FAULT("고장", "고장");

	private final String label;
	private final String operationLabel;
}
