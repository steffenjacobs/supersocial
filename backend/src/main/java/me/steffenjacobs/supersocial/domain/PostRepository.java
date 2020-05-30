package me.steffenjacobs.supersocial.domain;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.repository.CrudRepository;

import me.steffenjacobs.supersocial.domain.entity.Post;

public interface PostRepository extends CrudRepository<Post, UUID> {
	List<Post> findByExternalIdIn(Set<String> externalIds);
}
