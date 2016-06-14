package com.warptronic.itdm.jira;

import javax.json.JsonObject;
import javax.ws.rs.client.*;
import javax.ws.rs.core.*;

public class Request {
	private AuthorizationManager authManager;
	private AuthType authType;
	
	private Client client;
	private WebTarget jiraRestBaseTarget;
	
	public Request(String jiraBasePath, String userName, String password, AuthType authType) {
		authManager = new AuthorizationManager(userName, password);
		this.authType = authType;
		
		this.client = ClientBuilder.newClient();
		this.jiraRestBaseTarget = client.target(jiraBasePath).path("rest");
	}
	
	public JsonObject getJiraAllIssues() {
		WebTarget jiraIssueSearch = jiraRestBaseTarget.path("api").path("2").path("search").queryParam("jql", "");
		
		Invocation.Builder invocationBuilder = jiraIssueSearch.queryParam("maxResults", "0").request(MediaType.APPLICATION_JSON);
		invocationBuilder = authType == AuthType.BASIC_AUTH ? authManager.addBasicAuthorizationHeader(invocationBuilder) : authManager.addCookieAuthHeader(invocationBuilder);
		JsonObject jsonObject = invocationBuilder.get(JsonObject.class);
		
		int totalResults = jsonObject.getInt("total");
		System.out.println("Total Results " + totalResults);
		
		invocationBuilder = jiraIssueSearch.queryParam("maxResults", totalResults).request(MediaType.APPLICATION_JSON);
		invocationBuilder = authType == AuthType.BASIC_AUTH ? authManager.addBasicAuthorizationHeader(invocationBuilder) : authManager.addCookieAuthHeader(invocationBuilder);
		
		return invocationBuilder.get(JsonObject.class);
	}

	public JsonObject getJiraSpecificIssueBaseAuth(String issue) {
		WebTarget jiraIssue = jiraRestBaseTarget.path("api").path("2").path("issue");
		WebTarget currentIssue = jiraIssue.path(issue);
		
		Invocation.Builder invocationBuilder = currentIssue.request(MediaType.APPLICATION_JSON);
		invocationBuilder = authType == AuthType.BASIC_AUTH ? authManager.addBasicAuthorizationHeader(invocationBuilder) : authManager.addCookieAuthHeader(invocationBuilder);
		
		return invocationBuilder.get(JsonObject.class);
	}
	

}
