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

/** @author Steffen Jacobs */
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

	public SupersocialUserDTO registerNewUser(String displayName, String password, String email) {
		// create user
		StandaloneUser user = new StandaloneUser();
		user.setEmail(email);
		user.setPassword(bCryptPasswordEncoder.encode(password));
		user.setDisplayName(displayName);
		user = standaloneUserRepository.save(user);
		
		//create Supersocial user
		SupersocialUser supersocialUser = new SupersocialUser();
		supersocialUser.setLoginProvider(LoginProvider.SUPERSOCIAL);
		supersocialUser.setExternalId(user.getId().toString());
		supersocialUser.setName(user.getDisplayName());
		supersocialUser = supersocialUserRepository.save(supersocialUser);

		// create default user group for user
		UserGroup defaultUserGroup = new UserGroup();
		Set<SupersocialUser> users = new HashSet<>();
		users.add(supersocialUser);
		defaultUserGroup.setUsers(users);
		defaultUserGroup.setName("default-" + displayName);
		defaultUserGroup = userGroupRepository.save(defaultUserGroup);
		
		supersocialUser.setDefaultUserGroup(defaultUserGroup);
		
		//create ACL for default user group
		Map<UserGroup, SecuredAction> permittedActionsGroup = new HashMap<>();
		permittedActionsGroup.put(defaultUserGroup, SecuredAction.ALL);
		AccessControlList aclGroup = new AccessControlList();
		aclGroup.setPermittedActions(permittedActionsGroup);
		aclGroup = accessControlListRepository.save(aclGroup);
		
		//add user group ACL to user group
		defaultUserGroup.setAccessControlList(aclGroup);
		defaultUserGroup = userGroupRepository.save(defaultUserGroup);
		
		//create ACL for user
		Map<UserGroup, SecuredAction> permittedActions = new HashMap<>();
		permittedActions.put(defaultUserGroup, SecuredAction.ALL);
		AccessControlList aclUser = new AccessControlList();
		aclUser.setPermittedActions(permittedActions);
		aclUser = accessControlListRepository.save(aclUser);
		
		//add user ACL to user
		user.setAccessControlList(aclUser);
		user = standaloneUserRepository.save(user);
		
		//create ACL for Supersocial user
		Map<UserGroup, SecuredAction> permittedActionsSupersocailUser = new HashMap<>();
		permittedActionsSupersocailUser.put(defaultUserGroup, SecuredAction.ALL);
		AccessControlList aclSupersocialUser = new AccessControlList();
		aclSupersocialUser.setPermittedActions(permittedActionsSupersocailUser);
		aclSupersocialUser = accessControlListRepository.save(aclSupersocialUser);
		
		//add Supersocial user ACL to Supersocial user
		supersocialUser.setAccessControlList(aclSupersocialUser);
		supersocialUser = supersocialUserRepository.save(supersocialUser);
		return SupersocialUserDTO.fromSupersocialUser(supersocialUser);
	}

	@Override
	@Transactional(readOnly = true)
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		SupersocialUser user = supersocialUserRepository.findByName(username).orElseThrow(() -> new UsernameNotFoundException(username));
		StandaloneUser sUser = standaloneUserRepository.findById(UUID.fromString(user.getExternalId())).orElseThrow(() -> new UsernameNotFoundException(username));
		return new User(user.getName(), sUser.getPassword(), new HashSet<>());
	}

}
