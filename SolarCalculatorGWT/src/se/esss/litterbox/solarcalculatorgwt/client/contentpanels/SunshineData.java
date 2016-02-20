package se.esss.litterbox.solarcalculatorgwt.client.contentpanels;

import se.esss.litterbox.solarcalculatorgwt.client.EntryPointApp;
import se.esss.litterbox.solarcalculatorgwt.client.gskel.CsvFilePanel;
import se.esss.litterbox.solarcalculatorgwt.client.gskel.GskelSetupApp;
import se.esss.litterbox.solarcalculatorgwt.client.gskel.GskelVerticalPanel;

public class SunshineData extends GskelVerticalPanel
{

	private CsvFilePanel sunshineDataCsvFilePanel;
	
	public CsvFilePanel getSunshineDataCsvFilePanel() {return sunshineDataCsvFilePanel;}

	public SunshineData(String tabTitle, GskelSetupApp setupApp, EntryPointApp entryPointApp) 
	{
		super(tabTitle, setupApp);
		sunshineDataCsvFilePanel = new CsvFilePanel(this, "SunshineData", 3);
		add(sunshineDataCsvFilePanel);
		setParentTabVisibile(false);
	}
	private void fillScrollPanel()
	{
		sunshineDataCsvFilePanel.setWidth("100%");
		sunshineDataCsvFilePanel.getDataGridScrollPane().setHeight(getGskelTabLayoutScrollPanel().getPanelHeight() - 50 + "px");
		setHeight(getGskelTabLayoutScrollPanel().getPanelHeight() - 50 + "px");
		
		sunshineDataCsvFilePanel.resizeMe();
	}
	@Override
	public void tabLayoutPanelInterfaceAction(String message) 
	{
		fillScrollPanel();
	}

	@Override
	public void optionDialogInterfaceAction(String choiceButtonText) 
	{
		
	}

	@Override
	public void tabLayoutScrollPanelResizeInterfaceAction(String message) 
	{
		fillScrollPanel();
		
	}
}
