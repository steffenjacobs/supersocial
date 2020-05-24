package me.steffenjacobs.supersocial.endpoints;

import java.util.Arrays;
import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import me.steffenjacobs.supersocial.domain.dto.CurrentUserDTO;
import me.steffenjacobs.supersocial.domain.dto.UserDTO;
import me.steffenjacobs.supersocial.domain.dto.UserGroupDTO;

/** @author Steffen Jacobs */
class OrganizationControllerTest extends UserAwareTest {

	@Test
	void crudUserGroup() {
		String sessionCookie = registerAndLogin();

		UserGroupDTO userGroup = new UserGroupDTO();
		userGroup.setName("test-" + UUID.randomUUID());

		// create a user group
		UserGroupDTO userGroupCreated = sendRequest(sessionCookie, userGroup, HttpMethod.PUT, "/api/organization", UserGroupDTO.class, HttpStatus.CREATED);
		Assertions.assertEquals(userGroup.getName(), userGroupCreated.getName());
		Assertions.assertNotNull(userGroupCreated.getId());
		Assertions.assertNull(userGroupCreated.getError());

		// check if it is still there
		UserGroupDTO[] userGroupsQueried = sendRequest(sessionCookie, null, HttpMethod.GET, "/api/organization", UserGroupDTO[].class, HttpStatus.OK);
		Assertions.assertTrue(Arrays.stream(userGroupsQueried).anyMatch(userGroupCreated::equals));

		// update the user group
		userGroupCreated.setName("test-" + UUID.randomUUID());
		UserGroupDTO userGroupUpdated = sendRequest(sessionCookie, userGroupCreated, HttpMethod.PUT, "/api/organization", UserGroupDTO.class, HttpStatus.ACCEPTED);
		Assertions.assertNotNull(userGroupUpdated.getId());
		Assertions.assertEquals(userGroupCreated.getId(), userGroupUpdated.getId());
		Assertions.assertEquals(userGroupCreated.getName(), userGroupUpdated.getName());

		// check if the updated user group is still there as expected
		userGroupsQueried = sendRequest(sessionCookie, null, HttpMethod.GET, "/api/organization", UserGroupDTO[].class, HttpStatus.OK);
		Assertions.assertTrue(Arrays.stream(userGroupsQueried).anyMatch(userGroupUpdated::equals));

		// delete the user group
		sendRequest(sessionCookie, userGroupCreated, HttpMethod.DELETE, String.format("/api/organization/%s", userGroupUpdated.getId()), UserGroupDTO.class, HttpStatus.NO_CONTENT);

		// check if the updated user group is gone for real
		userGroupsQueried = sendRequest(sessionCookie, null, HttpMethod.GET, "/api/organization", UserGroupDTO[].class, HttpStatus.OK);
		Assertions.assertTrue(Arrays.stream(userGroupsQueried).noneMatch(userGroupUpdated::equals));
	}

	@Test
	void createAndDeleteUserGroupAndAddUser() {
		String sessionCookie = registerAndLogin();
		String sessionCookie2 = registerAndLogin();
		CurrentUserDTO user2 = getUserInfo(sessionCookie2);

		UserGroupDTO userGroup = new UserGroupDTO();
		userGroup.setName("test-" + UUID.randomUUID());

		// create a user group
		UserGroupDTO userGroupCreated = sendRequest(sessionCookie, userGroup, HttpMethod.PUT, "/api/organization", UserGroupDTO.class, HttpStatus.CREATED);

		// add the user to the newly created user group
		UserGroupDTO userGroupUpdated = sendRequest(sessionCookie, userGroup, HttpMethod.PUT,
				String.format("/api/organization/%s/%s", userGroupCreated.getId().toString(), user2.getId().toString()), UserGroupDTO.class, HttpStatus.ACCEPTED);
		Assertions.assertTrue(userGroupUpdated.getUsers().stream().map(UserDTO::getId).anyMatch(i->user2.getId().equals(i)));

		// check if the user is still in the updated group
		UserGroupDTO[] userGroupsQueried = sendRequest(sessionCookie, null, HttpMethod.GET, "/api/organization", UserGroupDTO[].class, HttpStatus.OK);
		Assertions.assertTrue(Arrays.stream(userGroupsQueried).anyMatch(userGroupUpdated::equals));

		// delete user from the group
		sendRequest(sessionCookie, userGroup, HttpMethod.DELETE, String.format("/api/organization/%s/%s", userGroupCreated.getId().toString(), user2.getId().toString()),
				UserGroupDTO.class, HttpStatus.ACCEPTED);

		// make sure the user group is gone for good
		userGroupsQueried = sendRequest(sessionCookie, null, HttpMethod.GET, "/api/organization", UserGroupDTO[].class, HttpStatus.OK);
		Assertions.assertTrue(Arrays.stream(userGroupsQueried).noneMatch(userGroupUpdated::equals));
	}
}
