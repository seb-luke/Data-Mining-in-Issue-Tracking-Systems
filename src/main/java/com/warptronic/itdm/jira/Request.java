package com.warptronic.itdm.jira;

import java.util.function.Function;

import javax.json.JsonObject;
import javax.ws.rs.client.*;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.*;

import com.warptronic.itdm.config.ProgramOptions;
import com.warptronic.itdm.utils.JsonUtils;
import com.warptronic.itdm.utils.StringUtils;
import com.warptronic.itdm.utils.Writer;

/**
 * This class contains all the methods to communicate with Jira
 * @author Sebastian Luca
 *
 */
public class Request {
	private static final String Q_PARAM_MAX_RESULTS = "maxResults";
	private static final String Q_PARAM_START_AT = "startAt";
	private static final int JIRA_MAX_ISSUES = 1000;
	
	private AuthorizationManager authManager;
	private AuthType authType;
	
	private WebTarget jiraRestBaseTarget;
	
	//this is kept here for further options that were not saved in the constructor i.e. project name
	private ProgramOptions programOptions = null;
	
	/**
	 * 
	 * @param jiraBasePath - a {@link String} containing the URL path to the Jira Installation <br><em>(e.g. https://jira.domain.com:8089)</em>
	 * @param authType - an {@link AuthType} showing what authentication is wanted in communicating with Jira
	 * @param userName - a {@link String} containing the User Name of any user accepted by Jira
	 * @param password - a {@link String} containing the Password for the provided user
	 */
	public Request(String jiraBasePath, AuthType authType, String userName, String password) {
		authManager = new AuthorizationManager(userName, password);
		
		this.jiraRestBaseTarget = ClientBuilder.newClient().target(jiraBasePath).path("rest");
		this.authType = authType;
	}
	
	public Request(ProgramOptions options) {
		this(options.getBaseUrl(), options.getAuthenticationType(), options.getUsername(), options.getPassword());
		this.programOptions = options;
	}
	
	/**
	 * This method will return only the needed information in the issue for all of the issues from Jira
	 * @param filteringFunction - a Method Reference ({@link Function}) whose role is to take only the needed information from the provided Issues
	 * @return a {@link JsonObject} containing the filtered data from all of Jira's Issues
	 */
	public JsonObject getFilteredJiraIssues(Function<JsonObject, JsonObject> filteringFunction) {
		
		return this.getJiraAllIssues(filteringFunction);
	}
	
	/**
	 * The method will return <strong>all</strong> Jira issues. For Total Issues > JIRA_MAX_ISSUES it will do subsequent API calls.
	 * @return a {@link JsonObject} containing all the issues requested
	 */
	private JsonObject getJiraAllIssues(Function<JsonObject, JsonObject> filteringFunction) {
		JsonObject finalIssueList = null;
		
		WebTarget jiraIssueSearch = jiraRestBaseTarget.path("api").path("2").path("search");
		jiraIssueSearch = addJql(jiraIssueSearch);
		int totalResults = getTotalIssues(jiraIssueSearch);
		Writer.writeln("Total issues: " + totalResults);
		
		if (totalResults > JIRA_MAX_ISSUES) {
			finalIssueList = getPaginatedJiraIssues(jiraIssueSearch, totalResults, filteringFunction);
		} else {
			Builder invocationBuilder = jiraIssueSearch.queryParam(Q_PARAM_MAX_RESULTS, totalResults).request(MediaType.APPLICATION_JSON);
			invocationBuilder = addAuthIfNeeded(invocationBuilder);
			finalIssueList = filteringFunction.apply(invocationBuilder.get(JsonObject.class));
		}
		
		return finalIssueList;
	}
	
	private JsonObject getPaginatedJiraIssues(WebTarget jiraIssueSearch, int totalResults, Function<JsonObject, JsonObject> filteringFunction) {
		int startAt = 0;
		int total = -1;
		JsonObject completeIssueList = null;
		
		while (total != 0) {
			JsonObject nextIssueList = getRequestWithPagination(jiraIssueSearch, startAt, filteringFunction);
			total = nextIssueList.getInt("total");
			
			if (completeIssueList == null) {
				completeIssueList = nextIssueList;
			} else {
				completeIssueList = JsonUtils.combineIssues(completeIssueList, nextIssueList);
			}
			
			startAt += JIRA_MAX_ISSUES;
			if (total > 0) {
				//TODO Log #read lines
				Writer.writeln("Read " + total + " issues");
			}
		}
		
		return completeIssueList;
	}
	
	private JsonObject getRequestWithPagination(WebTarget jiraIssueSearch, int startAt, Function<JsonObject, JsonObject> filteringFunction) {
		Builder invocationBuilder = jiraIssueSearch
										.queryParam(Q_PARAM_START_AT, startAt)
										.queryParam(Q_PARAM_MAX_RESULTS, JIRA_MAX_ISSUES)
										.request(MediaType.APPLICATION_JSON);
		invocationBuilder = addAuthIfNeeded(invocationBuilder);
		
		return filteringFunction.apply(invocationBuilder.get(JsonObject.class));
	}
	
	private WebTarget addJql(WebTarget jiraIssueSearch) {
		String jqlQuery = "";
		
		if (this.programOptions != null && !StringUtils.isNullOrEmpty(programOptions.getProjectName())) {
			jqlQuery += "project=" + programOptions.getProjectName();
		}
		// here we can have a forEach on a map that holds custom JQL parameters and construct the jqlQuery
		
		return jiraIssueSearch.queryParam("jql", jqlQuery);
	}
	
	private int getTotalIssues(WebTarget currentIssueSearch) {
		WebTarget totalIssuesSearch = currentIssueSearch.queryParam(Q_PARAM_MAX_RESULTS, 0);
		Builder invocationBuilder = totalIssuesSearch.request(MediaType.APPLICATION_JSON);
		invocationBuilder = addAuthIfNeeded(invocationBuilder);
		
		JsonObject jsonResponse = invocationBuilder.get(JsonObject.class);
		return jsonResponse.getInt("total");
	}

	private Builder addAuthIfNeeded(Builder invocationBuilder) {
		Builder builder;
		
		switch (this.authType) {
			case BASIC_AUTH:
				builder = this.authManager.addBasicAuthorizationHeader(invocationBuilder);
				break;
			case COOKIE_BASED_AUTH:
				builder = this.authManager.addCookieAuthHeader(invocationBuilder);
				break;
			default:
				builder = invocationBuilder;
				break;
		}
		
		return builder;
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
}
