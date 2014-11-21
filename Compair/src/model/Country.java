package model;

public class Country {

	private String id;
	private String capital;
	private String name;
	private String latitude;
	private String longitude;

	public Country() {

		name = this.name;
		id = this.id;
		capital = this.capital;
		longitude = this.longitude;
		latitude = this.latitude;

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
