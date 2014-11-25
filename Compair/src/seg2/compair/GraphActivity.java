package seg2.compair;



import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;




import model.Indicator;
import model.download.JSONParser;
import model.download.JSONParserListener;
import model.graph.LineGraph;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

/**
 * This class defines the graph ui and the functionality to change indicators. 
 * @author Sean
 */
public class GraphActivity extends Activity implements JSONParserListener<HashMap> {

	//Spinners for the x indicators and the y indicators
	Spinner xindicator;
	Spinner yindicator;
	//Update button
	Button update;
	//The labels for the x and y axis. 
	String xLabel = "x";
	String yLabel = "y";
	//The object to manipulate the line graph. 
	LineGraph graph;
	//The name of the indicator. 
	String IndicatorName;
	//The example list of countries.
	ArrayList<String> countries;
	//The count of countries used to add datasets.
	int countriescount;
	//The layout we add the graph to, and the loading animation.
	LinearLayout layout;

	//The animationed loading animation
	ImageView logoanimated;

	//ImageView of the lock to unlock the Xaxis.
	ImageView lock;

	//Boolean that is used to see if the lock is unlocked or locked.
	boolean isOpen = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_graph);

		/*
		 * I still need to get all countries that are to be compared from the other view. This is so i know what datasets to get. 
		 * We can put the country objects into an array and iterate through when adding datasets to the graph. 
		 */

		// We get the x any y indicators and update button.

		xindicator = (Spinner)findViewById(R.id.xspinner);
		yindicator = (Spinner)findViewById(R.id.yspinner);
		update = (Button)findViewById(R.id.update);

		//Initialise the layout to add animations and graphs to.
		layout = (LinearLayout) findViewById(R.id.chart);

		//We initialise the imageview of the lock.
		lock = (ImageView)findViewById(R.id.xlock);


		//We add adapters to the x and y spinners, to edit the labels to match the correct chosen label. 
		setXAdapterDate();

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

	public void lock(View v)
	{

		if(isOpen == false){


			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			LayoutInflater inflater = this.getLayoutInflater();
			builder.setView(inflater.inflate(R.layout.confirmindicator_dialog, null));

			builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface dialog, int which) {
					
					setXAdapterArray();
					lock.setImageResource(R.drawable.unlock);
					layout.removeAllViews();
					xindicator.setEnabled(true);
					isOpen = true;

				}

			});

			final AlertDialog alertDialog = builder.create();
			alertDialog.show();
		} else {
			setXAdapterDate();
			lock.setImageResource(R.drawable.lock);
			layout.removeAllViews();
			xindicator.setEnabled(false);
			isOpen = false;
			
		}
	}

	public void setXAdapterDate()
	{
		//We add adapters to the x and y spinners, to edit the labels to match the correct chosen label. 
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
				R.array.date, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		xindicator.setAdapter(adapter);
		xindicator.setEnabled(false);
	}
	
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
		//We remove the old graph & animation from the view. 
		layout.removeAllViews();

		/*
		 * We create the animated logo animation and set the resources. 
		 */
		logoanimated = new ImageView(this);
		logoanimated.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
		final Animation animRotate = AnimationUtils.loadAnimation(this, R.anim.rotate);

		logoanimated.startAnimation(animRotate);
		logoanimated.setImageResource(R.drawable.ic_perm_group_sync_settings);
		//We add the image to the view. 
		layout.addView(logoanimated);

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
