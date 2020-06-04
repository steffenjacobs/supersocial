package me.steffenjacobs.supersocial.service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JsonParserFactory;
import org.springframework.stereotype.Component;

import me.steffenjacobs.supersocial.domain.Platform;
import me.steffenjacobs.supersocial.domain.dto.PostDTO;
import me.steffenjacobs.supersocial.domain.entity.Post;
import me.steffenjacobs.supersocial.domain.entity.ScheduledPost;
import me.steffenjacobs.supersocial.domain.entity.SecuredAction;
import me.steffenjacobs.supersocial.persistence.PostPersistenceManager;
import me.steffenjacobs.supersocial.persistence.ScheduledPostPersistenceManager;
import me.steffenjacobs.supersocial.persistence.exception.PostNotFoundException;
import me.steffenjacobs.supersocial.persistence.exception.ScheduledPostNotFoundException;
import me.steffenjacobs.supersocial.security.SecurityService;
import me.steffenjacobs.supersocial.service.exception.PlatformNotFoundException;

/**
 * Handles publishing of newly created, previously created an scheduled posts.
 * 
 * @author Steffen Jacobs
 */
@Component
public class PostPublishingService {

	private static final Logger LOG = LoggerFactory.getLogger(PostPublishingService.class);

	@Autowired
	private FacebookService facebookService;

	@Autowired
	private TwitterService twitterService;

	@Autowired
	private PostPersistenceManager postPersistenceManager;

	@Autowired
	private PostService postService;

	@Autowired
	private SecurityService securityService;

	@Autowired
	private ScheduledPostPersistenceManager scheduledPostPersistenceManager;

	/**
	 * Publish a given {@link Post} to the associated social network immediately
	 * and return the resulting {@link PostDTO}. If the publishing failed, this
	 * will be saved and the resulting {@link PostDTO} will contain an
	 * error.<br/>
	 * There are <b>no security checks</b> to be performed here!
	 * 
	 * @throws PlatformNotFoundException
	 *             if the associated
	 *             {@link me.steffenjacobs.supersocial.domain.entity.SocialMediaAccount}
	 *             has no valid {@link Platform} specified.
	 * 
	 * 
	 */
	@SuppressWarnings("unchecked")
	public PostDTO publish(Post post) {
		post.setErrorMessage("");
		if (post.getSocialMediaAccountToPostWith().getPlatform() == Platform.FACEBOOK) {
			String result = facebookService.postMessage(post);
			// TODO: use JsonPath
			Map<String, Object> json = JsonParserFactory.getJsonParser().parseMap(result);
			if (json.containsKey("error")) {
				Map<String, ?> error = (Map<String, ?>) json.get("error");
				LOG.error("Received error from Facebook API: {}", json);
				return postPersistenceManager.updateWithErrorMessage(post.getId(), "" + error.get("message"));
			} else {
				LOG.info("Published post on Facebook: {}", post);
				return postPersistenceManager.updateWithExternalId(post.getId(), "" + json.get("id"));
			}
		} else if (post.getSocialMediaAccountToPostWith().getPlatform() == Platform.TWITTER) {
			String result = twitterService.tweet(post);
			Map<String, Object> json = JsonParserFactory.getJsonParser().parseMap(result);
			if (json.containsKey("errors")) {
				List<Map<String, ?>> errors = (List<Map<String, ?>>) json.get("errors");
				for (Map<String, ?> error : errors) {
					LOG.error("Received error from Twitter API: {}", json);
					postPersistenceManager.updateWithErrorMessage(post.getId(), "" + error.get("message"));
				}
				return postService.findPostById(post.getId());
			} else {
				LOG.info("Published post on Twitter: {}", post);
				return postPersistenceManager.updateWithExternalId(post.getId(), "" + json.get("id"));
			}
		} else {
			throw new PlatformNotFoundException(post.getSocialMediaAccountToPostWith().getPlatform());
		}
	}

	/**
	 * Publish a given {@link Post} to the associated social network immediately
	 * and return the resulting {@link PostDTO}. If the publishing failed, this
	 * will be saved and the resulting {@link PostDTO} will contain an error.
	 * Also unschedules the post if it was scheduled before.<br/>
	 * All procedures include security checks.
	 * 
	 * @throws PostNotFoundException
	 *             if the given {@code postId} does not have a post associated
	 *             to it or the current user is not permitted to see it.
	 * 
	 * @throws me.steffenjacobs.supersocial.security.exception.AuthorizationException
	 *             if the current user is not allowed to publish this
	 *             {@link Post} with the associated
	 *             {@link me.steffenjacobs.supersocial.domain.entity.SocialMediaAccount}.
	 */
	public PostDTO publishNow(UUID postId) {
		try {
			final Post p = postService.findOriginalPostById(postId);
			securityService.checkIfCurrentUserIsPermitted(p, SecuredAction.UPDATE);
			try {
				// unschedule post if one was scheduled
				ScheduledPost sp = scheduledPostPersistenceManager.findOriginalByPostId(postId);
				securityService.checkIfCurrentUserIsPermitted(sp, SecuredAction.DELETE);
				scheduledPostPersistenceManager.deleteScheduledPost(sp.getId());
			} catch (ScheduledPostNotFoundException e) {
				// ignore -> there was probably no scheduled post
			}
			return this.publish(p);
		} catch (PostNotFoundException e) {
			// maybe there is a scheduled post with this id
			ScheduledPost sp = scheduledPostPersistenceManager.findById(postId).orElseThrow(() -> new PostNotFoundException(postId));
			securityService.checkIfCurrentUserIsPermitted(sp, SecuredAction.DELETE);
			scheduledPostPersistenceManager.deleteScheduledPost(sp.getId());
			return this.publish(sp.getPost());
		}

	}
}
