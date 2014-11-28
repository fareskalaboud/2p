package seg2.compair;



import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.ProgressDialog;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import model.Country;
import model.Indicator;
import model.download.JSONParser;
import model.download.JSONParserListener;
import model.graph.LineGraph;
import model.graph.ScatterGraph;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
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
import android.widget.Toast;

/**
 * This class defines the graph ui and the functionality to change indicators. 
 * @author Sean
 * @author Amrinder
 */
@SuppressWarnings("rawtypes")
public class GraphActivity extends Activity implements JSONParserListener<HashMap> {

	//We use this for the dates
	ArrayList<String> dates = new ArrayList<String>();

	//Spinners for the x indicators and the y indicators
	Spinner xindicator;
	Spinner yindicator;
	//Seekbar for dualindicators
	SeekBar datesSeekBar;

	//progress bar for loading
	private ProgressDialog dialog;

	//Text for the date, invisible default
	TextView datetext;

	//Texview for any country that does not have data when using dual indicators.
	TextView nodata;

	//Update button
	Button update;
	//The labels for the x and y axis. 
	String xLabel = "x";
	String yLabel = "y";
	//The object to manipulate the line graph. 
	LineGraph graph;
	//The object to manipulate the scatter graph.
	ScatterGraph scatterGraph;
	//The name of the indicator. 
	String IndicatorName;
	//The example list of countries.
	ArrayList<Country> countries;
	//The count of countries used to add datasets.
	int countriescount = 0;
	//The layout we add the graph to, and the loading animation.
	LinearLayout layout;
	LinearLayout layoutindicator;

	//ImageView of the lock to unlock the Xaxis.
	ImageView lock;
	//ImageView of the switch indicator.
	ImageView switchindicator;

	//Boolean that is used to see if the lock is unlocked or locked.
	boolean isOpen = false;
	//Boolean that is used to see if the seekbar is visible.
	boolean seekBarIsVisible = false;
	//Boolean that is used to see if a graph has been made in the view.
	boolean lineGraphExists = false;
	boolean scatterGraphExists = false;
	//These two strings represent the name of indicators when using dual indicators.
	String IndicatorNamey;
	String IndicatorNamex;

