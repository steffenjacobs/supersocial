package me.steffenjacobs.supersocial.domain.entity;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.GenericGenerator;
import org.springframework.security.core.AuthenticatedPrincipal;

/** @author Steffen Jacobs */

@Entity
public class SupersocialUser implements AuthenticatedPrincipal {

	@Id
	@GeneratedValue(generator = "UUID")
	@GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
	@Column(name = "id", updatable = false, nullable = false)
	private UUID id;

	@Column
	private String externalId;

	@Column
	private int loginProviderId;

	@Column
	private String name;

	public UUID getId() {
		return id;
	}

	public String getExternalId() {
		return externalId;
	}

	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}

	public LoginProvider getLoginProvider() {
		return LoginProvider.fromId(loginProviderId);
	}

	public void setLoginProvider(LoginProvider loginProvider) {
		this.loginProviderId = loginProvider.getId();
	}

	@Override
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
