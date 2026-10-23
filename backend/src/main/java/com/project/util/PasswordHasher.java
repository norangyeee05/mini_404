package com.project.util;

import org.springframework.stereotype.Component;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;






@Component
public class PasswordHasher {

	private static final String ALGORITHM = "PBKDF2WithHmacSHA256";
	private static final int ITERATIONS = 120_000;
	private static final int SALT_BYTES = 16;
	private static final int KEY_BITS = 256;

	private final SecureRandom random = new SecureRandom();

	public String encode(String rawPassword) {
		byte[] salt = new byte[SALT_BYTES];
		random.nextBytes(salt);
		byte[] hash = pbkdf2(rawPassword, salt, ITERATIONS);
		return ITERATIONS + ":" + Base64.getEncoder().encodeToString(salt)
				+ ":" + Base64.getEncoder().encodeToString(hash);
	}

	public boolean matches(String rawPassword, String stored) {
		try {
			String[] parts = stored.split(":");
			int iterations = Integer.parseInt(parts[0]);
			byte[] salt = Base64.getDecoder().decode(parts[1]);
			byte[] expected = Base64.getDecoder().decode(parts[2]);
			byte[] actual = pbkdf2(rawPassword, salt, iterations);
			return MessageDigest.isEqual(expected, actual);
		} catch (RuntimeException e) {
			return false;
		}
	}

	private byte[] pbkdf2(String password, byte[] salt, int iterations) {
		try {
			PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, KEY_BITS);
			return SecretKeyFactory.getInstance(ALGORITHM).generateSecret(spec).getEncoded();
		} catch (GeneralSecurityException e) {
			throw new IllegalStateException("비밀번호 해시 생성에 실패했습니다.", e);
		}
	}
}
