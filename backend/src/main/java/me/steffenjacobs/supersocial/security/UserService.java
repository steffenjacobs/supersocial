package me.steffenjacobs.supersocial.security;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import me.steffenjacobs.supersocial.domain.AccessControlListRepository;
import me.steffenjacobs.supersocial.domain.StandaloneUserRepository;
import me.steffenjacobs.supersocial.domain.SupersocialUserRepository;
import me.steffenjacobs.supersocial.domain.UserGroupRepository;
import me.steffenjacobs.supersocial.domain.dto.CurrentUserDTO;
import me.steffenjacobs.supersocial.domain.entity.AccessControlList;
import me.steffenjacobs.supersocial.domain.entity.LoginProvider;
import me.steffenjacobs.supersocial.domain.entity.SecuredAction;
import me.steffenjacobs.supersocial.domain.entity.StandaloneUser;
import me.steffenjacobs.supersocial.domain.entity.SupersocialUser;
import me.steffenjacobs.supersocial.domain.entity.UserGroup;
import me.steffenjacobs.supersocial.security.exception.InvalidEmailException;
import me.steffenjacobs.supersocial.security.exception.InvalidPasswordException;
import me.steffenjacobs.supersocial.security.exception.InvalidUsernameException;
import me.steffenjacobs.supersocial.security.exception.UserAlreadyExistsException;

/**
 * The UserService handles creation and loading of {@link SupersocialUser}s and
 * {@link StandaloneUser}s and their respected default user groups as well as
 * permission setups via {@link AccessControlList}s.
 * 
 * @author Steffen Jacobs
 */
@Component
public class UserService implements UserDetailsService {

	private static final Pattern EMAIL_PATTERN = Pattern.compile(
			"(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])");

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

	@Autowired
	private UserGroupService userGroupService;

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
	 * @return a {@link CurrentUserDTO} with the newly initialized user.
	 * 
	 * @throws UserAlreadyExistsException
	 *             if the given {@code displayName} is already associated with
	 *             another user.
	 */
	public CurrentUserDTO registerNewUser(String displayName, String password, String email) {

		if (StringUtils.isEmpty(displayName)) {
			throw new InvalidUsernameException(displayName);
		}

		if (StringUtils.isEmpty(password)) {
			throw new InvalidPasswordException();
		}

		if (StringUtils.isEmpty(email) || !EMAIL_PATTERN.matcher(email).matches()) {
			throw new InvalidEmailException(password);
		}

		if (supersocialUserRepository.findByName(displayName).isPresent()) {
			throw new UserAlreadyExistsException(displayName);
		}
		// create user
		StandaloneUser user = new StandaloneUser();
		user.setEmail(email);
		user.setPassword(bCryptPasswordEncoder.encode(password));
		user.setDisplayName(displayName);
		user.setActive(1);
		user = standaloneUserRepository.save(user);

		// create Supersocial user
		SupersocialUser supersocialUser = new SupersocialUser();
		supersocialUser.setLoginProvider(LoginProvider.SUPERSOCIAL);
		supersocialUser.setExternalId(user.getId().toString());
		supersocialUser.setName(user.getDisplayName());
		supersocialUser = supersocialUserRepository.save(supersocialUser);

		// create default user group for user
		UserGroup defaultUserGroup = userGroupService.createDefaultUserGroup(supersocialUser);
		supersocialUser.setDefaultUserGroup(defaultUserGroup);

		// create ACL for default user group
		AccessControlList aclGroup = userGroupService.createUserGroupAcl(defaultUserGroup);

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
		AccessControlList aclSupersocialUser = userGroupService.createAclWithUserGroup(defaultUserGroup);

		// add Supersocial user ACL to Supersocial user
		supersocialUser.setAccessControlList(aclSupersocialUser);
		supersocialUser = supersocialUserRepository.save(supersocialUser);
		return CurrentUserDTO.fromUser(supersocialUser);
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
		// TODO: use SSO user if user.loginProvider is not set to standalone.
		StandaloneUser sUser = standaloneUserRepository.findById(UUID.fromString(user.getExternalId())).orElseThrow(() -> new UsernameNotFoundException(username));
		return new User(user.getName(), sUser.getPassword(), new HashSet<>());
	}

}
