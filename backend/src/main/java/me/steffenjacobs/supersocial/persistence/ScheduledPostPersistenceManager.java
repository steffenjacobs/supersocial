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

/** @author Steffen Jacobs */
@Component
public class ScheduledPostPersistenceManager {

	@Autowired
	private ScheduledPostRepository scheduledPostRepository;

	public Stream<ScheduledPost> getAllScheduledPosts() {
		return StreamSupport.stream(scheduledPostRepository.findAll().spliterator(), false);
	}

	public Optional<ScheduledPost> findById(UUID id) {
		return scheduledPostRepository.findById(id);
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

	public Pair<ScheduledPost, Boolean> updateScheduledPost(ScheduledPost sPost, Post actualPost, Date scheduled, SupersocialUser creator) {
		sPost.setPost(actualPost);
		sPost.setScheduledDate(scheduled);
		sPost.setCreator(creator);

		Boolean created = sPost.getId() == null; // needs to be stored before
													// .save() is called
		return new Pair<>(scheduledPostRepository.save(sPost), created);
	}
}
