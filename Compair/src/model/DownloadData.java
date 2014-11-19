package model;

import android.os.AsyncTask;
import android.util.Log;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by alextelek on 19/11/14.
 * DownloadData class is a a class witch downloads data from
 * the internet and gives a JSON array to parse
 * @see android.os.AsyncTask
 */
public class DownloadData extends AsyncTask<String, String, JSONArray> {



    /**
     * Overridden method for downloading a JSON data
     * using a url
     * @see android.os.AsyncTask
     * @param params the parameters of the asynctask class
     * @return the json array
     */
    @Override
    protected JSONArray doInBackground(String... params) {
        StringBuilder content = new StringBuilder();

        try {
            HttpClient client = new DefaultHttpClient();
            HttpGet get = new HttpGet(params[0]);
            HttpResponse response = client.execute(get);
            int responseCode = response.getStatusLine().getStatusCode();

            if (responseCode == 200) {
                InputStream in = response.getEntity().getContent();
                BufferedReader reader =
                        new BufferedReader(new InputStreamReader(in));

                // Build string from input stream
                String readLine = reader.readLine();
                while (readLine != null) {
                    content.append(readLine);
                    readLine = reader.readLine();
                }
            } else {
                Log.w("Data retrieval", "Api response: " + responseCode);
                content = null;
            }

        } catch (ClientProtocolException e) {
            Log.e("readData", "CLientProtocolException during reading");
        } catch (IOException e) {
            Log.e("readData", "IOException during reading");
        }

        // return json array if the content of the
        // downloaded data is not empty
        if (content != null) {
            try {
                return new JSONArray(content.toString());
            } catch (JSONException e) {
                Log.e("JSON parse", "Error while converting string to json");
            }
        } else {
            return null;
        }

        return null;
    }
}
