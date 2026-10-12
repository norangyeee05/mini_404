package com._404._404.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

@Getter
@Setter
public class SignupForm {

	private static final Pattern LOGIN_ID = Pattern.compile("^[a-zA-Z0-9]{4,20}$");
	private static final Pattern EMAIL = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

	private String loginId;
	private String password;
	private String name;
	private String email;

	/** 필드명 -> 오류 메시지. 비어 있으면 검증 통과 */
	public Map<String, String> validate() {
		Map<String, String> errors = new LinkedHashMap<>();
		if (isBlank(loginId) || !LOGIN_ID.matcher(loginId.trim()).matches()) {
			errors.put("loginId", "아이디는 영문/숫자 4~20자로 입력해 주세요.");
		}
		if (isBlank(password) || password.length() < 8 || password.length() > 30) {
			errors.put("password", "비밀번호는 8~30자로 입력해 주세요.");
		}
		if (isBlank(name) || name.trim().length() > 50) {
			errors.put("name", "이름을 50자 이내로 입력해 주세요.");
		}
		if (isBlank(email) || email.trim().length() > 100 || !EMAIL.matcher(email.trim()).matches()) {
			errors.put("email", "이메일 형식이 올바르지 않습니다.");
		}
		return errors;
	}

	private static boolean isBlank(String s) {
		return s == null || s.isBlank();
	}
}
