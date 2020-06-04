package me.steffenjacobs.supersocial.security;

import java.util.Date;
import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import me.steffenjacobs.supersocial.domain.StandaloneUserRepository;
import me.steffenjacobs.supersocial.domain.SupersocialUserRepository;
import me.steffenjacobs.supersocial.domain.dto.CurrentUserDTO;
import me.steffenjacobs.supersocial.domain.entity.AccessControlList;
import me.steffenjacobs.supersocial.domain.entity.LoginProvider;
import me.steffenjacobs.supersocial.domain.entity.SecuredAction;
import me.steffenjacobs.supersocial.domain.entity.SecuredType;
import me.steffenjacobs.supersocial.domain.entity.StandaloneUser;
import me.steffenjacobs.supersocial.domain.entity.SupersocialUser;
import me.steffenjacobs.supersocial.domain.entity.UserGroup;
import me.steffenjacobs.supersocial.security.exception.InvalidEmailException;
import me.steffenjacobs.supersocial.security.exception.InvalidPasswordException;
import me.steffenjacobs.supersocial.security.exception.InvalidUsernameException;
import me.steffenjacobs.supersocial.security.exception.UserAlreadyExistsException;

/** @author Steffen Jacobs */
@SpringBootTest
public class UserServiceTest {

	@Autowired
	private UserService userService;

	@Autowired
	private SupersocialUserRepository supersocialUserRepository;

	@Autowired
	private StandaloneUserRepository standaloneUserRepository;

	@Autowired
	private SecurityService securityService;

	@Test
	public void registerNewUser() {
		String username = generateTestUserName();
		String password = "test";
		String email = "em@i.li";
		CurrentUserDTO createdUser = userService.registerNewUser(username, password, email);
		Assertions.assertEquals(username, createdUser.getUsername(), "Expected different username.");
		Assertions.assertEquals(LoginProvider.SUPERSOCIAL.getId(), createdUser.getProviderId(), "Expected different login provider.");

		// check login
		UserDetails user = userService.loadUserByUsername(username);
		Assertions.assertEquals(username, user.getUsername(), "Expected different username.");
		Assertions.assertNotEquals(password, user.getPassword(), "Expected password to be not stored in plain text.");

		// check entities
		SupersocialUser supUser = supersocialUserRepository.findByName(username).orElseThrow(() -> new UsernameNotFoundException(username));
		Assertions.assertEquals(LoginProvider.SUPERSOCIAL, supUser.getLoginProvider(), "Expected different login provider.");
		Assertions.assertEquals(username, supUser.getName(), "Expected different username.");
		Assertions.assertEquals(SecuredType.SUPERSOCIAL_USER, supUser.getSecuredType(), "Expected different secured type.");
		assertCreationDate(supUser.getCreated());

		AccessControlList acl = supUser.getAccessControlList();
		UserGroup defaultUserGroup = supUser.getDefaultUserGroup();
		assertAcl(acl, defaultUserGroup);
		assertAcl(defaultUserGroup.getAccessControlList(), defaultUserGroup);

		Assertions.assertNotNull(defaultUserGroup.getName(), "Expected default user group to actually have a name.");
		Assertions.assertNotNull(defaultUserGroup.getUsers(), "Expected default user group to contain users.");
		Assertions.assertTrue(defaultUserGroup.getUsers().contains(supUser), "Expected default user group to contain the created user.");

		Assertions.assertNotNull(supUser.getUserConfigurations(), "Expected default user configuration to be not null.");

		StandaloneUser sUser = standaloneUserRepository.findById(UUID.fromString(supUser.getExternalId())).orElseThrow(() -> new UsernameNotFoundException(username));
		assertAcl(sUser.getAccessControlList(), defaultUserGroup);
		Assertions.assertEquals(1, sUser.getActive(), "Expected user to be set active.");
		assertCreationDate(sUser.getCreated());
		Assertions.assertEquals(username, sUser.getDisplayName(), "Expected different username.");
		Assertions.assertEquals(email, sUser.getEmail(), "Expected different email address.");
		Assertions.assertNotEquals(password, sUser.getPassword(), "Expected password to be not stored in plain text.");
		Assertions.assertEquals(SecuredType.USER, sUser.getSecuredType(), "Expected different secured type.");

	}

	@Test
	public void registerUserTwice() {
		String username = generateTestUserName();
		String password = "test2";
		String email = "em@i.li";
		userService.registerNewUser(username, password, email);
		Assertions.assertThrows(UserAlreadyExistsException.class, () -> userService.registerNewUser(username, password, email), "Expected user to already exist.");
	}

	@Test
	public void registerUserWithEmptyUsername() {
		Assertions.assertThrows(InvalidUsernameException.class, () -> userService.registerNewUser("", "test", "em@i.li"), "Expected username to be invalid.");
	}

	@Test
	public void registerUserWithEmptyPassword() {
		Assertions.assertThrows(InvalidPasswordException.class, () -> userService.registerNewUser(generateTestUserName(), "", "em@i.li"), "Expected password to be invalid.");
	}

	@Test
	public void registerUserWithEmptyEmail() {
		Assertions.assertThrows(InvalidEmailException.class, () -> userService.registerNewUser(generateTestUserName(), "test", ""), "Expected email to be invalid.");
	}

	@Test
	public void registerUserWithInvalidEmail() {
		Assertions.assertThrows(InvalidEmailException.class, () -> userService.registerNewUser(generateTestUserName(), "test", "user@"), "Expected email to be invalid.");
		Assertions.assertThrows(InvalidEmailException.class, () -> userService.registerNewUser(generateTestUserName(), "test", "user"), "Expected email to be invalid.");
		Assertions.assertThrows(InvalidEmailException.class, () -> userService.registerNewUser(generateTestUserName(), "test", "user@test."), "Expected email to be invalid.");
		Assertions.assertThrows(InvalidEmailException.class, () -> userService.registerNewUser(generateTestUserName(), "test", "user@test"));
		Assertions.assertThrows(InvalidEmailException.class, () -> userService.registerNewUser(generateTestUserName(), "test", "user.test.de"), "Expected email to be invalid.");
		Assertions.assertThrows(InvalidEmailException.class, () -> userService.registerNewUser(generateTestUserName(), "test", "@test.de"), "Expected email to be invalid.");
		Assertions.assertThrows(InvalidEmailException.class, () -> userService.registerNewUser(generateTestUserName(), "test", "test.de"), "Expected email to be invalid.");
	}

	private String generateTestUserName() {
		return "user-" + UUID.randomUUID().toString();
	}

	private void assertCreationDate(Date creationDate) {
		Assertions.assertNotNull(creationDate, "Expected creation date to exist.");
		Assertions.assertTrue(new Date().after(creationDate), "Expected creation date to be in the past.");
	}

	private void assertAcl(AccessControlList acl, UserGroup userGroup) {
		Assertions.assertNotNull(acl, "Expected ACL to exist.");
		assertCreationDate(acl.getCreated());
		Assertions.assertNotNull(userGroup, "Expected user group to exist.");
		assertCreationDate(userGroup.getCreated());
		Assertions.assertEquals(SecuredType.USER_GROUP, userGroup.getSecuredType());
		Assertions.assertNotNull(acl.getPermittedActions(), "Expected ACL to contain permitted actions.");
		Assertions.assertTrue(acl.getPermittedActions().containsKey(userGroup), "Expected permitted actions on ACL to contain given user group.");
		Assertions.assertTrue(securityService.implies(acl.getPermittedActions().get(userGroup), SecuredAction.ALL),
				"Expected permitted actions on ACL associated to given user group to imply SecuredAction.ALL.");
	}

}
