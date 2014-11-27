package seg2.compair;



import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;

import model.Indicator;
import model.download.JSONParser;
import model.download.JSONParserListener;
import model.graph.LineGraph;
import model.graph.ScatterGraph;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
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
	ArrayList<String> countries;
	//The count of countries used to add datasets.
	int countriescount;
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


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_graph);

		/*
		 * I still need to get all countries that are to be compared from the other view. This is so i know what datasets to get. 
		 * We can put the country objects into an array and iterate through when adding datasets to the graph. 
		 */


		/*
		 * For ease, we add all the possible years to an arraylist by iteration, for the dates spinner.
		 * This will become an issue, many countries do not have data on those dates for that indicator. We may have to let that slide for now. 
		 */

		for(int i = 1970; i<=2014; i++)
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

		//We set the maximum number of years for the seekbar.
		datesSeekBar.setMax(numberOfYears-1);
		//Listener for the seekbar on dual indicators. 
		datesSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

				for(int i=0; i<numberOfYears; i++) {

					if(datesSeekBar.getProgress()==i) {
						String date = dates.get(i);
						datetext.setText(date);
						year = date;

						//get data for year i

						if(accessspinnercount == 0)
						{

						} else {

							layout.removeAllViews();
							scatterGraph.clearAll(xLabel, yLabel);

							for(String country: countries)
							{
								scatterGraph.addDataToGraph(country, date);
							}

							scatterGraph.addRenderers();

							scatterGraph.setXAxisMinMax(xaxisminmax[0],xaxisminmax[1]);
							scatterGraph.setYAxisMinMax(yaxisminmax[0],yaxisminmax[1]);

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
		});
		
		

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

		ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,
				R.array.indicatorName, android.R.layout.simple_spinner_item);
		adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		yindicator.setAdapter(adapter2);

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

		//We create a fake arraylist, and put countries in for now. 

		countries = new ArrayList<String>();

		countries.add("GB");
		countries.add("USA");
		countries.add("AUT");
		countries.add("AUS");
		countries.add("CAN");
	}
	
	
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
		//This if statement checks if the lock is already open. 
		if(isOpen == false){
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
		} else {
			//We set the spinner back to 0 for the next instance of using dual indicators.
			accessspinnercount = 0;
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
		
		

		if(isOpen == true)
		{
			/*
			 * This means that the user wants to get a scatter graph. So we need to add this if statement to the parse listener below
			 * to make sure it adds the value to the new scatter graph class. We will add a scatter graph instead, using a lot more data. 
			 */
			scatterGraph.clearAll(xLabel, yLabel);

			//We get the selected index to get the index code. 
			int IndicatorPosy = yindicator.getSelectedItemPosition();
			int IndicatorPosx = xindicator.getSelectedItemPosition();
			IndicatorNamey = getResources().getStringArray(R.array.idicatorID)[IndicatorPosy];
			IndicatorNamex = getResources().getStringArray(R.array.idicatorID)[IndicatorPosx];
			//We create the parser object
			JSONParser parser = new JSONParser(this);
			//We reset the countriescount
			countriescount = 0;
			//We iterate through the countries add get the indicators.
			for(String country: countries)
			{
				parser.getIndicatorFor(country, IndicatorNamey, "1970","2014");
				parser.getIndicatorFor(country, IndicatorNamex, "1970", "2014");
			}

		} else {

			/*
			 * We initialise the renderer and dataseries. 
			 */

			graph.clear(xLabel,yLabel);
			//We get the selected index to get the index code. 
			int IndicatorPos = yindicator.getSelectedItemPosition();
			IndicatorName = getResources().getStringArray(R.array.idicatorID)[IndicatorPos];
			//We create the parser object
			JSONParser parser = new JSONParser(this);
			//We reset the countriescount
			countriescount = 0;
			//We iterate through the countries add get the indicators.
			for(String country: countries)
			{
				parser.getIndicatorFor(country, IndicatorName, "1970","2014");
			}
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
	@Override
	public void onJSONParseFinished(String type, HashMap result) {

		if(isOpen == true)
		{
			/*
			 * Lets put the two different hashmaps into a hashmap to add. 
			 * We are going to have to deal with the listener being called twice, once for the xaxis, once for the yaxis.
			 */

			if(axiscount == 0)
			{
				/*
				 * This call is for the y axis. 
				 */
				//We get the indicator to get the data. 
				Indicator indicator = (Indicator) result.get(IndicatorNamey);
				//We get the hashmap to iterate through. 
				HashMap<String,String> hashmap = indicator.getValuesIterator();

				scatterGraph.addYDataSet(hashmap, countries.get(countriescount));
				axiscount++;
			} else {
				/*
				 * this call is for the x axis. 
				 */
				//We reset axis count for the next set of data. 

				Indicator indicator = (Indicator) result.get(IndicatorNamex);
				//We get the hashmap to iterate through. 
				HashMap<String,String> hashmap = indicator.getValuesIterator();

				scatterGraph.addXDataSet(hashmap, countries.get(countriescount));

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
					 * We get the layout for the graph, remove all views from the graph and add the new graph to the layout in the form of a view. 
					 * We also set the loading animation to be transparent
					 */
					logoanimated.setImageResource(android.R.color.transparent);
					layout.removeAllViews();
					layout.addView(scatterGraph.getScatterGraph(getApplicationContext()), new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));

					//We set the textview to represent missing data
					nodata.setText(scatterGraph.getMissingCountries());
					//The spinner can now be used, since an update has occured. 
					accessspinnercount = 1;
					update.setEnabled(true);
				}else {
					//We increase the count for the next call. 
					countriescount++;
				}

				axiscount = 0;
			}
		} else {

			//We get the indicator to get the data. 
			Indicator indicator = (Indicator) result.get(IndicatorName);
			//We get the hashmap to iterate through. 
			HashMap<String,String> hashmap = indicator.getValuesIterator();

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
				 * We get the layout for the graph, remove all views from the graph and add the new graph to the layout in the form of a view. 
				 * We also set the loading animation to be transparent
				 */
				logoanimated.setImageResource(android.R.color.transparent);
				layout.removeAllViews();
				layout.addView(graph.getLineView(getApplicationContext()), new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));


				update.setEnabled(true);
			}else {
				//We increase the count for the next call. 
				countriescount++;
			}
		}
	}
}
