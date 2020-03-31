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

import me.steffenjacobs.supersocial.domain.dto.CredentialDTO;

/** @author Steffen Jacobs */
@Entity
public class Credential implements Secured {

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
	private SocialMediaAccount account;

	public Credential() {
	}

	private void setId(UUID id) {
		this.id = id;
	}

	public UUID getId() {
		return id;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public String getDescriptor() {
		return descriptor;
	}

	public void setDescriptor(String descriptor) {
		this.descriptor = descriptor;
	}

	public SocialMediaAccount getAccount() {
		return account;
	}

	public void setAccount(SocialMediaAccount account) {
		this.account = account;
	}

	public static Credential fromDto(CredentialDTO dto) {
		Credential c = new Credential();
		c.setDescriptor(dto.getDescriptor());
		c.setValue(dto.getValue());
		c.setId(dto.getId());
		return c;
	}

	public CredentialDTO toDTO() {
		CredentialDTO dto = new CredentialDTO();
		dto.setDescriptor(this.descriptor);
		dto.setValue("(omitted)");
		dto.setId(this.id);
		dto.setOmitted(true);
		dto.setAccountId(this.getAccount() != null ? this.getAccount().getId() : null);
		dto.setCreated(this.getCreated());
		return dto;
	}

	@Override
	public SecuredType getSecuredType() {
		return SecuredType.Credential;
	}

	@Override
	public AccessControlList getAccessControlList() {
		return accessControlList;
	}

	@Override
	public void setAccessControlList(AccessControlList accessControlList) {
		this.accessControlList = accessControlList;
	}

	public Date getCreated() {
		return created;
	}

}
