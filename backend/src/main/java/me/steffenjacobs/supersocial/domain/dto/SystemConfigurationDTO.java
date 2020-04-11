package me.steffenjacobs.supersocial.domain.dto;

import me.steffenjacobs.supersocial.domain.entity.SystemConfiguration;

/** @author Steffen Jacobs */
public class SystemConfigurationDTO {
	private String descriptor;
	private String value;
	private String error;

	public SystemConfigurationDTO(String error) {
		this.error = error;
	}

	private SystemConfigurationDTO() {
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

	public void setError(String error) {
		this.error = error;
	}

	public String getError() {
		return error;
	}

	public static SystemConfigurationDTO fromSystemConfiguration(SystemConfiguration systemConf) {
		SystemConfigurationDTO dto = new SystemConfigurationDTO();
		dto.setDescriptor(systemConf.getDescriptor());
		dto.setValue(systemConf.getValue());
		return dto;
	}
}
