package me.steffenjacobs.supersocial.security;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

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

/**
 * Handles security checks for secured objects with and without
 * {@link AuthorizationException}. Can also retrieve the user currently logged
 * in.
 * 
 * @author Steffen Jacobs
 */
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

	/**
	 * Fetches the currently logged in {@link SupersocialUser}. <br/>
	 * <b>Attention</b>: This method is not thread-safe. <br/>
	 * <b>Attention</b>: This method relies on ThreadLocal and will not return a
	 * user if called from a different thread than the request was initiated
	 * from.
	 * 
	 * @return the currently logged in {@link SupersocialUser} or an anonymous
	 *         user.
	 */
	public SupersocialUser getCurrentUser() {
		if (SecurityContextHolder.getContext().getAuthentication() == null) {
			return createAnonymousUser();
		}
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

	/**
	 * @return a newly created, not persisted anonymous user without a
	 *         {@link LoginProvider}.
	 */
	private SupersocialUser createAnonymousUser() {
		SupersocialUser anonymousUser = new SupersocialUser();
		anonymousUser.setName("Anonymous");
		anonymousUser.setLoginProvider(LoginProvider.NONE);
		return anonymousUser;
	}

	/**
	 * Performs a permission check for the given {@link SupersocialUser} on the
	 * given {@link Secured} object for the given {@link SecuredAction}. <br/>
	 * Checks, whether the given user is part of any {@link UserGroup} that has
	 * the required permission to perform the given action on the given secured
	 * object.
	 * 
	 * @return true if the {@link SupersocialUser user } is permitted to perform
	 *         the {@link SecuredAction} on the {@link Secured} object.
	 */
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

	/**
	 * Checks if the given {@link SecuredAction action} implies another given
	 * {@link SecuredAction}.<br/>
	 * If one action implies another one, this means that any user which is
	 * permitted to perform the implying action will also be permitted to
	 * perform the implied action.
	 * 
	 * @return true if the first action implies the second action.
	 */
	public boolean implies(SecuredAction action, SecuredAction impliedAction) {
		return (impliedAction.getMask() & action.getMask()) == impliedAction.getMask();
	}

	/**
	 * Append a fully-fledged {@link AccessControlList} to an existing
	 * {@link Secured} object. The current user will have all permissions for
	 * this object.
	 */
	@Transactional
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

	/**
	 * Performs the permission check
	 * {@link SecurityService.isPermitted(SupersocialUser, Secured,
	 * SecuredAction} for the current user.
	 * 
	 * @return true if the current user is permitted to perform the given
	 *         {@link SecuredAction} on the given {@link Secured secured
	 *         object}.
	 */
	public boolean isCurrentUserPermitted(Secured securedObject, SecuredAction actionToPerform) {
		return isPermitted(getCurrentUser(), securedObject, actionToPerform);
	}

	/**
	 * Performs the permission check
	 * {@link SecurityService.isPermitted(SupersocialUser, Secured,
	 * SecuredAction} for the current user and throws an exception if the check
	 * failed.
	 * 
	 * @throws AuthorizationException
	 *             if the current user ist not permitted to perform the given
	 *             {@link SecuredAction action} on the given {@link Secured
	 *             secured object}.
	 */
	public void checkIfCurrentUserIsPermitted(Secured securedObject, SecuredAction actionToPerform) {
		if (!isCurrentUserPermitted(securedObject, actionToPerform)) {
			throw new AuthorizationException(securedObject.getClass().getSimpleName(), actionToPerform);
		}
	}

	/**
	 * Fetches the first user group the current user is part of that is
	 * permitted to perform the given {@link SecuredAction} on the given
	 * {@link Secured secured object}. For display purposes only.
	 * 
	 * @return the first user group with the current user in it which is allowed
	 *         to execute the given {@link SecuredAction action} on the given
	 *         {@link Secured object}.
	 */
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

	/**
	 * Filter a stream of {@link Secured} objects by the given
	 * {@link SecuredAction} for the current user.
	 * 
	 * @return a stream where all {@link Secured} objects the current user is
	 *         not permitted to execute the given {@link SecuredAction action}
	 *         on are filtered out.
	 */
	public <S extends Secured> Stream<S> filterForCurrentUser(Stream<S> stream, SecuredAction actionToPerform) {
		return stream.filter(c -> isCurrentUserPermitted(c, actionToPerform));
	}

	/**
	 * Filter an optional with potentially a {@link Secured} object inside by
	 * {@link SecuredAction} for the current user.
	 * 
	 * @return an optional which is empty if the current user is not permitted
	 *         to execute the given {@link SecuredAction action} on the original
	 *         {@link Secured} object in it.
	 */
	public <S extends Secured> Optional<S> filterForCurrentUser(Optional<S> optional, SecuredAction actionToPerform) {
		return optional.map(c -> isCurrentUserPermitted(c, actionToPerform) ? c : null);
	}
}
