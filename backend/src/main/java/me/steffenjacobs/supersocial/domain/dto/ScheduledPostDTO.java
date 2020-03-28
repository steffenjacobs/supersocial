package me.steffenjacobs.supersocial.domain.dto;

import java.util.Date;
import java.util.UUID;

import me.steffenjacobs.supersocial.domain.entity.ScheduledPost;

/** @author Steffen Jacobs */
public class ScheduledPostDTO extends PostDTO {
	private Date scheduled;

	public ScheduledPostDTO(UUID id, String text, int platformId, Date created, String creatorName, String errorMessage, String postUrl, Date published, Date scheduled) {
		super(id, text, platformId, created, creatorName, errorMessage, postUrl, published);
		this.scheduled = scheduled;
	}

	public Date getScheduled() {
		return scheduled;
	}

	public void setScheduled(Date scheduled) {
		this.scheduled = scheduled;
	}

	public static ScheduledPostDTO fromScheduledPost(ScheduledPost post) {
		return new ScheduledPostDTO(post.getId(), post.getPost().getText(), post.getPost().getPlatform().getId(), post.getPost().getCreated(), post.getPost().getCreator().getName(),
				null, null, null, post.getScheduledDate());
	}
}
