package me.steffenjacobs.supersocial.domain.entity;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/** @author Steffen Jacobs */
@Converter
public class SecuredActionConverter implements AttributeConverter<SecuredAction, Integer> {

	@Override
	public Integer convertToDatabaseColumn(SecuredAction action) {
		if (action == null) {
			return null;
		}
		return action.getMask();
	}

	@Override
	public SecuredAction convertToEntityAttribute(Integer mask) {
		return SecuredAction.fromMask(mask);
	}

}
