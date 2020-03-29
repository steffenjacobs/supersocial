package me.steffenjacobs.supersocial.persistence;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Component;

import me.steffenjacobs.supersocial.domain.CredentialRepository;
import me.steffenjacobs.supersocial.domain.dto.CredentialDTO;
import me.steffenjacobs.supersocial.domain.entity.Credential;
import me.steffenjacobs.supersocial.domain.entity.SecuredAction;
import me.steffenjacobs.supersocial.domain.entity.UserGroup;
import me.steffenjacobs.supersocial.persistence.exception.CredentialNotFoundException;
import me.steffenjacobs.supersocial.security.SecurityService;
import me.steffenjacobs.supersocial.util.Pair;

/** @author Steffen Jacobs */
@Component
public class CredentialPersistenceManager {

	@Autowired
	private Environment env;

	@Autowired
	private CredentialRepository credentialRepository;

	@Autowired
	private SecurityService securityService;

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
	public Stream<Pair<UserGroup, Credential>> getCredentialByUserGroup(CredentialDTO credential) {
		if ("true".equalsIgnoreCase(env.getProperty("security.encrypt_credentials"))) {
			throw new SecurityException("Credentials are encrypted and cannot be read without password.");
		}
		return securityService.filterForCurrentUser(credentialRepository.findAllById(credential.getId()), SecuredAction.READ)
				.map(c -> new Pair<>(securityService.getFirstMatchinUserGroupForCurrentUser(c, SecuredAction.READ), c));
	}

	public Stream<Pair<UserGroup, Credential>> getAll() {
		return securityService.filterForCurrentUser(StreamSupport.stream(credentialRepository.findAll().spliterator(), false), SecuredAction.READ)
				.map(c -> new Pair<>(securityService.getFirstMatchinUserGroupForCurrentUser(c, SecuredAction.READ), c));
	}

	public Optional<Credential> getCredentialForUserGroup(UserGroup userGroup, CredentialType credentialType) {
		return securityService.filterForCurrentUser(StreamSupport.stream(credentialRepository.findByDescriptor(credentialType.getKey()).spliterator(), false), SecuredAction.READ)
				.findFirst();
	}

	public Pair<Credential, Boolean> createOrUpdateCredential(CredentialDTO credential) {
		if (credential.getId() == null) {
			throw new CredentialNotFoundException(credential.getId());
		}
		// TODO: check create action
		Credential cred = securityService.filterForCurrentUser(this.credentialRepository.findById(credential.getId()), SecuredAction.UPDATE).orElse(new Credential());
		final boolean created = cred.getId() == null;
		cred.setDescriptor(credential.getDescriptor());
		cred.setValue(credential.getValue());
		if (created) {
			securityService.appendAcl(cred);
		}
		return new Pair<>(this.credentialRepository.save(cred), created);
	}

	public void deleteCredential(UUID id) {
		try {
			Credential c = credentialRepository.findById(id).orElseThrow(() -> new CredentialNotFoundException(id));
			securityService.checkIfCurrentUserIsPermitted(c, SecuredAction.DELETE);
			credentialRepository.deleteById(id);
		} catch (EmptyResultDataAccessException e) {
			throw new CredentialNotFoundException(id);
		}
	}
}
