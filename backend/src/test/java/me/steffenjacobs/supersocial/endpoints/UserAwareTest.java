package me.steffenjacobs.supersocial.endpoints;

import java.util.UUID;

import org.junit.jupiter.api.Assertions;
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
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import me.steffenjacobs.supersocial.domain.dto.CurrentUserDTO;
import me.steffenjacobs.supersocial.domain.dto.UserRegistrationDTO;

/** @author Steffen Jacobs */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class UserAwareTest {

	@LocalServerPort
	protected int port;

	@Autowired
	protected TestRestTemplate restTemplate;

	protected <T> T sendRequest(String sessionCookie, T body, HttpMethod method, String uri, Class<T> type, HttpStatus expectedHttpStatusResponse) {
		final HttpHeaders headers = new HttpHeaders();
		headers.add("Cookie", sessionCookie);
		HttpEntity<T> updateUserLocationRequest = body != null ? new HttpEntity<>(body, headers) : new HttpEntity<>(headers);
		ResponseEntity<T> response = restTemplate.exchange(getBaseUrlWithPort() + uri, method, updateUserLocationRequest, type);
		Assertions.assertEquals(expectedHttpStatusResponse, response.getStatusCode());
		return response.getBody();
	}

	protected String registerAndLogin() {
		final UserRegistrationDTO user = createUserRegistrationDto();
		registerUser(user);
		return loginUser(user);
	}

	private String getBaseUrlWithPort() {
		return "http://localhost:" + port;
	}

	protected CurrentUserDTO registerUser(UserRegistrationDTO userRegistrationDto) {
		HttpEntity<UserRegistrationDTO> registrationRequest = new HttpEntity<>(userRegistrationDto, new HttpHeaders());
		ResponseEntity<CurrentUserDTO> response = restTemplate.exchange(getBaseUrlWithPort() + "/api/register", HttpMethod.POST, registrationRequest, CurrentUserDTO.class);
		Assertions.assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
		return response.getBody();
	}

	protected String loginUser(UserRegistrationDTO userDto) {

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

	protected UserRegistrationDTO createUserRegistrationDto() {
		UserRegistrationDTO userDto = new UserRegistrationDTO();
		userDto.setDisplayName("user-" + UUID.randomUUID());
		userDto.setEmail("test@mai.li");
		userDto.setPassword("test");
		return userDto;
	}

	protected CurrentUserDTO getUserInfo(String sessionCookie) {
		return sendRequest(sessionCookie, null, HttpMethod.GET, "/api/loginstatus", CurrentUserDTO.class, HttpStatus.OK);
	}
}
