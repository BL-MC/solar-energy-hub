package se.esss.litterbox.solarcalculator2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Date;

@SuppressWarnings({ "serial", "unused" })
public class PvgisData implements Serializable
{
	public final static String[] monthName = {"Jan", "Feb",  "Mar",  "Apr",   "May",    "Jun",    "Jul",    "Aug",    "Sep",    "Oct",    "Nov",    "Dec"};
	public final static int[][]  monthDay  = {{0,30},{31,58},{59,89},{90,119},{120,150},{151,180},{181,211},{212,242},{243,272},{273,303},{304,333},{334,364}}; 
	public final static int[]    montNday  = {31,    28,     31,     30,      31,       30,       31,       31,       30,       31,       30,       31};
	private int nrows = -1;
	private int ncols = -1;
	private double xllcorner;
	private double yllcorner;
	private double cellSize;
	private short[][] solarData = null;
	private int imonth = -1;
	
	public PvgisData(PvgisData pvgisData)
	{
		copy(pvgisData);
	}
	
	public PvgisData(String datafolder, int imonth, boolean ascii) throws IOException, ClassNotFoundException
	{
		this.imonth = imonth;
		if (ascii)
		{
			readAsciiFile(datafolder); 
		}
		else
		{
			copy(deserialize(datafolder, imonth));
		}
	}
	private void copy(PvgisData pvgisData)
	{
		this.nrows = pvgisData.nrows;
		this.ncols = pvgisData.ncols;
		this.xllcorner = pvgisData.xllcorner;
		this.yllcorner = pvgisData.yllcorner;
		this.cellSize = pvgisData.cellSize;
		solarData = new short[nrows][ncols];
		this.imonth = pvgisData.imonth;
		for (int irow = 0; irow < nrows; ++irow)
		{
			for (int icol = 0; icol < ncols; ++icol)
			{
				solarData[irow][icol] =  pvgisData.solarData[irow][icol];
			}
		}
		
	}
	private void readAsciiFile(String datafolder) throws IOException
	{
		File aFile = new File(datafolder+ File.separator + monthName[imonth] + ".txt");
		BufferedReader input =  new BufferedReader(new FileReader(aFile));
		ncols = Integer.parseInt(input.readLine().split(" ")[1]);
		nrows = Integer.parseInt(input.readLine().split(" ")[1]);
		xllcorner = Double.parseDouble(input.readLine().split(" ")[1]);
		yllcorner = Double.parseDouble(input.readLine().split(" ")[1]);
		cellSize = Double.parseDouble(input.readLine().split(" ")[1]);
		Double.parseDouble(input.readLine().split(" ")[1]); //no data val
		solarData = new short[nrows][ncols];
		String[] dataRow;
		double dataVal;
		for (int irow = 0; irow < nrows; ++irow)
		{
			dataRow = input.readLine().split(" ");
			for (int icol = 0; icol < ncols; ++icol)
			{
			 	dataVal = 100.0 * Double.parseDouble(dataRow[icol]);
			 	if (dataVal < 0 ) dataVal = -1;
				solarData[irow][icol] =  (short) dataVal;
			}
		}
		input.close();
		
	}
    public void serialize(String datafolder) throws IOException 
    {
        FileOutputStream fos = new FileOutputStream(datafolder + File.separator + monthName[imonth] + ".dat");
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(this);
        fos.close();
    }
    private static PvgisData deserialize(String datafolder, int imonth) throws IOException,ClassNotFoundException 
    {
    	FileInputStream fis = new FileInputStream(datafolder + File.separator + monthName[imonth] + ".dat");
    	ObjectInputStream ois = new ObjectInputStream(fis);
    	PvgisData pvgisData = (PvgisData) ois.readObject();
    	ois.close();
    	return pvgisData;
    }
    private int longitudeIndex(double longitudeDeg) throws Exception
    {
    	double span = ((double)(ncols - 1)) * cellSize;
    	double frac = (longitudeDeg - xllcorner) / span;
    	if ((frac < 0) || (frac > 1)) throw new Exception("Requested longitude not inside data set");
    	int index = (int) Math.round(frac * ((double)(ncols - 1)));
    	return index;
    }
    private int latitudeIndex(double latitudeDeg) throws Exception
    {
    	double span = ((double)(nrows - 1)) * cellSize;
    	double frac = (span + yllcorner - latitudeDeg ) / span;
    	if ((frac < 0) || (frac > 1)) throw new Exception("Requested latitude not inside data set");
    	int index = (int) Math.round(frac * ((double)(nrows - 1)));
    	return index;
    }
    public double getGlobalHorizontalDailyAvg(double latitudeDeg, double longitudeDeg) throws Exception
    {
// returns in Watts-hour per m^2
    	if (nrows < 0) throw new Exception("No Data has been read");
    	int irow = latitudeIndex(latitudeDeg);
    	int icol = longitudeIndex(longitudeDeg);
    	double dataVal = solarData[irow][icol];
    	if (dataVal < 0) throw new Exception("No Data available for the requested coordinates");
    	dataVal = dataVal / 100.0;
    	dataVal = 1000.0 * dataVal / ((double) montNday[imonth]);
    	return dataVal;
    }
    public static void convertDataFromAsciiToBinary(String inputParentFolder, String outputParentFolder) throws ClassNotFoundException, IOException
    {
		for (int imonth = 0; imonth < 12; ++imonth)
		{
			PvgisData pvgisData = new PvgisData(inputParentFolder, imonth, true);
			pvgisData.serialize(outputParentFolder);
		}
    }
    public static double[] getGlobalHorizontalDailyAvgForYear(double latitudeDeg, double longitudeDeg, String dataFolder, boolean ascii) throws Exception
    {
    	double[] monthlyData = new double[12];
		for (int imonth = 0; imonth < 12; ++imonth)
		{
			PvgisData pvgisData = new PvgisData(dataFolder, imonth, ascii);
			monthlyData[imonth] = pvgisData.getGlobalHorizontalDailyAvg(latitudeDeg, longitudeDeg);
		}
		return monthlyData;
    }

	public static void main(String[] args) throws Exception 
	{
		double latitudeDeg = 43.919;
		double longitudeDeg = 0.088;
		String asciiDataFolder = "data/cmsafAscii";
		String binDataFolder = "data/cmsafBin";
//		PvgisData.convertDataFromAsciiToBinary(asciiDataFolder, binDataFolder);
		Date startTime = new Date();
		double[] monthlyData =  PvgisData.getGlobalHorizontalDailyAvgForYear(latitudeDeg, longitudeDeg, binDataFolder, false);
		for (int imonth = 0; imonth < 12; ++imonth)
		{
			System.out.println(monthName[imonth] + " " + monthlyData[imonth]);
		}
		Date stopTime = new Date();
		System.out.println("Stop  = " + (stopTime.getTime() - startTime.getTime()));
	}
}
