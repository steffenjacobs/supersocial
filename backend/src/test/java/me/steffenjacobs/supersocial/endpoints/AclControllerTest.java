package me.steffenjacobs.supersocial.endpoints;

import java.util.HashSet;
import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import me.steffenjacobs.supersocial.domain.Platform;
import me.steffenjacobs.supersocial.domain.dto.AccessControlListDTO;
import me.steffenjacobs.supersocial.domain.dto.CurrentUserDTO;
import me.steffenjacobs.supersocial.domain.dto.SocialMediaAccountDTO;
import me.steffenjacobs.supersocial.domain.dto.UserGroupDTO;
import me.steffenjacobs.supersocial.domain.entity.SecuredAction;

/** @author Steffen Jacobs */
class AclControllerTest extends UserAwareTest {

	@Test
	void testAddRemoveWithUserGroupFromAcl() {
		String sessionCookie = registerAndLogin();
		String sessionCookie2 = registerAndLogin();

		SocialMediaAccountDTO sma = new SocialMediaAccountDTO();
		sma.setCredentials(new HashSet<>());
		sma.setDisplayName("test-" + UUID.randomUUID());
		sma.setPlatformId(Platform.FACEBOOK.getId());

		// create a secured social media account
		SocialMediaAccountDTO accountCreated = sendRequest(sessionCookie, sma, HttpMethod.PUT, "api/socialmediaaccount", SocialMediaAccountDTO.class, HttpStatus.CREATED);

		// make sure the first user can see the newly created social media
		// account
		SocialMediaAccountDTO accountQueried = sendRequest(sessionCookie, null, HttpMethod.GET, "api/socialmediaaccount/" + accountCreated.getId(), SocialMediaAccountDTO.class,
				HttpStatus.OK);
		Assertions.assertEquals(accountCreated, accountQueried);

		// make sure the second user cannot see the newly created social media
		// account
		sendRequest(sessionCookie2, null, HttpMethod.GET, "api/socialmediaaccount/" + accountCreated.getId(), SocialMediaAccountDTO.class, HttpStatus.NOT_FOUND);

		// create a new user group
		UserGroupDTO userGroup = new UserGroupDTO();
		userGroup.setName("test-" + UUID.randomUUID());
		UserGroupDTO userGroupCreated = sendRequest(sessionCookie, userGroup, HttpMethod.PUT, "/api/organization", UserGroupDTO.class, HttpStatus.CREATED);

		// add the user to the newly created user group
		CurrentUserDTO user2 = getUserInfo(sessionCookie2);
		UserGroupDTO userGroupUpdated = sendRequest(sessionCookie, userGroup, HttpMethod.PUT,
				String.format("/api/organization/%s/%s", userGroupCreated.getId().toString(), user2.getId().toString()), UserGroupDTO.class, HttpStatus.ACCEPTED);

		// permit the newly created user group with SecuredAction.READ
		// permissions
		AccessControlListDTO aclUpdated = sendRequest(sessionCookie, null, HttpMethod.PUT,
				String.format("/api/security/acl/%s/%s/%s", accountQueried.getAclId(), userGroupUpdated.getId(), SecuredAction.READ.getMask()), AccessControlListDTO.class,
				HttpStatus.ACCEPTED);
		Assertions.assertEquals(aclUpdated.getId(), accountQueried.getAclId());
		Assertions.assertTrue(aclUpdated.getPermittedActions().containsKey(userGroupUpdated.getId()));
		Assertions.assertEquals(SecuredAction.READ.getMask(), aclUpdated.getPermittedActions().get(userGroupUpdated.getId()));
		Assertions.assertNull(aclUpdated.getError());

		// check if the updated ACL is still there and updated as expected
		AccessControlListDTO aclQueried = sendRequest(sessionCookie, null, HttpMethod.GET, String.format("/api/security/acl/%s", accountQueried.getAclId()),
				AccessControlListDTO.class, HttpStatus.OK);
		Assertions.assertEquals(aclUpdated, aclQueried);

		// check if the ACl attached to the social media account DTO had been
		// updated as well
		SocialMediaAccountDTO accountQueriedUpdated = sendRequest(sessionCookie, null, HttpMethod.GET, "api/socialmediaaccount/" + accountCreated.getId(),
				SocialMediaAccountDTO.class, HttpStatus.OK);
		Assertions.assertEquals(aclQueried.getId(), accountQueriedUpdated.getAclId());
		Assertions.assertEquals(aclQueried.getPermittedActions(), accountQueriedUpdated.getAcl());

		// check if user 2 now has access
		SocialMediaAccountDTO accountQueriedUpdated2 = sendRequest(sessionCookie2, null, HttpMethod.GET, "api/socialmediaaccount/" + accountCreated.getId(),
				SocialMediaAccountDTO.class, HttpStatus.OK);
		Assertions.assertEquals(aclQueried.getId(), accountQueriedUpdated2.getAclId());
		Assertions.assertEquals(aclQueried.getPermittedActions(), accountQueriedUpdated2.getAcl());

		// remove the user group from the ACL
		sendRequest(sessionCookie, null, HttpMethod.DELETE,
				String.format("/api/security/acl/%s/%s/%s", accountQueried.getAclId(), userGroupUpdated.getId(), SecuredAction.READ.getMask()), AccessControlListDTO.class,
				HttpStatus.ACCEPTED);

		// check if the updated ACL has been updated and the user group is no
		// longer on it
		aclUpdated.getPermittedActions().remove(userGroupUpdated.getId());
		AccessControlListDTO aclQueriedRemoved = sendRequest(sessionCookie, null, HttpMethod.GET, String.format("/api/security/acl/%s", accountQueried.getAclId()),
				AccessControlListDTO.class, HttpStatus.OK);
		Assertions.assertEquals(aclUpdated, aclQueriedRemoved);

		// check if the ACl attached to the social media account DTO had been
		// updated as well
		SocialMediaAccountDTO accountQueriedUpdatedRemoved = sendRequest(sessionCookie, null, HttpMethod.GET, "api/socialmediaaccount/" + accountCreated.getId(),
				SocialMediaAccountDTO.class, HttpStatus.OK);
		Assertions.assertEquals(aclQueried.getId(), accountQueriedUpdatedRemoved.getAclId());
		Assertions.assertEquals(aclQueriedRemoved.getPermittedActions(), accountQueriedUpdatedRemoved.getAcl());

		// check if user 2 no longer has access
		sendRequest(sessionCookie2, null, HttpMethod.GET, "api/socialmediaaccount/" + accountCreated.getId(), SocialMediaAccountDTO.class, HttpStatus.NOT_FOUND);
	}

}
