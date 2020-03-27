package me.steffenjacobs.supersocial.domain;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.repository.CrudRepository;

import me.steffenjacobs.supersocial.domain.entity.Credential;

public interface CredentialRepository extends CrudRepository<Credential, UUID> {

	Optional<Credential> findByDescriptor(String descriptor);
}
