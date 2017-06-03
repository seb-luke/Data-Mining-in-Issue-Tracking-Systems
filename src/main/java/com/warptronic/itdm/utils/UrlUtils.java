package com.warptronic.itdm.utils;

import org.apache.commons.validator.routines.UrlValidator;
import org.glassfish.jersey.Beta;

public class UrlUtils {
	
	private UrlUtils() {}

	@Beta
	public static boolean isUrl(String url) {
		
		return new UrlValidator(new String[] {"http", "https"}).isValid(url);
	}

}
