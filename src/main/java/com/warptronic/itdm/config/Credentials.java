package com.warptronic.itdm.config;

import com.warptronic.itdm.jira.AuthType;
import com.warptronic.itdm.utils.UrlUtils;

public class Credentials {
	private String baseUrl;
	private String username = null;
	private String password = null;
	private AuthType authenticationType = AuthType.NO_AUTH;
	
	private Credentials() {
		// Constructor not needed; use static method instead
	}
	
	public static Credentials fromArgs(String[] args) throws CredentialsException {
		Credentials credentials = new Credentials();
		
		if (UrlUtils.isUrl(args[0])) {
			credentials.baseUrl = args[0];
		} else {
			throw new CredentialsException("The provided URL \"" + args[0] + "\" is non-compliant");
		}
		
		for (int i = 1; i < args.length; i++) {
			switch (args[i]) {
				case "-user":
					credentials.username = args[++i];
					break;
				case "-pwd":
					credentials.password = args[++i];
					break;
				case "-authtype":
					int authCode = Integer.parseInt(args[++i]);
					credentials.authenticationType = AuthType.fromInt(authCode);
					break;
				default:
					throw new CredentialsException("Parameter " + args[i] + " does not exist");
			}
		}
		
		return credentials;
	}

	public String getBaseUrl() {
		return baseUrl;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public AuthType getAuthenticationType() {
		return authenticationType;
	}
}
