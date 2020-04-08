package me.steffenjacobs.supersocial.service.exception;

/** @author Steffen Jacobs */
public class AnalyticsException extends RuntimeException{
	private static final long serialVersionUID = -5618451879932290478L;
	
	public AnalyticsException(String message, Throwable cause) {
		super(message, cause);
	}

}
