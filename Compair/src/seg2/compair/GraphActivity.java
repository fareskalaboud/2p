package seg2.compair;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;

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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
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
 * This class defines the graph ui and the functionality to change indicators. 
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
	//Seekbar for dualindicators
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
	//The count of countries used to add datasets.
	private int countriescount = 0;
	//The layout we add the graph to, and the loading animation.
	private LinearLayout layout;

	//ImageView of the lock to unlock the Xaxis.
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

	//This is the axiscount, we use this when using dual indicators to tell what axis information we are 
	//listening for.
	private int axiscount = 0;
	//This count is used when deciding if the call that the datespinner activates is null and to not be used
	private int accessseekbarcount  = 0;
	//Used for the seekbar, to get the correct year and the number of years to display.
	private int numberOfYears;
	//The minimum and maximum value for the scattergraph in two arrays.
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
	//The playbutton that is used to auto increment the seekbar.
	private Button playbutton;

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_graph);

		//We get the string array from CountrySelectActivity.
		countries = (ArrayList<Country>) getIntent().getSerializableExtra("countries");

		//We add the years from 1960 to 2012 into an arraylist. 
		for(int i = 1960; i<=2012; i++)
		{
			dates.add(String.valueOf(i));
		}

		//We get the total size of the dates for the seekbar.
		numberOfYears = dates.size();

		// We get the x any y indicators and update button.
		xindicator = (Spinner)findViewById(R.id.xspinner);
		yindicator = (Spinner)findViewById(R.id.yspinner);
		update = (Button)findViewById(R.id.update);

		//Initialise the layout to add animations and graphs to.
		layout = (LinearLayout) findViewById(R.id.chart);
		//We add the background tutorial to the graph background.
		ImageView background = new ImageView(this);
		background.setImageResource(R.drawable.graphbackground);

		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.CENTER;
		background.setLayoutParams(params);
		layout.addView(background);
		//We initialise the imageview of the lock.
		lock = (Button)findViewById(R.id.xlock);
		//We initialise the imageview of the switch indicator. 
		switchindicator = (ImageView)findViewById(R.id.switchindicator);

		//We intialise the play button
		playbutton = (Button)findViewById(R.id.playbutton);
		playbutton.setVisibility(View.GONE);
		//Initialise the date text for dual indicators.
		datetext = (TextView)findViewById(R.id.datetext);
		//Initialise the fittoview button.
		fittoview = new Button(this);
		fittoview.setText("Fit to View");

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
		fittoview.setBackgroundResource(R.drawable.custombutton_btn_default_holo_light);
		//We get the layout fit to view button is added to.
		bottomlayout = (LinearLayout)findViewById(R.id.bottomlayout);


		//We check if the device is a tablet. If it is, we make the button bigger to fit the screen.
		if(isTablet(this))
		{
			addFitButton(30,450);
		} else if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
		{
			addFitButton(15,450);
		} else {
			addFitButton(15,300);
		}

		/*
		 * This spinner is kept invisible till unlocked when the dual indicators are unlocked. 
		 */
		datesSeekBar = (SeekBar)findViewById(R.id.datespinner);
		datesSeekBar.setVisibility(View.GONE);
	
		//We intialise the help imageview button.
		help = (Button)findViewById(R.id.btnHelp);


		//We set the maximum number of years for the seekbar.
		datesSeekBar.setMax(numberOfYears-1);
		//Listener for the seekbar on dual indicators. 
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
	 * This method is used to add the fit to view button to the screen depending on what size the text is
	 * @param size The size of the text on the button.
	 * @param paramsize The size of the button itself (width)
	 */
	public void addFitButton(int size, int paramsize)
	{
		fittoview.setTextSize(size);
		//We add the button to the bottom layout and set the onclick listener.
		bottomlayout.addView(fittoview,new LayoutParams(paramsize, LayoutParams.WRAP_CONTENT));
	}

	/**
	 * This method resets the graph view. Used by the fit to view button in the graph activity.
	 */
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
		//We save the lockstatus, seekbarstatus, x and y indicators and if a graph has been created.
		//We also give the scattergraph and linegraph to easily repaint the view onto the activity.
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

	@Override
	protected void onRestoreInstanceState(Bundle savedState) {
		super.onRestoreInstanceState(savedState);
		// restore the current data, for instance when changing the screen
		// orientation
		/*
		 * We get the lock, if the seekbar is visible, if the graphs exists, the position of the indicators
		 * the year that is currently shown on the textview, the position of the seekbar if it exists, 
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
		//If a linegraph exists, we want to build it again in this view.
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

			//We set the seekbar to active
			accessseekbarcount = 1;
			//We set the spinner position
			datesSeekBar.setProgress(yearPosition);

			//We get the minimum/maximum values for the axis.
			xaxisminmax = scatterGraph.getXMinMax();
			yaxisminmax = scatterGraph.getYMinMax();
			//We set the lock open, and a scatter graph already exists in the viewport.
			if(isOpen == true)
			{
				setDualIndicator(true, year, false);
			}
		} else if(isOpen == true)
		{
			setDualIndicator(false, year, true);
		}


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
	 * Class defines the time seekbar, changing the graph everytime the time changes.
	 * @author Sean
	 */
	private class seekBarListener implements SeekBar.OnSeekBarChangeListener{

		@SuppressWarnings("deprecation")
		@Override
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

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
						layout.addView(scatterGraph.getScatterGraph(getApplicationContext()), new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));

					}
				}
			}
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
		}

	}
	/**
	 * This method is used by the play button to start stop incrementing the seekbar.
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
	 * This method is used by the switch indicators imageview, to execute the animation and to switch the indicators in the spinners.
	 * @param v
	 */
	public void switchIndicators(View v)
	{
		if(isOpen == false)
		{

		} else {
			final Animation animRotate = AnimationUtils.loadAnimation(this, R.anim.rotateindicator);
			switchindicator.startAnimation(animRotate);
			int positionx = xindicator.getSelectedItemPosition();
			int positiony = yindicator.getSelectedItemPosition();

			yindicator.setSelection(positionx);
			xindicator.setSelection(positiony);
		}

	}
	/**
	 * This method handles changing the UI to support dual indicators.
	 * @param scatterexists Does a scattergraph already exist in viewport.
	 * @param year The year that we want to set the date text at (Usually 1960).
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
		//We set the textfield to now show a date.
		datetext.setText(year);
		datetext.setTextSize(30f);
		//We set the visibility of the seekbar to visible. 
		datesSeekBar.setVisibility(View.VISIBLE);
		//We set the adapter up, and change the  lock image to an unlocked image. 
		setXAdapterArray();
		//We change the lock image to an unlocked image.
		lock.setBackgroundResource(R.drawable.customlockopen);
		//We change the colour of the switch indicator, indicating it is now possible to switch.
		switchindicator.setImageResource(R.drawable.switchindicatorblue);

		if(removeView == true)
		{
			//Remove all views out of the graph if there was any ready for the next graph. 
			layout.removeAllViews();
		}

		//Both graphs do not exist currently, as the view is now empty.
		lineGraphExists = false;
		scatterGraphExists = scatterexists;
		//Enable x indicator spinner.
		xindicator.setEnabled(true);
		//the lock is now open. 
		isOpen = true;
		//The seekbar is available.
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

			//We add the fit to view button to the bottom layout and set the onclick listener.
			bottomlayout.addView(fittoview, new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));

			//We turn the help dialog button back to gray
			help.setBackgroundResource(R.drawable.help);

			//We set the spinner back to 0 for the next instance of using dual indicators.
			accessseekbarcount = 0;
			//We set the x indicator back to the date array, to remove the rest of the indicators. 
			setXAdapterDate();
			//We set the lock back to close.
			lock.setBackgroundResource(R.drawable.lock);
			//We change the colour of the switch indicator, indicating it is not possible to switch.
			switchindicator.setImageResource(R.drawable.switchindicatorgrey);
			//We remove the play button
			playbutton.setVisibility(View.GONE);
			hasStopped = true;
			isRunning = false;
			playbutton.setBackgroundResource(R.drawable.ic_media_play);
			//We remove the seekbar and the date.
			datesSeekBar.setVisibility(View.GONE);
			datetext.setText("");
			//Remove all views out of the graph if there was any ready for the next graph. 
			layout.removeAllViews();

			//Both graphs do not exist currently, as the view is now empty.
			lineGraphExists = false;
			scatterGraphExists = false;

			//Prevent editing of the xindicator.
			xindicator.setEnabled(false);
			//We prevent the user from seeing anything but date. 
			isOpen = false;
			//The seekbar is now not available.
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
			dialog.setMessage("Loading. Please wait...");
			dialog.setIndeterminate(true);
			dialog.setCancelable(false);
			dialog.setCanceledOnTouchOutside(false);
			dialog.show();

			//We check if the lock is open, for dual indicators. 
			if(isOpen == true)
			{

				//We set progress of the seekbar to 0
				datesSeekBar.setProgress(0);

				//We clear the renderer and datasets.
				scatterGraph.clearAll(xLabel, yLabel);

				//We get the selected index to get the index code. 
				int IndicatorPosy = yindicator.getSelectedItemPosition();
				int IndicatorPosx = xindicator.getSelectedItemPosition();
				IndicatorNamey = getResources().getStringArray(R.array.indicatorID)[IndicatorPosy];
				IndicatorNamex = getResources().getStringArray(R.array.indicatorID)[IndicatorPosx];
				//We create the parser object
				JSONParser parser = new JSONParser(this);
				//We reset the countriescount
				countriescount = 0;
				//We iterate through the countries and get the indicators map.
				for(Country country: countries)
				{
					parser.getIndicatorFor(country.getId(), IndicatorNamey, "1960","2012");
					parser.getIndicatorFor(country.getId(), IndicatorNamex, "1960", "2012");
				}

			} else {

				//We clear the renderer and datasets.
				graph.clear(xLabel,yLabel);
				//We get the selected index to get the index code. 
				int IndicatorPos = yindicator.getSelectedItemPosition();
				IndicatorName = getResources().getStringArray(R.array.indicatorID)[IndicatorPos];
				//We create the parser object
				JSONParser parser = new JSONParser(this);
				//We reset the countriescount
				countriescount = 0;
				//We iterate through the countries and get the indicators map.
				for(Country country: countries)
				{
					parser.getIndicatorFor(country.getId(), IndicatorName, "1960","2012");
				}
			}

		} else{
			// no internet connection
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
					//We get the hashmap to iterate through.
					HashMap<String,String> hashmap = indicator.getValues();
					//We add the y dataset to the graph.
					scatterGraph.addYDataSet(hashmap, countries.get(countriescount));
					//Increment the value to show the next dataset is for the y axis.
				} else {
					// No data for xAxis, so probably no internet at all
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
					//We get the hashmap to iterate through.
					HashMap<String,String> hashmap = indicator.getValues();
					//We add the dataset to the class.
					scatterGraph.addXDataSet(hashmap, countries.get(countriescount));
					//We add the data to the graph. We using countriescount to keep track of what countries data we are adding.
					scatterGraph.addDataToGraph(countries.get(countriescount), year);

					//We compare the count to the size of the arraylist, to see if we have finished adding the data.
					if(countriescount == (countries.size()-1))
					{

						//We get the minimum/maximum values for the axis.
						xaxisminmax = scatterGraph.getXMinMax();
						yaxisminmax = scatterGraph.getYMinMax();

						//We now set the minmax for the graph.

						scatterGraph.setXAxisMinMax(xaxisminmax[0],xaxisminmax[1]);
						scatterGraph.setYAxisMinMax(yaxisminmax[0],yaxisminmax[1]);

						/*
						 * We get the layout for the graph, remove all views from the graph.
						 * We add the new graph to the layout in the form of a view.
						 * We also set the loading animation to be transparent.
						 */
						dialog.dismiss();
						layout.removeAllViews();
						layout.addView(scatterGraph.getScatterGraph(getApplicationContext()), new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));

						//A scatter graph exists now.
						scatterGraphExists = true;
						//We create a dialog to represent missing data

						String missing = graph.getMissingDatasets();
						if(missing.equals(""))
						{
							//Do nothing, as there is no missing countries.
						} else {
							createMissingDialog(missing);
						}
						//The spinner can now be used, since an update has occured.
						accessseekbarcount = 1;
						//The update button can now be pressed again.
						update.setEnabled(true);
						//The orientation can be changed now.
						setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
						} else {
						//We increase the count for the next call.
						countriescount++;
					}
				} else {
					// Make sure we update the countries count even we don't have internet
					if(countriescount != (countries.size()-1)) {
						countriescount++;
					}
				}
				//We have finished parsing this countries data, so we reset the count.
				axiscount = 0;
			}
		} else {
			// This means that the data we are getting is for a single indicator. 
			// we got result when there is no internet connection,
			// but the size of the hashmap is empty
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
			//We get the hashmap to iterate through. 
			HashMap<String,String> hashmap = indicator.getValues();

			//We add the dataset based on the country and indicator. 
			try {
				graph.addDataSet(hashmap, countries.get(countriescount));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			//We compare the count to the size of the arraylist, to see if we have finished adding the data. 
			if(countriescount == (countries.size()-1))
			{
				//We add the correct amount of renderers. 
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
			}else {
				//We increase the count for the next call. 
				countriescount++;
			}
		}
	}
	/**
	 * This code will run the seekbar and the date text, and increment the values on a background thread.
	 */
	public void startAnimationSeek()
	{
		Thread thread = new Thread(new Runnable()
		{
			@Override
			public void run() {

				while(hasStopped == false)
				{
					for (i = datesSeekBar.getProgress(); i < numberOfYears; i++) {

						if(hasStopped == false)
						{

							runOnUiThread(new Runnable()
							{

								@Override
								public void run() {
									datesSeekBar.setProgress(i);
									datetext.setText(dates.get(i));
								}

							});
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

				//We reset hasstopped for the next iteration, since the thread has stopped.
				hasStopped = false;
			}

		});

		thread.start();


	}

	/**
	 * This method is used to check if the device currently running is a tablet or a phone.l=
	 * @param context The context of the application
	 * @return is the device is a tablet.
	 */
	public static boolean isTablet(Context context) {
		return (context.getResources().getConfiguration().screenLayout
				& Configuration.SCREENLAYOUT_SIZE_MASK)
				>= Configuration.SCREENLAYOUT_SIZE_LARGE;
	}


	/**
	 * Alex's method to check if internet is available. We check before pressing update to make sure there is an internet connection. 
	 * @return boolean representing if there is an internet connection.
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
