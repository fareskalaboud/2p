package seg2.compair;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;

import android.graphics.Color;
import android.view.*;
import model.Country;
import model.Indicator;
import model.download.JSONParser;
import model.download.JSONParserListener;
import model.graph.LineGraph;
import model.graph.ScatterGraph;

import org.achartengine.GraphicalView;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * This class defines the graph UI and the functionality to change indicators. 
 * @author Sean
 * @author Amrinder
 */
@SuppressWarnings("rawtypes")
public class GraphActivity extends Activity implements JSONParserListener<HashMap> {

	//We use this for the dates
	private ArrayList<String> dates = new ArrayList<String>();

	//Spinners for the x indicators and the y indicators
	private Spinner xindicator;
	private Spinner yindicator;
	//SeekBar for Dual indicators
	private SeekBar datesSeekBar;

	//progress bar for loading
	private ProgressDialog dialog;

	//Text for the date, invisible default
	private TextView datetext;

	//Update button
	private Button update;
	//The labels for the x and y axis. 
	private String xLabel = "x";
	private String yLabel = "y";
	//The object to manipulate the line graph. 
	private LineGraph graph = new LineGraph();
	//The object to manipulate the scatter graph.
	private ScatterGraph scatterGraph = new ScatterGraph();
	//The name of the indicator. 
	private String IndicatorName;
	//The example list of countries.
	private ArrayList<Country> countries;
	//The count of countries used to add DataSets.
	private int countriescount = 0;
	//The layout we add the graph to, and the loading animation.
	private LinearLayout layout;

	//ImageView of the lock to unlock the X axis.
	private Button lock;
	//ImageView of the switch indicator.
	private ImageView switchindicator;

	//Boolean that is used to see if the lock is unlocked or locked.
	private boolean isOpen = false;
	//Boolean that is used to see if the seekbar is visible.
	private boolean seekBarIsVisible = false;
	//Boolean that is used to see if a graph has been made in the view.
	private boolean lineGraphExists = false;
	private boolean scatterGraphExists = false;
	//These two strings represent the name of indicators when using dual indicators.
	private String IndicatorNamey;
	private String IndicatorNamex;

	//This is the axis count, we use this when using dual indicators to tell what axis information we are 
	//listening for.
	private int axiscount = 0;
	//This count is used when deciding if the call that the dates spinner activates is null and to not be used
	private int accessseekbarcount  = 0;
	//Used for the SeekBar, to get the correct year and the number of years to display.
	private int numberOfYears;
	//The minimum and maximum value for the scatter graph in two arrays.
	private double[] xaxisminmax;
	private double[] yaxisminmax;
	//The default year string for the dual indicators. 
	private String year = "1960";

	//we use this button to fit the graph to view.
	private Button fittoview; 
	//We get the linear layout this button has to be added to.
	private LinearLayout bottomlayout;
	//This is the help dialog button.
	private Button help;

	//Values used in the play/stop button.
	private int i = 0;
	private boolean hasStopped = false;
	private boolean isRunning = false;
	//The Play button that is used to auto increment the SeekBar.
	private Button playbutton;
	//Counter for loading countries
	private int counter = 0;

