package model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class Country implements Comparable<Country>, Serializable {

    private static final long serialVersionUID = -1507630908466214585L;
    private String id;
    private String capital;
    private String name;
    private String region;
    private String latitude;
    private String longitude;
    private boolean selected;

    /**
     * Constrctor of the Country class to create a country
     * object and store its data
     *
     * @param id        the ID of the country
     * @param name      the presentation name of the country
     * @param region    the region where the country is located
     * @param capital   the capital city of the country
     * @param latitude  the latitude of the country (middle)
     * @param longitude the longitude of the country (middle)
     */
    public Country(String id, String name, String region, String capital, String latitude, String longitude) {

        this.name = name;
        this.id = id;
        this.region = region;
        this.capital = capital;
        this.latitude = latitude;
        this.longitude = longitude;
        this.selected = false;

    }

    public Country() {
    }

    /**
     * Getter for the name of the country
     *
     * @return the name of the country
     */
    public String getName() {
        return name;
    }

    /**
     * Getter for the ID of the country
     *
     * @return the ID of the country
     */
    public String getId() {
        return id;
    }

    /**
     * Getter for the capital of the country
     *
     * @return the capital of the country
     */
    public String getCapital() {
        return capital;
    }

    /**
     * Getter for the latitude of the country
     *
     * @return the latitude of the country
     */
    public String getLatitude() {
        return latitude;
    }

    /**
     * Getter for the longitude of the country
     *
     * @return the longitude of the country
     */
    public String getLongitude() {
        return longitude;
    }

    /**
     * Getter for the region of the country
     *
     * @return the region of the country
     */
    public String getRegion() {
        return region;
    }

    /**
     * Getter for whether the country is selected
     *
     * @return whether the country is selected
     */
    public boolean isSelected() {
        return selected;
    }

    /**
     * Setter for whether the country is selected
     *
     * @param selected whether the country is selected
     */
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    /**
     * The toString method of Country, which returns
     * the country's name.
     *
     * @return the country's name
     */
    @Override
    public String toString() {
        return name;
    }

    /**
     * Custom comparator class to sort the array of coutries
     *
     * @param another the other country class
     * @return 0 if it equals
     * 1 if rhs > lhs
     * -1 if rhs < lhs
     */
    @Override
    public int compareTo(Country another) {
        return name.compareTo(another.name);
    }
}
