package me.steffenjacobs.supersocial.endpoints;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import me.steffenjacobs.supersocial.domain.dto.SocialMediaAccountDTO;
import me.steffenjacobs.supersocial.persistence.exception.CredentialNotFoundException;
import me.steffenjacobs.supersocial.security.exception.AuthorizationException;
import me.steffenjacobs.supersocial.service.SocialMediaAccountService;
import me.steffenjacobs.supersocial.service.exception.CouldNotDeleteEntityException;
import me.steffenjacobs.supersocial.service.exception.SocialMediaAccountNotFoundException;
import me.steffenjacobs.supersocial.util.Pair;

/**
 * Contains endpoints with CRUD operations for social media accounts.
 * 
 * @author Steffen Jacobs
 */
@RestController
public class SocialMediaAccountController {

	private static final Logger LOG = LoggerFactory.getLogger(SocialMediaAccountController.class);

	@Autowired
	private SocialMediaAccountService socialMediaAccountService;

	/** Retrieve all social media accounts. */
	@GetMapping(path = "/api/socialmediaaccount")
	public Set<SocialMediaAccountDTO> getAllSocialMediaAccounts() {
		LOG.info("Retrieving all social media accounts");
		return socialMediaAccountService.getAllSocialMediaAccounts().collect(Collectors.toSet());
	}

	/** Retrieve a single social media account with a specific {@code id}. */
	@GetMapping(path = "/api/socialmediaaccount/{id}")
	public ResponseEntity<SocialMediaAccountDTO> getSocialMediaAccountById(@PathVariable(name = "id") UUID id) {
		LOG.info("Retrieving social media account with id {}", id);
		try {
			return new ResponseEntity<>(socialMediaAccountService.findById(id), HttpStatus.OK);
		} catch (SocialMediaAccountNotFoundException e) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	/** Create a new social media account. */
	@PutMapping(path = "/api/socialmediaaccount", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<SocialMediaAccountDTO> createOrUpdateSocialMediaAccount(@RequestBody SocialMediaAccountDTO creationDto) throws Exception {
		LOG.info("Creating or updating social media account {}", creationDto);
		try {
			final Pair<Boolean, SocialMediaAccountDTO> acc = socialMediaAccountService.createOrUpdateSocialMediaAccount(creationDto);
			return new ResponseEntity<>(acc.getB(), acc.getA() ? HttpStatus.CREATED : HttpStatus.ACCEPTED);
		} catch (SocialMediaAccountNotFoundException e) {
			return new ResponseEntity<>(new SocialMediaAccountDTO(e.getMessage()), HttpStatus.BAD_REQUEST);
		}
	}

	/** Associate a given credential to a given social media account. */
	@PutMapping(path = "/api/socialmediaaccount/{accountId}/{credentialId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<SocialMediaAccountDTO> addCredentialToSocialMediaAccount(@PathVariable(name = "accountId") UUID accountId,
			@PathVariable(name = "credentialId") UUID credentialId) throws Exception {
		LOG.info("Appending credential {} to social media account {}", credentialId, accountId);
		try {
			return new ResponseEntity<>(socialMediaAccountService.appendCredential(accountId, credentialId), HttpStatus.ACCEPTED);
		} catch (SocialMediaAccountNotFoundException | CredentialNotFoundException e) {
			return new ResponseEntity<>(new SocialMediaAccountDTO(e.getMessage()), HttpStatus.BAD_REQUEST);
		} catch (AuthorizationException e) {
			return new ResponseEntity<>(new SocialMediaAccountDTO(e.getMessage()), HttpStatus.UNAUTHORIZED);
		}
	}

	/** Delete a given social media account by {@code id}.s*/
	@DeleteMapping(path = "/api/socialmediaaccount/{id}")
	public ResponseEntity<SocialMediaAccountDTO> createOrUpdateSocialMediaAccount(@PathVariable(name = "id") UUID id) throws Exception {
		LOG.info("Deleting social media account {}", id);
		try {
			socialMediaAccountService.deleteSocialMediaAccount(id);
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} catch (SocialMediaAccountNotFoundException | CouldNotDeleteEntityException e) {
			return new ResponseEntity<>(new SocialMediaAccountDTO(e.getMessage()), HttpStatus.BAD_REQUEST);
		}
	}

}
