package me.steffenjacobs.supersocial.endpoints;

import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import me.steffenjacobs.supersocial.domain.dto.CurrentUserDTO;
import me.steffenjacobs.supersocial.domain.dto.LocationDTO;
import me.steffenjacobs.supersocial.domain.dto.UserRegistrationDTO;
import me.steffenjacobs.supersocial.domain.entity.LoginProvider;
import me.steffenjacobs.supersocial.domain.entity.UserConfigurationType;

/** @author Steffen Jacobs */
@ActiveProfiles({ "systemConfigurationMock", "twitterServiceMock" })
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class UserControllerTest {

	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate restTemplate;

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
		LocationDTO updated = updateUserLocation(sessionCookie, location);

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
			if (UserConfigurationType.Latitude.getKey().equals(config.getDescriptor())) {
				Assertions.assertEquals(location.getLatitude(), Double.parseDouble(config.getValue()));
			} else if (UserConfigurationType.Longitude.getKey().equals(config.getDescriptor())) {
				Assertions.assertEquals(location.getLongitude(), Double.parseDouble(config.getValue()));

			} else if (UserConfigurationType.Location.getKey().equals(config.getDescriptor())) {
				Assertions.assertNotNull(config.getValue());
			} else {
				Assertions.fail("Unexpected configuration descriptor.");
			}
			Assertions.assertNull(config.getError());
		});
	}

	private LocationDTO updateUserLocation(String sessionCookie, LocationDTO location) {
		final HttpHeaders headers = new HttpHeaders();
		headers.add("Cookie", sessionCookie);
		HttpEntity<LocationDTO> updateUserLocationRequest = new HttpEntity<>(location, headers);
		ResponseEntity<LocationDTO> response = restTemplate.exchange(getBaseUrlWithPort() + "/api/user/location", HttpMethod.PUT, updateUserLocationRequest, LocationDTO.class);
		Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
		return response.getBody();
	}

	private String registerAndLogin() {
		final UserRegistrationDTO user = createUserRegistrationDto();
		registerUser(user);
		return loginUser(user);
	}

	private String getBaseUrlWithPort() {
		return "http://localhost:" + port;
	}

	private void assertUserInfo(UserRegistrationDTO userDto, CurrentUserDTO currentUserDto) {
		Assertions.assertNotNull(currentUserDto);
		Assertions.assertEquals(userDto.getDisplayName(), currentUserDto.getUsername());
		Assertions.assertEquals(LoginProvider.SUPERSOCIAL.getId(), currentUserDto.getProviderId());
	}

	private CurrentUserDTO getUserInfo(String sessionCookie) {
		final HttpHeaders headers = new HttpHeaders();
		headers.add("Cookie", sessionCookie);
		HttpEntity<?> registrationRequest = new HttpEntity<>(headers);
		ResponseEntity<CurrentUserDTO> response = restTemplate.exchange(getBaseUrlWithPort() + "/api/loginstatus", HttpMethod.GET, registrationRequest, CurrentUserDTO.class);
		Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
		return response.getBody();
	}

	private CurrentUserDTO registerUser(UserRegistrationDTO userRegistrationDto) {
		HttpEntity<UserRegistrationDTO> registrationRequest = new HttpEntity<>(userRegistrationDto, new HttpHeaders());
		ResponseEntity<CurrentUserDTO> response = restTemplate.exchange(getBaseUrlWithPort() + "/api/register", HttpMethod.POST, registrationRequest, CurrentUserDTO.class);
		Assertions.assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
		return response.getBody();
	}

	private String loginUser(UserRegistrationDTO userDto) {

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
		map.add("username", userDto.getDisplayName());
		map.add("password", userDto.getPassword());
		HttpEntity<MultiValueMap<String, String>> loginRequest = new HttpEntity<MultiValueMap<String, String>>(map, headers);

		ResponseEntity<CurrentUserDTO> response = restTemplate.exchange(getBaseUrlWithPort() + "/api/perform_login", HttpMethod.POST, loginRequest, CurrentUserDTO.class);
		Assertions.assertEquals(HttpStatus.FOUND, response.getStatusCode());
		Assertions.assertEquals(getBaseUrlWithPort() + "/api/loginstatus", response.getHeaders().get("Location").get(0));
		return response.getHeaders().get("Set-Cookie").get(0).split(";")[0];
	}

	private UserRegistrationDTO createUserRegistrationDto() {
		UserRegistrationDTO userDto = new UserRegistrationDTO();
		userDto.setDisplayName("user-" + UUID.randomUUID());
		userDto.setEmail("test@mai.li");
		userDto.setPassword("test");
		return userDto;
	}

}
