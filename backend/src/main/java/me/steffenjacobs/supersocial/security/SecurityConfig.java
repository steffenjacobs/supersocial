package me.steffenjacobs.supersocial.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/** @author Steffen Jacobs */
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private Environment env;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()//
				.antMatchers("/login/discourse/success").permitAll()//
				.anyRequest().authenticated();//
		if("true".equals(env.getProperty("security.dummy"))) {
			http.httpBasic();
			http.csrf().disable();
		}
		else {
			http.formLogin().loginPage("/login").permitAll();
		}
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		if ("true".equals(env.getProperty("security.dummy"))) {
			auth.inMemoryAuthentication().withUser("user").password("{noop}pass") // Spring Security 5 requires specifying the password storage format
					.roles("USER");
		} else {
			super.configure(auth);
		}
	}
}
