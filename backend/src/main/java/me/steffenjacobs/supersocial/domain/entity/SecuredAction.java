package me.steffenjacobs.supersocial.domain.entity;

/** @author Steffen Jacobs */
public enum SecuredAction {
	//TODO: split into DefaultSecuredAction and e.g. PostSecuredAction
	CREATE(0b1), READ(0b10), UPDATE(0b110), DELETE(0b1110), UPDATE_ACL(0b10010), ALL(0b11111), ALL_NO_ACL(0b01111);

	private final int mask;

	private SecuredAction(int mask) {
		this.mask = mask;
	}

	public int getMask() {
		return mask;
	}

	public static SecuredAction fromMask(Integer mask) {
		for (SecuredAction sa : SecuredAction.values()) {
			if (sa.getMask() == mask) {
				return sa;
			}

		}
		return null;
	}
}
