package me.steffenjacobs.supersocial.domain.dto;

import java.util.Date;
import java.util.UUID;

import me.steffenjacobs.supersocial.domain.entity.Post;

/** @author Steffen Jacobs */
public class PostDTO implements WithErrorDTO {

	private UUID id;

	private String text;

	private int platformId;

	private Date created;

	private Date published;

	private String creatorName;

	private String errorMessage;

	private String postUrl;

	private String error;

	private UUID accountId;

	public PostDTO(String error) {
		this.error = error;
	}

	protected PostDTO(UUID id, String text, int platformId, Date created, String creatorName, String errorMessage, String postUrl, Date published, UUID accountId) {
		super();
		this.id = id;
		this.text = text;
		this.platformId = platformId;
		this.created = created;
		this.creatorName = creatorName;
		this.errorMessage = errorMessage;
		this.postUrl = postUrl;
		this.published = published;
		this.accountId = accountId;
	}

	public UUID getId() {
		return id;
	}

	public String getText() {
		return text;
	}

	public int getPlatformId() {
		return platformId;
	}

	public Date getCreated() {
		return created;
	}

	public String getCreatorName() {
		return creatorName;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public String getPostUrl() {
		return postUrl;
	}

	public Date getPublished() {
		return published;
	}

	public void setPublished(Date published) {
		this.published = published;
	}

	public String getError() {
		return error;
	}

	public UUID getAccountId() {
		return accountId;
	}

	public static PostDTO fromPost(Post post, String url) {
		return new PostDTO(post.getId(), post.getText(), post.getSocialMediaAccountToPostWith().getPlatform().getId(), post.getCreated(), post.getCreator().getName(),
				post.getErrorMessage(), url, post.getPublished(), post.getSocialMediaAccountToPostWith().getId());
	}

	public PostDTO(UUID id, String text, int platformId, Date created, Date published, String creatorName, String errorMessage, String postUrl, String error, UUID accountId) {
		super();
		this.id = id;
		this.text = text;
		this.platformId = platformId;
		this.created = created;
		this.published = published;
		this.creatorName = creatorName;
		this.errorMessage = errorMessage;
		this.postUrl = postUrl;
		this.error = error;
		this.accountId = accountId;
	}

}
