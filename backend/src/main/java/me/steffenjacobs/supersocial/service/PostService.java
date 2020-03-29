package me.steffenjacobs.supersocial.service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import me.steffenjacobs.supersocial.domain.Platform;
import me.steffenjacobs.supersocial.domain.dto.MessagePublishingDTO;
import me.steffenjacobs.supersocial.domain.dto.PostDTO;
import me.steffenjacobs.supersocial.domain.dto.ScheduledPostDTO;
import me.steffenjacobs.supersocial.persistence.PostPersistenceManager;
import me.steffenjacobs.supersocial.persistence.ScheduledPostPersistenceManager;

/** @author Steffen Jacobs */
@Component
public class PostService {
	@Autowired
	private PostPersistenceManager postPersistenceManager;
	@Autowired
	private ScheduledPostPersistenceManager scheduledPostPersistenceManager;

	public Set<PostDTO> createPosts(MessagePublishingDTO messagePublishingDto) {
		Set<PostDTO> result = new HashSet<>();
		for (String platform : messagePublishingDto.getPlatforms()) {
			result.add(postPersistenceManager.storePost(messagePublishingDto.getMessage(), Platform.fromId(Integer.parseInt(platform))));
		}
		if (messagePublishingDto.getPlatforms().isEmpty()) {
			result.add(postPersistenceManager.storePost(messagePublishingDto.getMessage(), Platform.UNKNOWN));
		}
		return result;
	}

	public Set<PostDTO> getAllPosts() {
		return postPersistenceManager.getAllPosts().stream().map(this::addSchedulingInformationIfAvailable).collect(Collectors.toSet());
	}

	private PostDTO addSchedulingInformationIfAvailable(PostDTO post) {
		if (post.getPublished() != null || !StringUtils.isEmpty(post.getErrorMessage())) {
			return post;
		}
		Optional<ScheduledPostDTO> optionalPost = scheduledPostPersistenceManager.findByPostId(post.getId());
		// cannot be done with .orElse because of the different types of the
		// optional
		if (optionalPost.isPresent()) {
			return optionalPost.get();
		}
		return post;
	}

	public PostDTO findPostById(UUID id) {
		return postPersistenceManager.findPostById(id);
	}
}
