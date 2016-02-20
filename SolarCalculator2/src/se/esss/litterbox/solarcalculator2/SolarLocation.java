package se.esss.litterbox.solarcalculator2;

import java.io.FileNotFoundException;

public class SolarLocation 
{
	final static double PI = Math.PI;
	final static double TWOPI = 2.0 * PI;
	final static double degToRad = PI / 180.0;
	final static double radToDeg = 180.0 / PI;
	private double latitudeDeg;
	public double getLatitudeDeg() {return latitudeDeg;}
	public void setLatitudeDeg(double latitudeDeg) {this.latitudeDeg = latitudeDeg;}
	
	public SolarLocation(double latitudeDeg)
	{
		this.setLatitudeDeg(latitudeDeg);
	}
	public double solarElevationAngleDeg(double hoursAfterNoon, int ndayJan1)
	{
// Based on http://en.wikipedia.org/wiki/Solar_zenith_angle
		double latitudeRad = degToRad * latitudeDeg; 
		double sunDecRad = degToRad * sunDeclinationDeg(ndayJan1);
		
		double sinElev = Math.cos(TWOPI * hoursAfterNoon / 24.0) * Math.cos(sunDecRad) * Math.cos(latitudeRad) 
				+ Math.sin(sunDecRad) * Math.sin(latitudeRad);
		return Math.asin(sinElev) * radToDeg;
	}
	private double sunDeclinationDeg(int ndayJan1)
	{
// Based on http://en.wikipedia.org/wiki/Position_of_the_Sun
		double nDayPerYear = 365.0;
		double ang1 = TWOPI * (((double) ndayJan1) + 10.0) / nDayPerYear;
		double ang2 = 2.0 * 0.0167 * Math.sin(TWOPI * (((double) ndayJan1) - 2.0) / nDayPerYear);
		double sinDec = Math.sin(-23.44 * degToRad) * Math.cos(ang1 + ang2);
		return Math.asin(sinDec) * radToDeg;
	}
	public double sunAzimuthDeg(double hoursAfterNoon, int ndayJan1)
	{
// Based http://en.wikipedia.org/wiki/Solar_azimuth_angle
		double elvAng = degToRad * solarElevationAngleDeg(hoursAfterNoon, ndayJan1);
		double decAng = degToRad * sunDeclinationDeg(ndayJan1);
		double hourAng = TWOPI * hoursAfterNoon / 24.0;
		double latAng = latitudeDeg * degToRad;
		double sinAz = -Math.sin(hourAng) * Math.cos(decAng) / Math.cos(elvAng);
		
		double cosAz = -(Math.sin(decAng) * Math.cos(latAng) - Math.cos(hourAng) * Math.cos(decAng) * Math.sin(latAng)) / Math.cos(elvAng);
//		if (decAng < 0.0) cosAz = -cosAz;
		double az = Math.atan2(sinAz, cosAz) * radToDeg;
//		System.out.println(hoursAfterNoon + "," + az + "," + sinAz + "," + cosAz + "," + solarElevationAngleDeg(hoursAfterNoon, ndayJan1, latitudeDeg));
		return az;
	}
	public static void main(String[] args) throws FileNotFoundException 
	{
		int ndayJan1 = 171;
		double latitudeDeg = 55;
		double hoursAfterNoon = 0.0;
		SolarLocation solarLocation = new SolarLocation(latitudeDeg);
		System.out.println("Sun dec = " + solarLocation.sunDeclinationDeg(ndayJan1));
		System.out.println("Sun elv = " + solarLocation.solarElevationAngleDeg(hoursAfterNoon, ndayJan1));
		System.out.println("Sun Azm = " + solarLocation.sunAzimuthDeg(hoursAfterNoon, ndayJan1));

	}

}
