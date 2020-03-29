package me.steffenjacobs.supersocial.persistence;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import me.steffenjacobs.supersocial.domain.PostRepository;
import me.steffenjacobs.supersocial.domain.ScheduledPostRepository;
import me.steffenjacobs.supersocial.domain.dto.LinkedScheduledPostDTO;
import me.steffenjacobs.supersocial.domain.dto.ScheduledPostDTO;
import me.steffenjacobs.supersocial.domain.entity.ScheduledPost;
import me.steffenjacobs.supersocial.persistence.exception.PostAlreadyScheduledException;
import me.steffenjacobs.supersocial.persistence.exception.ScheduledPostNotFoundException;
import me.steffenjacobs.supersocial.security.SecurityService;
import me.steffenjacobs.supersocial.util.Pair;

/** @author Steffen Jacobs */
@Component
public class ScheduledPostPersistenceManager {

	@Autowired
	private ScheduledPostRepository scheduledPostRepository;

	@Autowired
	private PostRepository postRepository;

	@Autowired
	private SecurityService securityService;

	public Pair<ScheduledPostDTO, Boolean> scheduleOrUpdateScheduledPost(LinkedScheduledPostDTO post) {
		ScheduledPost sPost = post.getId() == null ? new ScheduledPost() : scheduledPostRepository.findById(post.getId()).orElse(new ScheduledPost());

		if (sPost.getId() == null && scheduledPostRepository.findByPostId(post.getPostId()).isPresent()) {
			throw new PostAlreadyScheduledException(post.getPostId());
		}

		sPost.setPost(postRepository.findById(post.getPostId()).orElseThrow());
		sPost.setScheduledDate(post.getScheduled());
		sPost.setCreator(securityService.getCurrentUser());

		Boolean created = sPost.getId() == null; // needs to be stored before
													// .save() is called
		return new Pair<>(ScheduledPostDTO.fromScheduledPost(scheduledPostRepository.save(sPost)), created);
	}

	public Set<ScheduledPostDTO> getAllScheduledPosts() {
		return StreamSupport.stream(scheduledPostRepository.findAll().spliterator(), false).map(ScheduledPostDTO::fromScheduledPost).collect(Collectors.toSet());
	}

	public Optional<ScheduledPostDTO> findById(UUID id) {
		return ScheduledPostDTO.fromScheduledPost(scheduledPostRepository.findById(id));
	}

	public void deleteScheduledPost(UUID id) {
		try {
			scheduledPostRepository.deleteById(id);
		} catch (EmptyResultDataAccessException e) {
			throw new ScheduledPostNotFoundException("Scheduled post with this id was not found", e);
		}
	}

	public Set<ScheduledPost> getAllScheduledAndNotPublishedPosts() {
		return StreamSupport.stream(scheduledPostRepository.findAll().spliterator(), false)
				.filter(p -> p.getPost().getPublished() == null && StringUtils.isEmpty(p.getPost().getErrorMessage())).collect(Collectors.toSet());
	}

	public Optional<ScheduledPostDTO> findByPostId(UUID id) {
		return ScheduledPostDTO.fromScheduledPost(scheduledPostRepository.findByPostId(id));
	}
}
