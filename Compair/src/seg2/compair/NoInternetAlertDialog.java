package seg2.compair;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;
import android.view.View;

/**
 * Created by faresalaboud on 26/11/14.
 */
public class NoInternetAlertDialog {

    Activity activity;

    public NoInternetAlertDialog(Activity activity) {
        this.activity = activity;
        openAlert(new View(activity.getApplicationContext()));
    }

    private void openAlert(View view) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity, AlertDialog.THEME_HOLO_DARK);

        alertDialogBuilder.setTitle("No Internet Connection");
        alertDialogBuilder.setMessage("You need a working internet connection to use this app. Please check that you are connected.");
        alertDialogBuilder.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                activity.startActivityForResult(new Intent(Settings.ACTION_WIFI_SETTINGS), 0);
            }
        });
        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        // show alert
        alertDialog.show();
    }
}
