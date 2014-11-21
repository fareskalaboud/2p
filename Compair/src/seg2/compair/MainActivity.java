package seg2.compair;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import model.Country;
import model.download.JSONParser;

import java.util.*;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

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
}
