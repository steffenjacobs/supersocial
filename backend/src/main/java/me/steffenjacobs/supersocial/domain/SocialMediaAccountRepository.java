package me.steffenjacobs.supersocial.domain;

import java.util.UUID;

import org.springframework.data.repository.CrudRepository;

import me.steffenjacobs.supersocial.domain.entity.SocialMediaAccount;

public interface SocialMediaAccountRepository extends CrudRepository<SocialMediaAccount, UUID> {

}
