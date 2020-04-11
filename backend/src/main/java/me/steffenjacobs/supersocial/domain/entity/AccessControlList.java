package me.steffenjacobs.supersocial.domain.entity;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyColumn;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

/** @author Steffen Jacobs */
@Entity(name = "acl")
public class AccessControlList {
	@Id
	@GeneratedValue(generator = "UUID")
	@GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
	@Column(name = "id", updatable = false, nullable = false)
	private UUID id;

	@Column
	@CreationTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	private Date created;

	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "acl_usergroup_secured_action", joinColumns = { @JoinColumn(name = "acl_id", referencedColumnName = "id") })
	@MapKeyColumn(name = "user_group_id")
	@Column(name = "acl_secured_action")
	private Map<UserGroup, SecuredAction> permittedActions = new HashMap<>();

	public UUID getId() {
		return id;
	}

	public Map<UserGroup, SecuredAction> getPermittedActions() {
		return permittedActions;
	}

	public void setPermittedActions(Map<UserGroup, SecuredAction> permittedActions) {
		this.permittedActions = permittedActions;
	}

	public Date getCreated() {
		return created;
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
		AccessControlList other = (AccessControlList) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}
