package model.download;

import android.util.Log;
import model.Country;
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

    private static final String JSON_FORMAT = "format=json";
    private static final String COUNTRIES = "http://api.worldbank.org/countries";

    /**
     * Get countries from the WorldBank API
     * @return the list of the countries
     */
    public static HashMap<String, Country> getCountries() {
        // Key - Country ID, Value - Country object
        HashMap<String, Country> countries = new HashMap<String, Country>();

        String url = COUNTRIES + "?per_page=300&" + JSON_FORMAT;
        JSONArray jsonResponse = download(url);
        if (jsonResponse != null && jsonResponse.length() > 1) {
            try {
                JSONArray jsonCountries = jsonResponse.getJSONArray(1);
                for (int i = 0; i < jsonCountries.length(); i++) {
                    JSONObject object = jsonCountries.getJSONObject(i);
                    String id = object.getString("id");
                    String name = object.getString("name");
                    String capital = object.getString("capitalCity");
                    String latitude = object.getString("latitude");
                    String longitude = object.getString("longitude");

                    // check if the country exist or not (meaning it has lat and long)
                    if (!latitude.equals("") && !longitude.equals("")) {
                        Country newCountry = new Country(id, name, capital, latitude, longitude);
                        countries.put(id, newCountry);
                    }
                }
            } catch (JSONException e) {
                Log.e("JSONException", "Get countries data at index 1");
            }
        }
        return countries;
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