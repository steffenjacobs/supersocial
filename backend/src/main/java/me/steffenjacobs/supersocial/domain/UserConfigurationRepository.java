package me.steffenjacobs.supersocial.domain;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.repository.CrudRepository;

import me.steffenjacobs.supersocial.domain.entity.UserConfiguration;

public interface UserConfigurationRepository extends CrudRepository<UserConfiguration, UUID> {

	Optional<UserConfiguration> findByDescriptor(String descriptor);
}
