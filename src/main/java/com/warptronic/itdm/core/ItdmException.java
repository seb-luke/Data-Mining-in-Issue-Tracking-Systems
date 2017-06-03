package com.warptronic.itdm.core;

/**
 * @author Sebastian Luca
 *
 */
public class ItdmException extends WarptronicBaseException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3048416805703244021L;

	public ItdmException() {
		// empty constructor
	}

	/**
	 * @param message
	 */
	public ItdmException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public ItdmException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public ItdmException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public ItdmException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
