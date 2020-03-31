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
		boolean created = false;
		if (creationDto.getId() != null) {
			optAccount = socialMediaAccountRepository.findById(creationDto.getId());
			optAccount.ifPresent(a -> securityService.checkIfCurrentUserIsPermitted(a, SecuredAction.UPDATE));
		}
		SocialMediaAccount acc = optAccount.orElse(createNewSocialMediaAccount(Platform.fromId(creationDto.getPlatformId()), creationDto.getDisplayName()));
		created = acc.getId() == null;
		return new Pair<>(created, createPrunedDTO(acc));
	}

	private SocialMediaAccount createNewSocialMediaAccount(Platform platform, String displayName) {
		SocialMediaAccount account = new SocialMediaAccount();
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
		// permission + existance check already done

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

}
