package me.steffenjacobs.supersocial;

import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import me.steffenjacobs.supersocial.domain.dto.PostDTO;
import me.steffenjacobs.supersocial.persistence.PostPersistenceManager;
import me.steffenjacobs.supersocial.service.exception.PostNotFoundException;

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
	public ResponseEntity<PostDTO> getPublishedPostById(@PathVariable(name="id") UUID id) {
		try {
			return new ResponseEntity<>(postPersistenceManager.findPostById(id), HttpStatus.OK);
		} catch (PostNotFoundException e) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}
}
