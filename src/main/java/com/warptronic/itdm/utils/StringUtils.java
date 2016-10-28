package com.warptronic.itdm.utils;

public class StringUtils {

	private StringUtils() {
		// empty constructor
	}
	
	public static boolean isNullOrEmpty(String s) {
		return (s == null) || (s.isEmpty());
	}
}
