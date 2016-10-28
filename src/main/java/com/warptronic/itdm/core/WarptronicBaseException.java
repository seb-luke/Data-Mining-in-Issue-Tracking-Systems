package com.warptronic.itdm.core;

/**
 * @author Sebastian Luca
 *
 */
public abstract class WarptronicBaseException extends Exception {

	private static final long serialVersionUID = 1L;

	public WarptronicBaseException() {
		// empty constructor
	}

	/**
	 * @param message
	 */
	public WarptronicBaseException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public WarptronicBaseException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public WarptronicBaseException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public WarptronicBaseException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
