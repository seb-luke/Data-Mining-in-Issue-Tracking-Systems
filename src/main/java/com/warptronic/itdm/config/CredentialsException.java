package com.warptronic.itdm.config;

import com.warptronic.itdm.core.WarptronicBaseException;

/**
 * @author Sebastian Luca
 *
 */
public class CredentialsException extends WarptronicBaseException {

	private static final long serialVersionUID = 6736922721917974549L;

	/**
	 * @param message
	 */
	public CredentialsException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public CredentialsException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public CredentialsException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public CredentialsException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
