package me.steffenjacobs.supersocial.security;

import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Holds security object beans like the password encoder.
 * 
 * @author Steffen Jacobs
 */
@Component
public class SecurityBeans {

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

}
