package me.steffenjacobs.supersocial.domain.dto;

import java.util.Date;
import java.util.UUID;

import me.steffenjacobs.supersocial.domain.entity.Post;

/** @author Steffen Jacobs */
public class PostDTO {

	private UUID id;

	private String text;

	private int platformId;

	private Date created;

	private String creatorName;

	private PostDTO(UUID id, String text, int platformId, Date created, String creatorName) {
		super();
		this.id = id;
		this.text = text;
		this.platformId = platformId;
		this.created = created;
		this.creatorName = creatorName;
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
	
	public static PostDTO fromPost(Post post) {
		return new PostDTO(post.getId(), post.getText(), post.getPlatform().getId(), post.getCreated(), post.getCreator().getName());
	}

}
