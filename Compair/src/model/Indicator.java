package model;

import java.util.HashMap;

/**
 * Created by AlexTelek on 22/11/14.
 */
public class Indicator {
    @SuppressWarnings("unused")
	private String id;
    private String name;
    private boolean isDouble;
    private HashMap<String, String> indicatorValues;

    /**
     * Constructor of the indicator class, with creates
     * an Indicator object with the id, name and the values
     * for a specific date
     * @param id the ID of the Indicator
     * @param name the name of the indicator
     */
    public Indicator(String id, String name) {
        this.id = id;
        this.name = name;

        indicatorValues = new HashMap<String, String>();
    }

    /**
     * Add an element to the hashMap
     * with the data as the key and the value associated with it
     * @param date the key of the hashmap
     * @param value the value of the hashmap
     */
    public void addValue(String date, String value) {
        indicatorValues.put(date, value);
    }

    /**
     * Get indicators entry set
     * @return the Entry of the indicators hash map
     */
    public HashMap<String, String> getValues() {
        return indicatorValues;
    }

    /**
     * Getter method for the name of the indicator
     * to show it on the x or y coordinates
     * @return the name of the indicator
     */
    public String getName() {
        return name;
    }

    /**
     * Setter for the name of the indicator which
     * is parsed from the API call
     * @param name the name of the Indicator we creating
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Boolean variable if the indicator stores a double
     * value or just an integer. Use it when converting
     * the string to number
     * @return wheter the indicator stores double values
     */
    public boolean isDouble() {
        return isDouble;
    }

    /**
     * Set if the indicator stores double values or not
     * @param isDouble true if the value is in double
     *                 false if the value is in integer
     */
    public void setDouble(boolean isDouble) {
        this.isDouble = isDouble;
    }
}
