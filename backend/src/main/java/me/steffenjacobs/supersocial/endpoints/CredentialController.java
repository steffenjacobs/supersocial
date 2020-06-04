package me.steffenjacobs.supersocial.endpoints;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
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

import me.steffenjacobs.supersocial.domain.dto.CredentialDTO;
import me.steffenjacobs.supersocial.persistence.CredentialPersistenceManager;
import me.steffenjacobs.supersocial.persistence.exception.CredentialNotFoundException;
import me.steffenjacobs.supersocial.service.exception.SocialMediaAccountNotFoundException;
import me.steffenjacobs.supersocial.util.Pair;

/**
 * Contains endpoints with CRUD operations for credentials.
 * 
 * @author Steffen Jacobs
 */
@RestController
public class CredentialController {
	private static final Logger LOG = org.slf4j.LoggerFactory.getLogger(CredentialController.class);

	@Autowired
	private CredentialPersistenceManager credentialPersistenceManager;

	/**
	 * Create a new or update an existing credential for a given social media
	 * account.
	 */
	@PutMapping(path = "/api/credential", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<CredentialDTO> createOrUpdateCredential(@RequestBody CredentialDTO credential) throws Exception {
		try {
			LOG.info("Creating or updating credential {} for account {}.", credential.getDescriptor(), credential.getAccountId());
			Pair<CredentialDTO, Boolean> c = credentialPersistenceManager.createOrUpdateCredential(credential);
			return new ResponseEntity<>(c.getA(), c.getB() ? HttpStatus.CREATED : HttpStatus.ACCEPTED);
		} catch (CredentialNotFoundException e) {
			return new ResponseEntity<>(new CredentialDTO(e.getMessage()), HttpStatus.NOT_FOUND);
		}
	}

	/** Get all credentials. */
	@GetMapping(path = "/api/credential", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Set<CredentialDTO>> getCredentials() throws Exception {
		LOG.info("Retrieving all credentials.");
		try {
			return new ResponseEntity<>(credentialPersistenceManager.getAll().collect(Collectors.toSet()), HttpStatus.OK);
		} catch (SocialMediaAccountNotFoundException e) {
			return new ResponseEntity<>(Set.of(new CredentialDTO(e.getMessage())), HttpStatus.NOT_FOUND);
		}
	}

	/** Delete a specific credential by its {@code id}. */
	@DeleteMapping(path = "/api/credential/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<CredentialDTO> deleteCredential(@PathVariable(name = "id") UUID id) {
		LOG.info("Deleting credential {}.", id);
		try {
			credentialPersistenceManager.deleteCredential(id);
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} catch (CredentialNotFoundException e) {
			return new ResponseEntity<>(new CredentialDTO(e.getMessage()), HttpStatus.NOT_FOUND);
		}
	}
}
