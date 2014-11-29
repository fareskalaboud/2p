package model.graph;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import model.Country;

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

public class LineGraph implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1843423110498732877L;
	//The dataset we are adding data to.
	private  XYMultipleSeriesDataset dataset;
	//The renderer we add renderers to.
	private  XYMultipleSeriesRenderer renderer;
	
	//The number of datasets that have been added.
	private  int numberOfSets = 0;
	//Array of colours that can be used.
	private int[] colours = {Color.parseColor("#CD5C5C"),Color.parseColor("#4169E1"),Color.parseColor("#9ACD32"),Color.parseColor("#8A2BE2")
			,Color.parseColor("#2897B7"),Color.parseColor("#2F74D0"),Color.parseColor("#6755E3"),Color.parseColor("#93BF96")
			,Color.parseColor("#75D6FF"),Color.parseColor("#79FC4E"),Color.parseColor("#DFDF00"),Color.parseColor("#EEF093")
			,Color.parseColor("#89FC63"),Color.parseColor("#8FFEDD"),Color.parseColor("#BBBBFF"),Color.parseColor("#DFB0FF")
			,Color.parseColor("#BAD0EF"),Color.parseColor("#7DFDD7"),Color.parseColor("#FFBBF7"),Color.parseColor("#FFA8A8")};
	//Used to iterate through the array of colours.
	private int colourCount = 0;



	/**
	 * This class resets the dataset and the renderer. We add the default properties to the renderer. 
	 * @param xLabel The label on the X Axis
	 * @param yLabel The Label on the Y Axis
	 */
	public void clear(String xLabel, String yLabel)
	{
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
		renderer.setLegendTextSize(24);
		//This is used to set the default number of Labels on the X & Y axis. to increase labels, increase number. 
		renderer.setYLabels(16);

		//We add the x and y names to the graph. 
		renderer.setXTitle(xLabel);
		renderer.setYTitle(yLabel);

		//Sets the size of the individual points.
		renderer.setPointSize(5f);
		//We increase the margin size on the left side and bottom of the screen to prevent clipping of the axis. 
		renderer.setMargins(new int[] {0, 80, 30, 0});

		renderer.setYLabelsAlign(Align.RIGHT, 0);
		renderer.setFitLegend(true);
		//The grid layout on the chart. 
		renderer.setShowGrid(true);
		renderer.setGridColor(Color.LTGRAY);
		//Set the colours of the labels and the axis. 
		renderer.setAxesColor(Color.DKGRAY);
		renderer.setLabelsColor(Color.DKGRAY);
		renderer.setApplyBackgroundColor(true);
		//We can set the background and margin colors using the RGB values.
		renderer.setBackgroundColor(Color.rgb(255, 255, 255));
		renderer.setMarginsColor(Color.rgb(255, 255, 255));
		renderer.setXLabelsColor(Color.DKGRAY);
		renderer.setYLabelsColor(0, Color.DKGRAY);
	}
	/**
	 * This method defines adding a data set to the graph. 
	 * @param map The Hashmap we have to iterate. 
	 * @param c The country of the data. 
	 * @throws ParseException The date was in the incorrect format (has to just be a year i.e 2009).
	 */
	public void addDataSet(HashMap<String,String> map,Country c) throws ParseException
	{
		//We use the SDF to parse the strings into the correct format to create the Date object. 
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

		TimeSeries timeSeries = new TimeSeries(c.getName());
		//We iterate through the map, getting the date and value. We put this into a series.

		for (Map.Entry<String, String> entry : map.entrySet()) {
			/*
			 * We use a string builder to automatically add the month and day to the date, to parse it in the correct form. 
			 */

			String value = entry.getValue();
			//We changed the margins depending on how big the value is.
			double valuedouble = Double.valueOf(value);
			if(valuedouble>1000000){
				renderer.setMargins(new int[] {0, 130, 30, 0});
			} 
			if(valuedouble>10000000){
				renderer.setMargins(new int[] {0, 160, 30, 0});
			} 
			if(valuedouble>100000000){
				renderer.setMargins(new int[] {0, 200, 30, 0});
			} 
			//We don't add the value as it means that the value does not exist.
			if(value.equals("0"))
			{

			}else{
				//We append the date using a stringbuilder for the graph, as the graph uses date format.
				StringBuilder builder = new StringBuilder();

				String date = entry.getKey();

				builder.append("01-01-" + date);
				date = builder.toString();

				Date convertedDate = sdf.parse(date);
				//We add the date and the value.
				timeSeries.add(convertedDate, valuedouble);
			}
		}
		// Increase the count of the number of sets, and we add this series to the main series dataset. 
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
		GraphicalView chart = ChartFactory.getTimeChartView(c, dataset, renderer,"dd/MM/yyyy");

		return chart;
	}
}
