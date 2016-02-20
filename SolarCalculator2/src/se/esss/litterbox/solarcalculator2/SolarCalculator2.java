package se.esss.litterbox.solarcalculator2;

import java.io.File;
import java.io.PrintStream;
import java.text.DecimalFormat;

import org.jfree.data.category.DefaultCategoryDataset;

public class SolarCalculator2 
{
	final static double MaxSolarFlux = 950.0; //Watts per m2
	final static double PI = Math.PI;
	final static double TWOPI = 2.0 * PI;
	final static double degToRad = PI / 180.0;
	final static double radToDeg = 180.0 / PI;
	
	public final static String[] monthName = {"Jan", "Feb",  "Mar",  "Apr",   "May",    "Jun",    "Jul",    "Aug",    "Sep",    "Oct",    "Nov",    "Dec"};
	public final static int[][]  monthDay  = {{0,30},{31,58},{59,89},{90,119},{120,150},{151,180},{181,211},{212,242},{243,272},{273,303},{304,333},{334,364}}; 
	public final static int[]    montNday  = {31,    28,     31,     30,      31,       30,       31,       31,       30,       31,       30,       31};
	
//	Input Parameters
	private double fieldAreaHectares = 1;
	private double fieldUtilFactorPer = 100.0;
	private double panelInclinDeg = 0.0;
	private double panelEta25Per = 17.0;
	private double transEtaPer = 85.0;
	private double kLperDegC = 0.0048;
	private int    coldPanelDayStart = 135;
	private int    coldPanelDayEnd = 258;
	private double hotPanelTempC = 80.0;
	private double coldPanelTempC = 25.0;
	private double latitudeDeg = 55.741;
	private double longitudeDeg = 13.263;
	private int    peakDay =  -1;
	private String binDataFolder;

//	Input getters
	public double getFieldAreaHectares() {return fieldAreaHectares;}
	public double getFieldUtilFactorPer() {return fieldUtilFactorPer;}
	public double getPanelInclinDeg() {return panelInclinDeg;}
	public double getPanelEta25Per() {return panelEta25Per;}
	public double getTransEtaPer() {return transEtaPer;}
	public double getkLperDegC() {return kLperDegC;}
	public int    getColdPanelDayStart() {return coldPanelDayStart;}
	public int    getColdPanelDayEnd() {return coldPanelDayEnd;}
	public double getHotPanelTempC() {return hotPanelTempC;}
	public double getColdPanelTempC() {return coldPanelTempC;}
	public double getLatitudeDeg() {return latitudeDeg;}
	public double getLongitudeDeg() {return longitudeDeg;}
	public int    getPeakDay() {return peakDay;}

	private SolarLocation solarLocation;
	private double[] effectiveDaylightHoursForMonth  = new double[12];
	private double[] skyAttenuation = new double[12];
	
// Plotting Arrays
	double[] peakSolarPowerDensityWM2 = new double[12];
	double[] avgSolarPowerDensityWM2  = new double[12];
	
	double[] peakSolarPowerMW = new double[12];
	double[] avgSolarPowerMW  = new double[12];
	
	double[] peakElectricalPowerMW = new double[12];
	double[] avgElectricalPowerMW  = new double[12];
	
	double[] peakHotThermalPowerMW = new double[12];
	double[] avgHotThermalPowerMW  = new double[12];
	
	double[] solarEnergyMWDay      = new double[12];
	double[] electricalEnergyMWDay = new double[12];
	double[] hotThermalEnergyMWDay = new double[12];

	double[] accumSolarEnergyMWYear      = new double[12];
	double[] accumElectricalEnergyMWYear = new double[12];
	double[] accumHotThermalEnergyMWYear = new double[12];
	
	double[] panelTempC = new double[12];
	double[] panelEffPerc = new double[12];
	double[] shadow09Perc = new double[12];
	double[] shadow12Perc = new double[12];

