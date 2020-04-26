package me.steffenjacobs.supersocial.domain.dto;

import java.util.Map;
import java.util.UUID;

/** @author Steffen Jacobs */
public interface WithAclDTO {
	
	Map<UUID, Integer> getAcl();
	
	UUID getAclId();

}
