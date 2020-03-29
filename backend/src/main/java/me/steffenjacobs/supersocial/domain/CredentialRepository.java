package me.steffenjacobs.supersocial.domain;

import java.util.UUID;
import java.util.stream.Stream;

import org.springframework.data.repository.CrudRepository;

import me.steffenjacobs.supersocial.domain.entity.Credential;

public interface CredentialRepository extends CrudRepository<Credential, UUID> {

	Stream<Credential> findAllById(UUID id);
	
	Stream<Credential> findByDescriptor(String descriptor);
}
