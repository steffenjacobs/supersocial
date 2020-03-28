package me.steffenjacobs.supersocial.persistence;

import java.util.Date;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import me.steffenjacobs.supersocial.domain.Platform;
import me.steffenjacobs.supersocial.domain.PostRepository;
import me.steffenjacobs.supersocial.domain.dto.PostDTO;
import me.steffenjacobs.supersocial.domain.entity.Post;
import me.steffenjacobs.supersocial.persistence.exception.PostNotFoundException;
import me.steffenjacobs.supersocial.security.SecurityService;

/** @author Steffen Jacobs */
@Component
public class PostPersistenceManager {

	@Autowired
	private PostRepository postRepository;

	@Autowired
	private SecurityService securityService;

	public PostDTO storePost(String postText, Platform platform) {
		Post p = new Post();
		p.setPlatform(platform);
		p.setText(postText);
		p.setCreator(securityService.getCurrentUser());
		return toDto(postRepository.save(p));
	}

	public PostDTO updateWithExternalId(UUID postId, String externalId) {
		Post p = postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException(postId));
		p.setExternalId(externalId);
		p.setPublished(new Date());
		return toDto(postRepository.save(p));
	}

	public PostDTO updateWithErrorMessage(UUID postId, String errorMessage) {
		Post p = postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException(postId));
		p.setErrorMessage(errorMessage);
		return toDto(postRepository.save(p));
	}

	public Set<PostDTO> getAllPosts() {
		return StreamSupport.stream(postRepository.findAll().spliterator(), true).map(this::toDto).collect(Collectors.toSet());
	}

	public PostDTO findPostById(UUID id) {
		return toDto(postRepository.findById(id).orElseThrow(() -> new PostNotFoundException(id)));
	}

	public PostDTO toDto(Post post) {
		if (StringUtils.isEmpty(post.getExternalId())) {
			return PostDTO.fromPost(post, "");
		}
		if (post.getPlatform() == Platform.FACEBOOK) {
			String[] ids = post.getExternalId().split("_");
			String url = String.format("https://www.facebook.com/permalink.php?story_fbid=%s&id=%s", ids[1], ids[0]);
			return PostDTO.fromPost(post, url);
		} else if (post.getPlatform() == Platform.TWITTER) {
			String url = String.format("https://twitter.com/%s/status/%s", "Steffen_Jacobs_", post.getExternalId());
			return PostDTO.fromPost(post, url);
		}
		return PostDTO.fromPost(post, "TBD");
	}

}
