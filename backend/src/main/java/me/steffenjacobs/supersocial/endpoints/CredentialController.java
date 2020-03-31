package me.steffenjacobs.supersocial.endpoints;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

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
import me.steffenjacobs.supersocial.domain.entity.Credential;
import me.steffenjacobs.supersocial.persistence.CredentialPersistenceManager;
import me.steffenjacobs.supersocial.persistence.exception.CredentialNotFoundException;
import me.steffenjacobs.supersocial.service.exception.SocialMediaAccountNotFoundException;
import me.steffenjacobs.supersocial.util.Pair;

/** @author Steffen Jacobs */
@RestController
public class CredentialController {

	@Autowired
	private CredentialPersistenceManager credentialPersistenceManager;

	@PutMapping(path = "/api/credential", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<CredentialDTO> createOrUpdateCredential(@RequestBody CredentialDTO credential) throws Exception {
		try {
			Pair<Credential, Boolean> c = credentialPersistenceManager.createOrUpdateCredential(credential);
			return new ResponseEntity<>(c.getA().toDTO(), c.getB() ? HttpStatus.CREATED : HttpStatus.ACCEPTED);
		} catch (CredentialNotFoundException e) {
			return new ResponseEntity<>(new CredentialDTO(e.getMessage()), HttpStatus.NOT_FOUND);
		}
	}

	@GetMapping(path = "/api/credential", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Set<CredentialDTO>> getCredentials() throws Exception {
		try {
			return new ResponseEntity<>(credentialPersistenceManager.getAll().map(Credential::toDTO).collect(Collectors.toSet()), HttpStatus.OK);
		} catch (SocialMediaAccountNotFoundException e) {
			return new ResponseEntity<>(Set.of(new CredentialDTO(e.getMessage())), HttpStatus.NOT_FOUND);
		}
	}

	@DeleteMapping(path = "/api/credential/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<CredentialDTO> deleteCredential(@PathVariable(name = "id") UUID id) {
		try {
			credentialPersistenceManager.deleteCredential(id);
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} catch (CredentialNotFoundException e) {
			return new ResponseEntity<>(new CredentialDTO(e.getMessage()), HttpStatus.NOT_FOUND);
		}
	}
}
