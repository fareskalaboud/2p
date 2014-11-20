package model.storage;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by alextelek on 20/11/14.
 */
public class Store {

    public static final String HOME = "home";
    public static final String LAST_DESTINATION = "last_destination";

    /**
     * Save data to the phone's local storage for further reference
     * no need to download it again
     * @param origin the origin acivity that calls the saving
     * @param key the key where the value will be stored
     * @param value the object we want to store
     */
    public static void saveData(Activity origin, String key, String value) {
        SharedPreferences sharedPreferences = origin.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    /**
     * Load data from the local storage for a specifi key
     * If there is no data stored in the local storage
     * it will return null
     * @see android.content.SharedPreferences
     * @param origin the origin activity where we are loading from
     * @param key the key associated with a value
     * @return the value if exist otherwise null
     */
    public static String loadData(Activity origin, String key) {
        SharedPreferences sharedPreferences = origin.getPreferences(Context.MODE_PRIVATE);
        return sharedPreferences.getString(key, "");
    }
}
