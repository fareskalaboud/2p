package seg2.compair;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import model.Country;
import model.download.JSONParser;

import java.util.*;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

        Fonts.makeFonts(this);

		HashMap<String, Country> countries = new HashMap<String, Country>();
		if (isInternetAvailable()) {
			countries = JSONParser.getCountries();
		}

		ListView listView = (ListView)findViewById(R.id.listView);
		ArrayList<String> countriesName = new ArrayList<String>();

		Iterator iterator = countries.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry pairs = (Map.Entry)iterator.next();
			countriesName.add(((Country)pairs.getValue()).getName());
		}
		Collections.sort(countriesName);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, countriesName);
		listView.setAdapter(adapter);
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

	private boolean isInternetAvailable() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

		if (networkInfo != null && networkInfo.isConnected()) {
			return true;
		}

		Toast.makeText(getApplicationContext(), "No Internet Connection...", Toast.LENGTH_SHORT).show();
		return  false;
	}

    private class MyAdapter extends BaseAdapter {

        private List<Object> objects; // obviously don't use object, use whatever you really want
        private final Context context;

        public MyAdapter(Context context, List<Object> objects) {
            this.context = context;
            this.objects = objects;
        }

        @Override
        public int getCount() {
            return objects.size();
        }

        @Override
        public Object getItem(int position) {
            return objects.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            Object obj = objects.get(position);

            TextView tv = new TextView(context);
            tv.setText(obj.toString()); // use whatever method you want for the label
            tv.setTypeface(Fonts.LATO_BOLD);
            return tv;
        }
    }
}
