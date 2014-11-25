package com.example.interactivegraph;

import graph.LineGraph;

import java.text.ParseException;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;

/**
 * This class defines the graph ui and the functionality to change indicators. 
 * @author Sean
 */
public class GraphActivity extends Activity {

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

		//We add adapters to the x and y spinners, to edit the labels to match the correct chosen label. 
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
				R.array.indicators2_array, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		xindicator.setAdapter(adapter);
		xindicator.setEnabled(false);

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
				R.array.indicators_array, android.R.layout.simple_spinner_item);
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
	}

	/**
	 * This method updates the graph based on the values in the LineGraph object. 
	 * @param v
	 */
	@SuppressWarnings("deprecation")
	public void update(View v)
	{
		/*
		 * We initialise the renderer and dataseries. 
		 */
		graph.clear(xLabel,yLabel);

		//We add the dataset based on the country and indicator. 
		try {
			graph.addDataSet("COUNTRY", "INDICATOR", "LABEL");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//We add the correct amount of renderers. 
		graph.addRenderers();
		/*
		 * We get the layout for the graph, remove all views from the graph and add the new graph to the layout in the form of a view. 
		 */
		LinearLayout layout = (LinearLayout) findViewById(R.id.chart);
		layout.removeAllViews();
		layout.addView(graph.getLineView(getApplicationContext()), new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));

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
}
