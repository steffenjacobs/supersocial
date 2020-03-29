package me.steffenjacobs.supersocial.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import me.steffenjacobs.supersocial.domain.SocialMediaAccountRepository;
import me.steffenjacobs.supersocial.domain.entity.SecuredAction;
import me.steffenjacobs.supersocial.domain.entity.SocialMediaAccount;
import me.steffenjacobs.supersocial.security.SecurityService;
import me.steffenjacobs.supersocial.service.exception.SocialMediaAccountNotFoundException;

/** @author Steffen Jacobs */
@Component
public class SocialMediaAccountService {
	@Autowired
	private SocialMediaAccountRepository socialMediaAccountRepository;

	@Autowired
	private SecurityService securityService;

	public SocialMediaAccount findById(UUID id) {
		return securityService.filterForCurrentUser(socialMediaAccountRepository.findById(id), SecuredAction.READ).orElseThrow(() -> new SocialMediaAccountNotFoundException(id));
	}

}
