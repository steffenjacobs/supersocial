package me.steffenjacobs.supersocial.domain.dto;

import java.util.Date;
import java.util.UUID;

import me.steffenjacobs.supersocial.domain.entity.ScheduledPost;

/** @author Steffen Jacobs */
public class LinkedScheduledPostDTO {
	private Date scheduled;

	private UUID postId;

	private UUID id;

	private LinkedScheduledPostDTO(UUID postId, Date scheduledDate, UUID id) {
		this.postId = postId;
		scheduled = scheduledDate;
		this.id = id;
	}

	public static LinkedScheduledPostDTO fromScheduledPost(ScheduledPost post) {
		return new LinkedScheduledPostDTO(post.getPost().getId(), post.getScheduledDate(), post.getId());
	}

	public Date getScheduled() {
		return scheduled;
	}

	public UUID getPostId() {
		return postId;
	}

	public UUID getId() {
		return id;
	}
}
