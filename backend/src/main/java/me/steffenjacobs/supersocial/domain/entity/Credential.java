package me.steffenjacobs.supersocial.domain.entity;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.GenericGenerator;

import me.steffenjacobs.supersocial.domain.dto.CredentialDTO;

/** @author Steffen Jacobs */
@Entity
public class Credential {

	@Id
	@GeneratedValue(generator = "UUID")
	@GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
	@Column(name = "id", updatable = false, nullable = false)
	private UUID id;

	@Column
	private String value;

	@Column
	private String descriptor;

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
		return dto;
	}

}
