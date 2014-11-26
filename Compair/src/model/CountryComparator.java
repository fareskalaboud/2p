package model;

import java.util.Comparator;

/**
 * Created by alextelek on 26/11/14.
 */
public class CountryComparator implements Comparator<Country> {

    /**
     * Custom comparator class to sort the array of coutries
     * @param country1 one country class
     * @param country2 the other country class
     * @return  0 if it equals
     * 			1 if rhs > lhs
     * 			-1 if rhs < lhs
     */
    @Override
    public int compare(Country country1, Country country2) {
        return country1.getName().compareTo(country1.getName());
    }
}
