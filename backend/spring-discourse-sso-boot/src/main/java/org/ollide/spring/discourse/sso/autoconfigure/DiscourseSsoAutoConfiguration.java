package org.ollide.spring.discourse.sso.autoconfigure;

import org.ollide.spring.discourse.sso.DiscourseSsoProperties;
import org.ollide.spring.discourse.sso.DiscourseSsoVerificationFilter;
import org.ollide.spring.discourse.sso.SsoEndpoint;
import org.ollide.spring.discourse.sso.security.DiscourseSigner;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "discourse.sso", name = {"discourseUrl", "secret"})
@AutoConfigureBefore(WebMvcAutoConfiguration.class)
@EnableConfigurationProperties(DiscourseSsoProperties.class)
public class DiscourseSsoAutoConfiguration {

    @Bean
    DiscourseSigner discourseSigner(DiscourseSsoProperties properties) {
        return new DiscourseSigner(properties.getSecret());
    }

    @Bean
    DiscourseSsoVerificationFilter filterRegistrationBean(DiscourseSsoProperties properties, DiscourseSigner signer) {
        DiscourseSsoVerificationFilter filter = new DiscourseSsoVerificationFilter(properties.getReturnPath());
        filter.setSigner(signer);
        return filter;
    }

    @Bean
    SsoEndpoint ssoEndpoint(DiscourseSsoProperties properties, DiscourseSigner signer) {
        return new SsoEndpoint(properties, signer);
    }
}