package me.steffenjacobs.supersocial.service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import me.steffenjacobs.supersocial.domain.Platform;
import me.steffenjacobs.supersocial.domain.SocialMediaAccountRepository;
import me.steffenjacobs.supersocial.domain.dto.CredentialDTO;
import me.steffenjacobs.supersocial.domain.dto.SocialMediaAccountDTO;
import me.steffenjacobs.supersocial.domain.entity.Credential;
import me.steffenjacobs.supersocial.domain.entity.SecuredAction;
import me.steffenjacobs.supersocial.domain.entity.SocialMediaAccount;
import me.steffenjacobs.supersocial.persistence.CredentialPersistenceManager;
import me.steffenjacobs.supersocial.security.SecurityService;
import me.steffenjacobs.supersocial.service.exception.CouldNotDeleteEntityException;
import me.steffenjacobs.supersocial.service.exception.SocialMediaAccountNotFoundException;
import me.steffenjacobs.supersocial.util.Pair;

/**
 * Manages {@link SocialMediaAccount social media accounts} and associates
 * {@link Credential credentials} with them.
 * 
 * @author Steffen Jacobs
 */
@Component
public class SocialMediaAccountService {
	@Autowired
	private SocialMediaAccountRepository socialMediaAccountRepository;

	@Autowired
	private CredentialPersistenceManager credentialPersistenceManager;

	@Autowired
	private SecurityService securityService;

	/**
	 * Retrieves the {@link SocialMediaAccountDTO} associated to the given
	 * identifier and prunes the associated credentials.
	 * 
	 * @throws SocialMediaAccountNotFoundException
	 *             if the social media account could not be found.
	 */
	public SocialMediaAccountDTO findById(UUID id) {
		return securityService.filterForCurrentUser(socialMediaAccountRepository.findById(id), SecuredAction.READ).map(this::createPrunedDTO)
				.orElseThrow(() -> new SocialMediaAccountNotFoundException(id));
	}

	/**
	 * Retrieves the {@link SocialMediaAccount} (not the DTO object) associated
	 * to the given identifier.
	 * 
	 * @throws SocialMediaAccountNotFoundException
	 *             if the social media account could not be found.
	 */
	public SocialMediaAccount findByIdNonDto(UUID id) {
		return securityService.filterForCurrentUser(socialMediaAccountRepository.findById(id), SecuredAction.READ).orElseThrow(() -> new SocialMediaAccountNotFoundException(id));
	}

	/**
	 * Retrieves all {@link SocialMediaAccount social media accounts} and
	 * filters the one the current user is allowed to view. Prunes the
	 * associated credentials.
	 */
	public Stream<SocialMediaAccountDTO> getAllSocialMediaAccounts() {
		return securityService.filterForCurrentUser(StreamSupport.stream(socialMediaAccountRepository.findAll().spliterator(), false), SecuredAction.READ)
				.map(this::createPrunedDTO);
	}

	/**
	 * Creates or updates a given {@link SocialMediaAccountDTO}.
	 * 
	 * @return {@code <true,account>} if the account has been created. <br/>
	 *         Returns <false,account> if the account was updated.
	 */
	public Pair<Boolean, SocialMediaAccountDTO> createOrUpdateSocialMediaAccount(SocialMediaAccountDTO creationDto) {
		Optional<SocialMediaAccount> optAccount = Optional.empty();
		if (creationDto.getId() != null) {
			optAccount = socialMediaAccountRepository.findById(creationDto.getId());
			optAccount.ifPresent(a -> securityService.checkIfCurrentUserIsPermitted(a, SecuredAction.UPDATE));
		}
		SocialMediaAccount acc = optAccount.orElseGet(SocialMediaAccount::new);
		boolean created = acc.getId() == null;
		if (created) {
			createNewSocialMediaAccount(acc, Platform.fromId(creationDto.getPlatformId()), creationDto.getDisplayName());
		}
		acc.setDisplayName(creationDto.getDisplayName());
		acc.setPlatform(Platform.fromId(creationDto.getPlatformId()));
		return new Pair<>(created, createPrunedDTO(socialMediaAccountRepository.save(acc)));
	}

	/**
	 * Create a new {@link SocialMediaAccount} and give ownership to the current
	 * user.
	 */
	private SocialMediaAccount createNewSocialMediaAccount(SocialMediaAccount account, Platform platform, String displayName) {
		account.setDisplayName(displayName);
		account.setPlatform(platform);
		account = socialMediaAccountRepository.save(account);
		securityService.appendCurrentUserAcl(account);
		return account;
	}

	/**
	 * Append the given {@link Credential} identified by it's credentialId to
	 * the given {@link SocialMediaAccount} identified by it's accountId.
	 * 
	 * @throws SocialMediaAccountNotFoundException
	 *             if the social media account could not be found.
	 * @throws me.steffenjacobs.supersocial.persistence.exception.CredentialNotFoundException
	 *             if the credential could not be found.
	 */
	public SocialMediaAccountDTO appendCredential(UUID accountId, UUID credentialId) {
		SocialMediaAccount account = socialMediaAccountRepository.findById(accountId).orElseThrow(() -> new SocialMediaAccountNotFoundException(accountId));
		securityService.checkIfCurrentUserIsPermitted(account, SecuredAction.UPDATE);

		Credential credential = credentialPersistenceManager.findById(credentialId);
		securityService.checkIfCurrentUserIsPermitted(credential, SecuredAction.UPDATE);

		credential = credentialPersistenceManager.appendToAccount(credential, account);

		account.getCredentials().add(credential);

		return createPrunedDTO(socialMediaAccountRepository.save(account));
	}

	/**
	 * Prune the {@link Credential credentials} associated to the given
	 * {@link SocialMediaAccount} by omitting the non-public values and convert
	 * it to a {@link SocialMediaAccountDTO}.
	 */
	private SocialMediaAccountDTO createPrunedDTO(SocialMediaAccount account) {
		Set<CredentialDTO> credentials = new HashSet<>();

		account.getCredentials().forEach(c -> {
			if (securityService.isCurrentUserPermitted(c, SecuredAction.READ)) {
				credentials.add(c.toDTO(credentialPersistenceManager.isCredentialPublic(c)));
			}
		});

		return SocialMediaAccountDTO.fromSocialMediaAccount(account, credentials);
	}

	/**
	 * Delete the {@link SocialMediaAccount} associated to the given id.
	 * 
	 * @throws SocialMediaAccountNotFoundException
	 *             if the associated account does not exist.
	 * @throws me.steffenjacobs.supersocial.security.exception.AuthorizationException
	 *             if the current user is not allowed to delete the account
	 */
	public void deleteSocialMediaAccount(UUID id) {
		SocialMediaAccount account = socialMediaAccountRepository.findById(id).orElseThrow(() -> new SocialMediaAccountNotFoundException(id));
		securityService.checkIfCurrentUserIsPermitted(account, SecuredAction.DELETE);
		try {
			socialMediaAccountRepository.deleteById(id);
		} catch (Exception e) {
			throw new CouldNotDeleteEntityException(
					"Could not delete social media account because there are still posts scheduled to be posted or credentials associated with this account. Please revise them before deleting the account.");
		}
	}

}
