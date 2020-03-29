package me.steffenjacobs.supersocial.security;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import javax.persistence.EntityManager;

import org.ollide.spring.discourse.sso.authentication.DiscoursePrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import me.steffenjacobs.supersocial.domain.AccessControlListRepository;
import me.steffenjacobs.supersocial.domain.SupersocialUserRepository;
import me.steffenjacobs.supersocial.domain.entity.AccessControlList;
import me.steffenjacobs.supersocial.domain.entity.LoginProvider;
import me.steffenjacobs.supersocial.domain.entity.Secured;
import me.steffenjacobs.supersocial.domain.entity.SecuredAction;
import me.steffenjacobs.supersocial.domain.entity.StandaloneUser;
import me.steffenjacobs.supersocial.domain.entity.SupersocialUser;
import me.steffenjacobs.supersocial.domain.entity.UserGroup;
import me.steffenjacobs.supersocial.security.exception.AuthorizationException;

/** @author Steffen Jacobs */
@Component
public class SecurityService {

	@Autowired
	private SupersocialUserRepository supersocialUserRepository;

	@Autowired
	private AccessControlListRepository accessControlListRepository;

	@Autowired
	private EntityManager entityManager;
	
	@Autowired
	private UserService userService;

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
			userService.createAclWithDefaultUserGroup(newUser);
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
		if (securedObject == null || securedObject.getAccessControlList() == null || securedObject.getAccessControlList().getPermittedActions() == null) {
			return false;
		}
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

	public void appendAcl(Secured securedObject) {
		// retrieve or create ACL
		AccessControlList acl = securedObject.getAccessControlList();
		if (acl == null) {
			acl = new AccessControlList();
		}

		// retrieve or create permitted actions
		Map<UserGroup, SecuredAction> permittedActions = acl.getPermittedActions();
		if (permittedActions == null) {
			permittedActions = new HashMap<UserGroup, SecuredAction>();
		}
		permittedActions.put(getCurrentUser().getDefaultUserGroup(), SecuredAction.ALL);
		acl.setPermittedActions(permittedActions);
		accessControlListRepository.save(acl);

		// assign ACL to secured object
		securedObject.setAccessControlList(acl);
		entityManager.persist(securedObject);
	}

	public boolean isCurrentUserPermitted(Secured securedObject, SecuredAction actionToPerform) {
		return isPermitted(getCurrentUser(), securedObject, actionToPerform);
	}

	public void checkIfCurrentUserIsPermitted(Secured securedObject, SecuredAction actionToPerform) {
		if (!isCurrentUserPermitted(securedObject, actionToPerform)) {
			throw new AuthorizationException(securedObject.getClass().getSimpleName(), actionToPerform);
		}
	}

	public UserGroup getFirstMatchinUserGroupForCurrentUser(Secured securedObject, SecuredAction actionToPerform) {
		if (securedObject == null || securedObject.getAccessControlList() == null || securedObject.getAccessControlList().getPermittedActions() == null) {
			return null;
		}

		SupersocialUser user = getCurrentUser();
		for (Map.Entry<UserGroup, SecuredAction> entry : securedObject.getAccessControlList().getPermittedActions().entrySet()) {
			if (entry.getKey().getUsers().contains(user)) {
				if (implies(entry.getValue(), actionToPerform)) {
					return entry.getKey();
				}
			}
		}
		return null;
	}

	public <S extends Secured> Stream<S> filterForCurrentUser(Stream<S> stream, SecuredAction actionToPerform) {
		return stream.filter(c -> isCurrentUserPermitted(c, actionToPerform));
	}

	public <S extends Secured> Optional<S> filterForCurrentUser(Optional<S> optional, SecuredAction actionToPerform) {
		return optional.map(c -> isCurrentUserPermitted(c, actionToPerform) ? c : null);
	}
}
