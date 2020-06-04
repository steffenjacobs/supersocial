package me.steffenjacobs.supersocial.persistence;

import java.util.Date;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import me.steffenjacobs.supersocial.domain.ScheduledPostRepository;
import me.steffenjacobs.supersocial.domain.dto.ScheduledPostDTO;
import me.steffenjacobs.supersocial.domain.entity.Post;
import me.steffenjacobs.supersocial.domain.entity.ScheduledPost;
import me.steffenjacobs.supersocial.domain.entity.SupersocialUser;
import me.steffenjacobs.supersocial.persistence.exception.ScheduledPostNotFoundException;
import me.steffenjacobs.supersocial.util.Pair;

/**
 * Handles persistence for CRUD operations on scheduling information for posts.
 * Permission checks are handled in
 * {@link me.steffenjacobs.supersocial.service.ScheduledPostService}.
 * 
 * @author Steffen Jacobs
 */
@Component
public class ScheduledPostPersistenceManager {

	@Autowired
	private ScheduledPostRepository scheduledPostRepository;

	/**
	 * Get all scheduled posts.
	 * 
	 * @return all {@link ScheduledPost}s.
	 */
	public Stream<ScheduledPost> getAllScheduledPosts() {
		return StreamSupport.stream(scheduledPostRepository.findAll().spliterator(), false);
	}

	/**
	 * Find a scheduled post by its unique identifier (not the same identifier
	 * of the associated {@link Post}).
	 * 
	 * @return the {@link ScheduledPost}
	 */
	public Optional<ScheduledPost> findById(UUID id) {
		return scheduledPostRepository.findById(id);
	}

	/**
	 * Delete a scheduled post (not the same identifier of the associated
	 * {@link Post}).
	 * 
	 * @throws ScheduledPostNotFoundException
	 *             if the scheduled post could not be found.
	 */
	public void deleteScheduledPost(UUID id) {
		try {
			scheduledPostRepository.deleteById(id);
		} catch (EmptyResultDataAccessException e) {
			throw new ScheduledPostNotFoundException(id);
		}
	}

	/**
	 * Retrieve all scheduled posts that are scheduled but not yet published.
	 * This does not include posts that remain unpublished because the
	 * publishing failed.
	 * 
	 * @return the {@link ScheduledPost}s that are scheduled but not yet
	 *         published.
	 */
	public Set<ScheduledPost> getAllScheduledAndNotPublishedPosts() {
		return StreamSupport.stream(scheduledPostRepository.findAll().spliterator(), false)
				.filter(p -> p.getPost().getPublished() == null && StringUtils.isEmpty(p.getPost().getErrorMessage())).collect(Collectors.toSet());
	}

	/**
	 * Find a scheduled post by the identifier of its associated {@link Post}.
	 * 
	 * @return the associated {@link ScheduledPostDTO}.
	 */
	public Optional<ScheduledPostDTO> findByPostId(UUID id) {
		return ScheduledPostDTO.fromScheduledPost(scheduledPostRepository.findByPostId(id));
	}

	/**
	 * Find a scheduled post by the identifier of its associated {@link Post}.
	 * 
	 * @return the associated {@link ScheduledPost} (non-DTO).
	 */
	public ScheduledPost findOriginalByPostId(UUID id) {
		return scheduledPostRepository.findByPostId(id).orElseThrow(() -> new ScheduledPostNotFoundException(id));
	}

	/**
	 * Create a new or update an existing scheduled post.
	 * 
	 * @return {@code <true,scheduledPost>} if the post was newly created.
	 *         {@code <false,scheduledPost>} if the post was merely updated.
	 */
	public Pair<ScheduledPost, Boolean> updateScheduledPost(ScheduledPost sPost, Post actualPost, Date scheduled, SupersocialUser creator) {
		sPost.setPost(actualPost);
		sPost.setScheduledDate(scheduled);
		sPost.setCreator(creator);

		Boolean created = sPost.getId() == null; // needs to be stored before
													// .save() is called
		return new Pair<>(scheduledPostRepository.save(sPost), created);
	}
}
