package me.steffenjacobs.supersocial.security;

import java.util.Optional;

import org.ollide.spring.discourse.sso.authentication.DiscoursePrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import me.steffenjacobs.supersocial.domain.SupersocialUserRepository;
import me.steffenjacobs.supersocial.domain.entity.LoginProvider;
import me.steffenjacobs.supersocial.domain.entity.SupersocialUser;

/** @author Steffen Jacobs */
@Component
public class SecurityService {

	@Autowired
	SupersocialUserRepository supersocialUserRepository;

	public synchronized SupersocialUser getCurrentUser() {
		Object user = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		if (user instanceof SupersocialUser) {
			return (SupersocialUser) user;
		}

		if (user instanceof DiscoursePrincipal) {
			DiscoursePrincipal principal = (DiscoursePrincipal) user;
			Optional<SupersocialUser> currentUser = supersocialUserRepository.findByExternalId(principal.getExternalId());
			if (currentUser.isPresent()) {
				return currentUser.get();
			}

			SupersocialUser newUser = new SupersocialUser();
			newUser.setExternalId(principal.getExternalId());
			newUser.setLoginProvider(LoginProvider.DISCOURSE);
			return supersocialUserRepository.save(newUser);
		}
		
		if(user instanceof User) {
			//Dummy Authentication
			return supersocialUserRepository.findByName("dummy").orElseGet(()->{
				SupersocialUser dummyUser = new SupersocialUser();
				dummyUser.setLoginProvider(LoginProvider.SUPERSOCIAL);
				dummyUser.setName("dummy");
				return supersocialUserRepository.save(dummyUser);
			});
		}

		SupersocialUser anonymousUser = new SupersocialUser();
		anonymousUser.setLoginProvider(LoginProvider.NONE);
		return anonymousUser;
	}
}
