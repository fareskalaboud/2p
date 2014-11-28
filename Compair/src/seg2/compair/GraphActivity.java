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
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
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

	//The animationed loading animation
	ImageView logoanimated;

	//ImageView of the lock to unlock the Xaxis.
	ImageView lock;
	//ImageView of the switch indicator.
	ImageView switchindicator;

	//Boolean that is used to see if the lock is unlocked or locked.
	boolean isOpen = false;
	
	//These two strings represent the name of indicators when using dual indicators.
	String IndicatorNamey;
	String IndicatorNamex;

	//This is the axiscount, we use this when using dual indicators to tell what axis information we are 
	//listening for.
	int axiscount = 0;
	//This count is used when deciding if the call that the datespinner activates is null and to not be used
	int accessspinnercount  = 0;
	//Used for the seekbar, to get the correct year and the number of years to display.
	int numberOfYears;
	int yearPosition;
	//The minimum and maximum value for the scattergraph in two arrays.
	double[] xaxisminmax;
	double[] yaxisminmax;
	//The default year string for the dual indicators. 
	String year = "1970";


	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_graph);

		/*
		 * I still need to get all countries that are to be compared from the other view. This is so i know what datasets to get. 
		 * We can put the country objects into an array and iterate through when adding datasets to the graph. 
		 */

		//We get the string array from CountrySelectActivity.


		countries = (ArrayList<Country>) getIntent().getSerializableExtra("countries");


		Log.e("SIZE",countries.size()+"");
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

					if(accessspinnercount == 0)
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

					//We set the textfield to now show a date.
					datetext.setText("1970");
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
					//Remove all views out of the graph if there was any ready for the next graph. 
					layout.removeAllViews();
					//Enable x indicator spinner.
					xindicator.setEnabled(true);
					//the lock is now open. 
					isOpen = true;
				}
			});

			final AlertDialog alertDialog = builder.create();
			alertDialog.show();
		} else if(update.isEnabled() == true) {
			//We set the spinner back to 0 for the next instance of using dual indicators.
			accessspinnercount = 0;
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
			//Prevent editing of the xindicator.
			xindicator.setEnabled(false);
			//We prevent the user from seeing anything but date. 
			isOpen = false;
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
	 * This method updates the graph based on the values in the LineGraph object. 
	 * @param v
	 */
	public void update(View v)
	{

		if(isInternetAvailable())
		{
			update.setEnabled(false);
			datesSeekBar.setProgress(0);

			//We reset this count so the spinner has to effect on the graph. 
			accessspinnercount = 0;

			//We remove the old graph & animation from the view. 
			layout.removeAllViews();

			//We create the animated logo animation and set the resources. 
			logoanimated = new ImageView(this);
			logoanimated.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
			final Animation animRotate = AnimationUtils.loadAnimation(this, R.anim.rotate);

			logoanimated.startAnimation(animRotate);
			logoanimated.setImageResource(R.drawable.ic_perm_group_sync_settings);
			//We add the image to the view. 
			layout.addView(logoanimated);

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
			//Do nothing, there is no internet connection. 
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
				//We get the indicator to get the data. 
				Indicator indicator = (Indicator) result.get(IndicatorNamey);
				//We get the hashmap to iterate through. 
				HashMap<String,String> hashmap = indicator.getValues();
				//We add the y dataset to the graph.
				scatterGraph.addYDataSet(hashmap, countries.get(countriescount));
				//Increment the value to show the next dataset is for the y axis.
				axiscount++;
			} else {
				//This means the data that has finished parsing is for the Y Axis.

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
					logoanimated.setImageResource(android.R.color.transparent);
					layout.removeAllViews();
					layout.addView(scatterGraph.getScatterGraph(getApplicationContext()), new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));

					//We set the textview to represent missing data
					nodata.setText(scatterGraph.getMissingCountries());
					//The spinner can now be used, since an update has occured. 
					accessspinnercount = 1;
					//The update button can now be pressed again.
					update.setEnabled(true);
				}else {
					//We increase the count for the next call. 
					countriescount++;
				}
				//We have finished parsing this countries data, so we reset the count.
				axiscount = 0;
			}
		} else {
			// This means that the data we are getting is for a single indicator. 

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
				logoanimated.setImageResource(android.R.color.transparent);
				layout.removeAllViews();
				layout.addView(graph.getLineView(getApplicationContext()), new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
				//The update button can now be pressed again.
				update.setEnabled(true);
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

		Toast.makeText(getApplicationContext(), "No Internet Connection...", Toast.LENGTH_SHORT).show();
		return  false;
	}
}