	//Image for no data picture
	private ImageView nodata;

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_graph);

		//We get the string array from CountrySelectActivity.
		countries = (ArrayList<Country>) getIntent().getSerializableExtra("countries");

		//We add the years from startYear to endYear into an ArrayList. 
		for(int i = 1960; i<=2012; i++)
		{
			dates.add(String.valueOf(i));
		}

		//We get the total size of the dates for the SeekBar,.
		numberOfYears = dates.size();

		// We get the x any y indicators and update button.
		xindicator = (Spinner)findViewById(R.id.xspinner);
		yindicator = (Spinner)findViewById(R.id.yspinner);
		update = (Button)findViewById(R.id.update);

		//Initialize the layout to add animations and graphs to.
		layout = (LinearLayout) findViewById(R.id.chart);
		//We add the background tutorial to the graph background and the nodata picture.
		nodata = new ImageView(this);

		//We set the background to be the nodata symbol, we also set the correct parameters.
		nodata = new ImageView(this);
		nodata.setBackgroundResource(R.drawable.nodata);

		nodata.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));
		ImageView background = new ImageView(this);
		//If the orientation is landscape we use a different image
		if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
		{
			background.setImageResource(R.drawable.graphbackgroundlarge);
		} else {
			background.setImageResource(R.drawable.graphbackground);
		}

		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.CENTER;
		background.setLayoutParams(params);
		layout.addView(background);
		//We initialize the ImageView of the lock.
		lock = (Button)findViewById(R.id.xlock);
		//We initialize the ImageView of the switch indicator. 
		switchindicator = (ImageView)findViewById(R.id.switchindicator);
		//We initialize the play button
		playbutton = (Button)findViewById(R.id.playbutton);
		playbutton.setVisibility(View.GONE);
		//Initialize the date text for dual indicators.
		datetext = (TextView)findViewById(R.id.datetext);
		//Initialize the FitToView button.
		fittoview = (Button) findViewById(R.id.fittoview);

		fittoview.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) {
				if(lineGraphExists == true)
				{
					//We add the view of the final graph.
					layout.removeAllViews();
					resetView();
				}
			}
		});
		//We get the layout fit to view button is added to.
		bottomlayout = (LinearLayout)findViewById(R.id.bottomlayout);

		/*
		 * This spinner is kept invisible till unlocked when the dual indicators are unlocked. 
		 */
		datesSeekBar = (SeekBar)findViewById(R.id.datespinner);
		datesSeekBar.setVisibility(View.GONE);

		//We Initialize the help ImageView button.
		help = (Button)findViewById(R.id.btnHelp);


		//We set the maximum number of years for the SeekBar.
		datesSeekBar.setMax(numberOfYears-1);
		//Listener for the SeekBar on dual indicators. 
		datesSeekBar.setOnSeekBarChangeListener(new seekBarListener());

		//We add adapters to the x and y spinners, to edit the labels to match the correct chosen label. 
		setXAdapterDate();
		//We get set the x indicator listener. 
		xindicator.setOnItemSelectedListener(new OnItemSelectedListener(){

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				xLabel = xindicator.getSelectedItem().toString();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
			}
		});

		//We create the adapter for the list view, using array indicatorName to fill the list. 
		ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,
				R.array.indicatorName, android.R.layout.simple_spinner_item);
		adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		yindicator.setAdapter(adapter2);
		//upon making a selection we set the label string correctly. 
		yindicator.setOnItemSelectedListener(new OnItemSelectedListener(){
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				yLabel = yindicator.getSelectedItem().toString();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
			}
		});


		//We add the list of countries to the scatter graph class.
		scatterGraph.addCountryList(countries);
	}


	/**
	 * This method resets the graph view. Used by the fit to view button in the graph activity.
	 */
	@SuppressWarnings("deprecation")
	private void resetView() {

		//We add the view of the final graph.
		GraphicalView view = graph.getLineView(getApplicationContext());

		layout.addView(view, new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
		scatterGraphExists = false;
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		//We save the current data when switching orientations.
		//We save the Lock status, SeekBar status, x and y indicators and if a graph has been created.
		//We also give the Scatter Graph and the Line Graph to easily repaint the view onto the activity.
		outState.putBoolean("lock", isOpen);
		outState.putBoolean("seekbar", seekBarIsVisible);
		outState.putBoolean("lineGraphExists", lineGraphExists);
		outState.putBoolean("scatterGraphExists", scatterGraphExists);
		outState.putInt("xindicator", xindicator.getSelectedItemPosition());
		outState.putInt("yindicator", yindicator.getSelectedItemPosition());
		outState.putInt("yearPosition", datesSeekBar.getProgress());
		outState.putString("year", year);
		outState.putSerializable("scattergraph", scatterGraph);
		outState.putSerializable("linegraph", graph);
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onRestoreInstanceState(Bundle savedState) {
		super.onRestoreInstanceState(savedState);
		// restore the current data, for instance when changing the screen
		// orientation
		/*
		 * We get the lock, if the SeekBar is visible, if the graphs exists, the position of the indicators
		 * the year that is currently shown on the TextView, the position of the SeekBar if it exists, 
		 * the graph itself as two graphical views.
		 */
		isOpen= savedState.getBoolean("lock");
		seekBarIsVisible = savedState.getBoolean("seekbar");
		lineGraphExists = savedState.getBoolean("lineGraphExists");
		scatterGraphExists = savedState.getBoolean("scatterGraphExists");
		int xindicatorpos = savedState.getInt("xindicator");
		int yindicatorpos = savedState.getInt("yindicator");
		year = savedState.getString("year");
		int yearPosition = savedState.getInt("yearPosition");
		graph = (LineGraph) savedState.getSerializable("linegraph");
		scatterGraph = (ScatterGraph) savedState.getSerializable("scattergraph");
		//If a LineGraph exists, we want to build it again in this view.
		if(lineGraphExists == true)
		{
			//We add the view of the final graph.
			layout.removeAllViews();
			layout.addView(graph.getLineView(this), new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
			scatterGraphExists = false;

		}

		//if a scatter graph exists, we want to build it again in this view.
		if(scatterGraphExists == true)
		{
			//We remove the fit the view button.
			bottomlayout.removeView(fittoview);
			//We add the view of the final graph.
			layout.removeAllViews();
			layout.addView(scatterGraph.getScatterGraph(this), new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
			lineGraphExists = false;

			//We set the SeekBar to active
			accessseekbarcount = 1;
			//We set the spinner position
			datesSeekBar.setProgress(yearPosition);

			//We get the minimum/maximum values for the axis.
			xaxisminmax = scatterGraph.getXMinMax();
			yaxisminmax = scatterGraph.getYMinMax();
			//We set the lock open, and a scatter graph already exists in the ViewPort.
			if(isOpen == true)
			{
				setDualIndicator(true, year, false);
			}

			//In this method we remove the old dates, and add the new dates that are available.
			ArrayList<String> scattercountries = scatterGraph.getAvailableYears(countries);

			dates.clear();
			for(String x : scattercountries)
			{
				dates.add(x);
			}

			numberOfYears = dates.size();

			datesSeekBar.setMax(numberOfYears-1);
		} else if(isOpen == true)
		{
			setDualIndicator(false, year, true);
		}


		//We set the indicators to represent the indicators selected before orientation switch.
		xindicator.setSelection(xindicatorpos);
		yindicator.setSelection(yindicatorpos);
	}

	/**
	 * This method creates a dialog box with a confirm button. 
	 * @param message The message that we want to tell the user. 
	 */
	public void createMissingDialog(String message)
	{
		//We create a dialog to confirm the user wants to open the lock and use dual indicators. 

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		LayoutInflater inflater = this.getLayoutInflater();
		builder.setView(inflater.inflate(R.layout.confirmindicator_dialog, null));

		builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int which) {

				dialog.dismiss();
			}
		});

		final AlertDialog alertDialog = builder.create();

		alertDialog.show();
		TextView description = (TextView)alertDialog.findViewById(R.id.description);
		description.setText(message);


	}
	/**
	 * This class creates the help dialog upon user pressing the question mark.
	 * @param view
	 */
	public void openHelpDialog(View view) {
		new HelpDialog(this);
	}

	/**
	 * Class defines the time SeekBar, changing the graph every time the time changes.
	 * @author Sean
	 */
	private class seekBarListener implements SeekBar.OnSeekBarChangeListener{

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
			//This method handles getting us the graph on the seekbar changing.
			getDualGraph();
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
		}

	}
	/**
	 * This method is used by the play button to start stop incrementing the SeekBar.
	 * @param v
	 */
	public void playbutton(View v)
	{
		if(isRunning == false)
		{
			//IF the button is not running already.
			playbutton.setBackgroundResource(R.drawable.custompausebutton);
			hasStopped = false;
			startAnimationSeek();
			isRunning = true;
		} else {
			playbutton.setBackgroundResource(R.drawable.customplaybutton);
			hasStopped = true;
			isRunning = false;
		}
	}

	/**
	 * This method is used by the switch indicators ImageView, to execute the animation and to switch the indicators in the spinners.
	 * @param v
	 */
	public void switchIndicators(View v)
	{
		if(isOpen == false)
		{

		} else {
			final Animation animRotate = AnimationUtils.loadAnimation(this, R.anim.rotateindicator);
			switchindicator.startAnimation(animRotate);
			//We switch their positions around. 
			int positionx = xindicator.getSelectedItemPosition();
			int positiony = yindicator.getSelectedItemPosition();

			yindicator.setSelection(positionx);
			xindicator.setSelection(positiony);

			if(scatterGraphExists == true)
			{

				scatterGraph.switchDataSets();
				double[] tempxminmax = xaxisminmax;

				xaxisminmax = yaxisminmax;
				yaxisminmax = tempxminmax;
				String tempLabel = xLabel;
				xLabel = yLabel;
				yLabel = tempLabel;
				getDualGraph();
			}
		}

	}
	/**
	 * This method gets the graph from the stored data in the scatter graph class for a particular year
	 * based on the countries and the year selected on the seekbar.
	 */
	@SuppressWarnings("deprecation")
	public void getDualGraph()
	{
		for(int i=0; i<numberOfYears; i++) {

			if(datesSeekBar.getProgress()==i) {
				String date = dates.get(i);
				datetext.setText(date);
				year = date;

				//get data for year i

				if(accessseekbarcount == 0)
				{//Do nothing, the graph has not been created yet to modify with the seekbar.
				} else {
					//We remove the old graph from view and set up the renderer and dataset.
					layout.removeAllViews();
					scatterGraph.clearAll(xLabel, yLabel);
					//We add the country data to the graph
					for(Country country: countries)
					{
						scatterGraph.addDataToGraph(country, date);
					}
					//We set the minimum and maximum values for the graphs. This is to prevent them from changing each time the slider changes. 
					scatterGraph.setXAxisMinMax(xaxisminmax[0],xaxisminmax[1]);
					scatterGraph.setYAxisMinMax(yaxisminmax[0],yaxisminmax[1]);
					//We add the view of the final graph.
					layout.addView(scatterGraph.getScatterGraph(getApplicationContext()), new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));

				}
			}
		}
	}
	/**
	 * This method handles changing the UI to support dual indicators.
	 * @param scatterexists Does a ScatterGraph already exist in ViewPort.
	 * @param year The year that we want to set the date text at (Usually startYear).
	 * @param removeView Do we want to remove all views out of graph(Usually yes, unless orientation change).
	 */
	public void setDualIndicator(boolean scatterexists, String year, boolean removeView)
	{
		//We remove the button that allows users to fit the line graph to view.
		bottomlayout.removeView(fittoview);

		//We change the help dialog button to blue
		help.setBackgroundResource(R.drawable.customdialoghelpblue);
		//We set the play button to be seen.
		playbutton.setVisibility(View.VISIBLE);
		//We set the TextField to now show a date.
		datetext.setText(year);
		datetext.setTextSize(30f);
		//We set the visibility of the SeekBar to visible. 
		datesSeekBar.setVisibility(View.VISIBLE);
		//We set the adapter up, and change the  lock image to an unlocked image. 
		setXAdapterArray();
		//We change the lock image to an unlocked image.
		lock.setBackgroundResource(R.drawable.customlockopen);
		//We change the Color of the switch indicator, indicating it is now possible to switch.
		switchindicator.setImageResource(R.drawable.switchindicatorblue);

		if(removeView == true)
		{
			//Remove all views out of the graph if there was any ready for the next graph. 
			layout.removeAllViews();
			layout.addView(nodata);


		}

		//Both graphs do not exist currently, as the view is now empty.
		lineGraphExists = false;
		scatterGraphExists = scatterexists;
		//Enable x indicator spinner.
		xindicator.setEnabled(true);
		//the lock is now open. 
		isOpen = true;
		//The SeekBar is available.
		seekBarIsVisible = true;

	}
	/**
	 * This method is called if user presses the lock button. 
	 * @param v The view.
	 */
	public void lock(View v)
	{
		//This if statement checks if the lock is already open and if the update method is running.
		if(isOpen == false && update.isEnabled() == true){
			//We create a dialog to confirm the user wants to open the lock and use dual indicators. 

			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			LayoutInflater inflater = this.getLayoutInflater();
			builder.setView(inflater.inflate(R.layout.confirmindicator_dialog, null));

			builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface dialog, int which) {

					setDualIndicator(false, "1960", true);
				}
			});

			builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface dialog, int which) {

					dialog.dismiss();
				}
			});

			final AlertDialog alertDialog = builder.create();
			alertDialog.show();
		} else if(update.isEnabled() == true) {
			//This means the lock is currently closed, and we want to go back to line graph.

			//We add the fit to view button to the bottom layout and set the onClickListener.
			bottomlayout.addView(fittoview, new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));

			//We turn the help dialog button back to gray
			help.setBackgroundResource(R.drawable.help);

			//We set the spinner back to 0 for the next instance of using dual indicators.
			accessseekbarcount = 0;
			//We set the x indicator back to the date array, to remove the rest of the indicators. 
			setXAdapterDate();
			//We set the lock back to close.
			lock.setBackgroundResource(R.drawable.lock);
			//We change the Color of the switch indicator, indicating it is not possible to switch.
			switchindicator.setImageResource(R.drawable.switchindicatorgrey);
			//We remove the play button
			playbutton.setVisibility(View.GONE);
			hasStopped = true;
			isRunning = false;
			playbutton.setBackgroundResource(R.drawable.ic_media_play);
			//We remove the SeekBar and the date.
			datesSeekBar.setVisibility(View.GONE);
			datetext.setText("");
			//Remove all views out of the graph if there was any ready for the next graph. 
			layout.removeAllViews();
			layout.addView(nodata);
			//Both graphs do not exist currently, as the view is now empty.
			lineGraphExists = false;
			scatterGraphExists = false;

			//Prevent editing of the xIndicator.
			xindicator.setEnabled(false);
			//We prevent the user from seeing anything but date. 
			isOpen = false;
			//The SeekBar is now not available.
			seekBarIsVisible = false;
		} else {
			//Do nothing as the update method is currently running. 
		}
	}
	/**
	 * This method adds adapters to the X and Y spinners based on the date array in strings.xml. 
	 */
	public void setXAdapterDate()
	{
		//We add adapters to the x and y spinners, to edit the labels to match the correct chosen label. 
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
				R.array.date, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		xindicator.setAdapter(adapter);
		xindicator.setEnabled(false);
	}
	/**
	 * This method adds adapters to the X and Y spinners based on the indicatorName array in strings.xml. 
	 */
	public void setXAdapterArray()
	{
		//We add adapters to the x and y spinners, to edit the labels to match the correct chosen label. 
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
				R.array.indicatorName, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		xindicator.setAdapter(adapter);
		xindicator.setEnabled(false);
	}
	/**
	 * Method handles updating the graph with new indicators.Handles all UI changes.
	 */
	public void updateGraph()
	{
		if(isInternetAvailable())
		{
			//We prevent the user from rotating the screen, causing issues with the parser sending information after the activity is destroyed.
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
			update.setEnabled(false);
			//We stop the play button.
			hasStopped = true;
			isRunning = false;
			playbutton.setBackgroundResource(R.drawable.ic_media_play);

			//We reset this count so the spinner has no effect on the graph. 
			accessseekbarcount = 0;

			//We remove the old graph & animation from the view. 
			layout.removeAllViews();
			//Both graphs do not exist currently, as the view is now empty.
			lineGraphExists = false;
			scatterGraphExists = false;

			// create the progress dialog
			dialog = new ProgressDialog(this, AlertDialog.THEME_HOLO_DARK);
			dialog.setMessage("Mining for data...");
			dialog.setIndeterminate(false);
			dialog.setCancelable(false);
			dialog.setCanceledOnTouchOutside(false);
			dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			dialog.setProgress(counter);
			dialog.setMax(countries.size());
			dialog.show();

			//We check if the lock is open, for dual indicators. 
			if(isOpen == true)
			{

				//We set progress of the SeekBar to 0
				datesSeekBar.setProgress(0);

				//We clear the renderer and DataSets.
				scatterGraph.clearAll(xLabel, yLabel);

				//We get the selected index to get the index code. 
				int IndicatorPosy = yindicator.getSelectedItemPosition();
				int IndicatorPosx = xindicator.getSelectedItemPosition();
				IndicatorNamey = getResources().getStringArray(R.array.indicatorID)[IndicatorPosy];
				IndicatorNamex = getResources().getStringArray(R.array.indicatorID)[IndicatorPosx];
				//We create the parser object.
				JSONParser parser = new JSONParser(this);
				//We reset the Countries count.
				countriescount = 0;
				//We iterate through the countries and get the indicators map.
				for(Country country: countries)
				{
					parser.getIndicatorFor(country.getId(), IndicatorNamey, "1960","2012");
					parser.getIndicatorFor(country.getId(), IndicatorNamex, "1960", "2012");
				}

			} else {

				//We clear the renderer and DataSets.
				graph.clear(xLabel,yLabel);
				//We get the selected index to get the index code. 
				int IndicatorPos = yindicator.getSelectedItemPosition();
				IndicatorName = getResources().getStringArray(R.array.indicatorID)[IndicatorPos];
				//We create the parser object
				JSONParser parser = new JSONParser(this);
				//We reset the Countries count.
				countriescount = 0;
				//We iterate through the countries and get the indicators map.
				for(Country country: countries)
				{
					parser.getIndicatorFor(country.getId(), IndicatorName, "1960","2012");
				}
			}

		} else{
			// no Internet connection.
		}
	}

	/**
	 * This method updates the graph based on the values in the LineGraph object. 
	 * @param v
	 */
	public void update(View v)
	{
		updateGraph();
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
	 * This class is a listener. On the data being parsed and created into an object, this class is called.
	 * We add data to the graph once it is called. Once all data is added, it creates the graph view and 
	 * adds it to the layout.
	 * 
	 * @param type
	 * @param result The resulting map of data.
	 */
	@SuppressWarnings("deprecation")
	@Override
	public void onJSONParseFinished(String type, HashMap result) {

		//We check if the lock is opened, and the information is dual indicators or just a single indicator (isOpen is false).
		if(isOpen == true)
		{
			//This means that the data that has finished parsing is for the X Axis.
			if(axiscount == 0)
			{
				if (((Indicator)result.get(IndicatorNamey)).getValues().size() > 0) {

					//We get the indicator to get the data.
					Indicator indicator = (Indicator) result.get(IndicatorNamey);
					//We get the HashMap to iterate through.
					HashMap<String,String> hashmap = indicator.getValues();
					//We add the y DataSet to the graph.
					scatterGraph.addYDataSet(hashmap, countries.get(countriescount));
					//Increment the value to show the next DataSet is for the y axis.
				} else {
					// No data for xAxis, so probably no Internet at all
					// show dialog
					dialog.dismiss();
					update.setEnabled(true);
					if (countriescount == 0){
						new NoInternetAlertDialog(this);
					}
				}
				axiscount++;
			} else {
				//This means the data that has finished parsing is for the Y Axis.
				if (((Indicator) result.get(IndicatorNamex)).getValues().size() > 0) {
					System.out.println("HAVE X");
					//We get the x axis data from the indicator class.
					Indicator indicator = (Indicator) result.get(IndicatorNamex);
					//We get the HashMap to iterate through.
					HashMap<String,String> hashmap = indicator.getValues();
					//We add the DataSet to the class.
					scatterGraph.addXDataSet(hashmap, countries.get(countriescount));

					if(countriescount == (countries.size()-1))
					{
						//This means that this is the final country to add to the graph.

						//We get the minimum/maximum values for the axis.
						xaxisminmax = scatterGraph.getXMinMax();
						yaxisminmax = scatterGraph.getYMinMax();

						dialog.dismiss();

						//A scatter graph exists now.
						scatterGraphExists = true;

						//In this method we remove the old dates, and add the new dates that are available.
						ArrayList<String> scattercountries = scatterGraph.getAvailableYears(countries);
						//We clear the dates arraylist and add arraylists given by the new dates that are actually available.
						dates.clear();
						for(String x : scattercountries)
						{
							dates.add(x);
						}

						numberOfYears = dates.size();

						datesSeekBar.setMax(numberOfYears-1);
						datesSeekBar.setProgress(0);
						//The spinner can now be used, since an update has occurred.
						accessseekbarcount = 1;
						//We create the graph
						getDualGraph();
						//We create a dialog to represent missing data
						String missing = scatterGraph.getMissingCountries();

						if(missing.equals(""))
						{
							//Do nothing, as there is no missing countries.
						} else {
							createMissingDialog(missing);
						}

						//The update button can now be pressed again.
						update.setEnabled(true);
						//The orientation can be changed now.
						setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
						//We reset the counter for the next update.
						counter = 0;
					} else {
						//We increase the progress counter.
						counter++;

						new Thread(new Runnable() {
							@Override
							public void run() {
								dialog.incrementProgressBy(1);
							}
						}).start();

						//We increase the count for the next call.
						countriescount++;
					}
				} else {
					// Make sure we update the countries count even we don't have Internet
					if(countriescount != (countries.size()-1)) {
						countriescount++;
					}
				}
				//We have finished parsing this countries data, so we reset the count.
				axiscount = 0;
			}
		} else {
			// This means that the data we are getting is for a single indicator. 
			// we got result when there is no Internet connection,
			// but the size of the HashMap is empty
			if (((Indicator)result.get(IndicatorName)).getName() == null) {
				// reset everything
				dialog.dismiss();
				update.setEnabled(true);
				System.out.println("show --");
				new NoInternetAlertDialog(this);
				return;
			}

			//We get the indicator to get the data. 
			Indicator indicator = (Indicator) result.get(IndicatorName);
			//We get the HashMap to iterate through. 
			HashMap<String,String> hashmap = indicator.getValues();

			//We add the DataSet based on the country and indicator. 
			try {
				graph.addDataSet(hashmap, countries.get(countriescount));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			//We compare the count to the size of the ArrayList, to see if we have finished adding the data. 
			if(countriescount == (countries.size()-1))
			{
				//We add the correct amount of series to the Renderer. 
				graph.addRenderers();

				/*
				 * We get the layout for the graph, remove all views from the graph.
				 * We add the new graph to the layout in the form of a view. 
				 * We also set the loading animation to be transparent.
				 */
				dialog.dismiss();
				layout.removeAllViews();
				layout.addView(graph.getLineView(getApplicationContext()), new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
				//The update button can now be pressed again.
				update.setEnabled(true);

				//A line graph exists now.
				lineGraphExists = true;
				String missing = graph.getMissingDatasets();
				if(missing.equals(""))
				{
					//Do nothing, as there is no missing countries.
				} else {
					createMissingDialog(missing);
				}
				//The orientation can be changed now.
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
				//We reset the count when we have finished getting all the values.
				counter = 0;
			}else {
				//We increase the count for the next call. 
				countriescount++;
				counter++;

				new Thread(new Runnable() {
					@Override
					public void run() {
						dialog.incrementProgressBy(1);
					}
				}).start();
			}
		}
	}
	/**
	 * This code will run the SeekBar and the date text, and increment the values on a background thread.
	 */
	public void startAnimationSeek()
	{
		Thread thread = new Thread(new Runnable()
		{
			@Override
			public void run() {
				/*
				 * While the user hasn't requested to stop the animation, keep iterating through.
				 */
				while(hasStopped == false)
				{
					for (i = datesSeekBar.getProgress(); i < numberOfYears; i++) {
						/*
						 * We check after every progression to check if the user wants to pause.
						 */
						if(hasStopped == false)
						{
							//We set the progress on the UI thread. This means the UI thread does the least amount of work, preventing
							//it from stalling.
							runOnUiThread(new Runnable()
							{
								@Override
								public void run() {
									datesSeekBar.setProgress(i);
									datetext.setText(dates.get(i));
								}
							});
							//We restart the loop by setting progress back to 0
							if(i+1 == numberOfYears)
							{
								runOnUiThread(new Runnable()
								{
									@Override
									public void run() {
										datesSeekBar.setProgress(0);
										datetext.setText(dates.get(0));
									}
								});
							}
							try {
								//Use this value to sleep the thread, causing a small pause.
								Thread.sleep(70);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						} else {
							break;
						}
					}
				}
				//We reset HasStopped for the next iteration, since the thread has stopped.
				hasStopped = false;
			}
		});
		thread.start();
	}

	/**
	 * This method is used to check if the device currently running is a tablet or a phone.l=
	 * @param context The context of the application
	 * @return is the device a tablet.
	 */
	public static boolean isTablet(Context context) {
		return (context.getResources().getConfiguration().screenLayout
				& Configuration.SCREENLAYOUT_SIZE_MASK)
				>= Configuration.SCREENLAYOUT_SIZE_LARGE;
	}


	/**
	 * Alex's method to check if Internet is available. We check before pressing update to make sure there is an internet connection. 
	 * @return boolean representing if there is an Internet connection.
	 */
	private boolean isInternetAvailable() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

		if (networkInfo != null && networkInfo.isConnected()) {
			return true;
		}
		return  false;
	}
}
