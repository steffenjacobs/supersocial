package me.steffenjacobs.supersocial.service;

import org.springframework.stereotype.Component;

import me.steffenjacobs.supersocial.domain.entity.Credential;
import me.steffenjacobs.supersocial.domain.entity.Post;
import me.steffenjacobs.supersocial.domain.entity.SocialMediaAccount;
import me.steffenjacobs.supersocial.persistence.CredentialPersistenceManager.CredentialType;
import me.steffenjacobs.supersocial.service.exception.CredentialMissingException;

/** @author Steffen Jacobs */
@Component
public class CredentialLookupService {

	public String getCredential(Post post, CredentialType credential) {
		return getCredential(post.getSocialMediaAccountToPostWith(), credential);
	}

	public String getCredential(SocialMediaAccount account, CredentialType credential) {
		return account.getCredentials().stream().filter(c -> credential.getKey().equals(c.getDescriptor())).findFirst().map(Credential::getValue)
				.orElseThrow(() -> new CredentialMissingException(credential));
	}
}
