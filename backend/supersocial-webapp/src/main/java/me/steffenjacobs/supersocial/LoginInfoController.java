package me.steffenjacobs.supersocial;

import org.ollide.spring.discourse.sso.authentication.DiscoursePrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/** @author Steffen Jacobs */
@RestController
public class LoginInfoController {

	@GetMapping(path = "/api/loginstatus")
	public DiscoursePrincipal getLoginStatus() throws Exception {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		if (principal instanceof DiscoursePrincipal) {
			return (DiscoursePrincipal) principal;
		}
		throw new Exception("Invalid principal");
	}
}
