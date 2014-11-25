package model.graph;

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

	int numberOfSets = 0;

	int[] colours = {Color.parseColor("#CD5C5C"),Color.parseColor("#4169E1"),Color.parseColor("#9ACD32"),Color.parseColor("#8A2BE2")
			,Color.parseColor("#2897B7"),Color.parseColor("#2F74D0"),Color.parseColor("#6755E3"),Color.parseColor("#9B4EE9")
			,Color.parseColor("#75D6FF"),Color.parseColor("#79FC4E"),Color.parseColor("#DFDF00"),Color.parseColor("#FF7575")};
	int colourCount = 0;

	String xLabel;
	String yLabel;

	/**
	 * This class resets the dataset and the renderer. We add the default properties to the renderer. 
	 * @param xLabel The label on the X Axis
	 * @param yLabel The Label on the Y Axis
	 */

	public void clear(String xLabel, String yLabel)
	{

		this.xLabel = xLabel;
		this.yLabel = yLabel;
		//We reset these counts. They tell later methods the amount of colours or sets that are needed. 
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
		renderer.setMargins(new int[] {0, 80, 20, 0});

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
	/**
	 * This method defines adding a data set to the graph. 
	 * @param map The Hashmap we have to iterate. 
	 * @param label The label of the data. 
	 * @throws ParseException The date was in the incorrect format (has to just be a year i.e 2009).
	 */
	public void addDataSet(HashMap<String,String> map,String label) throws ParseException
	{
		//We use the SDF to parse the strings into the correct format to create the Date object. 
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

		TimeSeries timeSeries = new TimeSeries(label);
		//We iterate through the map, getting the date and value. We put this into a series.

		for (Map.Entry<String, String> entry : map.entrySet()) {
			/*
			 * We use a string builder to automatically add the month and day to the date, to parse it in the correct form. 
			 */

			String value = entry.getValue();
			//If the value is greater than 1000000, we are going to have to change the margins size to fit the value in. 
			if(Double.valueOf(value)>1000000){
				renderer.setMargins(new int[] {0, 130, 20, 0});
			} 

			if(value.equals("0"))
			{

			}else{
				StringBuilder builder = new StringBuilder();

				String date = entry.getKey();

				builder.append("01-01-" + date);
				date = builder.toString();

				Date convertedDate = sdf.parse(date);
				timeSeries.add(convertedDate, Double.valueOf(value));
			}
		}
		/*
		 * Increase the count of the number of sets, and we add this series to the main series dataset. 
		 */
		numberOfSets++;
		dataset.addSeries(timeSeries);

	}

	
	/**
	 * This method adds all renderers once all datasets are added. 
	 */
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

	/**
	 * This method returns the graphical view that is to be placed in the activity. 
	 * @param c The context of the application.
	 * @return the chart we want to return. 
	 */

	public GraphicalView getLineView(Context c)
	{
		chart = ChartFactory.getTimeChartView(c, dataset, renderer,"dd/MM/yyyy");

		return chart;
	}

}