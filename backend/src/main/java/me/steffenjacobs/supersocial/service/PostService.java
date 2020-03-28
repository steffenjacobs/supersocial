package me.steffenjacobs.supersocial.service;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import me.steffenjacobs.supersocial.domain.Platform;
import me.steffenjacobs.supersocial.domain.dto.MessagePublishingDTO;
import me.steffenjacobs.supersocial.domain.dto.PostDTO;
import me.steffenjacobs.supersocial.persistence.PostPersistenceManager;

/** @author Steffen Jacobs */
@Component
public class PostService {
	@Autowired
	private PostPersistenceManager postPersistenceManager;

	public Set<PostDTO> createPosts(MessagePublishingDTO messagePublishingDto) {
		Set<PostDTO> result = new HashSet<>();
		for (String platform : messagePublishingDto.getPlatforms()) {
			result.add(postPersistenceManager.storePost(messagePublishingDto.getMessage(), Platform.fromId(Integer.parseInt(platform))));
		}
		return result;
	}

	public Set<PostDTO> getAllPosts() {
		return postPersistenceManager.getAllPosts();
	}

	public PostDTO findPostById(UUID id) {
		return postPersistenceManager.findPostById(id);
	}
}
