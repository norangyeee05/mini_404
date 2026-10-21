package com.project.service;

public class AccountRestrictedException extends RuntimeException {

	public AccountRestrictedException(String message) {
		super(message);
	}
}
