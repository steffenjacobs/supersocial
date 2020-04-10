package me.steffenjacobs.supersocial.security;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import me.steffenjacobs.supersocial.domain.AccessControlListRepository;
import me.steffenjacobs.supersocial.domain.StandaloneUserRepository;
import me.steffenjacobs.supersocial.domain.SupersocialUserRepository;
import me.steffenjacobs.supersocial.domain.UserGroupRepository;
import me.steffenjacobs.supersocial.domain.dto.SupersocialUserDTO;
import me.steffenjacobs.supersocial.domain.entity.AccessControlList;
import me.steffenjacobs.supersocial.domain.entity.LoginProvider;
import me.steffenjacobs.supersocial.domain.entity.SecuredAction;
import me.steffenjacobs.supersocial.domain.entity.StandaloneUser;
import me.steffenjacobs.supersocial.domain.entity.SupersocialUser;
import me.steffenjacobs.supersocial.domain.entity.UserGroup;

/**
 * The UserService handles creation and loading of {@link SupersocialUser}s and
 * {@link StandaloneUser}s and their respected default user groups as well as
 * permission setups via {@link AccessControlList}s.
 * 
 * @author Steffen Jacobs
 */
@Component
public class UserService implements UserDetailsService {

	@Autowired
	private StandaloneUserRepository standaloneUserRepository;

	@Autowired
	private SupersocialUserRepository supersocialUserRepository;

	@Autowired
	private AccessControlListRepository accessControlListRepository;

	@Autowired
	private UserGroupRepository userGroupRepository;

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	/**
	 * Create a new user. This will
	 * <ul>
	 * <li>Create the {@link StandaloneUser} object with the given
	 * {@code displayName}, {@code password} and {@code email}</li>
	 * <li>Create a correctly initialized {@link AccessControlList} for the
	 * newly created {@link StandaloneUser}</li>
	 * <li>Create the default {@link UserGroup} for the newly created user</li>
	 * <li>Create a correctly initialized {@link AccessControlList} for the
	 * default {@link UserGroup}</li>
	 * <li>Create a {@link SupersocialUser} to link the newly created
	 * {@link StandaloneUser} to</li>
	 * <li>Create a correctly initialized {@link AccessControlList} for the
	 * newly created {@link SupersocialUser}</li>
	 * </ul>
	 * 
	 * @return a {@link SupersocialUserDTO} with the newly initialized user.
	 */
	public SupersocialUserDTO registerNewUser(String displayName, String password, String email) {
		// create user
		StandaloneUser user = new StandaloneUser();
		user.setEmail(email);
		user.setPassword(bCryptPasswordEncoder.encode(password));
		user.setDisplayName(displayName);
		user = standaloneUserRepository.save(user);

		// create Supersocial user
		SupersocialUser supersocialUser = new SupersocialUser();
		supersocialUser.setLoginProvider(LoginProvider.SUPERSOCIAL);
		supersocialUser.setExternalId(user.getId().toString());
		supersocialUser.setName(user.getDisplayName());
		supersocialUser = supersocialUserRepository.save(supersocialUser);

		// create default user group for user
		UserGroup defaultUserGroup = createDefaultUserGroup(supersocialUser);
		supersocialUser.setDefaultUserGroup(defaultUserGroup);

		// create ACL for default user group
		AccessControlList aclGroup = createUserGroupAcl(defaultUserGroup);

		// add user group ACL to user group
		defaultUserGroup.setAccessControlList(aclGroup);
		defaultUserGroup = userGroupRepository.save(defaultUserGroup);

		// create ACL for user
		Map<UserGroup, SecuredAction> permittedActions = new HashMap<>();
		permittedActions.put(defaultUserGroup, SecuredAction.ALL);
		AccessControlList aclUser = new AccessControlList();
		aclUser.setPermittedActions(permittedActions);
		aclUser = accessControlListRepository.save(aclUser);

		// add user ACL to user
		user.setAccessControlList(aclUser);
		user = standaloneUserRepository.save(user);

		// create ACL for Supersocial user
		AccessControlList aclSupersocialUser = createAclWithUserGroup(defaultUserGroup);

		// add Supersocial user ACL to Supersocial user
		supersocialUser.setAccessControlList(aclSupersocialUser);
		supersocialUser = supersocialUserRepository.save(supersocialUser);
		return SupersocialUserDTO.fromSupersocialUser(supersocialUser);
	}

