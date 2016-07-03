package com.warptronic.itdm;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;
import javax.json.JsonValue.ValueType;

import com.warptronic.itdm.io.JsonFileManager;
import com.warptronic.itdm.jira.AuthType;
import com.warptronic.itdm.jira.Request;

/**
 * This is the main class that runs the Jira Issue Exporter
 * It is here just as an example. I advise you to write your own logic
 * @author Sebastian Luca
 *
 */
public class App {
	
	private App() {
		//class doesn't need initialization
	}
	
	/**
	 * 
	 * @param args arguments provided from command line in order: <code>username password auth_type</code>
	 */
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
		
		/**
		 * This is an example showing how to save all Jira issues to file
		 */
		 //JsonObject jsonObject = new Request(baseUrl, username, password, authenticationType).getJiraAllIssues();
		
		
		/**
		 * This is an example showing how to save only the needed data from Jira
		 */
		JsonObject jsonObject = new Request(baseUrl, username, password, authenticationType).getFilteredJiraIssues(App::jiraMinimalFilter);
		
		/**
		 * This is an example of how to write the created JSON object to file
		 */
		//TODO name the file based on date-time
		JsonFileManager.writeJsonToFile(jsonObject, "response");
	}

	/**
	 * <h3>Disclaimer: this is just an example showing how to create a filtering method to be refferenced</h3>
	 * This is a method referenced to {@link Request#getFilteredJiraIssues(java.util.function.Function)} as a parameter
	 * The purpose of this method is to filter the JSON received from Jira and save only the needed information 
	 * @param jsonObject a {@link JsonObject} parameter containing the requested information from Jira
	 * @return a {@link JsonObject} containing only the needed information
	 */
	private static JsonObject jiraMinimalFilter(JsonObject jsonObject) {
		JsonArrayBuilder jsonBuilder = Json.createArrayBuilder();
		
		for (JsonValue j : jsonObject.getJsonArray("issues")) {
			if (!ValueType.OBJECT.equals(j.getValueType())) {
				//TODO throw exception
				System.out.println("expected JsonValue was Object, but was " + j.getValueType());
				System.exit(-1);
			} 
			JsonObject json = (JsonObject) j;

			JsonObjectBuilder jobjBuilder = Json.createObjectBuilder();
			JsonObject parentKeyObj = json.getJsonObject("fields").getJsonObject("parent");
			String parentKey = parentKeyObj == null ? "" : parentKeyObj.getString("key");
			boolean isDone = json.getJsonObject("fields").containsKey("resolutiondate") && !ValueType.NULL.equals(json.getJsonObject("fields").get("resolutiondate").getValueType());
			String endDate = isDone ? json.getJsonObject("fields").getString("resolutiondate") : "";
			
			jobjBuilder.add("key", json.getString("key"))
						.add("parent-key", parentKey)
						.add("issuetype", json.getJsonObject("fields").getJsonObject("issuetype").getString("name"))
						.add("status", json.getJsonObject("fields").getJsonObject("status").getString("name"))
						.add("start-date", json.getJsonObject("fields").getString("created"))
						.add("end-date", endDate);

			jsonBuilder.add(jobjBuilder);
		}
		
		return Json.createObjectBuilder().add("total", jsonObject.getInt("total"))
										.add("issues", jsonBuilder).build();
	}
}




