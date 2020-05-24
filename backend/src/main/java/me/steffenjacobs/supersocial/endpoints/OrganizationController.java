package me.steffenjacobs.supersocial.endpoints;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import me.steffenjacobs.supersocial.domain.dto.UserGroupDTO;
import me.steffenjacobs.supersocial.security.UserGroupService;
import me.steffenjacobs.supersocial.security.exception.CouldNotDeleteDefaultUserFromDefaultUserGroup;
import me.steffenjacobs.supersocial.security.exception.CouldNotDeleteDefaultUserGroup;
import me.steffenjacobs.supersocial.security.exception.InvalidUsernameException;
import me.steffenjacobs.supersocial.security.exception.UserNotFoundException;
import me.steffenjacobs.supersocial.security.exception.UsergroupEmptyException;
import me.steffenjacobs.supersocial.security.exception.UsergroupNotFoundException;
import me.steffenjacobs.supersocial.util.Pair;

/** @author Steffen Jacobs */
@RestController
public class OrganizationController {
	private static final Logger LOG = org.slf4j.LoggerFactory.getLogger(OrganizationController.class);

	@Autowired
	private UserGroupService userGroupService;

	/** Create a new user group. */
	@PutMapping(path = "/api/organization", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<UserGroupDTO> createOrUpdateUserGroup(@RequestBody UserGroupDTO userGroup) throws Exception {
		LOG.info("Creating or updating organization {}.", userGroup.getId());
		try {
			Pair<UserGroupDTO, Boolean> c = userGroupService.createOrUpdateUserGroup(userGroup);
			return new ResponseEntity<>(c.getA(), c.getB() ? HttpStatus.CREATED : HttpStatus.ACCEPTED);
		} catch (UsergroupNotFoundException e) {
			return new ResponseEntity<>(new UserGroupDTO(e.getMessage()), HttpStatus.BAD_REQUEST);
		}
	}

	/** Add a given user to a user group. */
	@PutMapping(path = "/api/organization/{userGroupId}/{userId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<UserGroupDTO> addUserToUserGroup(@PathVariable(name = "userGroupId") UUID userGroupId, @PathVariable(name = "userId") String userId) throws Exception {
		LOG.info("Adding {} to organization {}.", userId, userGroupId);		
		try {
			
			return new ResponseEntity<>(userGroupService.addUserToUserGroup(userId, userGroupId), HttpStatus.ACCEPTED);
		} catch (IllegalArgumentException | UsergroupNotFoundException | InvalidUsernameException | UserNotFoundException e) {
			return new ResponseEntity<>(new UserGroupDTO(e.getMessage()), HttpStatus.BAD_REQUEST);
		}
	}

	/** Delete a given user to a user group. */
	@DeleteMapping(path = "/api/organization/{userGroupId}/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<UserGroupDTO> deleteUserToUserGroup(@PathVariable(name = "userGroupId") UUID userGroupId, @PathVariable(name = "userId") UUID userId) throws Exception {
		LOG.info("Removing {} from organization {}.", userId, userGroupId);
		try {
			return new ResponseEntity<>(userGroupService.deleteUserFromUserGroup(userId, userGroupId), HttpStatus.ACCEPTED);
		} catch (UsergroupNotFoundException | InvalidUsernameException | CouldNotDeleteDefaultUserFromDefaultUserGroup e) {
			return new ResponseEntity<>(new UserGroupDTO(e.getMessage()), HttpStatus.BAD_REQUEST);
		} catch (UsergroupEmptyException e) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}
	}

	/** Get all user groups. */
	@GetMapping(path = "/api/organization", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Set<UserGroupDTO>> getOrganizations() throws Exception {
		LOG.info("Retrieving all organizations.");
		return new ResponseEntity<>(userGroupService.getAll().collect(Collectors.toSet()), HttpStatus.OK);
	}

	/** Delete a specific user group by its {@code id}. */
	@DeleteMapping(path = "/api/organization/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<UserGroupDTO> deleteOrganization(@PathVariable(name = "id") UUID id) {
		LOG.info("Deleting organization {}.", id);
		try {
			userGroupService.deleteUserGroup(id);
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} catch (UsergroupNotFoundException e) {
			return new ResponseEntity<>(new UserGroupDTO(e.getMessage()), HttpStatus.NOT_FOUND);
		} catch (CouldNotDeleteDefaultUserGroup e) {
			return new ResponseEntity<>(new UserGroupDTO(e.getMessage()), HttpStatus.BAD_REQUEST);
		}
	}
}
