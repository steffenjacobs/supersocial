package me.steffenjacobs.supersocial.service;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import me.steffenjacobs.supersocial.domain.SocialMediaAccountRepository;
import me.steffenjacobs.supersocial.domain.dto.MessagePublishingDTO;
import me.steffenjacobs.supersocial.domain.dto.PostDTO;
import me.steffenjacobs.supersocial.domain.dto.ScheduledPostDTO;
import me.steffenjacobs.supersocial.domain.entity.Post;
import me.steffenjacobs.supersocial.domain.entity.ScheduledPost;
import me.steffenjacobs.supersocial.domain.entity.SecuredAction;
import me.steffenjacobs.supersocial.domain.entity.SocialMediaAccount;
import me.steffenjacobs.supersocial.persistence.PostPersistenceManager;
import me.steffenjacobs.supersocial.persistence.ScheduledPostPersistenceManager;
import me.steffenjacobs.supersocial.persistence.exception.PostNotFoundException;
import me.steffenjacobs.supersocial.persistence.exception.ScheduledPostNotFoundException;
import me.steffenjacobs.supersocial.security.SecurityService;
import me.steffenjacobs.supersocial.service.exception.SocialMediaAccountNotFoundException;

/**
 * Handles management of Posts. Relies on the {@link PostPublishingService} to
 * actually publish posts.
 * 
 * @author Steffen Jacobs
 */
@Component
public class PostService {
	@Autowired
	private PostPersistenceManager postPersistenceManager;
	@Autowired
	private ScheduledPostPersistenceManager scheduledPostPersistenceManager;

	@Autowired
	private SecurityService securityService;

	@Autowired
	private SocialMediaAccountRepository socialMediaAccountRepository;

	@Autowired
	private PostPublishingService postPublishingService;

	/**
	 * Create and publish a post from the newly created
	 * {@link MessagePublishingDTO}.
	 */
	public PostDTO createAndPublishPost(MessagePublishingDTO messagePublishingDTO) {
		return postPublishingService.publish(createPost(messagePublishingDTO));
	}

	/**
	 * Create a post from the newly created {@link MessagePublishingDTO} without
	 * publishing.
	 */
	public PostDTO createUnpublishedPost(MessagePublishingDTO messagePublishingDto) {
		return postPersistenceManager.toDto(createPost(messagePublishingDto));
	}

	/**
	 * Create a post from the newly created {@link MessagePublishingDTO}, do
	 * permission checks and store it to the database.
	 * 
	 * @throws SocialMediaAccountNotFoundException
	 *             if the associated {@link SocialMediaAccount} could not be
	 *             found or the current user is not permitted to see it.
	 * 
	 */
	private Post createPost(MessagePublishingDTO messagePublishingDto) {
		if (messagePublishingDto.getAccountId() == null) {
			throw new SocialMediaAccountNotFoundException(messagePublishingDto.getAccountId());
		}
		SocialMediaAccount account = socialMediaAccountRepository.findById(messagePublishingDto.getAccountId())
				.orElseThrow(() -> new SocialMediaAccountNotFoundException(messagePublishingDto.getAccountId()));
		securityService.checkIfCurrentUserIsPermitted(account, SecuredAction.READ);
		Post post = postPersistenceManager.storePost(messagePublishingDto.getMessage(), account);
		securityService.appendCurrentUserAcl(post);
		return post;
	}

	/**
	 * Retrieve all scheduled, non-scheduled, published and unpublished posts
	 * filtered by the current user's READ permission.
	 */
	public Set<PostDTO> getAllPosts() {
		return securityService.filterForCurrentUser(postPersistenceManager.getAllPosts(), SecuredAction.READ).map(postPersistenceManager::toDto)
				.map(this::addSchedulingInformationIfAvailable).collect(Collectors.toSet());
	}

	/**
	 * Enrich the given {@link PostDTO} with scheduling information if this post
	 * is scheduled.
	 */
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

	/**
	 * Find the post by ID.
	 * 
	 * @return a {@link PostDTO} with the given ID
	 * 
	 * @throws PostNotFoundException
	 *             if the post does not exist.
	 * 
	 * @throws me.steffenjacobs.supersocial.security.exception.AuthorizationException
	 *             if the current user is not allowed to view the post.
	 */
	public PostDTO findPostById(UUID id) {
		return postPersistenceManager.toDto(this.findOriginalPostById(id));
	}

	/**
	 * Find the post by ID but return a {@link Post} instead of a
	 * {@link PostDTO}.
	 * 
	 * @return a {@link Post} with the given ID
	 * 
	 * @throws PostNotFoundException
	 *             if the post does not exist.
	 * 
	 * @throws me.steffenjacobs.supersocial.security.exception.AuthorizationException
	 *             if the current user is not allowed to view the post.
	 */
	public Post findOriginalPostById(UUID id) {
		final Post post = postPersistenceManager.findPostById(id);
		securityService.checkIfCurrentUserIsPermitted(post, SecuredAction.READ);
		return post;
	}

	/**
	 * Delete the {@link Post} by the given id.
	 * 
	 * @throws PostNotFoundException
	 *             if the post does not exist.
	 * 
	 * @throws me.steffenjacobs.supersocial.security.exception.AuthorizationException
	 *             if the current user is not allowed to delete the post.
	 */
	public void deletePostById(UUID id) {
		try {
			// check the post itself
			Post p = postPersistenceManager.findPostById(id);
			securityService.checkIfCurrentUserIsPermitted(p, SecuredAction.DELETE);

			try {
				// is there a scheduled post this post is linked to? if so ->
				// delete
				ScheduledPost sp = scheduledPostPersistenceManager.findOriginalByPostId(id);
				securityService.checkIfCurrentUserIsPermitted(sp, SecuredAction.DELETE);
				scheduledPostPersistenceManager.deleteScheduledPost(sp.getId());
			} catch (ScheduledPostNotFoundException e) {
				// ignore
			}

			try {
				postPersistenceManager.deletePostById(id);
			} catch (EmptyResultDataAccessException e) {
				throw new PostNotFoundException(id);
			}
		} catch (PostNotFoundException e) {
			// this could also be the UUID of a scheduled post.
			ScheduledPost sp = scheduledPostPersistenceManager.findById(id).orElseThrow(() -> new PostNotFoundException(id));
			securityService.checkIfCurrentUserIsPermitted(sp, SecuredAction.DELETE);

			Post p = sp.getPost();
			securityService.checkIfCurrentUserIsPermitted(p, SecuredAction.DELETE);

			scheduledPostPersistenceManager.deleteScheduledPost(sp.getId());
			postPersistenceManager.deletePostById(p.getId());

		}
	}
}
