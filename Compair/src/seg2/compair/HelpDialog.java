package seg2.compair;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.text.Html;
import android.view.View;

/**
 * Created by FaresAlaboud on 3/12/14.
 */
public class HelpDialog {

    Activity activity;

    /**
     * The HelpDialog class represents an
     * alert dialog on the GraphActivity that has instructions
     * for the user on how to use the graph on the page.
     * @param activity the Activity that the dialog will
     *                 appear on.
     */
    public HelpDialog(Activity activity) {
        this.activity = activity;
        openAlert(new View(activity.getApplicationContext()));
    }

    /**
     * Opens the alert view.
     * @param view Unused view that is required for the method
     */
    private void openAlert(View view) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity, AlertDialog.THEME_HOLO_DARK);
        alertDialogBuilder.setTitle("How to Use");

        alertDialogBuilder.setMessage(Html.fromHtml("On this page, you can see a graph with two axes: the x-axis and y-axis." +
                "<br><br>By default, the x-axis is set to <i>Date</i>. You can change the y-axis to see the progress of" +
                "a statistic of your choice over time.<br><br>You must press on the <u>Update</u> button to update the graph." +
                "<br><br>If you'd like to compare two different statistics over and see their progress over the years " +
                "on a scatter graph, you can unlock the x-axis using the lock button and set the x-axis and y-axis " +
                "to two different statistics. <br><br>Once you've pressed on the <u>Update</u> button after setting both axes," +
                " you will see a slider on the bottom of the screen where you can see the progress of both statistics" +
                " over the years. <br><br><b>NOTE:</b> Some countries may be missing data from certain years."));

        alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        // show alert
        alertDialog.show();
    }
}