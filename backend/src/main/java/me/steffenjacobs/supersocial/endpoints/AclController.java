package me.steffenjacobs.supersocial.endpoints;

import java.util.UUID;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import me.steffenjacobs.supersocial.domain.dto.AccessControlListDTO;
import me.steffenjacobs.supersocial.domain.entity.SecuredAction;
import me.steffenjacobs.supersocial.persistence.exception.UserGroupNotOnAccessControlListExceptiopn;
import me.steffenjacobs.supersocial.security.SecurityService;
import me.steffenjacobs.supersocial.security.exception.UsergroupNotFoundException;

/** @author Steffen Jacobs */
@RestController
public class AclController {
	private static final Logger LOG = org.slf4j.LoggerFactory.getLogger(AclController.class);

	@Autowired
	private SecurityService securityService;

	/** Add a user group to an ACL. */
	@PutMapping(path = "/api/security/acl/{aclId}/{userGroupId}/{action}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<AccessControlListDTO> allowUserGroupToAccessSecuredObject(@PathVariable(name = "aclId") UUID aclId, @PathVariable(name = "userGroupId") UUID userGroupId,
			@PathVariable(name = "action") int action) throws Exception {
		LOG.info("Allowing user {} to access {} ({}).", aclId, userGroupId, action);
		try {
			return new ResponseEntity<>(securityService.addToAcl(aclId, userGroupId, SecuredAction.fromMask(action)), HttpStatus.ACCEPTED);
		} catch (UsergroupNotFoundException e) {
			return new ResponseEntity<>(new AccessControlListDTO(e.getMessage()), HttpStatus.BAD_REQUEST);
		}
	}

	/** Remove a user group from an ACL. */
	@DeleteMapping(path = "/api/security/acl/{aclId}/{userGroupId}/{action}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<AccessControlListDTO> denyUserGroupToAccessSecuredObject(@PathVariable(name = "aclId") UUID aclId, @PathVariable(name = "userGroupId") UUID userGroupId)
			throws Exception {
		LOG.info("Removing permission for user {} to access {}.", aclId, userGroupId);
		try {
			return new ResponseEntity<>(securityService.removeFromAcl(aclId, userGroupId), HttpStatus.ACCEPTED);
		} catch (UsergroupNotFoundException | UserGroupNotOnAccessControlListExceptiopn e) {
			return new ResponseEntity<>(new AccessControlListDTO(e.getMessage()), HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping(path = "/api/security/acl/{aclId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<AccessControlListDTO> retrieveAclById(@PathVariable(name = "aclId") UUID aclId) throws Exception {
		LOG.info("Retrieving ACL {}.", aclId);
		try {
			return new ResponseEntity<>(securityService.getAcl(aclId), HttpStatus.OK);
		} catch (UsergroupNotFoundException e) {
			return new ResponseEntity<>(new AccessControlListDTO(e.getMessage()), HttpStatus.BAD_REQUEST);
		}
	}
}
