package model.graph;

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
 * We need to get the data, we need to keep all the data for the countries chosen and store them in an 
 * hashmap to access.
 * When the user changes the year, we want to add that data to the graph using the addData method. 
 * So for all hashmaps stored in the hashmap we have to go through it all, and get the data for each axis.
 * 
 * Two different hashmap, one for the xAxis indicator, and one for the yAxis indicator.
 * 
 * in our add dataset, we add the xaxis hashmap to the x hashmap, the yaxis hashmap to the y hasmaph
 * when we get the data we get the
 * @author user
 *
 */

public class ScatterGraph {

	XYMultipleSeriesDataset dataset;
	XYMultipleSeriesRenderer renderer;

	int numberOfSets = 0;

	int[] colours = {Color.parseColor("#CD5C5C"),Color.parseColor("#4169E1"),Color.parseColor("#9ACD32"),Color.parseColor("#8A2BE2")
			,Color.parseColor("#2897B7"),Color.parseColor("#2F74D0"),Color.parseColor("#6755E3"),Color.parseColor("#9B4EE9")
			,Color.parseColor("#75D6FF"),Color.parseColor("#79FC4E"),Color.parseColor("#DFDF00"),Color.parseColor("#FF7575")};
	int colourCount = 0;

	String xLabel;
	String yLabel;

	HashMap<String,HashMap<String,String>> xMap;
	HashMap<String,HashMap<String,String>> yMap;

	public ScatterGraph()
	{
		xMap = new HashMap<String,HashMap<String,String>>();
		yMap = new HashMap<String,HashMap<String,String>>();
	}

	public GraphicalView getScatterGraph(Context context)
	{
		GraphicalView view = ChartFactory.getScatterChartView(context, dataset, renderer);
		
		return view;
	}

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

	public void addXDataSet(HashMap<String,String>xDataset,String country)
	{
		xMap.put(country, xDataset);
	}

	public void addYDataSet(HashMap<String,String>yDataset,String country)
	{
		yMap.put(country, yDataset);
	}

	public void addDataToGraph(String country,String year)
	{
		HashMap<String, String> xDataset = xMap.get(country);
		HashMap<String, String> yDataset = yMap.get(country);

		XYSeries series = new XYSeries(country);
		Double x = Double.valueOf(xDataset.get(year));
		Double y = Double.valueOf(yDataset.get(year));
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

	public void setXAxisMinMax(double min,double max)
	{
		renderer.setXAxisMin(min);
		renderer.setXAxisMax(max);
	}

	public void setYAxisMinMax(double min,double max)
	{
		renderer.setYAxisMin(min);
		renderer.setYAxisMax(max);
	}

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

	public void clearAll(String xLabel,String yLabel)
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
