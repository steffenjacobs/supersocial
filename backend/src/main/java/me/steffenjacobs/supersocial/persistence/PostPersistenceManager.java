package me.steffenjacobs.supersocial.persistence;

import java.util.Date;
import java.util.UUID;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import me.steffenjacobs.supersocial.domain.Platform;
import me.steffenjacobs.supersocial.domain.PostRepository;
import me.steffenjacobs.supersocial.domain.dto.PostDTO;
import me.steffenjacobs.supersocial.domain.entity.Post;
import me.steffenjacobs.supersocial.domain.entity.SocialMediaAccount;
import me.steffenjacobs.supersocial.persistence.exception.PostNotFoundException;
import me.steffenjacobs.supersocial.security.SecurityService;

/**
 * Handles persistence for CRUD operations on posts. Permission checks are
 * handled in {@link me.steffenjacobs.supersocial.service.PostService}.
 * 
 * @author Steffen Jacobs
 */
@Component
public class PostPersistenceManager {

	@Autowired
	private PostRepository postRepository;

	@Autowired
	private SecurityService securityService;

	/**
	 * Create a new post for a given social media account.
	 * 
	 * @return the newly created {@link Post}.
	 */
	public Post storePost(String postText, SocialMediaAccount account) {
		Post p = new Post();
		p.setText(postText);
		p.setCreator(securityService.getCurrentUser());
		p.setSocialMediaAccountToPostWith(account);
		securityService.appendCurrentUserAcl(p);
		return postRepository.save(p);
	}

	/**
	 * Update a given post with it's external identifier. This is usually done
	 * right after posting.
	 * 
	 * @return the updated post as a {@link PostDTO}.
	 * @throws PostNotFoundException
	 *             if the post could not be found.
	 */
	public PostDTO updateWithExternalId(UUID postId, String externalId) {
		Post p = postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException(postId));
		p.setExternalId(externalId);
		p.setPublished(new Date());
		return toDto(postRepository.save(p));
	}

	/**
	 * Update a given post with an error message. This is usually done right
	 * after the attempt to post it on the asscoiated social media platform has
	 * failed.
	 * 
	 * @return the updated post as a {@link PostDTO}.
	 * @throws PostNotFoundException
	 *             if the post could not be found.
	 */
	public PostDTO updateWithErrorMessage(UUID postId, String errorMessage) {
		Post p = postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException(postId));
		p.setErrorMessage(errorMessage);
		return toDto(postRepository.save(p));
	}

	/** @return all {@link Post}s. */
	public Stream<Post> getAllPosts() {
		return StreamSupport.stream(postRepository.findAll().spliterator(), false);
	}

	/**
	 * Find a single post by its unique identifier.
	 * 
	 * @return the {@link Post}
	 * @throws PostNotFoundException
	 *             if the post could not be found.
	 */
	public Post findPostById(UUID id) {
		return postRepository.findById(id).orElseThrow(() -> new PostNotFoundException(id));
	}

	/**
	 * Converts a {@link Post} entity to a {@link PostDTO}. If an external
	 * identifier is present, this also generates the associated direct link to
	 * the published post on its respective platform.
	 */
	public PostDTO toDto(Post post) {
		if (StringUtils.isEmpty(post.getExternalId())) {
			return PostDTO.fromPost(post, "");
		}
		if (post.getSocialMediaAccountToPostWith().getPlatform() == Platform.FACEBOOK) {
			String[] ids = post.getExternalId().split("_");
			String url = String.format("https://www.facebook.com/permalink.php?story_fbid=%s&id=%s", ids[1], ids[0]);
			return PostDTO.fromPost(post, url);
		} else if (post.getSocialMediaAccountToPostWith().getPlatform() == Platform.TWITTER) {
			String url = String.format("https://twitter.com/%s/status/%s", "Steffen_Jacobs_", post.getExternalId());
			return PostDTO.fromPost(post, url);
		}
		return PostDTO.fromPost(post, "TBD");
	}

	/** Deletes a post by its unique identifier. */
	public void deletePostById(UUID id) {
		postRepository.deleteById(id);
	}

}
