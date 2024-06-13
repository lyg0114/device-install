package com.install.global.security.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RoleType {

	ADMIN("ROLE_ADMIN", "관리자"),
	USER("ROLE_WORKER", "작업자 사용자");

	private final String key;
	private final String title;

	public static RoleType fromKey(String key) {
		for (RoleType role : RoleType.values()) {
			if (role.getKey().equals(key)) {
				return role;
			}
		}
		throw new IllegalArgumentException("No enum constant with key " + key);
	}
}
