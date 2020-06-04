package me.steffenjacobs.supersocial.domain.dto;

import java.util.UUID;

/** @author Steffen Jacobs */
public class MessagePublishingDTO {

	private String message;

	private UUID accountId;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public UUID getAccountId() {
		return accountId;
	}

	public void setAccountId(UUID accountId) {
		this.accountId = accountId;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("MessagePublishingDTO [message=").append(message).append(", accountId=").append(accountId).append("]");
		return builder.toString();
	}

}
