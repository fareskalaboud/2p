package seg2.compair;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import model.Country;
import model.CountryComparator;
import model.download.JSONParser;
import model.download.JSONParserListener;

import java.util.*;

public class CountrySelectActivity extends Activity implements JSONParserListener<HashMap>, View.OnClickListener {

    public JSONParser parser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("HI THERE. I'M THE GUY YOU'RE LOOKING FOR.");
        setContentView(R.layout.activity_countryselect);

        Fonts.makeFonts(this);

        if (isInternetAvailable()) {
            parser = new JSONParser(this);
            parser.getCountries();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Overridden method from JSONParserListener
     * @see model.download.JSONParserListener
     * @param result the parsed json data (HashMap)
     */
    @Override
    public void onJSONParseFinished(String type, HashMap result) {
        System.out.println("Parsing finished");
        if (type.equals(parser.TYPE_COUNTRY)) {
            ListView listView = (ListView)findViewById(R.id.listView);
            ArrayList<Country> countryList = new ArrayList<Country>();

            Iterator iterator = result.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry pairs = (Map.Entry)iterator.next();
                countryList.add((Country)pairs.getValue());
            }

            Collections.sort(countryList, new CountryComparator());

            CountryListAdapter clAdapter = new CountryListAdapter(this, R.layout.countrylistview_row, countryList);
            listView.setAdapter(clAdapter);

            //TODO: Uncomment to get data
//            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                @Override
//                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                    parser.getIndicatorFor("GB", "SP.POP.TOTL", "1960", "2014");
//                }
//            });

        } else if(type.equals(parser.TYPE_INDICATOR)) {
            System.out.println(result);
        }
    }

    private boolean isInternetAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        }

        Toast.makeText(getApplicationContext(), "No Internet Connection...", Toast.LENGTH_SHORT).show();
        return  false;
    }

    public void onClick(View v) {}

}
