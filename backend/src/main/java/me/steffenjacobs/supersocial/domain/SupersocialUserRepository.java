package me.steffenjacobs.supersocial.domain;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.repository.CrudRepository;

import me.steffenjacobs.supersocial.domain.entity.SupersocialUser;

public interface SupersocialUserRepository extends CrudRepository<SupersocialUser, UUID> {

	Optional<SupersocialUser> findByExternalId(String externalId);

	Optional<SupersocialUser> findByName(String string);
}
