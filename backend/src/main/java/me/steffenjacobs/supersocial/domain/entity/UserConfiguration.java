package me.steffenjacobs.supersocial.domain.entity;

import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

/** @author Steffen Jacobs */
@Entity
public class UserConfiguration implements Secured {

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
	private String value;

	@Column
	private String descriptor;

	@OneToOne
	private AccessControlList accessControlList;

	@ManyToOne
	private SupersocialUser user;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getDescriptor() {
		return descriptor;
	}

	public void setDescriptor(String descriptor) {
		this.descriptor = descriptor;
	}

	public UUID getId() {
		return id;
	}

	public Date getCreated() {
		return created;
	}

	public SupersocialUser getUser() {
		return user;
	}

	public void setUser(SupersocialUser user) {
		this.user = user;
	}

	@Override
	public SecuredType getSecuredType() {
		return SecuredType.UserConfiguration;
	}

	@Override
	public AccessControlList getAccessControlList() {
		return accessControlList;
	}

	@Override
	public void setAccessControlList(AccessControlList accessControlList) {
		this.accessControlList = accessControlList;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UserConfiguration other = (UserConfiguration) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}
