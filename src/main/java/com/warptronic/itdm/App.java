package com.warptronic.itdm;

import javax.json.JsonObject;
import javax.ws.rs.ClientErrorException;

import com.warptronic.itdm.config.ProgramOptions;
import com.warptronic.itdm.config.CredentialsException;
import com.warptronic.itdm.io.JsonFileManager;
import com.warptronic.itdm.jira.Request;
import com.warptronic.itdm.utils.JsonUtils;
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
			jsonObject = new Request(options).getFilteredJiraIssues(JsonUtils::jiraMinimalFilter);
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
}