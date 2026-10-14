package com.project.util;

public final class Validators {

	private Validators() {
	}

	public static String requireText(String value, String label, int maxLength) {
		if (value == null || value.isBlank()) {
			throw new IllegalArgumentException(label + "을(를) 입력해 주세요.");
		}
		String trimmed = value.trim();
		if (trimmed.length() > maxLength) {
			throw new IllegalArgumentException(label + "은(는) " + maxLength + "자 이내로 입력해 주세요.");
		}
		return trimmed;
	}

	public static <T> T requireNotNull(T value, String label) {
		if (value == null) {
			throw new IllegalArgumentException(label + "을(를) 선택해 주세요.");
		}
		return value;
	}
}
