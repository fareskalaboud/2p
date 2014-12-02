package seg2.compair;

import java.util.*;

import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.*;
import model.Country;
import model.download.JSONParser;
import model.download.JSONParserListener;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

@SuppressWarnings("rawtypes")
public class CountrySelectActivity extends Activity implements JSONParserListener<HashMap>, View.OnClickListener {

	private ProgressDialog dialog;
	private ListView countriesListView;
	private ListView alliancesListView;
	private RadioButton countryRadioButton;
	private RadioButton allianceRadioButton;
	private CountryListAdapter clAdapter;
	private Button graphButton;
	private EditText filterWidget;

	private JSONParser parser;
	private ArrayList<Country> checkedCountries = new ArrayList<Country>();
	private ArrayList<Country> countryList;

	private final ArrayList<String> alliancesName = new ArrayList<String>() {{add("NATO"); add("SCO"); add("BRICS"); add("ASEAN");}};
	private ArrayList<Country> natoCountries = new ArrayList<Country>();
	private ArrayList<Country> scoCountries = new ArrayList<Country>();
	private ArrayList<Country> bricsCountries = new ArrayList<Country>();
	private ArrayList<Country> aseanCountries = new ArrayList<Country>();

	//Value is used in deciding whether to lock orientation for a certain period of time.
	private int prevOrientation;

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_countryselect);

		// make sure we have an empty set of checked countries
		checkedCountries = new ArrayList<Country>();

		// register widgets
		countriesListView = (ListView) findViewById(R.id.countryListView);
		countriesListView.setVisibility(View.VISIBLE);
		alliancesListView = (ListView) findViewById(R.id.allianceListView);
		alliancesListView.setVisibility(View.GONE);
		graphButton = (Button) findViewById(R.id.graphButton);
		filterWidget = (EditText) findViewById(R.id.filter);

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

//		Fonts.makeFonts(this);

        configureRadioButtons();

		if (isInternetAvailable()) {
			dialog.show();

			parser = new JSONParser(this);
			parser.getCountries();

            filterWidget.setTypeface(Typeface.createFromAsset(getAssets(), "fontawesome-webfont.ttf"));

            filterWidget.addTextChangedListener(new TextWatcher() {
                public void afterTextChanged(Editable s) {
                }

                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if(clAdapter != null) {
                        clAdapter.getFilter().filter(s.toString());
                    }
                }
            });
		} else {
			Toast.makeText(getApplicationContext(), "No internet connection, please connect to the internet!", Toast.LENGTH_LONG).show();
		}
	}

    private void configureRadioButtons() {
        countryRadioButton = (RadioButton) findViewById(R.id.countryRadioButton);
		countryRadioButton.setChecked(true);
		countryRadioButton.setTextColor(new Color().rgb(0, 178, 255));
        allianceRadioButton = (RadioButton) findViewById(R.id.allianceRadioButton);

        countryRadioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    countryRadioButton.setTextColor(new Color().rgb(0, 178, 255));
                    allianceRadioButton.setTextColor(Color.BLACK);
					countriesListView.setVisibility(View.VISIBLE);
					alliancesListView.setVisibility(View.GONE);
					graphButton.setVisibility(View.VISIBLE);
					filterWidget.setEnabled(true);
                }
            }
        });

        allianceRadioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    allianceRadioButton.setTextColor(new Color().rgb(0, 178, 255));
                    countryRadioButton.setTextColor(Color.BLACK);
					alliancesListView.setVisibility(View.VISIBLE);
					countriesListView.setVisibility(View.GONE);
					graphButton.setVisibility(View.GONE);
					filterWidget.setEnabled(false);
                }
            }
        });
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
				countriesListView = (ListView)findViewById(R.id.countryListView);
				countryList = new ArrayList<Country>();

				// alliances
				ArrayList<String> natoCountriesString = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.NATO)));
				ArrayList<String> scoCountriesString = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.SCO)));
				ArrayList<String> bricsCountriesString = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.BRICS)));
				ArrayList<String> aseanCountriesString = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.ASEAN)));

				natoCountries = new ArrayList<Country>();
				scoCountries = new ArrayList<Country>();
				bricsCountries = new ArrayList<Country>();
				aseanCountries = new ArrayList<Country>();

				Iterator iterator = result.entrySet().iterator();
				while (iterator.hasNext()) {
					Map.Entry pairs = (Map.Entry)iterator.next();

					Country currentCountry = (Country)pairs.getValue();
					if (natoCountriesString.contains(currentCountry.getId())) natoCountries.add(currentCountry);
					if (scoCountriesString.contains(currentCountry.getId())) scoCountries.add(currentCountry);
					if (bricsCountriesString.contains(currentCountry.getId())) bricsCountries.add(currentCountry);
					if (aseanCountriesString.contains(currentCountry.getId())) aseanCountries.add(currentCountry);

					countryList.add((Country)pairs.getValue());
				}

				// Sort the countries by name and
				// pass these to the listView
				Collections.sort(countryList);
				clAdapter = new CountryListAdapter(this, R.layout.countrylistview_row, countryList);
				countriesListView.setAdapter(clAdapter);

				// Set the alliances to the other list view, so when the
				// the user switches it can see all the alliances we have
				setUpAlliancesListView();

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

	// Method for setting up the alliancesList view with an adapter and listener
	private void setUpAlliancesListView() {
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, alliancesName);
		alliancesListView.setAdapter(adapter);
		alliancesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				String allianceName = alliancesName.get(position);

				if (allianceName.equals("NATO")) presentNextView(natoCountries);
				else if (allianceName.equals("SCO")) presentNextView(scoCountries);
				else if (allianceName.equals("BRICS")) presentNextView(bricsCountries);
				else presentNextView(aseanCountries);
			}
		});
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
	@SuppressWarnings("unchecked")
	public void sendCheckedCountriesToGraph() {
		checkedCountries = new ArrayList<Country>();

		for (Country c : countryList) {
			if (c.isSelected()) {
				checkedCountries.add(c);
			}
		}

		System.out.println(checkedCountries.size());

		if (checkedCountries.size() >= 1 && checkedCountries.size() <= 20) {
			presentNextView(checkedCountries);
		} else {
			Toast.makeText(getApplicationContext(), "Please choose at least 1 and most 20 countries", Toast.LENGTH_SHORT).show();
		}
	}

	private void presentNextView(ArrayList<Country> countries) {
		Intent intent = new Intent(this,GraphActivity.class);

		Bundle countrybundle = new Bundle();
		countrybundle.putSerializable("countries", countries);
		intent.putExtras(countrybundle);
		startActivity(intent);
	}
}
