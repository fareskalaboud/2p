package model;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by alextelek on 22/11/14.
 */
public class Indicator {
    private String id;
    private String name;
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
    public Map.Entry getEntrySet() {
        return (Map.Entry) indicatorValues.entrySet();
    }

    /**
     * Getter method for the name of the indicator
     * to show it on the x or y coordinates
     * @return the name of the indicator
     */
    public String getName() {
        return name;
    }
}
