package me.steffenjacobs.supersocial.security;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import me.steffenjacobs.supersocial.domain.AccessControlListRepository;
import me.steffenjacobs.supersocial.domain.SupersocialUserRepository;
import me.steffenjacobs.supersocial.domain.UserGroupRepository;
import me.steffenjacobs.supersocial.domain.dto.UserGroupDTO;
import me.steffenjacobs.supersocial.domain.entity.AccessControlList;
import me.steffenjacobs.supersocial.domain.entity.SecuredAction;
import me.steffenjacobs.supersocial.domain.entity.SupersocialUser;
import me.steffenjacobs.supersocial.domain.entity.UserGroup;
import me.steffenjacobs.supersocial.security.exception.UserNotFoundException;
import me.steffenjacobs.supersocial.security.exception.UserNotInUserGroupException;
import me.steffenjacobs.supersocial.security.exception.UsergroupNotFoundException;
import me.steffenjacobs.supersocial.util.Pair;

/** @author Steffen Jacobs */
@Component
public class UserGroupService {

	@Autowired
	private UserGroupRepository userGroupRepository;

	@Autowired
	private SecurityService securityService;

	@Autowired
	private AccessControlListRepository accessControlListRepository;

	@Autowired
	private SupersocialUserRepository supersocialUserRepository;

	public UserGroup getUserGroup(UUID userGroupId) {
		return securityService.filterForCurrentUser(userGroupRepository.findById(userGroupId), SecuredAction.READ).orElseThrow(() -> new UsergroupNotFoundException(userGroupId));
	}

	public Stream<UserGroupDTO> getAll() {
		return securityService.filterForCurrentUser(StreamSupport.stream(userGroupRepository.findAll().spliterator(), false), SecuredAction.READ).map(UserGroupDTO::fromUserGroup);
	}

	public void deleteUserGroup(UUID userGroupId) {
		UserGroup userGroup = userGroupRepository.findById(userGroupId).orElseThrow(() -> new UsergroupNotFoundException(userGroupId));
		securityService.checkIfCurrentUserIsPermitted(userGroup, SecuredAction.DELETE);

		// delete ACL
		final AccessControlList acl = userGroup.getAccessControlList();
		userGroup.setAccessControlList(null);
		userGroup = userGroupRepository.save(userGroup);
		accessControlListRepository.delete(acl);

		userGroupRepository.delete(userGroup);
	}

	public Pair<UserGroupDTO, Boolean> createOrUpdateUserGroup(UserGroupDTO userGroupDto) {
		UserGroup userGroup;
		if (userGroupDto.getId() == null) {
			userGroup = createUserGroupWithAcl(securityService.getCurrentUser(), userGroupDto.getName());
		} else {
			userGroup = userGroupRepository.findById(userGroupDto.getId()).orElseThrow(() -> new UsergroupNotFoundException(userGroupDto.getId()));
			securityService.checkIfCurrentUserIsPermitted(userGroup, SecuredAction.UPDATE);
		}

		userGroup.setName(userGroupDto.getName());
		boolean created = userGroupDto.getId() == null;
		return new Pair<>(UserGroupDTO.fromUserGroup(userGroupRepository.save(userGroup)), created);
	}

	/** Create and save the a new user group with the given user in it. */
	public UserGroup createUserGroup(SupersocialUser supersocialUser, String userGroupName) {
		UserGroup userGroup = new UserGroup();
		Set<SupersocialUser> users = new HashSet<>();
		users.add(supersocialUser);
		userGroup.setUsers(users);
		userGroup.setName(userGroupName);
		userGroup = userGroupRepository.save(userGroup);
		return userGroup;
	}

	public UserGroupDTO addUserToUserGroup(UUID userId, UUID userGroupId) {

		// no permission check for finding the user. TODO: send invitation to
		// user
		SupersocialUser user = supersocialUserRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
		UserGroup userGroup = userGroupRepository.findById(userGroupId).orElseThrow(() -> new UsergroupNotFoundException(userGroupId));
		securityService.checkIfCurrentUserIsPermitted(userGroup, SecuredAction.UPDATE);

		userGroup.getUsers().add(user);

		return UserGroupDTO.fromUserGroup(userGroupRepository.save(userGroup));
	}

	public UserGroupDTO deleteUserFromUserGroup(UUID userId, UUID userGroupId) {

		SupersocialUser user = supersocialUserRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
		UserGroup userGroup = userGroupRepository.findById(userGroupId).orElseThrow(() -> new UsergroupNotFoundException(userGroupId));
		securityService.checkIfCurrentUserIsPermitted(userGroup, SecuredAction.UPDATE);

		if (!userGroup.getUsers().contains(user)) {
			throw new UserNotInUserGroupException(userId, userGroupId);
		}
		userGroup.getUsers().remove(user);

		return UserGroupDTO.fromUserGroup(userGroupRepository.save(userGroup));
	}

	private UserGroup createUserGroupWithAcl(SupersocialUser user, String userGroupName) {
		UserGroup userGroup = this.createUserGroup(user, userGroupName);
		AccessControlList acl = this.createAclWithUserGroup(userGroup);
		userGroup.setAccessControlList(acl);
		return userGroupRepository.save(userGroup);
	}

	/**
	 * Create an {@link AccessControlList} for the given {@link UserGroup} where
	 * the group itself has all permission on the newly created ACL.
	 * 
	 * @return the newly created {@link AccessControlList}
	 */
	public AccessControlList createUserGroupAcl(UserGroup defaultUserGroup) {
		Map<UserGroup, SecuredAction> permittedActionsGroup = new HashMap<>();
		permittedActionsGroup.put(defaultUserGroup, SecuredAction.ALL);
		AccessControlList aclGroup = new AccessControlList();
		aclGroup.setPermittedActions(permittedActionsGroup);
		aclGroup = accessControlListRepository.save(aclGroup);
		return aclGroup;
	}

	public UserGroup createDefaultUserGroup(SupersocialUser supersocialUser) {
		return createUserGroup(supersocialUser, "default-" + supersocialUser.getName());
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
	public AccessControlList createAclWithUserGroup(UserGroup userGroup) {
		Map<UserGroup, SecuredAction> permittedActionsSupersocialUser = new HashMap<>();
		permittedActionsSupersocialUser.put(userGroup, SecuredAction.ALL);
		AccessControlList aclSupersocialUser = new AccessControlList();
		aclSupersocialUser.setPermittedActions(permittedActionsSupersocialUser);
		aclSupersocialUser = accessControlListRepository.save(aclSupersocialUser);
		return aclSupersocialUser;
	}

}
