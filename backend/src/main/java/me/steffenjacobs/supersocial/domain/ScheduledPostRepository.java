package me.steffenjacobs.supersocial.domain;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.repository.CrudRepository;

import me.steffenjacobs.supersocial.domain.entity.ScheduledPost;

public interface ScheduledPostRepository extends CrudRepository<ScheduledPost, UUID> {

	Optional<ScheduledPost> findByPostId(UUID id);
}
