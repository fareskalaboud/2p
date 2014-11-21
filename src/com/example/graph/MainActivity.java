package com.example.graph;

import graph.Graph;

import java.text.ParseException;
import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends Activity {
	
	HashMap<String,String> map1 = new HashMap<String,String>();
	HashMap<String,String> map2 = new HashMap<String,String>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		map1.put("2014-11-21", "5");
		map1.put("2014-11-20", "10");
		map1.put("2014-11-19", "15");
		map1.put("2014-11-18", "16");
		map1.put("2014-11-17", "20");
		
		map2.put("2014-11-21", "10");
		map2.put("2014-11-20", "15");
		map2.put("2014-11-19", "15");
		map2.put("2014-11-18", "2");
		map2.put("2014-11-17", "5");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void createGraph(View view)
	{
		Graph graph = new Graph("Date", "Percent (%)");
		try {
			graph.addDataSet(map1, "Currency1");
			graph.addDataSet(map2, "Currency2");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Intent intent = graph.createGraph(getApplicationContext());
		
		startActivity(intent);
		
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
