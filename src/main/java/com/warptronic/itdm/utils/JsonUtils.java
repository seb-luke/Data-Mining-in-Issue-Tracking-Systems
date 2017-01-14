package com.warptronic.itdm.utils;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;
import javax.json.JsonValue.ValueType;

import com.warptronic.itdm.jira.Request;

public class JsonUtils {
	
	private JsonUtils() {
		//private constructor
	}
	
	/**
	 * <h3>Disclaimer: this is just an example showing how to create a filtering method to be refferenced</h3>
	 * This is a method referenced to {@link Request#getFilteredJiraIssues(java.util.function.Function)} as a parameter.<br>
	 * The purpose of this method is to filter the JSON received from Jira and save only the needed information 
	 * @param jsonObject a {@link JsonObject} parameter containing the requested information from Jira
	 * @return a {@link JsonObject} containing only the needed information
	 */
	public static JsonObject jiraMinimalFilter(JsonObject jsonObject) {
		JsonArrayBuilder jsonBuilder = Json.createArrayBuilder();
		
		JsonArray jsonIssueArray = jsonObject.getJsonArray("issues");
		
		for (JsonValue j : jsonIssueArray) {
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
		
		return Json.createObjectBuilder().add("total", jsonIssueArray.size())
										.add("issues", jsonBuilder).build();
	}
	
	public static JsonObject combineIssues(JsonObject json1, JsonObject json2) {
		if (json1 == null || json2 == null) {
			throw new NullPointerException("Both Json Object need to be different than null to be able to combine them!"); 
		}
		
		int totalIssues = json1.getInt("total") + json2.getInt("total");
		JsonObjectBuilder jobjBuilder = Json.createObjectBuilder();
		
		JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
		json1.getJsonArray("issues").forEach(jsonArrayBuilder::add);
		json2.getJsonArray("issues").forEach(jsonArrayBuilder::add);
		
		jobjBuilder.add("total", totalIssues);
		jobjBuilder.add("issues", jsonArrayBuilder);
		
		return jobjBuilder.build();
	}

}