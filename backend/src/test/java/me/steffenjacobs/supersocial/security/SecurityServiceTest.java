package me.steffenjacobs.supersocial.security;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import me.steffenjacobs.supersocial.domain.entity.AccessControlList;
import me.steffenjacobs.supersocial.domain.entity.SecuredType;
import me.steffenjacobs.supersocial.domain.entity.Secured;
import me.steffenjacobs.supersocial.domain.entity.SecuredAction;
import me.steffenjacobs.supersocial.domain.entity.SupersocialUser;
import me.steffenjacobs.supersocial.domain.entity.UserGroup;
import me.steffenjacobs.supersocial.util.Pair;

/** @author Steffen Jacobs */
class SecurityServiceTest {

	private static Set<Pair<SecuredAction, SecuredAction>> allowedImplications;

	@BeforeAll
	private static void setupAllowedImplications() {
		allowedImplications = new HashSet<>();
		allowedImplications.add(new Pair<>(SecuredAction.ALL, SecuredAction.CREATE));
		allowedImplications.add(new Pair<>(SecuredAction.ALL_NO_ACL, SecuredAction.CREATE));

		allowedImplications.add(new Pair<>(SecuredAction.UPDATE, SecuredAction.READ));
		allowedImplications.add(new Pair<>(SecuredAction.DELETE, SecuredAction.READ));
		allowedImplications.add(new Pair<>(SecuredAction.UPDATE_ACL, SecuredAction.READ));
		allowedImplications.add(new Pair<>(SecuredAction.ALL, SecuredAction.READ));
		allowedImplications.add(new Pair<>(SecuredAction.ALL_NO_ACL, SecuredAction.READ));

		allowedImplications.add(new Pair<>(SecuredAction.DELETE, SecuredAction.UPDATE));
		allowedImplications.add(new Pair<>(SecuredAction.ALL, SecuredAction.UPDATE));
		allowedImplications.add(new Pair<>(SecuredAction.ALL_NO_ACL, SecuredAction.UPDATE));

		allowedImplications.add(new Pair<>(SecuredAction.ALL, SecuredAction.DELETE));
		allowedImplications.add(new Pair<>(SecuredAction.ALL_NO_ACL, SecuredAction.DELETE));

		allowedImplications.add(new Pair<>(SecuredAction.ALL, SecuredAction.UPDATE_ACL));

		allowedImplications.add(new Pair<>(SecuredAction.ALL, SecuredAction.ALL_NO_ACL));
	}

	@Test
	void permissionImpliesCheckSame() {
		SecurityService securityService = new SecurityService();
		for (SecuredAction sa : SecuredAction.values()) {
			assertTrue(securityService.implies(sa, sa));
		}
	}

	@Test
	void permissionImpliesImplicit() {
		SecurityService securityService = new SecurityService();
		for (SecuredAction sa : SecuredAction.values()) {
			for (SecuredAction sa2 : SecuredAction.values()) {
				if (sa == sa2 || allowedImplications.contains(new Pair<>(sa, sa2))) {
					assertTrue(securityService.implies(sa, sa2));
				} else {
					assertFalse(securityService.implies(sa, sa2));
				}
			}
		}
	}

	@Test
	void permissionImpliesCheckAll() {
		SecurityService securityService = new SecurityService();
		for (SecuredAction sa : SecuredAction.values()) {
			assertTrue(securityService.implies(SecuredAction.ALL, sa));
		}
	}

	@Test
	void permissionImpliesCheckAllWithoutAcl() {
		SecurityService securityService = new SecurityService();
		for (SecuredAction sa : SecuredAction.values()) {
			if (sa == SecuredAction.ALL || sa == SecuredAction.UPDATE_ACL) {
				assertFalse(securityService.implies(SecuredAction.ALL_NO_ACL, sa));
			} else {
				assertTrue(securityService.implies(SecuredAction.ALL_NO_ACL, sa));
			}
		}
	}

	@Test
	void isPermittedCheckEmptyList() {
		SecurityService securityService = new SecurityService();

		Secured securedObject = createSecuredObject(new AccessControlList());

		SupersocialUser user = new SupersocialUser();

		for (SecuredAction sa : SecuredAction.values()) {
			assertFalse(securityService.isPermitted(user, securedObject, sa));
		}
	}
	
	@Test
	void isPermittedCheckPermittedSimple() {
		SecurityService securityService = new SecurityService();
		
		
		SupersocialUser user = new SupersocialUser();
		UserGroup userGroup = createUserGroup(user);
		
		AccessControlList acl = new AccessControlList();
		Map<UserGroup, SecuredAction> permittedActions = new HashMap<>();
		permittedActions.put(userGroup, SecuredAction.READ);
		acl.setPermittedActions(permittedActions);
		
		Secured securedObject = createSecuredObject(acl);
		
		for (SecuredAction sa : SecuredAction.values()) {
			if(sa == SecuredAction.READ) {
				assertTrue(securityService.isPermitted(user, securedObject, sa));
			}
			else {
				assertFalse(securityService.isPermitted(user, securedObject, sa));
			}
		}
	}
	
	@Test
	void isPermittedCheckPermittedTransitive() {
		SecurityService securityService = new SecurityService();
		
		
		SupersocialUser user = new SupersocialUser();
		UserGroup userGroup = createUserGroup(user);
		
		AccessControlList acl = new AccessControlList();
		Map<UserGroup, SecuredAction> permittedActions = new HashMap<>();
		permittedActions.put(userGroup, SecuredAction.ALL_NO_ACL);
		acl.setPermittedActions(permittedActions);
		
		Secured securedObject = createSecuredObject(acl);
		
		for (SecuredAction sa : SecuredAction.values()) {
			if(sa == SecuredAction.ALL || sa == SecuredAction.UPDATE_ACL) {
				assertFalse(securityService.isPermitted(user, securedObject, sa));
			}
			else {
				assertTrue(securityService.isPermitted(user, securedObject, sa));
			}
		}
	}

	private UserGroup createUserGroup(SupersocialUser user) {
		UserGroup userGroup = new UserGroup();
		Set<SupersocialUser> users = new HashSet<SupersocialUser>();
		users.add(user);
		userGroup.setUsers(users);
		return userGroup;
	}

	private Secured createSecuredObject(AccessControlList acl) {
		Secured securedObject = new Secured() {
			private AccessControlList accessControlList;

			@Override
			public void setAccessControlList(AccessControlList accessControlList) {
				this.accessControlList = accessControlList;
			}

			@Override
			public SecuredType getSecuredType() {
				return SecuredType.UNKNOWN;
			}

			@Override
			public AccessControlList getAccessControlList() {
				return accessControlList;
			}
		};
		securedObject.setAccessControlList(acl);
		return securedObject;
	}
}
