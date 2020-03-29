package me.steffenjacobs.supersocial.security;

import java.util.Map;
import java.util.Optional;

import org.ollide.spring.discourse.sso.authentication.DiscoursePrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import me.steffenjacobs.supersocial.domain.SupersocialUserRepository;
import me.steffenjacobs.supersocial.domain.entity.LoginProvider;
import me.steffenjacobs.supersocial.domain.entity.Secured;
import me.steffenjacobs.supersocial.domain.entity.SecuredAction;
import me.steffenjacobs.supersocial.domain.entity.StandaloneUser;
import me.steffenjacobs.supersocial.domain.entity.SupersocialUser;
import me.steffenjacobs.supersocial.domain.entity.UserGroup;

/** @author Steffen Jacobs */
@Component
public class SecurityService {

	@Autowired
	private SupersocialUserRepository supersocialUserRepository;

	public SupersocialUser getCurrentUser() {
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

		if (user instanceof User) {
			return supersocialUserRepository.findByName(((User) user).getUsername()).orElse(createAnonymousUser());
		}

		if (user instanceof StandaloneUser) {
			return supersocialUserRepository.findByExternalId(((StandaloneUser) user).getId().toString()).orElse(createAnonymousUser());
		}

		return createAnonymousUser();
	}

	private SupersocialUser createAnonymousUser() {
		SupersocialUser anonymousUser = new SupersocialUser();
		anonymousUser.setLoginProvider(LoginProvider.NONE);
		return anonymousUser;
	}

	public boolean isPermitted(SupersocialUser user, Secured securedObject, SecuredAction actionToPerform) {
		for (Map.Entry<UserGroup, SecuredAction> entry : securedObject.getAccessControlList().getPermittedActions().entrySet()) {
			if (entry.getKey().getUsers().contains(user)) {
				if (implies(entry.getValue(), actionToPerform)) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean implies(SecuredAction action, SecuredAction impliedAction) {
		return (impliedAction.getMask() & action.getMask()) == impliedAction.getMask();
	}
}
