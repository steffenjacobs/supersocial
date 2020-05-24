package me.steffenjacobs.supersocial.domain.dto;

import java.util.UUID;

/** @author Steffen Jacobs */
public class AclEntryDTO {
	private UUID id;
	private Integer value;

	public AclEntryDTO() {
		super();
	}

	public AclEntryDTO(UUID id, Integer value) {
		super();
		this.id = id;
		this.value = value;
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public Integer getValue() {
		return value;
	}

	public void setValue(Integer value) {
		this.value = value;
	}
}
