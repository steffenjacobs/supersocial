package me.steffenjacobs.supersocial.endpoints;

import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import me.steffenjacobs.supersocial.domain.dto.SystemConfigurationDTO;
import me.steffenjacobs.supersocial.persistence.SystemConfigurationManager;
import me.steffenjacobs.supersocial.persistence.exception.SystemConfigurationTypeNotFoundException;
import me.steffenjacobs.supersocial.service.exception.SocialMediaAccountNotFoundException;
import me.steffenjacobs.supersocial.util.Pair;

/**
 * This class contains meta-endpoints of the system, e.g. api version or login
 * data for the current user.
 * 
 * @author Steffen Jacobs
 */
@RestController
@PropertySource("classpath:application.properties")
public class ApiInfoController {
	private static final Logger LOG = LoggerFactory.getLogger(ApiInfoController.class);

	@Autowired
	private SystemConfigurationManager systemConfigurationManager;

	@Value("${app.version:unknown}")
	private String version;

	/**
	 * @return the current version of the software, read from the maven pom.xml.
	 */
	@GetMapping(path = "/api/version", produces = MediaType.APPLICATION_JSON_VALUE)
	public String getVersion() throws Exception {
		return "{\"version\": \"" + version + "\"}";
	}

	/**
	 * @return all current configuration objects.
	 */
	@GetMapping(path = "/api/config", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Set<SystemConfigurationDTO>> getAllConfigurationsv() throws Exception {
		LOG.info("Retrieving all configuration objects.");
		try {
			return new ResponseEntity<>(systemConfigurationManager.getAllConfigurations().collect(Collectors.toSet()), HttpStatus.OK);
		} catch (SocialMediaAccountNotFoundException e) {
			return new ResponseEntity<>(Set.of(new SystemConfigurationDTO(e.getMessage())), HttpStatus.NOT_FOUND);
		}
	}

	/**
	 * Create or update a configuration object.
	 */
	@PutMapping(path = "/api/config", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<SystemConfigurationDTO> createOrUpdateConfiguration(@RequestBody SystemConfigurationDTO systemConfigurationDTO) throws Exception {
		LOG.info("creating/updating configuration object {}.", systemConfigurationDTO.getDescriptor());
		try {
			Pair<Boolean, SystemConfigurationDTO> result = systemConfigurationManager.createOrUpdate(systemConfigurationDTO);
			return new ResponseEntity<>(result.getB(), result.getA() ? HttpStatus.CREATED : HttpStatus.ACCEPTED);
		} catch (SystemConfigurationTypeNotFoundException | SocialMediaAccountNotFoundException e) {
			return new ResponseEntity<>(new SystemConfigurationDTO(e.getMessage()), HttpStatus.NOT_FOUND);
		}
	}
}
