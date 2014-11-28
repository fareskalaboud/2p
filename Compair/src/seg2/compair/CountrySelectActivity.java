package seg2.compair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import model.Country;
import model.download.JSONParser;
import model.download.JSONParserListener;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

public class CountrySelectActivity extends Activity implements JSONParserListener<HashMap>, View.OnClickListener {

	private ProgressDialog dialog;
	private JSONParser parser;
	private ArrayList<Country> checkedCountries = new ArrayList<Country>();
	private ArrayList<Country> countryList;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		System.out.println("HI THERE. I'M THE GUY YOU'RE LOOKING FOR.");
		setContentView(R.layout.activity_countryselect);

		// create the progress dialog
		dialog = new ProgressDialog(this, AlertDialog.THEME_HOLO_DARK);
		dialog.setMessage("Loading. Please wait...");
		dialog.setIndeterminate(true);

		Fonts.makeFonts(this);

		if (isInternetAvailable()) {
			dialog.show();

			parser = new JSONParser(this);
			parser.getCountries();
		} else {
			Toast.makeText(getApplicationContext(), "No internet connection, please connect to the internet!", Toast.LENGTH_LONG).show();
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
		dialog.dismiss();

		if (type.equals(parser.TYPE_COUNTRY)) {
			ListView listView = (ListView)findViewById(R.id.listView);
			countryList = new ArrayList<Country>();

			Iterator iterator = result.entrySet().iterator();
			while (iterator.hasNext()) {
				Map.Entry pairs = (Map.Entry)iterator.next();
				countryList.add((Country)pairs.getValue());
			}

			Collections.sort(countryList);

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
		return  false;
	}

	public void onClick(View v) {
		if(isInternetAvailable()) {
			sendCheckedCountriesToGraph();
			Log.e("DONE","DONE");
		} else {
			new NoInternetAlertDialog(this);
		}
	}

	/**
	 * Checks which countries are selected and sends it
	 * to the next activity, which graphs the data it obtains
	 * from it.
	 */
	public void sendCheckedCountriesToGraph() {
		String s = new String();
		for (Country c : countryList) {
			if (c.isSelected()) {
				checkedCountries.add(c);
				
				s = s + " : " + c.getName();
			}
		}

		System.out.println("Countries checked: " + s);

		Intent intent = new Intent(this,GraphActivity.class);
		
		Bundle countrybundle = new Bundle();
		countrybundle.putParcelableArrayList("countries", (ArrayList<? extends Parcelable>) checkedCountries);
		intent.putExtras(countrybundle);
		startActivity(intent);

		// TODO: Send this in an intent
	}
	
	
	
}
