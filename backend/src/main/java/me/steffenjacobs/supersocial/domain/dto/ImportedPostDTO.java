package me.steffenjacobs.supersocial.domain.dto;

import java.time.Instant;

import me.steffenjacobs.supersocial.domain.entity.SocialMediaAccount;

/** @author Steffen Jacobs */
public class ImportedPostDTO {
	private String text;
	private Instant published;
	private String externalId;
	private SocialMediaAccount associatedAccount;

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Instant getPublished() {
		return published;
	}

	public void setPublished(Instant published) {
		this.published = published;
	}

	public String getExternalId() {
		return externalId;
	}

	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}

	public SocialMediaAccount getAssociatedAccount() {
		return associatedAccount;
	}

	public void setAssociatedAccount(SocialMediaAccount associatedAccount) {
		this.associatedAccount = associatedAccount;
	}

}
