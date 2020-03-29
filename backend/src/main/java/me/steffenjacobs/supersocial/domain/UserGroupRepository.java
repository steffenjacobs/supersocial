package me.steffenjacobs.supersocial.domain;

import java.util.UUID;

import org.springframework.data.repository.CrudRepository;

import me.steffenjacobs.supersocial.domain.entity.UserGroup;

public interface UserGroupRepository extends CrudRepository<UserGroup, UUID> {
}
