package me.steffenjacobs.supersocial.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/** @author Steffen Jacobs */
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()//
				.antMatchers("/login/discourse/success").permitAll()//
				.anyRequest().authenticated()//
				.and().formLogin().loginPage("/login").permitAll();
	}
}
