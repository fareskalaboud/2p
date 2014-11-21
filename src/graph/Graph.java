package graph;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.achartengine.ChartFactory;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;


public class Graph {

	XYMultipleSeriesRenderer renderer;
	XYMultipleSeriesDataset dataSet;

	String xLabel;
	String yLabel;

	int[] colours = {Color.RED,Color.BLUE,Color.GREEN,Color.MAGENTA};
	int colourCount = 0;

	int numberOfSets = 0;

/**
 * This class defines a graph intent. 
 * @param xLabel The Label we are giving the X Axis.
 * @param yLabel The Label we are giving the Y Axis.
 */

	public Graph(String xLabel, String yLabel)
	{
		renderer = new XYMultipleSeriesRenderer();
		dataSet = new XYMultipleSeriesDataset();

		this.xLabel = xLabel;
		this.yLabel = yLabel;

	
	}
	
	/**
	 * Class defines adding a set of data to the graph. 
	 * @param data The dataset we are to add in the format of <Date,Value>
	 * @param label The name of the dataset we are adding. 
	 * @throws ParseException If the date cannot be parsed. Currently the date has to be in the form of yyyy-MM-dd
	 */

	public void addDataSet(HashMap<String,String> data, String label) throws ParseException
	{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		TimeSeries timeSeries = new TimeSeries(label);

		for (Map.Entry<String, String> entry : data.entrySet()) {

			String date = entry.getKey();
			String value = entry.getValue();
			Date convertedDate = sdf.parse(date);
			timeSeries.add(convertedDate, Double.valueOf(value));

		}

		numberOfSets++;

		dataSet.addSeries(timeSeries);

	}

	/**
	 * Creates the graph with the given datasets. 
	 * @param context The context of the application. Required for creating the intent.
	 * @return Returns an intent including the graph. 
	 */
	public Intent createGraph(Context context)
	{
		renderer.setAxisTitleTextSize(40);
		renderer.setChartTitleTextSize(60);
		renderer.setLabelsTextSize(40);
		renderer.setLegendTextSize(16);
		renderer.setYLabels(16);
		renderer.setPointSize(7f);
		renderer.setMargins(new int[] {0, 60, 0, 0});

		for(int i = 0;i<numberOfSets;i++)
		{
			XYSeriesRenderer r = new XYSeriesRenderer();
			
			r.setColor(colours[colourCount]);
			
			r.setPointStyle(PointStyle.CIRCLE);
			r.setFillPoints(true);
			r.setLineWidth(3f);
			renderer.addSeriesRenderer(r);
			
			colourCount++;
		}

		renderer.setShowGrid(true);
		renderer.setAxesColor(Color.DKGRAY);
		renderer.setLabelsColor(Color.LTGRAY);
		

		Intent LineChart = ChartFactory.getTimeChartIntent(context, dataSet, renderer,"dd/MM/yyyy");
		return LineChart;

	}

}
