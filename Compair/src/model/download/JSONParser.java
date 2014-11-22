package model.download;

import android.util.Log;
import model.Country;
import model.Indicator;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

/**
 * Created by alextelek on 19/11/14.
 */
public class JSONParser {

    private static final String JSON_FORMAT = "&format=json";
    private static final String COUNTRIES = "http://api.worldbank.org/countries";
    private static final String PAGE_300 = "per_page=300";
    private static final String PAGE_100 = "&per_page=100";

    /**
     * Get countries from the WorldBank API
     * @return the list of the countries
     */
    public static HashMap<String, Country> getCountries() {
        // Key - Country ID, Value - Country object
        HashMap<String, Country> countries = new HashMap<String, Country>();

        String url = COUNTRIES + "?" + PAGE_300 + "&" + JSON_FORMAT;
        JSONArray jsonResponse = download(url);
        if (jsonResponse != null && jsonResponse.length() > 1) {
            try {
                JSONArray jsonCountries = jsonResponse.getJSONArray(1);
                for (int i = 0; i < jsonCountries.length(); i++) {
                    JSONObject object = jsonCountries.getJSONObject(i);
                    String id = object.getString("id");
                    String name = object.getString("name");
                    JSONObject region = object.getJSONObject("region");
                    String regionName = region.getString("value");
                    String capital = object.getString("capitalCity");
                    String latitude = object.getString("latitude");
                    String longitude = object.getString("longitude");

                    // check if the country exist or not (meaning it has lat and long)
                    if (!latitude.equals("") && !longitude.equals("")) {
                        Country newCountry = new Country(id, name,regionName, capital, latitude, longitude);
                        countries.put(id, newCountry);
                    }
                }
            } catch (JSONException e) {
                Log.e("JSONException", "Get countries data at index 1");
            }
        }
        return countries;
    }

    public static Indicator getIndicatorFor(String countryID, String indicatorID, String from, String to) {
        //The indicator which contains the values to represent it on the graph
        Indicator indicator = new Indicator(indicatorID, null);

        String url = COUNTRIES + "/" + countryID + "/indicators/" + indicatorID + "?date=" + from + ":" + to + JSON_FORMAT + PAGE_100;
        JSONArray jsonResponse = download(url);
        if (jsonResponse != null && jsonResponse.length() > 1) {
            try {
                JSONArray jsonIndicatorValues = jsonResponse.getJSONArray(1);
                for (int i = 0; i < jsonIndicatorValues.length(); i++) {
                    JSONObject object = jsonIndicatorValues.getJSONObject(i);
                    JSONObject indicatorName = object.getJSONObject("indicator");
                    String name = indicatorName.getString("value");
                    String value = object.getString("value");
                    boolean decimal = object.getBoolean("decimal");
                    String date = object.getString("date");

                    // Add the values to the indicator class
                    indicator.setName(name);
                    indicator.setDouble(decimal);

                    // check if the value for a date is null or not
                    // if it is, insert 0
                    if (value != null) {
                        indicator.addValue(date, value);
                    } else {
                        indicator.addValue(date, "0");
                    }
                }
            } catch (JSONException e) {
                Log.e("JSONException", "Get indicator for country " + countryID + " data error");
            }
        }

        return indicator;
    }

    // Execute download request and return a JSONArray
    // with the data
    private static JSONArray download(String url) {
        DownloadData data = new DownloadData();
        data.execute(url);

        try {
            return data.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return null;
    }
}