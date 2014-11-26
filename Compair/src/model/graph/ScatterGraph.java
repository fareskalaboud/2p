package model.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.util.Log;

/**
 *This class defines the scatter graph and all the dataset/operations and renderer for it. 
 * @author Sean
 *
 */

public class ScatterGraph {

	//The dataset and renderer we add data and renderers to
	XYMultipleSeriesDataset dataset;
	XYMultipleSeriesRenderer renderer;
	//Number of sets in the graph
	int numberOfSets = 0;
	//The array to store all colours
	int[] colours = {Color.parseColor("#CD5C5C"),Color.parseColor("#4169E1"),Color.parseColor("#8A2BE2")
			,Color.parseColor("#2897B7"),Color.parseColor("#2F74D0"),Color.parseColor("#6755E3"),Color.parseColor("#9B4EE9")
			,Color.parseColor("#75D6FF"),Color.parseColor("#79FC4E"),Color.parseColor("#DFDF00"),Color.parseColor("#FF7575"),Color.parseColor("#9ACD32")};
	//The colour count to see which colour to add from the above array
	int colourCount = 0;
	//The x and y labels for the graphs
	String xLabel;
	String yLabel;
	//The x and y maps to store the data, access later when changing years. 
	HashMap<String,HashMap<String,String>> xMap;
	HashMap<String,HashMap<String,String>> yMap;

	//This stringbuilder is used to create the missing countries string.
	StringBuilder builder;
	//We store the missing values in the seperate arraylists
	ArrayList<String> missingx;
	ArrayList<String> missingy;
	ArrayList<String> missingxy;
	/**
	 * Initialises the hashmaps and the string builder.
	 */
	public ScatterGraph()
	{
		xMap = new HashMap<String,HashMap<String,String>>();
		yMap = new HashMap<String,HashMap<String,String>>();
		builder = new StringBuilder();
		
		missingx = new ArrayList<String>();
		missingy = new ArrayList<String>();
		missingxy = new ArrayList<String>();
	}
	/**
	 * We create the final graph based on values given.
	 * @param context The application context
	 * @return the graph itself as a view. 
	 */
	public GraphicalView getScatterGraph(Context context)
	{
		GraphicalView view = ChartFactory.getScatterChartView(context, dataset, renderer);

		return view;
	}
	/**
	 * adds all renderers once datasets are added. Call this method once all datasets are added. 
	 */
	public void addRenderers(){

		for(int i = 0;i<numberOfSets;i++)
		{
			XYSeriesRenderer r = new XYSeriesRenderer();
			r.setColor(colours[colourCount]);
			r.setPointStyle(PointStyle.DIAMOND);
			r.setFillPoints(true);
			renderer.addSeriesRenderer(r);
			//Increase the count. 
			colourCount++;
		}

	}
	/**
	 * Adds dataset to the X Hashmap to reference later. 
	 */
	public void addXDataSet(HashMap<String,String>xDataset,String country)
	{
		xMap.put(country, xDataset);
	}
	/**
	 * Adds dataset to the Y Hashmap to reference later. 
	 */
	public void addYDataSet(HashMap<String,String>yDataset,String country)
	{
		yMap.put(country, yDataset);
	}
	/**
	 * We add the specific data from the hashmaps given. 
	 * @param country The country we want to access data for.
	 * @param year The specific year we want the data from.
	 */
	public void addDataToGraph(String country,String year)
	{
		HashMap<String, String> xDataset = xMap.get(country);
		HashMap<String, String> yDataset = yMap.get(country);

		XYSeries series = new XYSeries(country);
		//We get the values as doubles from the hashmaps. 
		Double x = Double.valueOf(xDataset.get(year));
		Double y = Double.valueOf(yDataset.get(year));

		//We see if the data is not there, if it isn't we do not add it and update the textview accordingly.
		if(x==0 && y ==0)
		{
			missingxy.add(country);
		} else if (x==0)
		{
			missingx.add(country);
		} else if (y==0)
		{
			missingy.add(country);
		} else {


			//If the y axis value is greater than a certain value, we need to move the margin.

			if(y>1000000){
				renderer.setMargins(new int[] {0, 130, 20, 0});
			} 
			if(y>10000000){
				renderer.setMargins(new int[] {0, 160, 20, 0});
			} 
			if(y>100000000){
				renderer.setMargins(new int[] {0, 200, 20, 0});
			} 

			series.add(x, y);
			numberOfSets++;
			dataset.addSeries(series);
		}
	}
	/**
	 * We set the minimum and maximium values of the X Axis
	 * @param min the minimum value
	 * @param max the maximum value
	 */
	public void setXAxisMinMax(double min,double max)
	{
		renderer.setXAxisMin(min);
		renderer.setXAxisMax(max);
	}
	/**
	 * We set the minimum and maximium values of the Y Axis
	 * @param min the minimum value
	 * @param max the maximum value
	 */
	public void setYAxisMinMax(double min,double max)
	{
		renderer.setYAxisMin(min);
		renderer.setYAxisMax(max);
	}
	/**
	 * We get the minimum and maximum value of the y axis, once all data is added to the class.
	 * @return array with minimum and maximum value. 
	 */
	public double[] getYMinMax()
	{
		double[] array = new double[2];

		double max = 0;
		double min = 0;

		Set keys = yMap.keySet();
		Iterator iterate = keys.iterator();
		HashMap<String,String> tempmap = yMap.get(iterate.next());
		Set keyset2 = tempmap.keySet();
		Iterator iterate2 = keyset2.iterator();
		String firstvalue = tempmap.get(iterate2.next());

		min = Double.valueOf(firstvalue);

		for(HashMap<String,String> values: yMap.values())
		{

			for(String data: values.values())
			{

				double number = Double.valueOf(data);

				if(number == 0)
				{

				} else {
					if(number>max)
					{
						max = number;
					}
					if(number<min)
					{
						min = number;
					}
				}
			}
		}

		array[0] = min;
		array[1] = max;

		return array;
	}
	/**
	 * We get the minimum and maximum value of the x axis, once all data is added to the class.
	 * @return array with minimum and maximum value. 
	 */
	public double[] getXMinMax()
	{

		double[] array = new double[2];

		double max = 0;
		double min = 0;

		Set keys = xMap.keySet();
		Iterator iterate = keys.iterator();
		HashMap<String,String> tempmap = xMap.get(iterate.next());
		Set keyset2 = tempmap.keySet();
		Iterator iterate2 = keyset2.iterator();
		String firstvalue = tempmap.get(iterate2.next());

		min = Double.valueOf(firstvalue);

		for(HashMap<String,String> values: xMap.values())
		{

			for(String data: values.values())
			{

				double number = Double.valueOf(data);

				if(number == 0)
				{

				} else {

					if(number>max)
					{
						max = number;
					}
					if(number<min)
					{
						min = number;
					}
				}
			}
		}

		array[0] = min;
		array[1] = max;

		return array;

	}

