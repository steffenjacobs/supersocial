package me.steffenjacobs.supersocial;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import me.steffenjacobs.supersocial.domain.dto.PostDTO;
import me.steffenjacobs.supersocial.persistence.PostPersistenceManager;

/** @author Steffen Jacobs */

@RestController
public class PostController {

	@Autowired
	PostPersistenceManager postPersistenceManager;

	@GetMapping(path = "/api/post")
	public Set<PostDTO> getAllPublishedPosts() {
		return postPersistenceManager.getAllPosts();
	}

	@GetMapping(path = "/api/post/{id}")
	public PostDTO getPublishedPostById(@PathVariable long id) {
		return postPersistenceManager.findPostById(id);
	}
}
