package seg2.compair;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.*;

import java.util.HashMap;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Fonts.makeFonts(this);

        if (!isInternetAvailable()) {
			new NoInternetAlertDialog(this);
		}

	}

    public void goToCountrySelector(View view) {
        if (isInternetAvailable()) {
            Intent intent = new Intent(this, CountrySelectActivity.class);
            startActivity(intent);
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

	// Get indicators that we are using from strings.xml
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