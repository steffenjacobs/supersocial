package me.steffenjacobs.supersocial.service;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import me.steffenjacobs.supersocial.domain.SocialMediaAccountRepository;
import me.steffenjacobs.supersocial.domain.dto.MessagePublishingDTO;
import me.steffenjacobs.supersocial.domain.dto.PostDTO;
import me.steffenjacobs.supersocial.domain.dto.ScheduledPostDTO;
import me.steffenjacobs.supersocial.domain.entity.Post;
import me.steffenjacobs.supersocial.domain.entity.SecuredAction;
import me.steffenjacobs.supersocial.domain.entity.SocialMediaAccount;
import me.steffenjacobs.supersocial.persistence.PostPersistenceManager;
import me.steffenjacobs.supersocial.persistence.ScheduledPostPersistenceManager;
import me.steffenjacobs.supersocial.security.SecurityService;
import me.steffenjacobs.supersocial.security.exception.AuthorizationException;
import me.steffenjacobs.supersocial.service.exception.SocialMediaAccountNotFoundException;

/** @author Steffen Jacobs */
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
	
	public PostDTO createAndPublishPost(MessagePublishingDTO messagePublishingDTO) {
		return postPublishingService.publish(createPost(messagePublishingDTO));
	}

	public PostDTO createUnpublishedPost(MessagePublishingDTO messagePublishingDto) {
		return postPersistenceManager.toDto(createPost(messagePublishingDto));
	}
	private Post createPost(MessagePublishingDTO messagePublishingDto) {
		if(messagePublishingDto.getAccountId() == null) {
			throw new SocialMediaAccountNotFoundException(messagePublishingDto.getAccountId());
		}
		SocialMediaAccount account = socialMediaAccountRepository.findById(messagePublishingDto.getAccountId())
				.orElseThrow(() -> new SocialMediaAccountNotFoundException(messagePublishingDto.getAccountId()));
		securityService.checkIfCurrentUserIsPermitted(account, SecuredAction.READ);
		Post post =  postPersistenceManager.storePost(messagePublishingDto.getMessage(), account);
		securityService.appendAcl(post);
		return post;
	}

	public Set<PostDTO> getAllPosts() {
		return securityService.filterForCurrentUser(postPersistenceManager.getAllPosts(), SecuredAction.READ).map(postPersistenceManager::toDto)
				.map(this::addSchedulingInformationIfAvailable).collect(Collectors.toSet());
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
		final Post post = postPersistenceManager.findPostById(id);
		if(securityService.isCurrentUserPermitted(post, SecuredAction.READ)){
			return postPersistenceManager.toDto(post);
		}else {
			throw new AuthorizationException("post", SecuredAction.READ);
		}
	}
}
