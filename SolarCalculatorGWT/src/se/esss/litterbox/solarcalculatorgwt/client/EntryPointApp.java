package se.esss.litterbox.solarcalculatorgwt.client;


import com.google.gwt.core.client.EntryPoint;

import se.esss.litterbox.solarcalculatorgwt.client.contentpanels.*;
import se.esss.litterbox.solarcalculatorgwt.client.gskel.GskelSetupApp;
public class EntryPointApp implements EntryPoint 
{
	private GskelSetupApp setupApp;
	private SolarFieldSetup solarFieldSetup;

	public SolarFieldSetup getSolarFieldSetup() {return solarFieldSetup;}

	public void onModuleLoad() 
	{
		setupApp = new GskelSetupApp();
		setupApp.setDebug(false);
		setupApp.setVersionDate("August 16, 2015 06:40");
		setupApp.setVersion("v2.0");
		setupApp.setAuthor("Dave McGinnis david.mcginnis@esss.se");
		setupApp.setLogoImage("images/essLogo.png");
		setupApp.setLogoTitle("Solar Calculator");
		setupApp.addWelcomeStatus();
		
		solarFieldSetup = new SolarFieldSetup("Field Setup", setupApp, this);		
		
	}
}
