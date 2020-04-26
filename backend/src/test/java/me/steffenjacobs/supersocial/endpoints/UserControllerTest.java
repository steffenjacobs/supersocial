package me.steffenjacobs.supersocial.endpoints;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

import me.steffenjacobs.supersocial.domain.dto.CurrentUserDTO;
import me.steffenjacobs.supersocial.domain.dto.LocationDTO;
import me.steffenjacobs.supersocial.domain.dto.UserConfigurationDTO;
import me.steffenjacobs.supersocial.domain.dto.UserRegistrationDTO;
import me.steffenjacobs.supersocial.domain.entity.LoginProvider;
import me.steffenjacobs.supersocial.domain.entity.UserConfigurationType;

/** @author Steffen Jacobs */
@ActiveProfiles({ "systemConfigurationMock", "twitterServiceMock" })
public class UserControllerTest extends UserAwareTest {

	@Test
	void testRegistrationAndLogin() {
		UserRegistrationDTO userDto = createUserRegistrationDto();
		registerUser(userDto);
		String sessionCookie = loginUser(userDto);
		Assertions.assertTrue(sessionCookie.startsWith("JSESSIONID="));
		assertUserInfo(userDto, getUserInfo(sessionCookie));
	}

	@Test
	void updateLocation() {
		String sessionCookie = registerAndLogin();
		LocationDTO location = new LocationDTO();
		location.setLatitude(1.2);
		location.setLongitude(2.1);
		LocationDTO updated = sendRequest(sessionCookie, location, HttpMethod.PUT, "/api/user/location", LocationDTO.class, HttpStatus.OK);

		// check if immediate result is correct
		Assertions.assertEquals(location.getLatitude(), updated.getLatitude());
		Assertions.assertEquals(location.getLongitude(), updated.getLongitude());
		Assertions.assertNotNull(updated.getLocationName());
		Assertions.assertNull(updated.getError());

		// check if configuration was appended to user settings as expected
		CurrentUserDTO dto = getUserInfo(sessionCookie);
		Assertions.assertNotNull(dto.getConfig());
		Assertions.assertEquals(3, dto.getConfig().size());

		dto.getConfig().forEach(config -> {
			if (UserConfigurationType.LATITUDE.getKey().equals(config.getDescriptor())) {
				Assertions.assertEquals(location.getLatitude(), Double.parseDouble(config.getValue()));
			} else if (UserConfigurationType.LONGITUDE.getKey().equals(config.getDescriptor())) {
				Assertions.assertEquals(location.getLongitude(), Double.parseDouble(config.getValue()));

			} else if (UserConfigurationType.LOCATION.getKey().equals(config.getDescriptor())) {
				Assertions.assertNotNull(config.getValue());
			} else {
				Assertions.fail("Unexpected configuration descriptor.");
			}
			Assertions.assertNull(config.getError());
		});
	}

	@Test
	void crudUserConfiguration() {
		String sessionCookie = registerAndLogin();

		UserConfigurationDTO config = new UserConfigurationDTO();
		config.setDescriptor("test-descriptor");
		config.setValue("test-value");

		// create new config object
		UserConfigurationDTO createdConfig = sendRequest(sessionCookie, config, HttpMethod.PUT, "api/user/config", UserConfigurationDTO.class, HttpStatus.CREATED);
		assertConfigValid(config, createdConfig);

		UserConfigurationDTO createdConfigRetrieved = getUserInfo(sessionCookie).getConfig().stream().filter(c -> config.getDescriptor().equals(c.getDescriptor())).findFirst()
				.get();
		assertConfigValid(config, createdConfigRetrieved);

		// update existing config object
		config.setValue("test-value-2");
		UserConfigurationDTO updatedConfig = sendRequest(sessionCookie, config, HttpMethod.PUT, "api/user/config", UserConfigurationDTO.class, HttpStatus.ACCEPTED);
		assertConfigValid(config, updatedConfig);

		UserConfigurationDTO updatedConfigRetrieved = getUserInfo(sessionCookie).getConfig().stream().filter(c -> config.getDescriptor().equals(c.getDescriptor())).findFirst()
				.get();
		assertConfigValid(config, updatedConfigRetrieved);

		// delete existing config object
		sendRequest(sessionCookie, null, HttpMethod.DELETE, "api/user/config/" + config.getDescriptor(), UserConfigurationDTO.class, HttpStatus.NO_CONTENT);
		Assertions.assertFalse(getUserInfo(sessionCookie).getConfig().stream().filter(c -> config.getDescriptor().equals(c.getDescriptor())).findFirst().isPresent());

		// attempt to delete again
		sendRequest(sessionCookie, null, HttpMethod.DELETE, "api/user/config/" + config.getDescriptor(), UserConfigurationDTO.class, HttpStatus.BAD_REQUEST);
	}

	private void assertConfigValid(UserConfigurationDTO config, UserConfigurationDTO createdConfig) {
		Assertions.assertNull(createdConfig.getError());
		Assertions.assertEquals(config.getDescriptor(), createdConfig.getDescriptor());
		Assertions.assertEquals(config.getValue(), createdConfig.getValue());
	}

	private void assertUserInfo(UserRegistrationDTO userDto, CurrentUserDTO currentUserDto) {
		Assertions.assertNotNull(currentUserDto);
		Assertions.assertEquals(userDto.getDisplayName(), currentUserDto.getUsername());
		Assertions.assertEquals(LoginProvider.SUPERSOCIAL.getId(), currentUserDto.getProviderId());
	}
}
