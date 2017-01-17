package com.warptronic.itdm.jira;

import java.util.Base64;

import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.Invocation.Builder;

import org.glassfish.jersey.Beta;

import com.warptronic.itdm.config.ProgramOptions;

public class AuthorizationManager {
	
	private String user;
	private String password;
	private String sessionName;
	private String sessionValue;
	
	public AuthorizationManager(String user, String password, String cookieValue) {
		this.user = user;
		this.password = password;
		
		this.sessionName = "JSESSIONID";
		this.sessionValue = cookieValue;
	}

	public AuthorizationManager(ProgramOptions credentials) {
		this(credentials.getUsername(), credentials.getPassword(), credentials.getCookie());
	}

	public Invocation.Builder addBasicAuthorizationHeader(Invocation.Builder invocationBuilder) {
		byte[] credentials = user.concat(":").concat(password).getBytes();
		String encodedCredentials = Base64.getEncoder().encodeToString(credentials);
		
		return invocationBuilder.header("Authorization", "Basic ".concat(encodedCredentials));
	}

	@Beta
	public Builder addCookieAuthHeader(Builder invocationBuilder) {

		return invocationBuilder.header("cookie", sessionName + "=" + sessionValue);
	}
}