	/**
	 * Create an {@link AccessControlList} for the given {@link UserGroup} where
	 * the group itself has all permission on the newly created ACL.
	 * 
	 * @return the newly created {@link AccessControlList}
	 */
	private AccessControlList createUserGroupAcl(UserGroup defaultUserGroup) {
		Map<UserGroup, SecuredAction> permittedActionsGroup = new HashMap<>();
		permittedActionsGroup.put(defaultUserGroup, SecuredAction.ALL);
		AccessControlList aclGroup = new AccessControlList();
		aclGroup.setPermittedActions(permittedActionsGroup);
		aclGroup = accessControlListRepository.save(aclGroup);
		return aclGroup;
	}

	/**
	 * Create an {@link AccessControlList} for the given
	 * {@link SupersocialUser}. Therefore, creates a default {@link UserGroup}
	 * and the associated {@link AccessControlList} first to be put on this ACL
	 * for the {@link SupersocialUser}
	 * 
	 */
	public void createAclWithDefaultUserGroup(SupersocialUser supersocialUser) {
		UserGroup defaultUserGroup = createDefaultUserGroup(supersocialUser);
		defaultUserGroup.setAccessControlList(createUserGroupAcl(defaultUserGroup));
		defaultUserGroup = userGroupRepository.save(defaultUserGroup);

		supersocialUser.setAccessControlList(createAclWithUserGroup(defaultUserGroup));
		supersocialUser.setDefaultUserGroup(defaultUserGroup);
	}

	/**
	 * Create an {@link AccessControlList} for the given default
	 * {@link UserGroup} granting all permissions to the given default
	 * {@link UserGroup}
	 * 
	 * @return the newly created {@link AccessControlList} for the
	 *         {@link SupersocialUser} with the newly created default
	 *         {@link UserGroup}.
	 */
	private AccessControlList createAclWithUserGroup(UserGroup defaultUserGroup) {
		Map<UserGroup, SecuredAction> permittedActionsSupersocialUser = new HashMap<>();
		permittedActionsSupersocialUser.put(defaultUserGroup, SecuredAction.ALL);
		AccessControlList aclSupersocialUser = new AccessControlList();
		aclSupersocialUser.setPermittedActions(permittedActionsSupersocialUser);
		aclSupersocialUser = accessControlListRepository.save(aclSupersocialUser);
		return aclSupersocialUser;
	}

	private UserGroup createDefaultUserGroup(SupersocialUser supersocialUser) {
		UserGroup defaultUserGroup = new UserGroup();
		Set<SupersocialUser> users = new HashSet<>();
		users.add(supersocialUser);
		defaultUserGroup.setUsers(users);
		defaultUserGroup.setName("default-" + supersocialUser.getName());
		defaultUserGroup = userGroupRepository.save(defaultUserGroup);
		return defaultUserGroup;
	}

	/**
	 * Loads a user by it's associated {@code username}. First loads the
	 * {@link SupersocialUser} and then merges it with the associated user (e.g.
	 * a {@code StandaloneUser}).
	 * 
	 * @return the merged {@link UserDetails} based on the
	 *         {@link SupersocialUser} and its descendent.
	 * 
	 * @throws UsernameNotFoundException
	 *             if the given {@code username} could not be found.s
	 */
	@Override
	@Transactional(readOnly = true)
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		SupersocialUser user = supersocialUserRepository.findByName(username).orElseThrow(() -> new UsernameNotFoundException(username));
		// TODO: user SSO user if user.loginProvider is not set to standalone.
		StandaloneUser sUser = standaloneUserRepository.findById(UUID.fromString(user.getExternalId())).orElseThrow(() -> new UsernameNotFoundException(username));
		return new User(user.getName(), sUser.getPassword(), new HashSet<>());
	}

}
