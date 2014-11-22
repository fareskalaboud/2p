package com.example.graph;


import java.text.ParseException;
import java.util.HashMap;

import model.graph.CubicGraph;
import model.graph.LineGraph;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
/**
 * This class is used to test if the graph.class works as intended, by using two fake datasets in the form of hashmaps. 
 * @author Sean
 *
 */
public class MainActivity extends Activity {
	//The two fakes hashmaps, we fill with data in the onCreate method. 
	HashMap<String,String> map1 = new HashMap<String,String>();
	HashMap<String,String> map2 = new HashMap<String,String>();
	
	LineGraph lineGraph;
	CubicGraph cubicGraph;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		//We add fake data in the form of <Date,Value>
		map1.put("2014", "5");
		map1.put("2013", "10");
		map1.put("2012", "15");
		map1.put("2011", "16");
		map1.put("2010", "20");

		map2.put("2014", "10");
		map2.put("2013", "15");
		map2.put("2012", "15");
		map2.put("2011", "2");
		map2.put("2010", "5");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void createLineGraph(View view)
	{
		//To prevent the ui thread from hanging on pressing the button, we use a thread runnable. I don't know how pre-historic the device this app will be ran on. 
		new Thread(new Runnable()
		{
			@Override
			public void run() {
				//We create the new graph object, with the X and Y Labels, and add the data sets we want to use. 
				lineGraph = new LineGraph("Date", "Percent (%)");
				try {
					lineGraph.addDataSet(map1, "Currency1");
					lineGraph.addDataSet(map2, "Currency2");
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//We have to create and start the intent on the main UI thread. 
				runOnUiThread( new Runnable()
				{

					@Override
					public void run() {
						Intent intent = lineGraph.createGraph(getApplicationContext());
						startActivity(intent);
					}
				});
			}
		}).start();
	}
	public void createCubicGraph(View view)
	{
		//To prevent the ui thread from hanging on pressing the button, we use a thread runnable. I don't know how pre-historic the device this app will be ran on. 
		new Thread(new Runnable()
		{
			@Override
			public void run() {
				//We create the new graph object, with the X and Y Labels, and add the data sets we want to use. 
				cubicGraph = new CubicGraph("Date", "Percent (%)");
				try {
					cubicGraph.addDataSet(map1, "Currency1");
					cubicGraph.addDataSet(map2, "Currency2");
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//We have to create and start the intent on the main UI thread. 
				runOnUiThread( new Runnable()
				{

					@Override
					public void run() {
						Intent intent = cubicGraph.createGraph(getApplicationContext());
						startActivity(intent);
					}
				});
			}
		}).start();
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
