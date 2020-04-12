package me.steffenjacobs.supersocial.domain.dto;

/** @author Steffen Jacobs */
public class LocationDTO implements WithErrorDTO {
	private double latitude;
	private double longitude;
	private String error;
	private String locationName;

	public LocationDTO() {

	}

	public LocationDTO(String error) {
		this.error = error;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public String getLocationName() {
		return locationName;
	}

	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}

	@Override
	public String getError() {
		return this.error;
	}

}
