package me.steffenjacobs.supersocial.security;

import java.util.Optional;

import org.ollide.spring.discourse.sso.authentication.DiscoursePrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import me.steffenjacobs.supersocial.domain.SupersocialUserRepository;
import me.steffenjacobs.supersocial.domain.entity.SupersocialUser;

/** @author Steffen Jacobs */
@Component
public class SecurityService {

	@Autowired
	SupersocialUserRepository supersocialUserRepository;

	public synchronized SupersocialUser getCurrentUser() {
		DiscoursePrincipal principal = (DiscoursePrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Optional<SupersocialUser> currentUser = supersocialUserRepository.findByExternalId(principal.getExternalId());
		if (currentUser.isPresent()) {
			return currentUser.get();
		}

		SupersocialUser newUser = new SupersocialUser();
		newUser.setExternalId(principal.getExternalId());
		return supersocialUserRepository.save(newUser);
	}
}
