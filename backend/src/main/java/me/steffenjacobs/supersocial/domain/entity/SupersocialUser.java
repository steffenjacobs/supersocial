package me.steffenjacobs.supersocial.domain.entity;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.security.core.AuthenticatedPrincipal;

/** @author Steffen Jacobs */

@Entity
public class SupersocialUser implements AuthenticatedPrincipal, Secured {

	@Id
	@GeneratedValue(generator = "UUID")
	@GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
	@Column(name = "id", updatable = false, nullable = false)
	private UUID id;

	@Column
	@CreationTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	private Date created;

	@Column
	private String externalId;

	@Column
	private int loginProviderId;

	@Column
	private String name;

	@OneToOne
	private AccessControlList accessControlList;

	@OneToOne
	private UserGroup defaultUserGroup;

	@OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
	private Set<UserConfiguration> userConfigurations = new HashSet<>();

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

	@Override
	public SecuredType getSecuredType() {
		return SecuredType.SupersocialUser;
	}

	@Override
	public AccessControlList getAccessControlList() {
		return accessControlList;
	}

	@Override
	public void setAccessControlList(AccessControlList accessControlList) {
		this.accessControlList = accessControlList;
	}

	public UserGroup getDefaultUserGroup() {
		return defaultUserGroup;
	}

	public void setDefaultUserGroup(UserGroup defaultUserGroup) {
		this.defaultUserGroup = defaultUserGroup;
	}

	public Date getCreated() {
		return created;
	}

	public Set<UserConfiguration> getUserConfigurations() {
		return userConfigurations;
	}

	public void setUserConfigurations(Set<UserConfiguration> userConfigurations) {
		this.userConfigurations = userConfigurations;
	}

}
