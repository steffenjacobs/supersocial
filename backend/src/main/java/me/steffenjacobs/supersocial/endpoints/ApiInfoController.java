package me.steffenjacobs.supersocial.endpoints;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import me.steffenjacobs.supersocial.domain.dto.CurrentUserDTO;
import me.steffenjacobs.supersocial.security.SecurityService;

/** @author Steffen Jacobs */
@RestController
@PropertySource("classpath:application.properties")
public class ApiInfoController {
	
	@Autowired
	private SecurityService securityService;
	
	@Value("${app.version:unknown}")
	private String version;

	@GetMapping(path = "/api/loginstatus")
	public CurrentUserDTO getLoginStatus() throws Exception {
		return CurrentUserDTO.fromUser(securityService.getCurrentUser());
	}
	
	@GetMapping(path = "/api/version")
	public String getVersion() throws Exception {
		return "{\"version:\": " + version + "\"}";
	}
}