	//This is the axiscount, we use this when using dual indicators to tell what axis information we are 
	//listening for.
	int axiscount = 0;
	//This count is used when deciding if the call that the datespinner activates is null and to not be used
	int accessseekbarcount  = 0;
	//Used for the seekbar, to get the correct year and the number of years to display.
	int numberOfYears;
	int yearPosition;
	//The minimum and maximum value for the scattergraph in two arrays.
	double[] xaxisminmax;
	double[] yaxisminmax;
	//The default year string for the dual indicators. 
	String year = "1970";
	//Value is used in deciding whether to lock orientation for a certain period of time.
	int prevOrientation;


	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_graph);

		//We get the string array from CountrySelectActivity.
		countries = (ArrayList<Country>) getIntent().getSerializableExtra("countries");

		//We set the fonts to lato in the activity.
		Fonts.makeFonts(this);

		//We add the years from 1970 to 2012 into an arraylist. 
		for(int i = 1970; i<=2012; i++)
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

		//Initialise the layout to add animations and graphs to.
		layoutindicator = (LinearLayout) findViewById(R.id.bottombar);

		//We initialise the imageview of the lock.
		lock = (ImageView)findViewById(R.id.xlock);
		//We initialise the imageview of the switch indicator. 
		switchindicator = (ImageView)findViewById(R.id.switchindicator);

		//Initialise the date text for dual indicators.
		datetext = (TextView)findViewById(R.id.datetext);

		//Initialise the textview for dual indicators of countries with no data.
		nodata = (TextView)findViewById(R.id.nodata);
		nodata.setText("");


		/*
		 * This spinner is kept invisible till unlocked when the dual indicators are unlocked. 
		 */
		datesSeekBar = (SeekBar)findViewById(R.id.datespinner);
		datesSeekBar.setVisibility(View.GONE);
		//We add a touch listener to the lock to change the lock when pressed.
		lock.setOnTouchListener(new View.OnTouchListener() {        
			@Override
			public boolean onTouch(View v, MotionEvent event) {

				if(isOpen == false){
					switch(event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						lock.setImageResource(R.drawable.lockpressed);
					case MotionEvent.ACTION_UP:
						lock.setImageResource(R.drawable.lock);
					}
				}
				return false;
			}
		});

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
		//We initialise the graph class. 
		graph = new LineGraph();
		//We initialise the Scatter graph class.
		scatterGraph = new ScatterGraph();
		//We initialise the renderer and dataseries.
		graph.clear(xLabel,yLabel);
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
		}

		//if a scatter graph exists, we want to build it again in this view.
		if(scatterGraphExists == true)
		{
			//We add the view of the final graph.
			layout.removeAllViews();
			layout.addView(scatterGraph.getScatterGraph(this), new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
			//We set the textview to represent missing data
			nodata.setText(scatterGraph.getMissingCountries());

			//We set the seekbar to active
			accessseekbarcount = 1;
			//We set the spinner position
			datesSeekBar.setProgress(yearPosition);

			//We get the minimum/maximum values for the axis.
			xaxisminmax = scatterGraph.getXMinMax();
			yaxisminmax = scatterGraph.getYMinMax();
		}
		if(isOpen == true)
		{
			setDualIndicator(true, year, false);
		}


		xindicator.setSelection(xindicatorpos);
		yindicator.setSelection(yindicatorpos);
	}


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
						//We add all renderers.
						scatterGraph.addRenderers();
						//We set the minimum and maximum values for the graphs. This is to prevent them from changing each time the slider changes. 
						scatterGraph.setXAxisMinMax(xaxisminmax[0],xaxisminmax[1]);
						scatterGraph.setYAxisMinMax(yaxisminmax[0],yaxisminmax[1]);
						//We add the view of the final graph.
						layout.addView(scatterGraph.getScatterGraph(getApplicationContext()), new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));

						//We set the textview to represent missing data
						nodata.setText(scatterGraph.getMissingCountries());
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

	public void setDualIndicator(boolean scatterexists, String year, boolean removeView)
	{

		//We set the textfield to now show a date.
		datetext.setText(year);
		datetext.setTextSize(30f);
		datetext.setTextColor(Color.DKGRAY);
		//We set the visibility of the seekbar to visible. 
		datesSeekBar.setVisibility(View.VISIBLE);
		//We set the adapter up, and change the  lock image to an unlocked image. 
		setXAdapterArray();
		//We change the lock image to an unlocked image.
		lock.setImageResource(R.drawable.unlock);
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

					setDualIndicator(false, "1970", true);
				}
			});

			final AlertDialog alertDialog = builder.create();
			alertDialog.show();
		} else if(update.isEnabled() == true) {
			//We set the spinner back to 0 for the next instance of using dual indicators.
			accessseekbarcount = 0;
			//We clear the mising countries textview.
			nodata.setText("");
			//We set the x indicator back to the date array, to remove the rest of the indicators. 
			setXAdapterDate();
			//We set the lock back to close.
			lock.setImageResource(R.drawable.lock);
			//We change the colour of the switch indicator, indicating it is not possible to switch.
			switchindicator.setImageResource(R.drawable.switchindicatorgrey);
			//We remove the seekbar and the date.
			datesSeekBar.setVisibility(View.GONE);
			datetext.setText("          ");
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

	public void updateGraph()
	{
		if(isInternetAvailable())
		{
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
			update.setEnabled(false);
			datesSeekBar.setProgress(0);

			//We reset this count so the spinner has to effect on the graph. 
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
				//We clear the renderer and datasets.
				scatterGraph.clearAll(xLabel, yLabel);

				//We clear the mising countries textview.
				nodata.setText("");

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
					parser.getIndicatorFor(country.getId(), IndicatorNamey, "1970","2012");
					parser.getIndicatorFor(country.getId(), IndicatorNamex, "1970", "2012");
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
					parser.getIndicatorFor(country.getId(), IndicatorName, "1970","2012");
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
					System.out.println("HAVE Y");
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
						System.out.println("show");
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
						//We add the correct amount of renderers.
						scatterGraph.addRenderers();

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
						//We set the textview to represent missing data
						nodata.setText(scatterGraph.getMissingCountries());
						//The spinner can now be used, since an update has occured.
						accessseekbarcount = 1;
						//The update button can now be pressed again.
						update.setEnabled(true);
						//The orientation can be changed now.
						setRequestedOrientation(prevOrientation);
					}else {
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

				//A scatter graph exists now.
				lineGraphExists = true;
				//The orientation can be changed now.
				setRequestedOrientation(prevOrientation);
			}else {
				//We increase the count for the next call. 
				countriescount++;
			}
		}
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
