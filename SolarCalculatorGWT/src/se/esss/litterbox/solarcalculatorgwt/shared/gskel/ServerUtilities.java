package se.esss.litterbox.solarcalculatorgwt.shared.gskel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ServerUtilities 
{
	public static String[] runExternalProcess(String command, boolean linux, boolean getInfo, boolean debug, boolean ignoreErrors) throws GskelException 
	{
    	Process p = null;
		String[] status = null;
    	String[] cmd = null;
    	if (!linux)
    	{ 
    		cmd = new String[3];
    		cmd[0] = command;
    		cmd[1] = "";
    		cmd[2] = "";
    	}
    	else
    	{
    		cmd = new String[3];
    		cmd[0] = "/bin/sh";
    		cmd[1] = "-c";
    		cmd[2] = command;
    	}
		if (debug) System.out.println("runExternalProcess Input " + cmd[0] + " " + cmd[1] + " "+ cmd[2]);
		try {p = Runtime.getRuntime().exec(cmd);} catch (IOException e) { if (!ignoreErrors) throw new GskelException(e);}
		if (!getInfo)
		{
			status  = new String[1];
			status[0] = "";
			return status;
		}
		InputStream iserr = p.getErrorStream();
		InputStreamReader isrerr = new InputStreamReader(iserr);
    	BufferedReader err = new BufferedReader(isrerr);  
    	String errline = null;  
		try {errline = err.readLine();} catch (IOException e) { if (!ignoreErrors) throw new GskelException(e);}
		try {err.close();} catch (IOException e){ if (!ignoreErrors) throw new GskelException(e);}
		try {isrerr.close();} catch (IOException e){ if (!ignoreErrors) throw new GskelException(e);}
		try {iserr.close();} catch (IOException e){ if (!ignoreErrors) throw new GskelException(e);}
		if (errline != null)
		{
			if (debug) System.out.println("runExternalProcess: errline = " + errline);
			if (ignoreErrors) throw new GskelException(errline);
		}
		InputStream is = p.getInputStream();
		if (is == null)
		{
			status  = new String[1];
			status[0] = "";
			if (debug) System.out.println("runExternalProcess Output = " + "NO OUTPUT");
			return status;
		}
		InputStreamReader isr = new InputStreamReader(is);
    	BufferedReader in = new BufferedReader(isr);  
    	String line = null;  
    	ArrayList<String> outputBuffer = new ArrayList<String>();
		try {
			while ((line = in.readLine()) != null) 
			{  
				outputBuffer.add(line);
			}
		} catch (IOException e){ if (!ignoreErrors) throw new GskelException(e);}
		int nlines = outputBuffer.size();
		if (nlines < 1) 
		{
			status  = new String[1];
			status[0] = "";
			if (debug) System.out.println("runExternalProcess Output = " + "NO OUTPUT");
		}
		else
		{
			status = new String[nlines];
			for (int il = 0; il < nlines; ++il)
			{
				status[il] = outputBuffer.get(il);
				if (debug) System.out.println("runExternalProcess Output = " + status[il]);
			}
		}
		try {in.close();} catch (IOException e) { if (!ignoreErrors) throw new GskelException(e);}
		try {isr.close();} catch (IOException e) { if (!ignoreErrors) throw new GskelException(e);}
		try {is.close();} catch (IOException e) { if (!ignoreErrors) throw new GskelException(e);}
		return status;
		
	}
	public static String getDateLabel(Date date)
	{
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        int monthInt = cal.get(Calendar.MONTH) + 1;
        String month = Integer.toString(monthInt);
        String day =  Integer.toString(cal.get(Calendar.DAY_OF_MONTH));
        String year = Integer.toString(cal.get(Calendar.YEAR));
        String hour = Integer.toString(cal.get(Calendar.HOUR_OF_DAY));
        String min = Integer.toString(cal.get(Calendar.MINUTE));
        String sec = Integer.toString(cal.get(Calendar.SECOND));
        if (month.length() < 2) month = "0" + month;
        if (day.length() < 2) day = "0" + day;
        if (hour.length() < 2) hour = "0" + hour;
        if (min.length() < 2) min = "0" + min;
        if (sec.length() < 2) sec = "0" + sec;
        String dateLabel = year + month + day + "_" + hour + min + sec;
        return dateLabel;
	}

}
