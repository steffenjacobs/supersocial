package me.steffenjacobs.supersocial.domain.dto;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import me.steffenjacobs.supersocial.domain.Platform;
import me.steffenjacobs.supersocial.domain.entity.ScheduledPost;

/** @author Steffen Jacobs */
public class ScheduledPostDTO extends PostDTO {
	private Date scheduled;

	public ScheduledPostDTO(String error) {
		super(error);
	}

	public ScheduledPostDTO(UUID id, String text, int platformId, Date created, String creatorName, String errorMessage, String postUrl, Date published, Date scheduled,
			UUID accountId) {
		super(id, text, platformId, created, creatorName, errorMessage, postUrl, published, accountId);
		this.scheduled = scheduled;
	}

	public Date getScheduled() {
		return scheduled;
	}

	public void setScheduled(Date scheduled) {
		this.scheduled = scheduled;
	}

	public static ScheduledPostDTO fromScheduledPost(ScheduledPost post) {
		return new ScheduledPostDTO(post.getId(), post.getPost().getText(),
				(post.getPost().getSocialMediaAccountToPostWith() != null ? post.getPost().getSocialMediaAccountToPostWith().getPlatform().getId() : Platform.UNKNOWN.getId()),
				post.getPost().getCreated(), post.getPost().getCreator().getName(), null, null, null, post.getScheduledDate(),
				(post.getPost().getSocialMediaAccountToPostWith() != null ? post.getPost().getSocialMediaAccountToPostWith().getId() : null));
	}

	public static Optional<ScheduledPostDTO> fromScheduledPost(Optional<ScheduledPost> optinalPost) {
		return optinalPost.map(post -> new ScheduledPostDTO(post.getId(), post.getPost().getText(),
				(post.getPost().getSocialMediaAccountToPostWith() != null ? post.getPost().getSocialMediaAccountToPostWith().getPlatform().getId() : Platform.UNKNOWN.getId()),
				post.getPost().getCreated(), post.getPost().getCreator().getName(), null, null, null, post.getScheduledDate(),
				(post.getPost().getSocialMediaAccountToPostWith() != null ? post.getPost().getSocialMediaAccountToPostWith().getId() : null)));
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ScheduledPostDTO [scheduled=").append(scheduled).append(super.toString()).append("]");
		return builder.toString();
	}

}
