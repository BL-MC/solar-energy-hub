package se.esss.litterbox.solarcalculator2;

import java.awt.Color;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.DatasetRenderingOrder;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

public class AigChartUtilities 
{
	public static void createSingleAxisMonthPlot(String title, String vertTitle, DefaultCategoryDataset dataset, String parentDirPath) throws IOException
	{
        JFreeChart chart = ChartFactory.createBarChart(
        		title,       // chart title
        		"Month",               // domain axis label
        		vertTitle,                  // range axis label
                dataset,                  // data
                PlotOrientation.VERTICAL, // orientation
                true,                     // include legend
                false,                     // tooltips?
                false                     // URLs?
            );
        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        plot.setDomainGridlinesVisible(true);
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setDrawBarOutline(false);
        renderer.setSeriesPaint(0, Color.red);
        renderer.setSeriesPaint(1, Color.blue);
        renderer.setSeriesPaint(2, Color.green);

	    plot.setBackgroundPaint(Color.white);
	    plot.setRangeGridlinePaint(Color.black);
	    plot.setDomainGridlinePaint(Color.green);
	    setNearestUpperBound(plot, 0);
	    ChartUtilities.saveChartAsPNG(new File(parentDirPath + title.replaceAll("\\s","") + ".png"), chart, 800, 600);
	}
	public static void createDualAxisMonthPlot(String title, String[] vertTitle, DefaultCategoryDataset[] dataset, String parentDirPath) throws IOException
	{
        JFreeChart chart = ChartFactory.createBarChart(
        		title,       // chart title
        		"Month",               // domain axis label
        		vertTitle[0],                  // range axis label
                dataset[0],                  // data
                PlotOrientation.VERTICAL, // orientation
                true,                     // include legend
                false,                     // tooltips?
                false                     // URLs?
            );
        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        plot.setDomainGridlinesVisible(true);
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setDrawBarOutline(false);
        renderer.setSeriesPaint(0, Color.red);
        renderer.setSeriesPaint(1, Color.green);
 
	    plot.setBackgroundPaint(Color.white);
	    plot.setRangeGridlinePaint(Color.black);
	    plot.setDomainGridlinePaint(Color.green);
	    setFact2AxisUpperBound(plot, 0);
	    
        LineAndShapeRenderer renderer2 = new LineAndShapeRenderer();

        NumberAxis axis2 = new NumberAxis(vertTitle[1]);
        plot.setRangeAxis(1, axis2);
        plot.setDataset(1, dataset[1]);
        plot.setRenderer(1, renderer2);
        renderer2.setSeriesPaint(0, Color.blue);
        plot.mapDatasetToRangeAxis(1, 1);
        plot.setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);
	    plot.getRangeAxis(1).setLowerBound(0.0);
	    setFact2AxisUpperBound(plot, 1);
	    NumberAxis rangeAxis0 = (NumberAxis) plot.getRangeAxis(0);
	    rangeAxis0.setTickUnit(new NumberTickUnit(plot.getRangeAxis(0).getUpperBound() / 10.0));;
	    NumberAxis rangeAxis1 = (NumberAxis) plot.getRangeAxis(1);
	    rangeAxis1.setTickUnit(new NumberTickUnit(plot.getRangeAxis(1).getUpperBound() / 10.0));;

	    ChartUtilities.saveChartAsPNG(new File(parentDirPath + title.replaceAll("\\s","") + ".png"), chart, 800, 600);
	}
	public static void setFact2AxisUpperBound(CategoryPlot plot, int axis)
	{
	    plot.getRangeAxis(axis).setLowerBound(0.0);
	    double upperbound = plot.getRangeAxis(axis).getUpperBound();
	    double pow10 = Math.log10(upperbound);
	    pow10 = Math.floor(pow10);
	    pow10 = Math.pow(10.0, pow10);
	    upperbound = upperbound / pow10;
	    upperbound = (Math.floor(upperbound / 2.0) + 1.0) * 2.0;
	    upperbound = upperbound * pow10;
	    plot.getRangeAxis(axis).setUpperBound(upperbound);
	}
	public static void setNearestUpperBound(CategoryPlot plot, int axis)
	{
	    plot.getRangeAxis(axis).setLowerBound(0.0);
	    double upperbound = plot.getRangeAxis(axis).getUpperBound();
	    if (upperbound < 10.0)
	    {
		    upperbound = Math.floor(upperbound) + 1.0;
		    plot.getRangeAxis(axis).setUpperBound(upperbound);
		    return;
	    }
	    double pow10 = Math.log10(upperbound);
	    pow10 = Math.floor(pow10);
	    pow10 = Math.pow(10.0, pow10);
	    upperbound = upperbound / pow10;
	    if (upperbound < (Math.floor(upperbound) + 0.5))
	    {
		    upperbound = Math.floor(upperbound) + 0.5;
	    }
	    else
	    {
		    upperbound = Math.floor(upperbound) + 1.0;
	    }
	    upperbound = upperbound * pow10;
	    plot.getRangeAxis(axis).setUpperBound(upperbound);
	    NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
	    rangeAxis.setTickUnit(new NumberTickUnit(upperbound / 10.0));;
	}
	public static void appendTextToFile(String filePath, String text) throws IOException
	{
		BufferedWriter output = new BufferedWriter(new FileWriter(filePath, true));
		output.append(text);
		output.close();
	}
	public static void main(String[] args) 
	{
		// TODO Auto-generated method stub

	}

}
