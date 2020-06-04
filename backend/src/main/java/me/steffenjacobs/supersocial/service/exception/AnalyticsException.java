package me.steffenjacobs.supersocial.service.exception;

/**
 * Should be fired if e.g.
 * {@link me.steffenjacobs.supersocial.domain.entity.Post} or
 * {@link me.steffenjacobs.supersocial.domain.entity.SocialMediaAccount}
 * statistics could not be retrieved from the elasticsearch.
 * 
 * @author Steffen Jacobs
 */
public class AnalyticsException extends RuntimeException {
	private static final long serialVersionUID = -5618451879932290478L;

	public AnalyticsException(String message, Throwable cause) {
		super(message, cause);
	}

}
