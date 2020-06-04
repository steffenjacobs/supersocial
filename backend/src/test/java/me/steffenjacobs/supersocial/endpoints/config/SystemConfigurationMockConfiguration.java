package me.steffenjacobs.supersocial.endpoints.config;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import me.steffenjacobs.supersocial.persistence.SystemConfigurationManager;

/** @author Steffen Jacobs */
@Profile("systemConfigurationMock")
@Configuration
public class SystemConfigurationMockConfiguration {
	@Bean
	@Primary
	public SystemConfigurationManager systemConfigurationManager() {
		return Mockito.mock(SystemConfigurationManager.class);
	}
}
