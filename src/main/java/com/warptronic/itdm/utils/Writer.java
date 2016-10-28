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
		String description = "Usage:\n";
		description += "\tjava App.java http://jira.domain.com [-user username -pwd password] "
				+ "[-authtype d] [-projectname name]\n";
		description += "\twhere 'd' is a number from: 0 = No Authentication, "
				+ "1 = Basic Jira Authentication, 2 = Cookie Jira Authentication";
		
		Writer.writeln(description);
	}

}
