package model.graph;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import org.achartengine.ChartFactory;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint.Align;

/**
 * This class defines the graph that visualises the datasets recieved from the user input from the Central Bank. 
 * @author Sean
 *
 */
public class CubicGraph {
	//The renderer and dataset for all the data added. 
	XYMultipleSeriesRenderer renderer;
	XYMultipleSeriesDataset dataSet;
	//The names of the X and Y Labels on the graph. 
	String xLabel;
	String yLabel;
	//The different colours for each dataset. We use a counter to add the correct number of renderers, for the number of datasets added.
	int[] colours = {Color.RED,Color.BLUE,Color.GREEN,Color.MAGENTA};
	int colourCount = 0;
	//The number of datasets. 
	int numberOfSets = 0;


	/**
	 * This class defines a graph intent. 
	 * @param xLabel The Label we are giving the X Axis.
	 * @param yLabel The Label we are giving the Y Axis.
	 */

	public CubicGraph(String xLabel, String yLabel)
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
		//The series we are to add the data to.
		XYSeries series = new XYSeries(label);

		for (Map.Entry<String, String> entry : data.entrySet()) {
			//We get the date and the value, and add them to the series. 
			String date = entry.getKey();
			String value = entry.getValue();
			series.add(Double.valueOf(date), Double.valueOf(value));
		}
		/*
		 * Increase the count of the number of sets, and we add this series to the main series dataset. 
		 */
		numberOfSets++;
		dataSet.addSeries(series);

	}

	/**
	 * Creates the graph with the given datasets. 
	 * @param context The context of the application. Required for creating the intent.
	 * @return Returns an intent including the graph. 
	 */
	public Intent createGraph(Context context)
	{
		//Sets the size of the title(I don't even think there is a title).
		renderer.setAxisTitleTextSize(20);

		//Sets the size of the Chart title(I don't think there is a title).
		renderer.setChartTitleTextSize(30);
		//Sets the size of the labels on X and Y.
		renderer.setLabelsTextSize(20);
		//Sets the size of the keys for each graph. 
		renderer.setLegendTextSize(14);
		//This is used to set the default number of Labels on the X & Y axis. to increase labels, increase number. 
		renderer.setYLabels(16);
		renderer.setXLabels(4);
		//We add the x and y names to the graph. 
		renderer.setXTitle(xLabel);
		renderer.setYTitle(yLabel);
		//We have to set pan to be true, and zoom to be false, so user cannot zoom in. This is to stop the years from sub-dividing. 
		renderer.setPanEnabled(true, true);
		renderer.setZoomEnabled(false, false);

		//Sets the size of the individual points.
		renderer.setPointSize(7f);
		//We increase the margin size on the left side of the screen to prevent clipping of the axis. 
		renderer.setMargins(new int[] {0, 50, 20, 0});

		renderer.setYLabelsAlign(Align.RIGHT, 0);
		renderer.setFitLegend(true);


		//We iterate through the  number of sets, creating a specific renderer for each one, changing the colour every time. 
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
		//The grid layout on the chart. 
		renderer.setShowGrid(true);
		renderer.setGridColor(Color.DKGRAY);
		//Set the colours of the labels and the axis. 
		renderer.setAxesColor(Color.BLACK);

		renderer.setLabelsColor(Color.BLACK);

		renderer.setApplyBackgroundColor(true);

		renderer.setBackgroundColor(Color.WHITE);

		renderer.setMarginsColor(Color.WHITE);

		renderer.setXLabelsColor(Color.BLACK);

		renderer.setYLabelsColor(0, Color.BLACK);
		//We create the intent and return it.
		Intent CubicChart = ChartFactory.getCubicLineChartIntent(context, dataSet, renderer, 0.5f);
		return CubicChart;

	}

}