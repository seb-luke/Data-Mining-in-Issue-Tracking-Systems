package com.warptronic.itdm.jira;

import java.util.Base64;

import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.Invocation.Builder;

public class AuthorizationManager {
	
	private String user;
	private String password;
	private String sessionName;
	private String sessionValue;
	
	public AuthorizationManager(String user, String password) {
		this.user = user;
		this.password = password;
	}

	public Invocation.Builder addBasicAuthorizationHeader(Invocation.Builder invocationBuilder) {
		byte[] credentials = user.concat(":").concat(password).getBytes();
		String encodedCredentials = Base64.getEncoder().encodeToString(credentials);
		
		return invocationBuilder.header("Authorization", "Basic ".concat(encodedCredentials));
	}

	public Builder addCookieAuthHeader(Builder invocationBuilder) {
		// TODO Auto-generated method stub
		return null;
	}
}
