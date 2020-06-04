package me.steffenjacobs.supersocial.domain;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.repository.CrudRepository;

import me.steffenjacobs.supersocial.domain.entity.SystemConfiguration;

public interface SystemConfigurationRepository extends CrudRepository<SystemConfiguration, UUID> {

	Optional<SystemConfiguration> findByDescriptor(String descriptor);
}
