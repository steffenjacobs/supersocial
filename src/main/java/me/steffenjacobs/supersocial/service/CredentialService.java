package me.steffenjacobs.supersocial.service;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

/** @author Steffen Jacobs */
@Component
public class CredentialService {

	enum Credential {
		TWITTER_API_KEY("twitter.api.key"), TWITTER_API_KEY_SECRET("twitter.api.secret"), TWITTER_ACCESS_TOKEN("twitter.api.accesstoken"), TWITTER_ACCESS_TOKEN_SECRET(
				"twitter.api.accesstoken.secret");

		private final String key;

		private Credential(String key) {
			this.key = key;
		}

		public String getKey() {
			return key;
		}
	}

	private ResourceBundle credentialResources;

	@PostConstruct
	private void loadCredentials() throws MalformedURLException {
		URL fileUrl = new File(".").toURI().toURL();
		ClassLoader loader = new URLClassLoader(new URL[] { fileUrl });
		credentialResources = ResourceBundle.getBundle("credentials", Locale.getDefault(), loader);
	}

	/**
	 * @return the given credential loaded from the credentials.properties files.
	 */
	public String getCredential(Credential credential) {
		return credentialResources.getString(credential.getKey());
	}

	/**
	 * @return true, if credentials could be loaded from the file. <br/>
	 *         else return false.
	 */
	public boolean hasCredentials() {
		return credentialResources != null;
	}
}
