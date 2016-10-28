package com.warptronic.itdm.jira;

import java.util.HashMap;
import java.util.Map;

public enum AuthType {
	
	NO_AUTH(0),
	BASIC_AUTH(1),
	COOKIE_BASED_AUTH(2);
	
	private int authCode;
	private static Map<Integer, AuthType> authMap;
	
	private AuthType(int authCode) {
		this.authCode = authCode;
	}
	
	static {
		authMap = new HashMap<>();
		for (AuthType auth : AuthType.values()) {
			authMap.put(auth.authCode, auth);
		}
	}
	
	public static AuthType fromInt(int authCode) {
		return authMap.get(authCode);
	}
}
