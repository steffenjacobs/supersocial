package me.steffenjacobs.supersocial.endpoints.config;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import me.steffenjacobs.supersocial.service.TwitterService;

/** @author Steffen Jacobs */
@Profile("twitterServiceMock")
@Configuration
public class TwitterServiceMockConfiguration {
	@Bean
	@Primary
	public TwitterService twitterService() {
		TwitterService mock = Mockito.mock(TwitterService.class);
		Mockito.when(mock.fetchTwitterRegionForLatLng(Mockito.anyDouble(), Mockito.anyDouble(), Mockito.any())).thenReturn("[{\"woeid\":1, \"name\":\"Global\"}]");
		return mock;
	}
}
