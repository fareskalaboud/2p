package model.graph;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import android.util.Log;
/**
 *This class defines the line graph and all the dataset/operations and renderer for it. 
 * @author Sean
 */
public class LineGraph implements Serializable {
	/**
	 * We have implemented serializable within this method so that when orientation of the screen changes, we can send the information
	 * to the new activity and resume as it was.
	 */
	private static final long serialVersionUID = -1843423110498732877L;
	//The dataset we are adding data to.
	private  XYMultipleSeriesDataset dataset;
	//The renderer we add renderers to.
	private  XYMultipleSeriesRenderer renderer;

	//The number of datasets that have been added.
	private  int numberOfSets = 0;
	//The array to store all colours
	private int[] colours = {Color.parseColor("#CD5C5C"),Color.parseColor("#4169E1"),Color.parseColor("#9ACD32"),Color.parseColor("#8A2BE2")
			,Color.parseColor("#2897B7"),Color.parseColor("#2F74D0"),Color.parseColor("#6755E3"),Color.parseColor("#93BF96")
			,Color.parseColor("#75D6FF"),Color.parseColor("#FE67EB"),Color.parseColor("#DFDF00"),Color.parseColor("#6094DB")
			,Color.parseColor("#89FC63"),Color.parseColor("#8FFEDD"),Color.parseColor("#BBBBFF"),Color.parseColor("#DFB0FF")
			,Color.parseColor("#BAD0EF"),Color.parseColor("#7DFDD7"),Color.parseColor("#FFBBF7"),Color.parseColor("#FFA8A8")
			,Color.parseColor("#FFB60B"),Color.parseColor("#FFAC62"),Color.parseColor("#CF8D72"),Color.parseColor("#CB59E8")
			,Color.parseColor("#A5D3CA"),Color.parseColor("#FF8E8E"),Color.parseColor("#67C7E2"),Color.parseColor("#A5D3CA")
			,Color.parseColor("#FFA8FF"),Color.parseColor("#9191FF"),Color.parseColor("#DECF9C"),Color.parseColor("#FF9331")
			,Color.parseColor("#C17753"),Color.parseColor("#990099"),Color.parseColor("#4A9586"),Color.parseColor("#F70000")
			,Color.parseColor("#FFE920"),Color.parseColor("#E0E04E"),Color.parseColor("#C48484"),Color.parseColor("#25A0C5")};

	//Used to iterate through the array of colours.
	private int colourCount = 0;
	//Used to create the missing datasets string.
	private StringBuilder builder;
	//Used to store the missing countries.
	private ArrayList<String> missingcountries;
	//We hold the min max values for the y axis
	double min = 0;
	double max;
	/**
	 * We intialise the missing countries arraylist.
	 */
	public LineGraph()
	{
		missingcountries = new ArrayList<String>();
	}

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

		//We reset the missingcountries arraylist.
		missingcountries = new ArrayList<String>();

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
		renderer.setBackgroundColor(Color.rgb(228, 228, 228));
		renderer.setMarginsColor(Color.rgb(228,228,228));
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

		int datasetcount = 0;
		int nodatacount = 0;

		for (Map.Entry<String, String> entry : map.entrySet()) {
			/*
			 * We use a string builder to automatically add the month and day to the date, to parse it in the correct form. 
			 */
			//We get the total number of data within the hashmap by creating the value by iterating.
			datasetcount++;

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
				nodatacount++;
			}else{
				//We append the date using a stringbuilder for the graph, as the graph uses date format.
				StringBuilder builder = new StringBuilder();

				String date = entry.getKey();
				builder.append("01-01-" + date);
				date = builder.toString();

				Date convertedDate = sdf.parse(date);
				//We add the date and the value.
				timeSeries.add(convertedDate, valuedouble);
				//If the min value is 0 it means it hasn't changed.
				if( min ==0)
				{
					min = valuedouble;
				}

				if(valuedouble < min)
				{
					min = valuedouble;	
				}
				if(valuedouble > max)
				{
					max = valuedouble;	
				}
			}
		}

		if(datasetcount == nodatacount)
		{
			missingcountries.add(c.getName());
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

		//We create the min and max dates and y values to prevent users from panning outside the graph.
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		String datestart = "01-01-1970";
		String dateend = "01-01-2012";
		Date convertedDateend = new Date();
		Date convertedDatestart = new Date();
		try {
			convertedDateend = sdf.parse(dateend);
			convertedDatestart = sdf.parse(datestart);
		} catch (ParseException e) {
			Log.e("ERRORPARSING","ERRORPARSING");
			e.printStackTrace();
		}
		
		double[] minmax = {convertedDatestart.getTime(),convertedDateend.getTime(),min,max};
		renderer.setPanLimits(minmax);

		GraphicalView chart = ChartFactory.getTimeChartView(c, dataset, renderer,"dd/MM/yyyy");

		return chart;
	}


	/**
	 * We create the string representing the countries missing the entire dataset.
	 * @return String representing the countries missing a dataset, dataset should be entered before calling this method.
	 */
	public String getMissingDatasets()
	{
		//We initialise the stringbuilder
		builder = new StringBuilder();

		//The size of the array
		int size = missingcountries.size();
		//if there are actually missing datasets, then we create the string.
		if(size > 0)
		{
			//We append the first part.
			builder.append("Some countries are missing a dataset: ");
			//We iterate through the entire arraylist, adding the countries to the list.

			for(int i = 0; i<size; i++)
			{
				//If this is the final country, then we want to put a fullstop instead.
				if(i+1 == size)
				{
					if(size == 1)
					{
						builder.append(missingcountries.get(i) + ".");
					} else {
						builder.append(" and " + missingcountries.get(i) + ".");
					}
					
				} else {
					builder.append(missingcountries.get(i) + ",");
				}
			}
		}
		return builder.toString();
	}
}
