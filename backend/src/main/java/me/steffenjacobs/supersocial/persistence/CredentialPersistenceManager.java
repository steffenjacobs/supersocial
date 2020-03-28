package me.steffenjacobs.supersocial.persistence;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Component;

import me.steffenjacobs.supersocial.domain.CredentialRepository;
import me.steffenjacobs.supersocial.domain.dto.CredentialDTO;
import me.steffenjacobs.supersocial.domain.entity.Credential;
import me.steffenjacobs.supersocial.persistence.exception.CredentialNotFoundException;
import me.steffenjacobs.supersocial.util.Pair;

/** @author Steffen Jacobs */
@Component
public class CredentialPersistenceManager {

	@Autowired
	private Environment env;

	@Autowired
	private CredentialRepository credentialRepository;

	public enum CredentialType {
		TWITTER_API_KEY("twitter.api.key"), TWITTER_API_KEY_SECRET("twitter.api.secret"), TWITTER_ACCESS_TOKEN("twitter.api.accesstoken"), TWITTER_ACCESS_TOKEN_SECRET(
				"twitter.api.accesstoken.secret"), FACEBOOK_PAGE_ID("facebook.page.id"), FACEBOOK_PAGE_ACCESSTOKEN("facebook.page.accesstoken");

		private final String key;

		private CredentialType(String key) {
			this.key = key;
		}

		public String getKey() {
			return key;
		}
	}

	/**
	 * @return the given credential loaded from the database.
	 */
	public Optional<String> getCredential(CredentialType credential) {
		if ("true".equalsIgnoreCase(env.getProperty("security.encrypt_credentials"))) {
			throw new SecurityException("Credentials are encrypted and cannot be read without password.");
		}
		return credentialRepository.findByDescriptor(credential.getKey()).map(Credential::getValue);
	}

	public Iterable<Credential> getAll() {
		return credentialRepository.findAll();
	}

	public Pair<Credential, Boolean> createOrUpdateCredential(CredentialDTO credential) {
		Credential cred = this.credentialRepository.findByDescriptor(credential.getDescriptor()).orElse(new Credential());
		cred.setDescriptor(credential.getDescriptor());
		cred.setValue(credential.getValue());
		final boolean created = cred.getId() == null; // must be assigned before the save(...) method is called.
		return new Pair<>(this.credentialRepository.save(cred), created);
	}

	public void deleteCredential(UUID id) {
		try {
			credentialRepository.deleteById(id);
		} catch (EmptyResultDataAccessException e) {
			throw new CredentialNotFoundException("Credential with this id was not found", e);
		}
	}
}
