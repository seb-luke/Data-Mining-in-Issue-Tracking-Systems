package com.warptronic.itdm.utils;

/**
 * @author Sebastian Luca
 *
 */
public class Writer {

	private Writer() {
		// class doesn't need initialization
	}
	
	public static void writeln(String message) {
		System.out.println(message);
	}
	
	public static void writeUsageDescription() {
		StringBuilder builder = new StringBuilder("Usage:\n");
		builder.append("\tjava App.java http://jira.domain.com");
		builder.append(" [-user username -pwd password]");
		builder.append(" [-cookie cookieValue]");
		builder.append(" [-authtype d]");
		builder.append(" [-projectname name]\n");
		builder.append("\twhere 'd' is a number from:");
		builder.append(" 0 = No Authentication,");
		builder.append(" 1 = Basic Jira Authentication,");
		builder.append(" 2 = Cookie Jira Authentication");
		builder.append("\n\tand cookieValue is the value of the 'JSESSIONID' cookie from the browser");
		
		Writer.writeln(builder.toString());
	}

}
