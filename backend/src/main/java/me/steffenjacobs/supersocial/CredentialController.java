package me.steffenjacobs.supersocial;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

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
import me.steffenjacobs.supersocial.service.CredentialService;
import me.steffenjacobs.supersocial.service.exception.CredentialNotFoundException;
import me.steffenjacobs.supersocial.util.Pair;

/** @author Steffen Jacobs */
@RestController
public class CredentialController {

	@Autowired
	private CredentialService credentialService;

	@PutMapping(path = "/api/credential", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<CredentialDTO> createOrUpdateCredential(@RequestBody CredentialDTO credential) throws Exception {
		Pair<Credential, Boolean> c = credentialService.createOrUpdateCredential(credential);
		return new ResponseEntity<>(c.getA().toDTO(), c.getB() ? HttpStatus.CREATED : HttpStatus.ACCEPTED);
	}

	@GetMapping(path = "/api/credential", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Set<CredentialDTO>> getCredentials() throws Exception {
		Set<CredentialDTO> dtos = StreamSupport.stream(credentialService.getAll().spliterator(), false).map(Credential::toDTO).collect(Collectors.toSet());
		return new ResponseEntity<>(dtos, HttpStatus.OK);
	}

	@DeleteMapping(path = "/api/credential/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<CredentialDTO> deleteCredential(@PathVariable(name = "id") UUID id) {
		try {
			credentialService.deleteCredential(id);
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} catch (CredentialNotFoundException e) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}
}
