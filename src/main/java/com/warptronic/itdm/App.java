package com.warptronic.itdm;

import javax.json.JsonObject;

import com.warptronic.itdm.io.JsonFileManager;
import com.warptronic.itdm.jira.AuthType;
import com.warptronic.itdm.jira.Request;


public class App {
	
	public static void main (String[] args) {
		String baseUrl;
		String username;
		String password;
		AuthType authenticationType;
		
		if (args.length < 3) {
			System.out.println("Too few parameters Please provide the URL to Jira, the username and password");
			System.out.println("e.g. java App.java http://jira.domain.com username password [d]");
			System.out.println("Where 'd' is an optional number: 1 = Basic Jira Authentication, 2 = Cookie Jira Authentication");
			System.exit(-1);
		}
		
		//TODO sanitization of strings/URLs and format validation
		baseUrl = args[0];
		username = args[1];
		password = args[2];
		
		int auth = args[3] == null ? 0 : Integer.parseInt(args[3]);
		switch (auth) {
		case 1:
			authenticationType = AuthType.BASIC_AUTH;
			break;
			
		case 2:
			authenticationType = AuthType.COOKIE_BASED_AUTH;
			break;
			
		default:
			authenticationType = AuthType.BASIC_AUTH;
			break;
		}
		
		JsonObject jsonObject = new Request(baseUrl, username, password, authenticationType).getJiraAllIssues();
		jsonObject = postProcessJson(jsonObject);
		
		//TODO name the file based on date-time
		JsonFileManager.writeJsonToFile(jsonObject, "response");
	}

	private static JsonObject postProcessJson(JsonObject jsonObject) {
		
		// TODO return only needed data
		
		
		return jsonObject;
	}
}




