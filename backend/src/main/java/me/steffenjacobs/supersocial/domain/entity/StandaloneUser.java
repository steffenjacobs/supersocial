package me.steffenjacobs.supersocial.domain.entity;

import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "standalone_user")
public class StandaloneUser implements Secured {

	@Id
	@GeneratedValue(generator = "UUID")
	@GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
	@Column(name = "id", updatable = false, nullable = false)
	private UUID id;

	@Column
	@CreationTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	private Date created;

	@Column(name = "email")
	private String email;

	@Column
	private String displayName;

	@Column(name = "password")
	private String password;
	@Column(name = "active")
	private int active;

	@OneToOne
	private AccessControlList accessControlList;

	public UUID getId() {
		return id;
	}

	@JsonIgnore
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public int getActive() {
		return active;
	}

	public void setActive(int active) {
		this.active = active;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public Date getCreated() {
		return created;
	}

	@Override
	public SecuredType getSecuredType() {
		return SecuredType.USER;
	}

	@Override
	public AccessControlList getAccessControlList() {
		return accessControlList;
	}

	@Override
	public void setAccessControlList(AccessControlList accessControlList) {
		this.accessControlList = accessControlList;
	}
}