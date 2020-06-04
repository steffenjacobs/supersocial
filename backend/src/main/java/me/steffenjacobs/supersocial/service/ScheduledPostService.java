package me.steffenjacobs.supersocial.service;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import me.steffenjacobs.supersocial.domain.PostRepository;
import me.steffenjacobs.supersocial.domain.dto.LinkedScheduledPostDTO;
import me.steffenjacobs.supersocial.domain.dto.ScheduledPostDTO;
import me.steffenjacobs.supersocial.domain.entity.Post;
import me.steffenjacobs.supersocial.domain.entity.ScheduledPost;
import me.steffenjacobs.supersocial.domain.entity.SecuredAction;
import me.steffenjacobs.supersocial.persistence.ScheduledPostPersistenceManager;
import me.steffenjacobs.supersocial.persistence.exception.PostAlreadyScheduledException;
import me.steffenjacobs.supersocial.persistence.exception.PostNotFoundException;
import me.steffenjacobs.supersocial.persistence.exception.ScheduledPostNotFoundException;
import me.steffenjacobs.supersocial.security.SecurityService;
import me.steffenjacobs.supersocial.util.Pair;

/**
 * Handles management of {@link ScheduledPost scheduled posts}. Relies on the
 * {@link PostPublishingService} to actually publish posts and
 * {@link PostService} to manage the associated {@link Post posts} themselves.
 * 
 * @author Steffen Jacobs
 */
@Component
public class ScheduledPostService {

	@Autowired
	private ScheduledPostPersistenceManager scheduledPostPersistenceManager;

	@Autowired
	private PostRepository postRepository;

	@Autowired
	private SecurityService securityService;

	/**
	 * Retrieve all scheduled posts filtered by READ permission for the current
	 * user.
	 * 
	 * @return a set of {@link ScheduledPostDTO} objects.
	 */
	public Set<ScheduledPostDTO> getAllScheduledPosts() {
		return securityService.filterForCurrentUser(scheduledPostPersistenceManager.getAllScheduledPosts(), SecuredAction.READ).map(ScheduledPostDTO::fromScheduledPost)
				.collect(Collectors.toSet());
	}

	/**
	 * Schedule or update the already existing scheduling information for an
	 * unpublishd post.
	 * 
	 * @return {@code <post,true>} if the scheduling information was created and
	 *         {@code <post,false>} if it was updated.
	 */
	public Pair<ScheduledPostDTO, Boolean> scheduleOrUpdateScheduledPost(LinkedScheduledPostDTO post) {
		ScheduledPost sPost = post.getId() == null ? new ScheduledPost() : scheduledPostPersistenceManager.findById(post.getId()).orElseGet(ScheduledPost::new);
		if (sPost.getId() != null) {
			securityService.checkIfCurrentUserIsPermitted(sPost, SecuredAction.UPDATE);
		} else {
			securityService.appendCurrentUserAcl(sPost);
		}

		if (sPost.getId() == null && scheduledPostPersistenceManager.findByPostId(post.getPostId()).isPresent()) {
			throw new PostAlreadyScheduledException(post.getPostId());
		}

		final Post actualPost = postRepository.findById(post.getPostId()).orElseThrow(() -> new PostNotFoundException(post.getPostId()));
		securityService.checkIfCurrentUserIsPermitted(actualPost, SecuredAction.READ);

		Pair<ScheduledPost, Boolean> result = scheduledPostPersistenceManager.updateScheduledPost(sPost, actualPost, post.getScheduled(), securityService.getCurrentUser());
		return new Pair<>(ScheduledPostDTO.fromScheduledPost(result.getA()), result.getB());
	}

	/**
	 * Unschedule a post.
	 * 
	 * @throws me.steffenjacobs.supersocial.security.exception.AuthorizationException
	 *             if the current user is not allowed to unschedule the post.
	 * @throws ScheduledPostNotFoundException
	 *             if there is no post scheduled with this identifier.
	 */
	public void deleteScheduledPost(UUID id) {
		ScheduledPost post = scheduledPostPersistenceManager.findById(id).orElseThrow(() -> new ScheduledPostNotFoundException(id));
		securityService.checkIfCurrentUserIsPermitted(post, SecuredAction.DELETE);
		scheduledPostPersistenceManager.deleteScheduledPost(id);
	}

}