	/**
	 * This method gets us the string that represents the missing countries of this graph.
	 * @return String to set text of missing countries textview. 
	 */
	public String getMissingCountries()
	{
		try{
			if(missingxy.size() !=0)
			{
				builder.append(" Countries missing (x,y): ");
				for(String x: missingxy)
				{
					builder.append(x+",");
				}
			}
			if(missingx.size() !=0)
			{
				builder.append(" Countries missing (x): ");
				for(String x: missingx)
				{
					builder.append(x+",");
				}
			}
			if(missingy.size() !=0)
			{
				builder.append(" Countries missing (y): ");
				for(String x: missingy)
				{
					builder.append(x+",");
				}
			}
		} catch (NullPointerException e)
		{

		}
		return builder.toString();
	}
	/**
	 * Clears the renderer and datasets, setting default variables on the renderer for use.
	 * @param xLabel the XLabel title
	 * @param yLabel the YLabel title
	 */
	public void clearAll(String xLabel,String yLabel)
	{
		this.xLabel = xLabel;
		this.yLabel = yLabel;

		//We reset these counts. They tell later methods the amount of colours or sets that are needed. 
		colourCount = 0;
		numberOfSets = 0;
		
		//We remove data from the missing country datastructures and builder.
		
		missingxy.clear();
		missingx.clear();
		missingy.clear();
		
		builder = new StringBuilder();

		dataset = new XYMultipleSeriesDataset();

		renderer = new XYMultipleSeriesRenderer();

		renderer.setAxisTitleTextSize(20);

		//Sets the size of the Chart title(I don't think there is a title).
		renderer.setChartTitleTextSize(30);
		//Sets the size of the labels on X and Y.
		renderer.setLabelsTextSize(20);
		//Sets the size of the keys for each graph. 
		renderer.setLegendTextSize(15);
		//This is used to set the default number of Labels on the X & Y axis. to increase labels, increase number. 
		renderer.setYLabels(16);

		//We add the x and y names to the graph. 
		renderer.setXTitle(xLabel);
		renderer.setYTitle(yLabel);

		//Sets the size of the individual points.
		renderer.setPointSize(8f);
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


}