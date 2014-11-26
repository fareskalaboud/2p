package seg2.compair;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.view.*;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Fonts.makeFonts(this);

        if (!isInternetAvailable()) {
            openAlert(new View(getApplicationContext()));
        }
	}

    public void goToCountrySelector(View view) {
        if (isInternetAvailable()) {
            Intent intent = new Intent(this, CountrySelectActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(this.getApplicationContext(), "You need a working internet connection!", Toast.LENGTH_LONG).show();
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

        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        }
        return  false;
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

    private void openAlert(View view) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this, AlertDialog.THEME_HOLO_DARK);

        alertDialogBuilder.setTitle("No Internet Connection");
        alertDialogBuilder.setMessage("You need a working internet connection to use this app. Please check that you are connected.");
        // set positive button: Yes message
        alertDialogBuilder.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // go to a new activity of the app
                startActivityForResult(new Intent(Settings.ACTION_WIFI_SETTINGS), 0);
            }
        });
        // set negative button: No message
        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // cancel the alert box and put a Toast to the user
                dialog.cancel();
                Toast.makeText(getApplicationContext(), "You chose a negative answer",
                        Toast.LENGTH_LONG).show();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        // show alert
        alertDialog.show();
    }

}