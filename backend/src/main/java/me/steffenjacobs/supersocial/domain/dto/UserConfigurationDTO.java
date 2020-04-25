package me.steffenjacobs.supersocial.domain.dto;

import me.steffenjacobs.supersocial.domain.entity.UserConfiguration;

/** @author Steffen Jacobs */
public class UserConfigurationDTO implements WithErrorDTO {
	private String descriptor;
	private String value;
	private String error;

	public UserConfigurationDTO(String error) {
		this.error = error;
	}

	public UserConfigurationDTO() {
	}

	public String getDescriptor() {
		return descriptor;
	}

	public void setDescriptor(String descriptor) {
		this.descriptor = descriptor;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String getError() {
		return error;
	}

	public static UserConfigurationDTO fromConfiguration(UserConfiguration userConf) {
		UserConfigurationDTO dto = new UserConfigurationDTO();
		dto.setDescriptor(userConf.getDescriptor());
		dto.setValue(userConf.getValue());
		return dto;
	}
}
