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
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Toast;

@SuppressWarnings("rawtypes")
public class CountrySelectActivity extends Activity implements JSONParserListener<HashMap>, View.OnClickListener {

	private ProgressDialog dialog;
	private JSONParser parser;
	private ArrayList<Country> checkedCountries = new ArrayList<Country>();
	private ArrayList<Country> countryList;
	//Value is used in deciding whether to lock orientation for a certain period of time.
	private int prevOrientation;
	//Checkbox that is used in the alert dialog to not show alert dialog again.
	private CheckBox showmessage;
	//We check here for any user preferences.
	private SharedPreferences preferences;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_countryselect);

		// make sure we have an empty set of checked countries
		checkedCountries = new ArrayList<Country>();

		//We prevent the user from rotating the screen, causing issues with the parser sending information after the activity is destroyed.
		prevOrientation = getRequestedOrientation();
		//We get the current orientation, then we compare it to the different available orientations.
		if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			//If the orientation is landscape, we make sure it stays in landscape.
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		} else if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		} else {
			//If the orientation is going to be changed, we prevent the change.
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
		}

		// create the progress dialog
		dialog = new ProgressDialog(this, AlertDialog.THEME_HOLO_DARK);
		dialog.setMessage("Loading. Please wait...");
		dialog.setIndeterminate(true);
		dialog.setCancelable(false);
		dialog.setCanceledOnTouchOutside(false);

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

		//The orientation can be changed now.
		setRequestedOrientation(prevOrientation);

		if (result.size() == 0) {
			countryList = new ArrayList<Country>();
			new NoInternetAlertDialog(this);
		} else {
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
	}

	/**
	 * Checks if the user's device is connected to mobile data or a WiFi router.
	 *
	 * @return whether the user's device is connected to mobile data or a WiFi router.
	 */
	private boolean isInternetAvailable() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

		if (networkInfo != null && networkInfo.isConnected()) {
			return true;
		}
		return  false;
	}

	public void onClick(View v) {
		if(isInternetAvailable() && countryList.size() != 0) {
			sendCheckedCountriesToGraph();
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
		checkedCountries = new ArrayList<Country>();

		for (Country c : countryList) {
			if (c.isSelected()) {
				checkedCountries.add(c);
			}
		}

		int checkedCountriesSize = checkedCountries.size();
		if (checkedCountriesSize >= 1 && checkedCountriesSize <= 20) {

			if(checkedCountriesSize > 8)
			{

				//We check if the user has chosen not to see the dialog box that warns them about exceeding the recommended number of countries.
				preferences = PreferenceManager.getDefaultSharedPreferences(this);
				String showdialog = preferences.getString("com.SEG2.showdialog", "showdialog");

				if(showdialog.equals("showdialog"))
				{

					//We create a dialog box telling the user that the recommended limit of countries has been exceeded.
					AlertDialog.Builder builder = new AlertDialog.Builder(this);
					LayoutInflater inflater = this.getLayoutInflater();
					builder.setView(inflater.inflate(R.layout.confirmcountrycount_dialog, null));
					builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int id) {
							//If the checkbox is checked, it means the user does not want to see the message again. We add this to preference for the next start-up.
							if(showmessage.isChecked())
							{
								SharedPreferences.Editor editor = preferences.edit();
								editor.putString("com.SEG2.showdialog", "dontshowdialog");
								editor.apply();
							}
							startIntent();
						}
					});
					builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int id) {
							dialog.dismiss();
						}
					});

					// creates the dialog
					final AlertDialog alertDialog = builder.create();
					alertDialog.show();
					showmessage = (CheckBox) alertDialog.findViewById(R.id.showmessage);
				} else {
					startIntent();
				}

			} else {
				startIntent();
			}

		} else {
			Toast.makeText(getApplicationContext(), "Please choose at least 1 and most 20 countries", Toast.LENGTH_SHORT).show();
		}
	}
	/**
	 * This method starts the graph activity class using the array of countries.
	 */
	public void startIntent()
	{
		Intent intent = new Intent(this,GraphActivity.class);
		Bundle countrybundle = new Bundle();
		countrybundle.putSerializable("countries", checkedCountries);
		intent.putExtras(countrybundle);
		startActivity(intent);
	}
}
