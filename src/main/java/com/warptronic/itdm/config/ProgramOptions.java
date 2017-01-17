package com.warptronic.itdm.config;

import com.warptronic.itdm.jira.AuthType;
import com.warptronic.itdm.utils.StringUtils;
import com.warptronic.itdm.utils.UrlUtils;

public class ProgramOptions {
	private String baseUrl;
	private String username = "";
	private String password = "";
	private String projectName = "";
	private String cookie = "";
	private AuthType authenticationType = AuthType.NO_AUTH;
	
	private ProgramOptions() {
		// Constructor not needed; use static method instead
	}
	
	public static ProgramOptions fromArgs(String[] args) throws CredentialsException {
		ProgramOptions credentials = new ProgramOptions();
		
		if (UrlUtils.isUrl(args[0])) {
			credentials.baseUrl = args[0];
		} else {
			throw new CredentialsException("The provided URL \"" + args[0] + "\" is non-compliant");
		}
		
		for (int i = 1; i < args.length; i++) {
			switch (args[i]) {
				case "-user":
					credentials.username = getValueFromArg("user", args, ++i);
					break;
				case "-pwd":
					credentials.password = getValueFromArg("pwd", args, ++i);
					break;
				case "-cookie":
					credentials.cookie = getValueFromArg("cookie", args, ++i);
					break;
				case "-authtype":
					String value = getValueFromArg("authtype", args, ++i); 
					int authCode = Integer.parseInt(value);
					credentials.authenticationType = AuthType.fromInt(authCode);
					break;
				case "-projectname":
					credentials.projectName = getValueFromArg("projectname", args, ++i);
					break;
				default:
					throw new CredentialsException("Parameter " + args[i] + " does not exist");
			}
		}
		
		return credentials;
	}
	
	private static String getValueFromArg(String key, String[] args, int position) throws CredentialsException {
		if (position >= args.length) {
			throw new CredentialsException("Value missing for key '-" + key + "'");
		}
		
		String value = args[position];
		if (StringUtils.isNullOrEmpty(value) || (value.charAt(0) == '-')) {
			throw new CredentialsException("Value '" + value + "' for '-" + key + "' is not correct.\n" +
					"Values should not be empty or start with a dash");
		}
		
		return value;
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
	
	public String getCookie() {
		return cookie;
	}

	public AuthType getAuthenticationType() {
		return authenticationType;
	}
	
	public String getProjectName() {
		return projectName;
	}
}
