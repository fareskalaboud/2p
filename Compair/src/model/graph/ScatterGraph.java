package model.graph;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import model.Country;

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

/**
 *This class defines the scatter graph and all the dataset/operations and renderer for it. 
 * @author Sean
 */
public class ScatterGraph implements Serializable {
	/**
	 * We have implemented serializable within this method so that when orientation of the screen changes, we can send the information
	 * to the new activity and resume as it was.
	 */
	private static final long serialVersionUID = 1523766701083062095L;
	//The dataset and renderer we add data and renderers to
	private XYMultipleSeriesDataset dataset;
	private XYMultipleSeriesRenderer renderer;
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

	//This hashmap will store the country and assign it a colour.
	private HashMap<String,Integer> colorMap;
	//We use this color count to select a colour from the array above to assign to the country.
	private int colourcount = 0;

	//The x and y maps to store the data, access later when changing years. 
	private HashMap<String,HashMap<String,String>> xMap;
	private HashMap<String,HashMap<String,String>> yMap;

	//This stringbuilder is used to create the missing countries string.
	private StringBuilder builder;
	//We store the missing values in the seperate arraylists
	private ArrayList<String> missing;



	/**
	 * Initialises the hashmaps, the string builder, the missing countries arrays, the countries array and the colorMap.
	 */
	public ScatterGraph()
	{
		xMap = new HashMap<String,HashMap<String,String>>();
		yMap = new HashMap<String,HashMap<String,String>>();
		builder = new StringBuilder();

		missing = new ArrayList<String>();

		colorMap = new HashMap<String,Integer>();

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
	 * We add the list of countries from the graph activity into this class to correctly select colours.
	 * @param countries The list of countries.
	 */
	public void addCountryList(ArrayList<Country> countries)
	{

		for(Country c: countries)
		{
			String country = c.getId();
			colorMap.put(country, colours[colourcount]);
			colourcount++;
		}
	}

	/**
	 * Adds dataset to the X Hashmap to reference later. 
	 */
	public void addXDataSet(HashMap<String,String>xDataset,Country c)
	{
		xMap.put(c.getId(), xDataset);
	}
	/**
	 * Adds dataset to the Y Hashmap to reference later. 
	 */
	public void addYDataSet(HashMap<String,String>yDataset,Country c)
	{
		yMap.put(c.getId(), yDataset);
	}
	/**
	 * We add the specific data from the hashmaps given. 
	 * @param c The country we want to access data for.
	 * @param year The specific year we want the data from.
	 */
	public void addDataToGraph(Country c,String year)
	{
		//We get the ID of the country to query through the datasets. 
		String country = c.getId();

		HashMap<String, String> xDataset = xMap.get(country);
		HashMap<String, String> yDataset = yMap.get(country);
		//We add the label as the name of the country. We also add a space to add a little bit of padding.
		XYSeries series = new XYSeries(" " + c.getName() + "  ");
		//We get the values as doubles from the hashmaps. 
		Double x = Double.valueOf(xDataset.get(year));
		Double y = Double.valueOf(yDataset.get(year));

		//We see if the data is not there, if it isn't we do not add it to an arraylist and update the textview accordingly at the end.
		//We add the name as the name of the country, not the ID
		if(x==0 || y ==0)
		{
			missing.add(c.getName());
		} else {
			//If the y axis value is greater than a certain value, we need to move the margin.

			if(y>1000000){
				renderer.setMargins(new int[] {0, 130, 40, 0});
			} 
			if(y>10000000){
				renderer.setMargins(new int[] {0, 160, 40, 0});
			} 
			if(y>100000000){
				renderer.setMargins(new int[] {0, 200, 40, 0});
			} 
			//We add these values to the graph. 
			series.add(x, y);
			//We add the series to the dataset.
			dataset.addSeries(series);

			//We create ta new renderer, set the colour by selecting the colour in the hashmap, set the point style and to fill.
			XYSeriesRenderer r = new XYSeriesRenderer();
			r.setColor(colorMap.get(country));
			r.setPointStyle(PointStyle.CIRCLE);
			r.setFillPoints(true);
			renderer.addSeriesRenderer(r);
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
	@SuppressWarnings("rawtypes")
	public double[] getYMinMax()
	{
		double[] array = new double[2];
		//The min and max values.
		double max = 0;
		double min = 0;

		//We get all keys within the yMap that holds all y values.
		Set keys = yMap.keySet();
		Iterator iterate = keys.iterator();
		/*
		 * This method is used to get the first value in the hashmap, to set the minimum value. 
		 * We cannot use zero, as that might be well below the minimum value in the graph, causing the graph to be out of scale.
		 */
		HashMap<String,String> tempmap = yMap.get(iterate.next());
		Set keyset2 = tempmap.keySet();
		Iterator iterate2 = keyset2.iterator();
		String firstvalue = tempmap.get(iterate2.next());

		min = Double.valueOf(firstvalue);
		//We iterate through the map and get every value. We compare each value to the min max, and adjust the values if greater or smaller.
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
		//We place the minimum and maximum values.
		array[0] = min;
		array[1] = max;

		return array;
	}
	/**
	 * We get the minimum and maximum value of the x axis, once all data is added to the class.
	 * @return array with minimum and maximum value. 
	 */
	@SuppressWarnings("rawtypes")
	public double[] getXMinMax()
	{
		double[] array = new double[2];
		//The min and max values.
		double max = 0;
		double min = 0;
		//We get all keys within the yMap that holds all y values.
		Set keys = xMap.keySet();
		Iterator iterate = keys.iterator();
		/*
		 * This method is used to get the first value in the hashmap, to set the minimum value. 
		 * We cannot use zero, as that might be well below the minimum value in the graph, causing the graph to be out of scale.
		 */
		HashMap<String,String> tempmap = xMap.get(iterate.next());
		Set keyset2 = tempmap.keySet();
		Iterator iterate2 = keyset2.iterator();
		String firstvalue = tempmap.get(iterate2.next());

		min = Double.valueOf(firstvalue);
		//We iterate through the map and get every value. We compare each value to the min max, and adjust the values if greater or smaller.
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
		//We place the minimum and maximum values.
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
		builder = new StringBuilder();

		//The size of the array
		int size = missing.size();
		//if there are actually missing data, then we create the string.
		if(size > 0)
		{
			//We append the first part.
			builder.append(" Some countries are missing data: ");
			//We iterate through the entire arraylist, adding the countries to the list.

			for(int i = 0; i<size; i++)
			{
				//If this is the final country, then we want to put a fullstop instead.
				if(i+1 == size)
				{
					builder.append(" and " + missing.get(i) + ".");
				} else {
					builder.append(missing.get(i) + ",");
				}
			}
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
		//We reset these counts. They tell later methods the amount of colours or sets that are needed. 
		colourcount = 0;

		//We remove data from the missing country datastructures and builder.
		missing.clear();

		//We recreate the builder for the missing countries, the dataset and renderer. We set default renderer properties.
		builder = new StringBuilder();

		dataset = new XYMultipleSeriesDataset();

		renderer = new XYMultipleSeriesRenderer();

		renderer.setAxisTitleTextSize(20);

		//Sets the size of the Chart title(I don't think there is a title).
		renderer.setChartTitleTextSize(30);
		//Sets the size of the labels on X and Y.
		renderer.setLabelsTextSize(20);
		//Sets the size of the keys for each graph. 
		renderer.setLegendTextSize(25);
		//This is used to set the default number of Labels on the X & Y axis. to increase labels, increase number. 
		renderer.setYLabels(16);

		//We add the x and y names to the graph. 
		renderer.setXTitle(xLabel);
		renderer.setYTitle(yLabel);

		//Sets the size of the individual points.
		renderer.setPointSize(7f);
		//We increase the margin size on the left side of the screen to prevent clipping of the axis. 
		renderer.setMargins(new int[] {0, 80, 40, 0});

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
		renderer.setBackgroundColor(Color.rgb(59, 59, 59));
		renderer.setMarginsColor(Color.rgb(59,59,59));
		renderer.setXLabelsColor(Color.WHITE);
		renderer.setYLabelsColor(0, Color.WHITE);
	}
}
