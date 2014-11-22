package model;

public class Country {

	private String id;
	private String capital;
	private String name;
	private String region;
	private String latitude;
	private String longitude;

	public Country(String id, String name, String region, String capital, String latitude, String longitude) {

		this.name = name;
		this.id = id;
		this.region = region;
		this.capital = capital;
		this.latitude = latitude;
		this.longitude = longitude;

	}

	public String getName() {
		return name;
	}

	public String getId() {
		return id;
	}

	public String getCapital() {
		return capital;
	}

	public String getLatitude() {
		return latitude;
	}

	public String getLongitude() {
		return longitude;
	}
}
