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
import me.steffenjacobs.supersocial.domain.entity.SocialMediaAccount;
import me.steffenjacobs.supersocial.domain.entity.UserGroup;
import me.steffenjacobs.supersocial.persistence.exception.CredentialNotFoundException;
import me.steffenjacobs.supersocial.security.SecurityService;
import me.steffenjacobs.supersocial.service.SocialMediaAccountService;
import me.steffenjacobs.supersocial.util.Pair;

/**
 * Handles persistence and permission checks for CRUD operations on credentials.
 * 
 * @author Steffen Jacobs
 */
@Component
public class CredentialPersistenceManager {

	@Autowired
	private Environment env;

	@Autowired
	private CredentialRepository credentialRepository;

	@Autowired
	private SecurityService securityService;

	@Autowired
	private SocialMediaAccountService socialMediaAccountService;

	/** Describes which information is stored in a credential. */
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
	 * @return the given credential loaded from the database and filtered by
	 *         READ permission for the current user.
	 */
	public Stream<Pair<UserGroup, Credential>> getCredentialByUserGroup(CredentialDTO credential) {
		if ("true".equalsIgnoreCase(env.getProperty("security.encrypt_credentials"))) {
			throw new SecurityException("Credentials are encrypted and cannot be read without password.");
		}
		return securityService.filterForCurrentUser(credentialRepository.findAllById(credential.getId()), SecuredAction.READ)
				.map(c -> new Pair<>(securityService.getFirstMatchinUserGroupForCurrentUser(c, SecuredAction.READ), c));
	}

	/**
	 * @return all credentials filtered by READ permission for the current user.
	 */
	public Stream<Credential> getAll() {
		return securityService.filterForCurrentUser(StreamSupport.stream(credentialRepository.findAll().spliterator(), false), SecuredAction.READ);
	}

	/**
	 * Update or create a credential.
	 * 
	 * @return {@code <credential,true>} if the credential was newly created and
	 *         {@code <credential,false>} if it was just updated.
	 * @throws me.steffenjacobs.supersocial.security.exception.AuthorizationException
	 *             if the current user is not permitted to update the credential
	 *             or the associated social media account (assuming one is
	 *             associated).
	 */
	public Pair<Credential, Boolean> createOrUpdateCredential(CredentialDTO credential) {
		// TODO: check create action
		Optional<Credential> optCred = Optional.empty();
		if (credential.getId() != null) {
			optCred = this.credentialRepository.findById(credential.getId());
			optCred.ifPresent(c -> securityService.checkIfCurrentUserIsPermitted(c, SecuredAction.UPDATE));
		}

		if (credential.getAccountId() != null) {
			// check if account exists and user is permitted to assign new
			// credentials to it
			securityService.checkIfCurrentUserIsPermitted(socialMediaAccountService.findByIdNonDto(credential.getAccountId()), SecuredAction.UPDATE);
		}

		Credential cred = optCred.orElseGet(Credential::new);
		boolean created = credential.getId() == null;

		cred.setDescriptor(credential.getDescriptor());
		cred.setValue(credential.getValue());
		cred = this.credentialRepository.save(cred);
		if (created) {
			securityService.appendAcl(cred);
		}

		if (credential.getAccountId() != null) {
			socialMediaAccountService.appendCredential(credential.getAccountId(), cred.getId());
		}

		return new Pair<>(cred, created);
	}

	/**
	 * Delete a credential with a given UUID
	 * 
	 * @throws CredentialNotFoundException
	 *             if the credential does not exist.
	 * @throws me.steffenjacobs.supersocial.security.exception.AuthorizationException
	 *             if the current user is not permitted to delete the
	 *             credential.
	 */
	public void deleteCredential(UUID id) {
		try {
			Credential c = credentialRepository.findById(id).orElseThrow(() -> new CredentialNotFoundException(id));
			securityService.checkIfCurrentUserIsPermitted(c, SecuredAction.DELETE);
			credentialRepository.deleteById(id);
		} catch (EmptyResultDataAccessException e) {
			throw new CredentialNotFoundException(id);
		}
	}

	/**
	 * Find a credential by it's unique identifier.
	 * 
	 * @return the credential.
	 * 
	 * @throws CredentialNotFoundException
	 *             if the credential does not exist.
	 * @throws me.steffenjacobs.supersocial.security.exception.AuthorizationException
	 *             if the current user is not permitted to read the credential.
	 */
	public Credential findById(UUID credentialId) {
		Credential c = credentialRepository.findById(credentialId).orElseThrow(() -> new CredentialNotFoundException(credentialId));
		securityService.checkIfCurrentUserIsPermitted(c, SecuredAction.READ);
		return c;
	}

	/**
	 * Add a credential to a given social media account. No permission checks.
	 */
	public Credential appendToAccount(Credential credential, SocialMediaAccount account) {
		credential.setAccount(account);
		return credentialRepository.save(credential);
	}
}
