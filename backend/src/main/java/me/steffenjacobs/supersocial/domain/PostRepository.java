package me.steffenjacobs.supersocial.domain;

import org.springframework.data.repository.CrudRepository;

import me.steffenjacobs.supersocial.domain.entity.Post;

public interface PostRepository extends CrudRepository<Post, Long> {
}
