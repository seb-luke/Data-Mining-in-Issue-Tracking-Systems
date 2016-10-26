package com.warptronic.itdm.jira;

import java.util.function.Function;

import javax.json.JsonObject;
import javax.ws.rs.client.*;
import javax.ws.rs.core.*;

/**
 * This class contains all the methods to communicate with Jira
 * @author Sebastian Luca
 *
 */
public class Request {
	private AuthorizationManager authManager;
	private AuthType authType;
	
	private Client client;
	private WebTarget jiraRestBaseTarget;
	
	/**
	 * 
	 * @param jiraBasePath - a {@link String} containing the URL path to the Jira Installation <br><em>(e.g. https://jira.domain.com:8089)</em>
	 * @param userName - a {@link String} containing the User Name of any user accepted by Jira
	 * @param password - a {@link String} containing the Password for the provided user
	 * @param authType - an {@link AuthType} showing what authentication is wanted in communicating with Jira
	 */
	public Request(String jiraBasePath, String userName, String password, AuthType authType) {
		authManager = new AuthorizationManager(userName, password);
		this.authType = authType;
		
		this.client = ClientBuilder.newClient();
		this.jiraRestBaseTarget = client.target(jiraBasePath).path("rest");
	}
	
	//TODO create a new method to take only 100 items per call if there are too many; check method time for a huge # of issues
	//TODO create a new method use the above together with a filtering parameter (Method Reference)
	
	//TODO akka (streams) / Actor (concurency)
	
	/**
	 * The method will return <strong>all</strong> Jira issues. Beware when the issues are more than 1000!
	 * @return a {@link JsonObject} containing all the issues requested
	 */
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

	/**
	 * This method only returns data regarding one issue from Jira, for the Issue-Key provided
	 * @param issue as {@link String} containing the Issue Key (Jira Style) <em>ISSUE-210</em>
	 * @return a {@link JsonObject} containing all the data for the requested issue
	 */
	public JsonObject getJiraSpecificIssueBaseAuth(String issue) {
		WebTarget jiraIssue = jiraRestBaseTarget.path("api").path("2").path("issue");
		WebTarget currentIssue = jiraIssue.path(issue);
		
		Invocation.Builder invocationBuilder = currentIssue.request(MediaType.APPLICATION_JSON);
		invocationBuilder = authType == AuthType.BASIC_AUTH ? authManager.addBasicAuthorizationHeader(invocationBuilder) : authManager.addCookieAuthHeader(invocationBuilder);
		
		return invocationBuilder.get(JsonObject.class);
	}

	/**
	 * This method will return only the needed information in the issue for all of the issues from Jira
	 * @param filteringFunction - a Method Reference ({@link Function}) whose role is to take only the needed information from the provided Issues
	 * @return a {@link JsonObject} containing the filtered data from all of Jira's Issues
	 */
	public JsonObject getFilteredJiraIssues(Function<JsonObject, JsonObject> filteringFunction) {
		JsonObject json = this.getJiraAllIssues();
		
		return filteringFunction.apply(json);
	}
	

}
