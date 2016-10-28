package com.warptronic.itdm;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;
import javax.json.JsonValue.ValueType;
import javax.ws.rs.ClientErrorException;

import com.warptronic.itdm.config.ProgramOptions;
import com.warptronic.itdm.config.CredentialsException;
import com.warptronic.itdm.io.JsonFileManager;
import com.warptronic.itdm.jira.Request;
import com.warptronic.itdm.utils.Writer;

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
		
		if (args.length < 1) {
			Writer.writeln("Too few parameters");
			Writer.writeUsageDescription();
			
			System.exit(-1);
		}
		
		ProgramOptions options = null;
		try {
			options = ProgramOptions.fromArgs(args);
		} catch (CredentialsException e) {
			Writer.writeln(e.getMessage());
			Writer.writeUsageDescription();
			
			System.exit(-1);
		}

		JsonObject jsonObject = null;
		try {
			jsonObject = new Request(options).getFilteredJiraIssues(App::jiraMinimalFilter);
		} catch (ClientErrorException e) {
			Writer.writeln("There was a problem logging in: " + e.getMessage());
			Writer.writeUsageDescription();
			System.exit(-1);
		}
		
		/**
		 * This is an example of how to write the created JSON object to file
		 */
		JsonFileManager.writeJsonToFile(jsonObject, "response");
	}

	/**
	 * <h3>Disclaimer: this is just an example showing how to create a filtering method to be refferenced</h3>
	 * This is a method referenced to {@link Request#getFilteredJiraIssues(java.util.function.Function)} as a parameter.<br>
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




