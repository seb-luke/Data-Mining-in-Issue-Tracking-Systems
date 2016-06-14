package com.warptronic.itdm;

import javax.json.JsonObject;

import com.warptronic.itdm.io.JsonFileManager;
import com.warptronic.itdm.jira.AuthType;
import com.warptronic.itdm.jira.Request;


public class App {
	
	public static void main (String[] args) {
		
		JsonObject jsonObject = new Request("http://jira.domain.com", "username", "password", AuthType.BASIC_AUTH).getJiraAllIssues();
		JsonFileManager.writeJsonToFile(jsonObject, "response");
	}
}
