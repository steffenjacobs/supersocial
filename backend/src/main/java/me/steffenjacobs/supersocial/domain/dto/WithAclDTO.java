package me.steffenjacobs.supersocial.domain.dto;

import java.util.List;
import java.util.UUID;

/** @author Steffen Jacobs */
public interface WithAclDTO {
	
	List<AclEntryDTO> getAcl();
	
	UUID getAclId();

}
