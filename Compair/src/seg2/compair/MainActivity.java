package seg2.compair;

import introduction.IntroductionActivity;

import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (!isInternetAvailable()) {
			new NoInternetAlertDialog(this);
		}
		
		//We check if the user has already seen the introduction, and we skip it since we don't need to show the user on start-up again.
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

		String skipintro = preferences.getString("com.SEG2.skipintro", "noskip");

		if(skipintro.equals("skipintro"))
		{
			Intent intent = new Intent(this, CountrySelectActivity.class);
			startActivity(intent);
		} else {

			//We start the introductionActivity since the user has not done the introduction.
			Intent intent = new Intent(this,IntroductionActivity.class);
			startActivity(intent);
		}

	}

	/**
	 * An intent that moves on to the next Activity.
	 * It first checks for an available internet connection.
	 *
	 * @param view
	 */
	public void goToCountrySelector(View view) {
		if (isInternetAvailable()) {
			Intent intent = new Intent(this, CountrySelectActivity.class);
			startActivity(intent);

			//We write into sharedpreferences that the user has already seen the intro.
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
			SharedPreferences.Editor editor = preferences.edit();
			editor.putString("com.SEG2.skipintro","skipintro");
			editor.apply();
		} else {
			new NoInternetAlertDialog(this);
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

	private boolean isInternetAvailable() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

		if (networkInfo != null && networkInfo.isConnected())
			return true;

		return false;
	}

	/**
	 * Delete when finished. This method is an example created by Alex to show how indicators can be retrieved. 
	 * @return HashMap of all dates and values of a certain country. 
	 */
	private HashMap<String, String> getIndicators () {
		//Code to use in different classes
		//Get indicators
		String[] indicatorsID = getResources().getStringArray(R.array.indicatorID);
		String[] indicatorsName = getResources().getStringArray(R.array.indicatorName);
		HashMap<String, String> indicators = new HashMap<String, String>();

		for (int i = 0;i < indicatorsID.length; i++) {
			indicators.put(indicatorsID[i], indicatorsName[i]);
		}

		return indicators;
	}

}