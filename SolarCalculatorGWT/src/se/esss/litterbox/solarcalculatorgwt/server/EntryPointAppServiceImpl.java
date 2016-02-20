package se.esss.litterbox.solarcalculatorgwt.server;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Date;

import se.esss.litterbox.solarcalculatorgwt.client.EntryPointAppService;
import se.esss.litterbox.solarcalculatorgwt.shared.gskel.CsvFile;
import se.esss.litterbox.solarcalculatorgwt.shared.gskel.GskelException;
import se.esss.litterbox.solarcalculatorgwt.shared.gskel.ServerUtilities;
import se.esss.litterbox.solarcalculator2.AigChartUtilities;
import se.esss.litterbox.solarcalculator2.SolarCalculator2;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server-side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class EntryPointAppServiceImpl extends RemoteServiceServlet implements EntryPointAppService 
{

	@Override
	public String[] gskelServerTest(String name, boolean debug, String[] debugResponse) throws GskelException 
	{
		System.out.println(name);
		if (debug)
		{
			try {Thread.sleep(3000);} catch (InterruptedException e) {}
			return debugResponse;
		}
		try {Thread.sleep(3000);} catch (InterruptedException e) {}
		String[] answer = {"high", "low"};
		return answer;
	}

	@Override
	public CsvFile[] runSolarCalculator(String dataSource, String[][] switchSettings, boolean debug, String[] debugResponse) throws GskelException 
	{
		String ip = getThreadLocalRequest().getRemoteAddr();
		String logFilePath = getServletContext().getRealPath("log/log.txt");
		String dataDirPath = getServletContext().getRealPath("solarRawData" + "/" + dataSource);
		String tempFilesPath = getServletContext().getRealPath("tempFiles");
		if (debug)
		{
			try 
			{
				for (int ii = 0; ii < switchSettings.length; ++ii)
				{
					System.out.println(switchSettings[ii][0] + " " + switchSettings[ii][1]);
				}
				SolarCalculator2 sc = new SolarCalculator2(dataDirPath, switchSettings);
				sc.summaryParameters(System.out, " ");
				CsvFile[] csvFileArray = new CsvFile[2];
				csvFileArray[0] = readCsvFile(tempFilesPath + "/summaryParameters.csv");
				csvFileArray[1] = readCsvFile(tempFilesPath + "/MonthlySunshineDataSheet.csv");
				System.out.println(ip + " requested data on " + new Date().toString());
				return csvFileArray;
			}
			catch (Exception e) 
			{
				throw new GskelException(e);
			}

		}
		try 
		{
			AigChartUtilities.appendTextToFile(logFilePath, new Date().toString() + " " + ip + "\n");
			boolean linux = true;
			boolean getInfo = true;
			boolean expDebug = false;
			boolean ignoreErrors = false;
			String command = "";
			command = "rm " + tempFilesPath + "/*";
			ServerUtilities.runExternalProcess(command, linux, getInfo, expDebug, ignoreErrors);
			try {Thread.sleep(1000);} catch (InterruptedException e) {}

			
			SolarCalculator2 sc = new SolarCalculator2(dataDirPath, switchSettings);
			PrintStream outputData = new PrintStream(tempFilesPath + "/summaryParameters.csv");
			sc.summaryParameters(outputData, ",");
			outputData.close();
			outputData = new PrintStream(tempFilesPath + "/MonthlySunshineDataSheet.csv");
			sc.printSunshineDataSheet(outputData);
			outputData.close();
			CsvFile[] csvFileArray = new CsvFile[2];
			csvFileArray[0] = readCsvFile(tempFilesPath + "/summaryParameters.csv");
			csvFileArray[1] = readCsvFile(tempFilesPath + "/MonthlySunshineDataSheet.csv");
			sc.creatCharts(tempFilesPath + "/");
			return csvFileArray;
		} catch (Exception e) 
		{
			throw new GskelException(e);
		}
	}
	static CsvFile readCsvFile(String csvFilePath) throws GskelException   
	{
		CsvFile csvFile = new CsvFile();
 		try 
		{
 			BufferedReader br;
 			FileInputStream fileInputStream = new FileInputStream(csvFilePath);
 	        InputStreamReader inputStreamReader;
			inputStreamReader = new InputStreamReader(fileInputStream);
	        br = new BufferedReader(inputStreamReader);
	        String line;
	        while ((line = br.readLine()) != null) 
	        {  
	        	csvFile.addLine(line);
	        }
	        br.close();
	        inputStreamReader.close();
	        fileInputStream.close();
	        csvFile.close();
			return csvFile;
		}
 		catch (IOException e) {throw new GskelException(e);} 
 		catch (GskelException e)  {throw new GskelException(e);}
	}
	

}
