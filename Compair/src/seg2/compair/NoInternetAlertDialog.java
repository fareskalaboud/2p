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

    /**
     * The NoInternetAlertDialog class represents an
     * alert dialog on the Activity passed into the
     * class in the constructor to warn the user that
     * they require an internet connection to use the app.
     * @param activity the Activity that the dialog will
     *                 appear on.
     */
    public NoInternetAlertDialog(Activity activity) {
        this.activity = activity;
        openAlert(new View(activity.getApplicationContext()));
    }

    /**
     * Opens the alert view.
     * @param view Unused view that is required for the method
     */
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
