package me.steffenjacobs.supersocial.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/** @author Steffen Jacobs */
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private Environment env;

	@Autowired
	private UserService userService;
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()//
				.antMatchers("/login/discourse/success").permitAll()//
				.antMatchers(HttpMethod.POST, "/api/register").permitAll()//
				.antMatchers(HttpMethod.OPTIONS,"/api/**").permitAll()//
				.anyRequest().authenticated();//
		if("true".equals(env.getProperty("security.standalone"))) {
			http.httpBasic();
			http.logout().logoutUrl("/logout");
			http.csrf().disable();
		}
		else {
			http.formLogin().loginPage("/login").permitAll();
		}
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		if ("true".equals(env.getProperty("security.standalone"))) {
			auth.userDetailsService(userService).passwordEncoder(passwordEncoder);
		} else {
			super.configure(auth);
		}
	}
}
