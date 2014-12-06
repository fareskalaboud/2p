package model.download;

import android.util.Log;
import model.Country;
import model.Indicator;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by alextelek on 19/11/14.
 */
public class JSONParser implements DownloadDataListener<JSONArray> {

    private final String JSON_FORMAT = "&format=json";
    private final String COUNTRIES = "http://api.worldbank.org/countries";
    private final String PAGE_300 = "per_page=300";
    private final String PAGE_100 = "&per_page=100";

    public final String TYPE_COUNTRY = "country";
    public final String TYPE_INDICATOR = "indicator";

    @SuppressWarnings("rawtypes")
	private JSONParserListener<HashMap> jsonParserListener;
    private String downloadType;

    /**
     * Constructor of the JSONParser.
     * It requires a listener to pass
     * @param jsonParserListener the listener which notifies when
     *                           the listener finished
     */
    @SuppressWarnings("rawtypes")
	public JSONParser(JSONParserListener<HashMap> jsonParserListener) {
        this.jsonParserListener = jsonParserListener;
    }

    /**
     * Get countries from the WorldBank API
     * @return the list of the countries
     */
    public void getCountries() {
        downloadType = TYPE_COUNTRY;

        String url = COUNTRIES + "?" + PAGE_300 + "&" + JSON_FORMAT;
        DownloadData data = new DownloadData(this);
        data.execute(url);
    }

    private void getCountries(JSONArray jsonResponse) {
        // Key - Country ID, Value - Country object
        HashMap<String, Country> countries = new HashMap<String, Country>();

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

        jsonParserListener.onJSONParseFinished(TYPE_COUNTRY, countries);
    }

    /**
     * Get the data for a specific indicator
     * @param countryID the country where we get the data from
     * @param indicatorID the indicatorID, we use to fetch the data
     *                    for that indicator
     * @param from the date we want to get the data from
     * @param to the date we want to get the data to
     */
    public void getIndicatorFor(final String countryID, final String indicatorID, final String from, final String to) {
        downloadType = TYPE_INDICATOR;

        String url = COUNTRIES + "/" + countryID + "/indicators/" + indicatorID + "?date=" + from + ":" + to + JSON_FORMAT + PAGE_100;
        DownloadData data = new DownloadData(new DownloadDataListener<JSONArray>() {
            @Override
            public void onDownloadFinished(JSONArray result) {
                getIndicatorFor(result, countryID, indicatorID, from, to);
            }
        });
        data.execute(url);
    }

    private void getIndicatorFor(JSONArray jsonResponse, String countryID, String indicatorID, String from, String to) {
        //The indicator which contains the values to represent it on the graph
        Indicator indicator = new Indicator(indicatorID, null);

        if (jsonResponse != null && jsonResponse.length() > 1) {
            try {
                JSONArray jsonIndicatorValues = jsonResponse.getJSONArray(1);
                String name = "";
                boolean decimal = false;

                for (int i = 0; i < jsonIndicatorValues.length(); i++) {
                    JSONObject object = jsonIndicatorValues.getJSONObject(i);
                    JSONObject indicatorName = object.getJSONObject("indicator");
                    name = indicatorName.getString("value");
                    String value = object.getString("value");
                    decimal = Boolean.valueOf(object.getString("decimal"));
                    String date = object.getString("date");

                    // check if the value for a date is null or not
                    // if it is, insert 0
                    if (!value.equals("null")) {
                        indicator.addValue(date, value);
                    } else {
                        indicator.addValue(date, "0");
                    }
                }

                // Add the values to the indicator class
                indicator.setName(name);
                indicator.setDouble(decimal);

            } catch (JSONException e) {
                Log.e("JSONException", "Get indicator for country " + countryID + " data error");
            }
        }

        HashMap<String, Indicator> indicatorHashMap = new HashMap<String, Indicator>();
        indicatorHashMap.put(indicatorID, indicator);

        jsonParserListener.onJSONParseFinished(TYPE_INDICATOR, indicatorHashMap);
    }

    /**
     * Overridden method from DownloadDataListener
     * @param result the object what the download
     */
    @Override
    public void onDownloadFinished(JSONArray result) {
        if (downloadType.equals(TYPE_COUNTRY)) {
            System.out.println("Download finished");
            getCountries(result);
        }
    }
}