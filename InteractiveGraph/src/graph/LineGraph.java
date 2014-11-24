package graph;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint.Align;

public class LineGraph {

	XYMultipleSeriesDataset dataset;
	XYMultipleSeriesRenderer renderer;

	GraphicalView chart;

	//The two fakes hashmaps, we fill with data in the onCreate method. 
	HashMap<String,String> map1 = new HashMap<String,String>();
	HashMap<String,String> map2 = new HashMap<String,String>();

	int numberOfSets = 0;

	int[] colours = {Color.parseColor("#CD5C5C"),Color.parseColor("#4169E1"),Color.parseColor("#9ACD32"),Color.parseColor("#8A2BE2")};
	int colourCount = 0;


	public LineGraph()
	{
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

	public void clear(String xLabel, String yLabel)
	{

		colourCount = 0;
		numberOfSets = 0;

		dataset = new XYMultipleSeriesDataset();

		renderer = new XYMultipleSeriesRenderer();

		renderer.setAxisTitleTextSize(20);

		//Sets the size of the Chart title(I don't think there is a title).
		renderer.setChartTitleTextSize(30);
		//Sets the size of the labels on X and Y.
		renderer.setLabelsTextSize(20);
		//Sets the size of the keys for each graph. 
		renderer.setLegendTextSize(14);
		//This is used to set the default number of Labels on the X & Y axis. to increase labels, increase number. 
		renderer.setYLabels(16);

		//We add the x and y names to the graph. 
		renderer.setXTitle(xLabel);
		renderer.setYTitle(yLabel);

		//Sets the size of the individual points.
		renderer.setPointSize(5f);
		//We increase the margin size on the left side of the screen to prevent clipping of the axis. 
		renderer.setMargins(new int[] {0, 60, 20, 0});

		renderer.setYLabelsAlign(Align.RIGHT, 0);
		renderer.setFitLegend(true);

		//The grid layout on the chart. 
		renderer.setShowGrid(true);
		renderer.setGridColor(Color.LTGRAY);
		//Set the colours of the labels and the axis. 
		renderer.setAxesColor(Color.DKGRAY);

		renderer.setLabelsColor(Color.DKGRAY);

		renderer.setApplyBackgroundColor(true);

		renderer.setBackgroundColor(Color.WHITE);

		renderer.setMarginsColor(Color.WHITE);

		renderer.setXLabelsColor(Color.DKGRAY);

		renderer.setYLabelsColor(0, Color.DKGRAY);

	}

	public void addDataSet(String country, String indicator,String label) throws ParseException
	{
		/*
		 * We need to get the dataset using alex's methods in the form of a hashmap. 
		 */

		HashMap<String,String> map = getDataMap("COUNTRY","INDICATOR");

		//We use the SDF to parse the strings into the correct format to create the Date object. 
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

		TimeSeries timeSeries = new TimeSeries(label);
		//We iterate through the map, getting the date and value. We put this into a series.


		for (Map.Entry<String, String> entry : map.entrySet()) {
			/*
			 * We use a string builder to automatically add the month and day to the date, to parse it in the correct form. 
			 */
			StringBuilder builder = new StringBuilder();

			String date = entry.getKey();

			builder.append("01-01-" + date);
			date = builder.toString();

			String value = entry.getValue();
			Date convertedDate = sdf.parse(date);
			timeSeries.add(convertedDate, Double.valueOf(value));
		}
		/*
		 * Increase the count of the number of sets, and we add this series to the main series dataset. 
		 */
		numberOfSets++;
		dataset.addSeries(timeSeries);

	}

	public void addRenderers()
	{
		/*
		 * We create the renderer for the lines. 
		 */
		for(int i = 0;i<numberOfSets;i++)
		{
			XYSeriesRenderer r = new XYSeriesRenderer();
			r.setColor(colours[colourCount]);
			r.setPointStyle(PointStyle.CIRCLE);
			r.setFillPoints(true);
			r.setLineWidth(3f);
			renderer.addSeriesRenderer(r);
			//Increase the count. 
			colourCount++;
		}

	}

	public HashMap<String,String> getDataMap(String country,String indicator)
	{
		//Here we get the map and return it. 
		return map1;
	}

	public GraphicalView getLineView(Context c)
	{
		chart = ChartFactory.getTimeChartView(c, dataset, renderer,"dd/MM/yyyy");

		return chart;
	}

}
