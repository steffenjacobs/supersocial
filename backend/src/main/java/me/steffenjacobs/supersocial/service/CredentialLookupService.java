package me.steffenjacobs.supersocial.service;

import org.springframework.stereotype.Component;

import me.steffenjacobs.supersocial.domain.entity.Credential;
import me.steffenjacobs.supersocial.domain.entity.Post;
import me.steffenjacobs.supersocial.domain.entity.SocialMediaAccount;
import me.steffenjacobs.supersocial.persistence.CredentialPersistenceManager.CredentialType;
import me.steffenjacobs.supersocial.service.exception.CredentialMissingException;

/**
 * Aids to get a certain credential from a {@link Post} and it's associated
 * {@link SocialMediaAccount}.
 * 
 * @author Steffen Jacobs
 */
@Component
public class CredentialLookupService {

	/**
	 * Retrieve the credential from the given {@link Post} based on the
	 * associated {@link SocialMediaAccount}.
	 */
	public String getCredential(Post post, CredentialType credential) {
		return getCredential(post.getSocialMediaAccountToPostWith(), credential);
	}

	/**
	 * Retrieve the credential directly from the given
	 * {@link SocialMediaAccount}.
	 */
	public String getCredential(SocialMediaAccount account, CredentialType credential) {
		return account.getCredentials().stream().filter(c -> credential.getKey().equals(c.getDescriptor())).findFirst().map(Credential::getValue)
				.orElseThrow(() -> new CredentialMissingException(credential));
	}
}
