package me.steffenjacobs.supersocial.domain.dto;

/** @author Steffen Jacobs */
public class UserRegistrationDTO {

	private String displayName;
	private String email;
	private String password;

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("UserRegistrationDTO [displayName=").append(displayName).append(", email=").append(email).append("]");
		return builder.toString();
	}

}
