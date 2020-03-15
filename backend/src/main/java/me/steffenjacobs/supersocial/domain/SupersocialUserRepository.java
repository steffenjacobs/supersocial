package me.steffenjacobs.supersocial.domain;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import me.steffenjacobs.supersocial.domain.entity.SupersocialUser;

public interface SupersocialUserRepository extends CrudRepository<SupersocialUser, Long> {

	Optional<SupersocialUser> findByExternalId(String externalId);

	Optional<SupersocialUser> findByName(String string);
}
