package me.steffenjacobs.supersocial.domain.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.springframework.security.core.AuthenticatedPrincipal;

/** @author Steffen Jacobs */

@Entity
public class SupersocialUser implements AuthenticatedPrincipal {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@Column
	private String externalId;

	@Column
	private int loginProviderId;

	@Column
	private String name;

	public long getId() {
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
