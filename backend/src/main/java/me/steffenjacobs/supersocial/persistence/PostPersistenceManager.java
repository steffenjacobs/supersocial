package me.steffenjacobs.supersocial.persistence;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import me.steffenjacobs.supersocial.domain.Platform;
import me.steffenjacobs.supersocial.domain.PostRepository;
import me.steffenjacobs.supersocial.domain.dto.PostDTO;
import me.steffenjacobs.supersocial.domain.entity.Post;
import me.steffenjacobs.supersocial.security.SecurityService;
import me.steffenjacobs.supersocial.service.exception.PostNotFoundException;

/** @author Steffen Jacobs */
@Component
public class PostPersistenceManager {

	@Autowired
	private PostRepository postRepository;

	@Autowired
	private SecurityService securityService;

	public Post storePost(String postText, Platform platform) {
		Post p = new Post();
		p.setPlatform(platform);
		p.setText(postText);
		p.setCreator(securityService.getCurrentUser());
		return postRepository.save(p);
	}

	public Post updateWithExternalId(long postId, String externalId) {
		Post p = postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException(postId));
		p.setExternalId(externalId);
		return postRepository.save(p);
	}

	public Set<PostDTO> getAllPosts() {
		return StreamSupport.stream(postRepository.findAll().spliterator(), true).map(PostDTO::fromPost).collect(Collectors.toSet());
	}

	public PostDTO findPostById(long id) {
		return PostDTO.fromPost(postRepository.findById(id).orElseThrow(() -> new PostNotFoundException(id)));
	}

}
