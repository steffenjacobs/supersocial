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

/** @author Steffen Jacobs */
@Component
public class SocialMediaAccountService {
	@Autowired
	private SocialMediaAccountRepository socialMediaAccountRepository;

	@Autowired
	private CredentialPersistenceManager credentialPersistenceManager;

	@Autowired
	private SecurityService securityService;

	public SocialMediaAccountDTO findById(UUID id) {
		return securityService.filterForCurrentUser(socialMediaAccountRepository.findById(id), SecuredAction.READ).map(this::createPrunedDTO)
				.orElseThrow(() -> new SocialMediaAccountNotFoundException(id));
	}

	public SocialMediaAccount findByIdNonDto(UUID id) {
		return securityService.filterForCurrentUser(socialMediaAccountRepository.findById(id), SecuredAction.READ).orElseThrow(() -> new SocialMediaAccountNotFoundException(id));
	}

	public Stream<SocialMediaAccountDTO> getAllSocialMediaAccounts() {
		return securityService.filterForCurrentUser(StreamSupport.stream(socialMediaAccountRepository.findAll().spliterator(), false), SecuredAction.CREATE)
				.map(this::createPrunedDTO);
	}

	public Pair<Boolean, SocialMediaAccountDTO> createOrUpdateSocialMediaAccount(SocialMediaAccountDTO creationDto) {
		Optional<SocialMediaAccount> optAccount = Optional.empty();
		if (creationDto.getId() != null) {
			optAccount = socialMediaAccountRepository.findById(creationDto.getId());
			optAccount.ifPresent(a -> securityService.checkIfCurrentUserIsPermitted(a, SecuredAction.UPDATE));
		}
		SocialMediaAccount acc = optAccount.orElse(new SocialMediaAccount());
		boolean created = acc.getId() == null;
		if(created) {
			createNewSocialMediaAccount(acc, Platform.fromId(creationDto.getPlatformId()), creationDto.getDisplayName());
		}
		acc.setDisplayName(creationDto.getDisplayName());
		acc.setPlatform(Platform.fromId(creationDto.getPlatformId()));
		return new Pair<>(created, createPrunedDTO(socialMediaAccountRepository.save(acc)));
	}

	private SocialMediaAccount createNewSocialMediaAccount(SocialMediaAccount account, Platform platform, String displayName) {
		account.setDisplayName(displayName);
		account.setPlatform(platform);
		account = socialMediaAccountRepository.save(account);
		securityService.appendAcl(account);
		return account;
	}

	public SocialMediaAccountDTO appendCredential(UUID accountId, UUID credentialId) {
		SocialMediaAccount account = socialMediaAccountRepository.findById(accountId).orElseThrow(() -> new SocialMediaAccountNotFoundException(accountId));
		securityService.checkIfCurrentUserIsPermitted(account, SecuredAction.UPDATE);

		Credential credential = credentialPersistenceManager.findById(credentialId);
		securityService.checkIfCurrentUserIsPermitted(credential, SecuredAction.UPDATE);

		credential = credentialPersistenceManager.appendToAccount(credential, account);

		account.getCredentials().add(credential);

		return createPrunedDTO(socialMediaAccountRepository.save(account));
	}

	private SocialMediaAccountDTO createPrunedDTO(SocialMediaAccount account) {
		Set<CredentialDTO> credentials = new HashSet<>();

		account.getCredentials().forEach(c -> {
			if (securityService.isCurrentUserPermitted(c, SecuredAction.READ)) {
				credentials.add(c.toDTO());
			}
		});

		return SocialMediaAccountDTO.fromSocialMediaAccount(account, credentials);
	}

	public void deleteSocialMediaAccount(UUID id) {
		SocialMediaAccount account = socialMediaAccountRepository.findById(id).orElseThrow(() -> new SocialMediaAccountNotFoundException(id));
		securityService.checkIfCurrentUserIsPermitted(account, SecuredAction.DELETE);
		try {
			socialMediaAccountRepository.deleteById(id);
		} catch (Exception e) {
			throw new CouldNotDeleteEntityException(
					"Could not delete social media account because there are still posts scheduled to be posted via this account. Please revise them before deleting the account.");
		}
	}

}