	public SolarCalculator2(String binDataFolder) 
	{
		this.binDataFolder = binDataFolder;
	}
	public SolarCalculator2(String binDataFolder, String[][] switchSettings) throws Exception
	{
		this.binDataFolder = binDataFolder;
		readSwitchSettings(switchSettings);
	}
	private void setLocation(double latitudeDeg, double longitudeDeg) throws Exception
	{
		this.latitudeDeg = latitudeDeg;
		this.longitudeDeg = longitudeDeg;
		solarLocation = new SolarLocation(latitudeDeg);
		for (int imonth = 0; imonth < 12; ++imonth) effectiveDaylightHoursForMonth[imonth] = effectiveDaylightHoursForMonth(imonth);
		double[] gbhda =  PvgisData.getGlobalHorizontalDailyAvgForYear(latitudeDeg, longitudeDeg, binDataFolder, false);
		for (int imonth = 0; imonth < 12; ++imonth)
		{
			double tcorr = effectiveDaylightHoursForMonth[imonth] * Math.sin(solarLocation.solarElevationAngleDeg(0.0, midMonthDay(imonth)) * degToRad);
			skyAttenuation[imonth] = gbhda[imonth] / tcorr / MaxSolarFlux;
		}
	}
	private double effectiveDaylightHoursForDay(int ndayJan1)
	{
		double hourStep = 2.0 / 60.0;
		double hour = -12.0;
		double effectiveDaylightHours = 0.0;
		double elevation = 0.0;
		while (hour < 12.0)
		{
			elevation = solarLocation.solarElevationAngleDeg(hour, ndayJan1);
			if (elevation > 0) effectiveDaylightHours = effectiveDaylightHours + Math.sin(elevation * degToRad);
			hour = hour + hourStep;
		}
		effectiveDaylightHours = effectiveDaylightHours * hourStep / Math.sin(solarLocation.solarElevationAngleDeg(0.0, ndayJan1) * degToRad);
		return effectiveDaylightHours;
		
	}
	private int midMonthDay(int imonth)
	{
		return (monthDay[imonth][1] + monthDay[imonth][0]) / 2;
	}
	private int numDaysInMonth(int imonth)
	{
		return monthDay[imonth][1] - monthDay[imonth][0] + 1;
	}
	public int getMonthIndex(int iday) throws Exception
	{
		if (iday < monthDay[0][0]) throw new Exception("Day out of range");
		if (iday > monthDay[11][1]) throw new Exception("Day out of range");
		int imonth = -1;
		while (imonth < 11)
		{
			imonth = imonth + 1;
			if ((monthDay[imonth][0] <= iday) && (iday <= monthDay[imonth][1])) return imonth;
		}
		throw new Exception("Day out of range");
	}
	private double effectiveDaylightHoursForMonth(int imonth)
	{
		double effectiveDaylightHours = 0.0;
		for (int iday = monthDay[imonth][0]; iday <= monthDay[imonth][1]; ++iday)
		{
			effectiveDaylightHours = effectiveDaylightHours + effectiveDaylightHoursForDay(iday) * Math.sin(solarLocation.solarElevationAngleDeg(0.0, iday) * degToRad); 
		}
		effectiveDaylightHours = effectiveDaylightHours / Math.sin(solarLocation.solarElevationAngleDeg(0.0, midMonthDay(imonth)) * degToRad);
		effectiveDaylightHours = effectiveDaylightHours / ((double) numDaysInMonth(imonth));
		return effectiveDaylightHours;
	}
	public double getPanelTemp(int iday)
	{
		double temp = hotPanelTempC;
		if ((coldPanelDayStart <= iday) && (iday <= coldPanelDayEnd)) temp = coldPanelTempC;
		return temp;
	}
	public double getPanelEffPer(int iday)
	{
		double eta = panelEta25Per * (1.0 - kLperDegC * (getPanelTemp(iday) - 25));
		return eta;
	}
	public double getShadowPerc(int iday, double hoursAfterNoon) throws Exception
	{
		double sinTheta = Math.sin(panelInclinDeg * degToRad);
		double cosTheta = Math.cos(panelInclinDeg * degToRad);
		double tanAlpha = Math.tan(solarLocation.solarElevationAngleDeg(hoursAfterNoon, iday) * degToRad);
		double shadow = fieldUtilFactorPer * sinTheta - (100.0 - fieldUtilFactorPer) * cosTheta * tanAlpha;
		shadow = shadow / (sinTheta + cosTheta * tanAlpha);
		shadow = 100.0 * shadow / fieldUtilFactorPer;
		if (shadow < 0.0) shadow = 0.0;
		return shadow;
	}
	public double getPanelAreaHectares()
	{
		return fieldAreaHectares * 0.01 * fieldUtilFactorPer / Math.cos(panelInclinDeg * degToRad);
	}
	public double getCollectionAreaHectares(int iday, double hoursAfterNoon) throws Exception
	{
		return getPanelAreaHectares() * (1.0 - 0.01 * getShadowPerc(iday, hoursAfterNoon));
	}
	public double getSolarPowerDensityWM2(int iday, double hoursAfterNoon, boolean peak) throws Exception
	{
		double altDeg = solarLocation.solarElevationAngleDeg(hoursAfterNoon, iday);
		double azDeg = solarLocation.sunAzimuthDeg(hoursAfterNoon, iday);
		double powerDensity = MaxSolarFlux 
				* (Math.cos(azDeg * degToRad) * Math.cos(altDeg * degToRad) * Math.sin(panelInclinDeg * degToRad)
						                      + Math.sin(altDeg * degToRad) * Math.cos(panelInclinDeg * degToRad));
		if (altDeg < 0.0) powerDensity = 0.0;
		if (!peak) powerDensity = powerDensity * skyAttenuation[getMonthIndex(iday)];
		return powerDensity;
	}
	public double getSolarPowerMW(int iday, double hoursAfterNoon, boolean peak) throws Exception
	{
		double power = getSolarPowerDensityWM2(iday, hoursAfterNoon, peak) * getCollectionAreaHectares(iday, hoursAfterNoon) * 10000.0 / 1.0e+06;
		return power;
	}
	public double getElectricalPowerMW(int iday, double hoursAfterNoon, boolean peak) throws Exception
	{
		double power = getSolarPowerMW(iday, hoursAfterNoon, peak) * 0.01 * getPanelEffPer(iday) * 0.01 * transEtaPer;
		return power;
	}
	public double getHotThermalPowerMW(int iday, double hoursAfterNoon, boolean peak) throws Exception
	{
		if (getPanelTemp(iday) < hotPanelTempC) return 0.0;
		return getSolarPowerMW(iday, hoursAfterNoon, peak) * (1.0 - 0.01 * getPanelEffPer(iday));
	}
	public double getSolarEnergyForDayMWDay(int iday, boolean peak) throws Exception
	{
		// do only half day and then multiply by 2
		double time = 0.0;
		double stepHour = 0.1;
		double energy = 0.0;
		while (time <= 11.0)
		{
			energy = energy + getSolarPowerMW(iday, time, peak);
			time = time + stepHour;
		}
		energy = 2.0 * energy * stepHour / 24.0;
		return energy;
	}
	public double getElectricalEnergyForDayMWDay(int iday, boolean peak) throws Exception
	{
		return getSolarEnergyForDayMWDay(iday, peak) * 0.01 * getPanelEffPer(iday) * 0.01 * transEtaPer;
	}
	public double getHotThermalEnergyForDayMWDay(int iday, boolean peak) throws Exception
	{
		if (getPanelTemp(iday) < hotPanelTempC) return 0;
		return getSolarEnergyForDayMWDay(iday, peak) * (1.0 - 0.01 * getPanelEffPer(iday));
	}
	public double getSolarEnergyForYearMWYear(boolean peak) throws Exception
	{
		double energy = 0.0;
		for (int iday = 0; iday < 365; ++iday)
			energy = energy + getSolarEnergyForDayMWDay(iday, peak) / 365.0;
		return energy;
	}
	public double getElectricalEnergyForYearMWYear(boolean peak) throws Exception
	{
		double energy = 0.0;
		for (int iday = 0; iday < 365; ++iday)
			energy = energy + getElectricalEnergyForDayMWDay(iday, peak) / 365.0;
		return energy;
	}
	public double getHotThermalEnergyForYearMWYear(boolean peak) throws Exception
	{
		double energy = 0.0;
		for (int iday = 0; iday < 365; ++iday)
			energy = energy + getHotThermalEnergyForDayMWDay(iday, peak) / 365.0;
		return energy;
	}
	public double getHotThermalAverageDailyEnergyMWDay(boolean peak) throws Exception
	{
		double energy = getHotThermalEnergyForYearMWYear(peak) * 365.0;
		double numDays = coldPanelDayStart + 365.0 - coldPanelDayEnd;
		return energy / numDays;
		
	}
	public void adjustShadowOnDate(int iday, double targetShadow) throws Exception
	{
		double fieldUtilStart = 98.0;
		double deltaFieldUtil = 1.0;
		double shadowPlus;
		double shadowMinus;
		double slope;
		int ntryMax = 20;
		double shadow;
		
		int ntry = 0;
		fieldUtilFactorPer = 10.0;
		while (ntry < ntryMax)
		{
			fieldUtilFactorPer = fieldUtilStart + deltaFieldUtil;
			shadowPlus = getShadowPerc(iday, 0.0);
			fieldUtilFactorPer = fieldUtilStart - deltaFieldUtil;
			shadowMinus = getShadowPerc(iday, 0.0);
			slope = (shadowPlus - shadowMinus) / (2.0 * deltaFieldUtil);
			fieldUtilFactorPer = fieldUtilStart;
			shadow = getShadowPerc(iday, 0.0);
			if (Math.abs(slope) > 0)
			{
				fieldUtilStart = fieldUtilStart + 0.25 * (targetShadow - shadow) / slope;
				ntry = ntry + 1;
			}
			else
			{
				ntry = ntryMax + 1;
			}
		}
		fieldUtilFactorPer = fieldUtilStart;
	}
	private void setPeakDay() throws Exception
	{
		peakDay = 1;
		double peakSunshine = getSolarPowerDensityWM2(peakDay, 0.0, true);
		for (int ii = 0; ii < 365; ++ii)
		{
			double sunshine = getSolarPowerDensityWM2(ii, 0.0, true);
			if (sunshine > peakSunshine)
			{
				peakSunshine = sunshine;
				peakDay = ii;
			}
		}
	}
	public void adjustFieldAreaToPeakElecPower(double targetPowerMW) throws Exception
	{
		fieldAreaHectares = fieldAreaHectares * targetPowerMW / getElectricalPowerMW(getPeakDay(), 0.0, true);
	}
	public void calcMonthlyAverages() throws Exception
	{
		for (int imonth = 0; imonth < 12; ++imonth)
		{
			peakSolarPowerDensityWM2[imonth] = 0.0;
			avgSolarPowerDensityWM2[imonth]  = 0.0;
			 
			peakSolarPowerMW[imonth] = 0.0;
			avgSolarPowerMW[imonth]  = 0.0;
			
			peakElectricalPowerMW[imonth] = 0.0;
			avgElectricalPowerMW[imonth]  = 0.0;
			
			peakHotThermalPowerMW[imonth] = 0.0;
			avgHotThermalPowerMW[imonth]  = 0.0;
			
			solarEnergyMWDay[imonth]      = 0.0;
			electricalEnergyMWDay[imonth] = 0.0;
			hotThermalEnergyMWDay[imonth] = 0.0;
			
			panelTempC[imonth]   = 0.0;
			panelEffPerc[imonth] = 0.0;
			shadow09Perc[imonth] = 0.0;
			shadow12Perc[imonth] = 0.0;

			for (int iday = monthDay[imonth][0]; iday <= monthDay[imonth][1]; ++iday)
			{
				peakSolarPowerDensityWM2[imonth] = peakSolarPowerDensityWM2[imonth] + getSolarPowerDensityWM2(iday, 0.0, true)  / ((double) montNday[imonth]);
				avgSolarPowerDensityWM2[imonth]  = avgSolarPowerDensityWM2[imonth]  + getSolarPowerDensityWM2(iday, 0.0, false) / ((double) montNday[imonth]);
				
				peakSolarPowerMW[imonth] = peakSolarPowerMW[imonth] + getSolarPowerMW(iday, 0.0, true)  / ((double) montNday[imonth]);
				avgSolarPowerMW[imonth]  = avgSolarPowerMW[imonth]  + getSolarPowerMW(iday, 0.0, false) / ((double) montNday[imonth]);
				
				peakElectricalPowerMW[imonth] = peakElectricalPowerMW[imonth] + getElectricalPowerMW(iday, 0.0, true)  / ((double) montNday[imonth]);
				avgElectricalPowerMW[imonth]  = avgElectricalPowerMW[imonth]  + getElectricalPowerMW(iday, 0.0, false) / ((double) montNday[imonth]);

				peakHotThermalPowerMW[imonth] = peakHotThermalPowerMW[imonth] + getHotThermalPowerMW(iday, 0.0, true)  / ((double) montNday[imonth]);
				avgHotThermalPowerMW[imonth]  = avgHotThermalPowerMW[imonth]  + getHotThermalPowerMW(iday, 0.0, false) / ((double) montNday[imonth]);

				solarEnergyMWDay[imonth]       = solarEnergyMWDay[imonth]       + getSolarEnergyForDayMWDay(iday, false) / ((double) montNday[imonth]);
				electricalEnergyMWDay[imonth]  = electricalEnergyMWDay[imonth]  + getElectricalEnergyForDayMWDay(iday, false) / ((double) montNday[imonth]);
				hotThermalEnergyMWDay[imonth]  = hotThermalEnergyMWDay[imonth]  + getHotThermalEnergyForDayMWDay(iday, false) / ((double) montNday[imonth]);
				
				panelTempC[imonth]   = panelTempC[imonth]   + getPanelTemp(iday)        / ((double) montNday[imonth]);
				panelEffPerc[imonth] = panelEffPerc[imonth] + getPanelEffPer(iday)      / ((double) montNday[imonth]);
				shadow09Perc[imonth] = shadow09Perc[imonth] + getShadowPerc(iday, -3.0) / ((double) montNday[imonth]);
				shadow12Perc[imonth] = shadow12Perc[imonth] + getShadowPerc(iday,  0.0) / ((double) montNday[imonth]);
				
			}
			accumSolarEnergyMWYear[imonth]      = solarEnergyMWDay[imonth] * ((double) montNday[imonth]) / 365.0;
			accumElectricalEnergyMWYear[imonth] = electricalEnergyMWDay[imonth] * ((double) montNday[imonth]) / 365.0;
			accumHotThermalEnergyMWYear[imonth] = hotThermalEnergyMWDay[imonth] * ((double) montNday[imonth]) / 365.0;
			if (imonth > 0)
			{
				accumSolarEnergyMWYear[imonth]      = accumSolarEnergyMWYear[imonth] + accumSolarEnergyMWYear[imonth - 1];
				accumElectricalEnergyMWYear[imonth] = accumElectricalEnergyMWYear[imonth] + accumElectricalEnergyMWYear[imonth - 1];
				accumHotThermalEnergyMWYear[imonth] = accumHotThermalEnergyMWYear[imonth] + accumHotThermalEnergyMWYear[imonth - 1];
			}
		}
	}
	public String getDataBaseName()
	{
		return new File(binDataFolder).getName();
	}
	public void summaryParameters(PrintStream pw, String comma) throws Exception
	{
		DecimalFormat f3 = new DecimalFormat("###.###");
		pw.println("Parameter                             " + comma + "Value" + comma + "Unit");
		pw.println("Latitude                              " + comma + f3.format(getLatitudeDeg()) + comma + "deg");
		pw.println("Longitude                             " + comma + f3.format(getLongitudeDeg()) + comma + "deg");
		pw.println("DataBase                              " + comma + getDataBaseName() + comma + " ");
		pw.println("Field Area                            " + comma + f3.format(getFieldAreaHectares()) + comma + "Hectares");
		pw.println("Field Utilization Factor              " + comma + f3.format(getFieldUtilFactorPer()) + comma + "%");
		pw.println("Panel Inclination Angle               " + comma + getPanelInclinDeg() + comma + "deg");
		pw.println("Panel Electrical Eff at 25C           " + comma + getPanelEta25Per() + comma + "%");
		pw.println("Electrical Transmission Efficiency    " + comma + getTransEtaPer() + comma + "%");
		pw.println("Panel Electrical De-rating            " + comma + getkLperDegC() + comma + "/degC");
		pw.println("Start Day for Cold Panel              " + comma + getColdPanelDayStart());
		pw.println("Stop Day for Cold Panel               " + comma + getColdPanelDayEnd());
		pw.println("Hot Panel Temperature                 " + comma + getHotPanelTempC() + comma + "degC");
		pw.println("Cold Panel Temperature                " + comma + getColdPanelTempC() + comma + "degC");
		pw.println("Panel Area                            " + comma + f3.format(getPanelAreaHectares()) + comma + "Hectares");
		pw.println("Peak Day                              " + comma + peakDay);
		pw.println("Shadow on Peak Day                    " + comma + f3.format(getShadowPerc(peakDay, 0.0)) + comma + "%");
		pw.println("Shadow Winter Solstice                " + comma + f3.format(getShadowPerc(355, 0.0)) + comma + "%");
		pw.println("Shadow on Vernal Equinox              " + comma + f3.format(getShadowPerc(81, 0.0)) + comma + "%");
		pw.println("Shadow on Autumnal Equinox            " + comma + f3.format(getShadowPerc(264, 0.0)) + comma + "%");
		pw.println("Peak Electrical Power on Peak Day     " + comma + f3.format(getElectricalPowerMW(peakDay, 0.0, true)) + comma + "MW");
		pw.println("Electrical Energy on Peak Day         " + comma + f3.format(getElectricalEnergyForDayMWDay(peakDay, false)) + comma + "MW-day");
		pw.println("Yearly Electrical Energy              " + comma + f3.format(getElectricalEnergyForYearMWYear(false)) + comma + "MW-Yr");
		pw.println("Yearly Hot Thermal Energy             " + comma + f3.format(getHotThermalEnergyForYearMWYear(false)) + comma + "MW-Yr");
		pw.println("Average Hot Thermal Daily Energy      " + comma + f3.format(getHotThermalAverageDailyEnergyMWDay(false)) + comma + "MW-Day");
		pw.println(comma);
	}
	public void printSunshineDataSheet(PrintStream ps) throws Exception
	{
		calcMonthlyAverages();
		DecimalFormat f3 = new DecimalFormat("###.###");
		ps.println("     ,Effective,   Sky  ,      Peak         ,      Average      ,   Peak    ,  Average  ,   Daily    ,Accumulated,       Peak      ,    Average     ,     Daily       ,   Accumulated   ,       Peak      ,      Average    ,       Daily      ,    Accumulated   ,  Panel    ,  Panel   , 9am  , 12pm ");
		ps.println("Month, Daylight,Clearity,Solar Power Density,Solar Power Density,Solar Power,Solar Power,Solar Energy,Solar Energy,Electrical Power,Electrical Power,Electrical Energy,Electrical Energy,Hot Thermal Power,Hot Thermal Power,Hot Thermal Energy,Hot Thermal Energy,Temperature,Efficiency,Shadow,Shadow");
		ps.println("     ,  hours  ,   %    , Watts/square meter, Watts/square meter,    MW     ,    MW     ,   MW-Day   ,   MW-Year  ,       MW       ,       MW       ,      MW-Day     ,      MW-Year    ,        MW       ,        MW       ,       MW-Day     ,       MW-Year    ,    degC   ,    %     ,   %  ,   %  ");
		for (int imonth = 0; imonth < 12; ++imonth)
		{
			String dataLine = monthName[imonth] 
					+ "," + f3.format(effectiveDaylightHoursForMonth[imonth]) 
					+ "," + f3.format(skyAttenuation[imonth] * 100.0) 
					+ "," + f3.format(peakSolarPowerDensityWM2[imonth]) 
					+ "," + f3.format(avgSolarPowerDensityWM2[imonth]) 
					+ "," + f3.format(peakSolarPowerMW[imonth]) 
					+ "," + f3.format(avgSolarPowerMW[imonth]) 
					+ "," + f3.format(solarEnergyMWDay[imonth]) 
					+ "," + f3.format(accumSolarEnergyMWYear[imonth]) 
					+ "," + f3.format(peakElectricalPowerMW[imonth]) 
					+ "," + f3.format(avgElectricalPowerMW[imonth]) 
					+ "," + f3.format(electricalEnergyMWDay[imonth]) 
					+ "," + f3.format(accumElectricalEnergyMWYear[imonth]) 
					+ "," + f3.format(peakHotThermalPowerMW[imonth]) 
					+ "," + f3.format(avgHotThermalPowerMW[imonth]) 
					+ "," + f3.format(hotThermalEnergyMWDay[imonth]) 
					+ "," + f3.format(accumHotThermalEnergyMWYear[imonth]) 
					+ "," + f3.format(panelTempC[imonth]) 
					+ "," + f3.format(panelEffPerc[imonth]) 
					+ "," + f3.format(shadow09Perc[imonth]) 
					+ "," + f3.format(shadow12Perc[imonth]);
			ps.println(dataLine);
		}
	}
	public void creatCharts(String parentDirPath) throws Exception
	{
		calcMonthlyAverages();
		String title;
		String vertTitle[] = {"",""};
		DefaultCategoryDataset[] dataset = new DefaultCategoryDataset[2];

		title = "Sky Conditions";
		vertTitle[0] = "Effective Daylight Hours";
		vertTitle[1] = "Sky Clearity (%)";
		dataset[0] = new DefaultCategoryDataset();
		dataset[1] = new DefaultCategoryDataset();
		for (int imonth = 0; imonth < 12; ++imonth)
		{
			dataset[0].addValue(effectiveDaylightHoursForMonth[imonth], "Effective Daylight Hours", monthName[imonth]);
			dataset[1].addValue(skyAttenuation[imonth] * 100.0, "Sky Clearity", monthName[imonth]);
		}
		AigChartUtilities.createDualAxisMonthPlot(title, vertTitle, dataset, parentDirPath);

		title = "Solar Power Density";
		vertTitle[0] = "Power Density (W/m2)";
		dataset[0] = new DefaultCategoryDataset();
		for (int imonth = 0; imonth < 12; ++imonth)
		{
			dataset[0].addValue(peakSolarPowerDensityWM2[imonth], "Peak", monthName[imonth]);
			dataset[0].addValue(avgSolarPowerDensityWM2[imonth], "Average", monthName[imonth]);
		}
		AigChartUtilities.createSingleAxisMonthPlot(title, vertTitle[0], dataset[0], parentDirPath);

		title = "Solar Power";
		vertTitle[0] = "Power (MW)";
		dataset[0] = new DefaultCategoryDataset();
		for (int imonth = 0; imonth < 12; ++imonth)
		{
			dataset[0].addValue(peakSolarPowerMW[imonth], "Peak", monthName[imonth]);
			dataset[0].addValue(avgSolarPowerMW[imonth], "Average", monthName[imonth]);
		}
		AigChartUtilities.createSingleAxisMonthPlot(title, vertTitle[0], dataset[0], parentDirPath);
	
		title = "Solar Energy";
		vertTitle[0] = "Daily Energy (MW-Day)";
		vertTitle[1] = "Accumulated Energy (MW-Year)";
		dataset[0] = new DefaultCategoryDataset();
		dataset[1] = new DefaultCategoryDataset();
		for (int imonth = 0; imonth < 12; ++imonth)
		{
			dataset[0].addValue(solarEnergyMWDay[imonth], "Daily", monthName[imonth]);
			dataset[1].addValue(accumSolarEnergyMWYear[imonth], "Accumulated", monthName[imonth]);
		}
		AigChartUtilities.createDualAxisMonthPlot(title, vertTitle, dataset, parentDirPath);

		title = "Electrical Power";
		vertTitle[0] = "Power (MW)";
		dataset[0] = new DefaultCategoryDataset();
		for (int imonth = 0; imonth < 12; ++imonth)
		{
			dataset[0].addValue(peakElectricalPowerMW[imonth], "Peak", monthName[imonth]);
			dataset[0].addValue(avgElectricalPowerMW[imonth], "Average", monthName[imonth]);
		}
		AigChartUtilities.createSingleAxisMonthPlot(title, vertTitle[0], dataset[0], parentDirPath);
	
		title = "Electrical Energy";
		vertTitle[0] = "Daily Energy (MW-Day)";
		vertTitle[1] = "Accumulated Energy (MW-Year)";
		dataset[0] = new DefaultCategoryDataset();
		dataset[1] = new DefaultCategoryDataset();
		for (int imonth = 0; imonth < 12; ++imonth)
		{
			dataset[0].addValue(electricalEnergyMWDay[imonth], "Daily", monthName[imonth]);
			dataset[1].addValue(accumElectricalEnergyMWYear[imonth], "Accumulated", monthName[imonth]);
		}
		AigChartUtilities.createDualAxisMonthPlot(title, vertTitle, dataset, parentDirPath);

		title = "Hot Thermal Power";
		vertTitle[0] = "Power (MW)";
		dataset[0] = new DefaultCategoryDataset();
		for (int imonth = 0; imonth < 12; ++imonth)
		{
			dataset[0].addValue(peakHotThermalPowerMW[imonth], "Peak", monthName[imonth]);
			dataset[0].addValue(avgHotThermalPowerMW[imonth], "Average", monthName[imonth]);
		}
		AigChartUtilities.createSingleAxisMonthPlot(title, vertTitle[0], dataset[0], parentDirPath);
	
		title = "Hot Thermal Energy";
		vertTitle[0] = "Daily Energy (MW-Day)";
		vertTitle[1] = "Accumulated Energy (MW-Year)";
		dataset[0] = new DefaultCategoryDataset();
		dataset[1] = new DefaultCategoryDataset();
		for (int imonth = 0; imonth < 12; ++imonth)
		{
			dataset[0].addValue(hotThermalEnergyMWDay[imonth], "Daily", monthName[imonth]);
			dataset[1].addValue(accumHotThermalEnergyMWYear[imonth], "Accumulated", monthName[imonth]);
		}
		AigChartUtilities.createDualAxisMonthPlot(title, vertTitle, dataset, parentDirPath);
		
		title = "Solar Panel State";
		vertTitle[0] = "Temperature (deg C)";
		vertTitle[1] = "Electrical Efficiency (%)";
		dataset[0] = new DefaultCategoryDataset();
		dataset[1] = new DefaultCategoryDataset();
		for (int imonth = 0; imonth < 12; ++imonth)
		{
			dataset[0].addValue(panelTempC[imonth], "Temperature", monthName[imonth]);
			dataset[1].addValue(panelEffPerc[imonth], "Electrical Efficiency", monthName[imonth]);
		}
		AigChartUtilities.createDualAxisMonthPlot(title, vertTitle, dataset, parentDirPath);

		title = "Solar Panel Shadow";
		vertTitle[0] = "Shadow (%)";
		dataset[0] = new DefaultCategoryDataset();
		for (int imonth = 0; imonth < 12; ++imonth)
		{
			dataset[0].addValue(shadow09Perc[imonth], "9am", monthName[imonth]);
			dataset[0].addValue(shadow12Perc[imonth], "12pm", monthName[imonth]);
		}
		AigChartUtilities.createSingleAxisMonthPlot(title, vertTitle[0], dataset[0], parentDirPath);
	}
	public void readSwitchSettings(String[][] switchSettings) throws Exception
	{
		int shadowDay = -1;
		double desiredPower = -1;
		for (int ii = 0; ii < switchSettings.length; ++ii)
		{
//			System.out.println(switchSettings[ii][0] + " " + switchSettings[ii][1]);
			if (switchSettings[ii][0].equals("-fa")) fieldAreaHectares = Double.parseDouble(switchSettings[ii][1]);
			if (switchSettings[ii][0].equals("-fu")) fieldUtilFactorPer = Double.parseDouble(switchSettings[ii][1]);
			if (switchSettings[ii][0].equals("-ia")) panelInclinDeg = Double.parseDouble(switchSettings[ii][1]);
			if (switchSettings[ii][0].equals("-pe")) panelEta25Per = Double.parseDouble(switchSettings[ii][1]);
			if (switchSettings[ii][0].equals("-ps")) kLperDegC = Double.parseDouble(switchSettings[ii][1]);
			if (switchSettings[ii][0].equals("-te")) transEtaPer = Double.parseDouble(switchSettings[ii][1]);
			if (switchSettings[ii][0].equals("-wt")) hotPanelTempC = Double.parseDouble(switchSettings[ii][1]);
			if (switchSettings[ii][0].equals("-st")) coldPanelTempC = Double.parseDouble(switchSettings[ii][1]);
			if (switchSettings[ii][0].equals("-sd")) coldPanelDayStart = Integer.parseInt(switchSettings[ii][1]);
			if (switchSettings[ii][0].equals("-ed")) coldPanelDayEnd = Integer.parseInt(switchSettings[ii][1]);
			if (switchSettings[ii][0].equals("-nb")) shadowDay = Integer.parseInt(switchSettings[ii][1]);
			if (switchSettings[ii][0].equals("-mp")) desiredPower = Double.parseDouble(switchSettings[ii][1]);
			if (switchSettings[ii][0].equals("-la")) latitudeDeg = Double.parseDouble(switchSettings[ii][1]);
			if (switchSettings[ii][0].equals("-lo")) longitudeDeg = Double.parseDouble(switchSettings[ii][1]);
		}
		if (coldPanelDayEnd < coldPanelDayStart) throw new Exception("coldPanelDayEnd must be greater than coldPanelDayStart");
		setLocation(latitudeDeg, longitudeDeg);
		if (shadowDay > 0) adjustShadowOnDate(shadowDay, 0.0);
		setPeakDay();
		if (desiredPower > 0) adjustFieldAreaToPeakElecPower(desiredPower);
	}
	public static void main(String[] args) throws Exception 
	{
		String[][] switchSettings = {
				{"-fa", "25.0"}, 
				{"-fu", "70.0"}, 
				{"-ia", "10.0"}, 
				{"-pe", "17.0"}, 
				{"-ps", "0.0048"}, 
				{"-te", "96.0"}, 
				{"-wt", "80.0"}, 
				{"-st", "25.0"},
				{"-sd", "120"},
				{"-ed", "273"},
				{"-nb", "50"},
				{"-mp", "25.0"},
				{"-la", "55.741"},
				{"-lo", "13.263"}
			};
		String binDataFolder = "data/cmsafBin";
		SolarCalculator2 solarCalculator = new SolarCalculator2(binDataFolder, switchSettings);
		solarCalculator.summaryParameters(System.out, " ");
		PrintStream outputData = new PrintStream("MonthlySunshineDataSheet.csv");
		solarCalculator.printSunshineDataSheet(outputData);
		outputData.close();
		outputData = new PrintStream("summaryParameters.csv");
		solarCalculator.summaryParameters(outputData, ",");
		outputData.close();
		solarCalculator.creatCharts("");
		
		
		
	}

}
