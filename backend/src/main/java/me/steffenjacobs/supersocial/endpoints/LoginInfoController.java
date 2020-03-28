package me.steffenjacobs.supersocial.endpoints;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticatedPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import me.steffenjacobs.supersocial.security.SecurityService;

/** @author Steffen Jacobs */
@RestController
public class LoginInfoController {
	
	@Autowired
	private SecurityService securityService;

	@GetMapping(path = "/api/loginstatus")
	public AuthenticatedPrincipal getLoginStatus() throws Exception {
		return securityService.getCurrentUser();
	}
}
