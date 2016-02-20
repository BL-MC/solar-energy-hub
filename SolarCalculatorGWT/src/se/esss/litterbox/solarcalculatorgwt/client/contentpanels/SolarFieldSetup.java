package se.esss.litterbox.solarcalculatorgwt.client.contentpanels;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CaptionPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

import se.esss.litterbox.solarcalculatorgwt.client.EntryPointApp;
import se.esss.litterbox.solarcalculatorgwt.client.gskel.CsvFilePanel;
import se.esss.litterbox.solarcalculatorgwt.client.gskel.GskelMessageDialog;
import se.esss.litterbox.solarcalculatorgwt.client.gskel.GskelSetupApp;
import se.esss.litterbox.solarcalculatorgwt.client.gskel.GskelVerticalPanel;
import se.esss.litterbox.solarcalculatorgwt.client.gskel.settinggridline.ListSettingGridLine;
import se.esss.litterbox.solarcalculatorgwt.client.gskel.settinggridline.RangeSettingGridLine;
import se.esss.litterbox.solarcalculatorgwt.shared.gskel.CsvFile;
import se.esss.litterbox.solarcalculatorgwt.shared.gskel.GskelException;

public class SolarFieldSetup extends GskelVerticalPanel
{

	private RangeSettingGridLine[] rangeSettingList = null;
	private ListSettingGridLine dataParkList = null;
	private Button runButton = new Button("Run");
	private EntryPointApp entryPointApp;
	private CsvFilePanel summaryParametersCsvFilePanel;
	private CaptionPanel resultCaptionPanel = new CaptionPanel("Results");
	private CaptionPanel plotLinkCaptionPanel;
	private CaptionPanel dataTableLinkCaptionPanel;

	public EntryPointApp getEntryPointApp() {return entryPointApp;}
	
	public SolarFieldSetup(String tabTitle, GskelSetupApp setupApp, EntryPointApp entryPointApp) 
	{
		super(tabTitle, setupApp);
		this.entryPointApp = entryPointApp;
		CaptionPanel settingsCaptionPanel = settingsCaptionPanel();
		settingsCaptionPanel.setWidth("400px");

		summaryParametersCsvFilePanel = new CsvFilePanel(this, "", 1);
		summaryParametersCsvFilePanel.setWidth("500px");
		summaryParametersCsvFilePanel.setHeight("450px");
		resultCaptionPanel.add(summaryParametersCsvFilePanel);
		resultCaptionPanel.setVisible(false);
		plotLinkCaptionPanel = plotLinkCaptionPanel();
		plotLinkCaptionPanel.setVisible(false);
		dataTableLinkCaptionPanel = dataTableLinkCaptionPanel();
		dataTableLinkCaptionPanel.setVisible(false);
		HorizontalPanel hp1 = new HorizontalPanel();
		hp1.add(settingsCaptionPanel());
		hp1.add(resultCaptionPanel);
		VerticalPanel linkPanels = new VerticalPanel();
		linkPanels.add(plotLinkCaptionPanel);;
		linkPanels.add(dataTableLinkCaptionPanel);
		hp1.add(linkPanels);
		add(hp1);
		add(runButton);
		runButton.addClickHandler(new RunButtonClickHandler(this));

	}
	@Override
	public void tabLayoutScrollPanelResizeInterfaceAction(String message) 
	{
		// TODO Auto-generated method stub
		
	}
	private String[][] getSwitchCommandArray() throws GskelException 
	{
		String[][] switchSettings = new String[rangeSettingList.length ][2];
		for (int ii = 0; ii < rangeSettingList.length; ++ii)
		{
			switchSettings[ii] = rangeSettingList[ii].getSwitchCommandArray();
		}
		return switchSettings; 
	}
	private CaptionPanel plotLinkCaptionPanel()
	{
		CaptionPanel plotLinkCaptionPanel = new CaptionPanel("Plots");
		VerticalPanel linkPanel = new VerticalPanel();
		Anchor[] imageAnchorArray = new Anchor[10];
		String[] imageLinkArray = new String[10];
		String[] imageTitle = new String[10];
		imageTitle[0] = "Sky Conditions";
		imageTitle[1] = "Solar Power Density";
		imageTitle[2] = "Solar Power";
		imageTitle[3] = "Solar Energy";
		imageTitle[4] = "Electrical Power";
		imageTitle[5] = "Electrical Energy";
		imageTitle[6] = "Hot Thermal Power";
		imageTitle[7] = "Hot Thermal Energy";
		imageTitle[8] = "Solar Panel State";
		imageTitle[9] = "Solar Panel Shadow";
		imageLinkArray[0] = "tempFiles/SkyConditions.png";
		imageLinkArray[1] = "tempFiles/SolarPowerDensity.png";
		imageLinkArray[2] = "tempFiles/SolarPower.png";
		imageLinkArray[3] = "tempFiles/SolarEnergy.png";
		imageLinkArray[4] = "tempFiles/ElectricalPower.png";
		imageLinkArray[5] = "tempFiles/ElectricalEnergy.png";
		imageLinkArray[6] = "tempFiles/HotThermalPower.png";
		imageLinkArray[7] = "tempFiles/HotThermalEnergy.png";
		imageLinkArray[8] = "tempFiles/SolarPanelState.png";
		imageLinkArray[9] = "tempFiles/SolarPanelShadow.png";
		
		for (int iplot = 0; iplot < 10; ++iplot)
		{
			imageAnchorArray[iplot] = new Anchor(imageTitle[iplot]);
			imageAnchorArray[iplot].addClickHandler(new ImageLinkClickHandler(imageLinkArray[iplot],this.getPlotDialog(), imageTitle[iplot]));
			linkPanel.add(imageAnchorArray[iplot]);
		}
		plotLinkCaptionPanel.add(linkPanel);

		return plotLinkCaptionPanel;
		
	}
	private CaptionPanel dataTableLinkCaptionPanel()
	{
		CaptionPanel dataTableLinkCaptionPanel = new CaptionPanel("Data");
		VerticalPanel linkPanel = new VerticalPanel();
		Anchor[] tableAnchorArray = new Anchor[2];
		String[] tableLinkArray = new String[2];
		String[] tableTitle = new String[2];
		tableTitle[0] = "Summary Parameters";
		tableTitle[1] = "Monthly Data";
		tableLinkArray[0] = "tempFiles/summaryParameters.csv";
		tableLinkArray[1] = "tempFiles/MonthlySunshineDataSheet.csv";
		
		for (int iplot = 0; iplot < 2; ++iplot)
		{
			tableAnchorArray[iplot] = new Anchor(tableTitle[iplot]);
			tableAnchorArray[iplot].addClickHandler(new DownLoadClickHandler(tableLinkArray[iplot]));
			linkPanel.add(tableAnchorArray[iplot]);
		}
		dataTableLinkCaptionPanel.add(linkPanel);

		return dataTableLinkCaptionPanel;
		
	}
	private CaptionPanel settingsCaptionPanel()
	{
		Grid settingGrid = new Grid(16, 6);
		
		HTMLTable.CellFormatter formatter = settingGrid.getCellFormatter();
		formatter.setHorizontalAlignment(0, 1, HasHorizontalAlignment.ALIGN_CENTER);
		formatter.setVerticalAlignment(0, 1, HasVerticalAlignment.ALIGN_MIDDLE);		
		
		settingGrid.setWidget(0, 0, new Label("Setting"));
		settingGrid.setWidget(0, 1, new Label("Value"));
		settingGrid.setWidget(0, 3, new Label("Range"));
		settingGrid.setWidget(0, 5, new Label("Enabled"));
		
		rangeSettingList = new RangeSettingGridLine[14];
		
		rangeSettingList[0] = new RangeSettingGridLine(this, "Field Area (Hectares)", 25, settingGrid, 1);
		rangeSettingList[0].setLowerRange(0);
		rangeSettingList[0].setUpperRange(100);
		rangeSettingList[0].setSwitchText("-fa");
		rangeSettingList[0].setMustUse(true);

		rangeSettingList[1] = new RangeSettingGridLine(this, "Field Util. (%)", 70, settingGrid, 2);
		rangeSettingList[1].setLowerRange(0);
		rangeSettingList[1].setUpperRange(100);
		rangeSettingList[1].setSwitchText("-fu");
		rangeSettingList[1].setMustUse(true);

		rangeSettingList[2] = new RangeSettingGridLine(this, "Inclination Angle (deg)", 10, settingGrid, 3);
		rangeSettingList[2].setLowerRange(5);
		rangeSettingList[2].setUpperRange(40);
		rangeSettingList[2].setSwitchText("-ia");
		rangeSettingList[2].setMustUse(true);

		rangeSettingList[3] = new RangeSettingGridLine(this, "PV eff @25C (%)", 17, settingGrid, 4);
		rangeSettingList[3].setLowerRange(0);
		rangeSettingList[3].setUpperRange(100);
		rangeSettingList[3].setSwitchText("-pe");
		rangeSettingList[3].setMustUse(true);
		
		rangeSettingList[4] = new RangeSettingGridLine(this, "PV eff slope (per degC)", 0.0048, settingGrid, 5);
		rangeSettingList[4].setLowerRange(0);
		rangeSettingList[4].setUpperRange(0.1);
		rangeSettingList[4].setSwitchText("-ps");
		rangeSettingList[4].setMustUse(true);

		rangeSettingList[5] = new RangeSettingGridLine(this, "Trans. eff (%)", 95, settingGrid, 6);
		rangeSettingList[5].setLowerRange(0);
		rangeSettingList[5].setUpperRange(100);
		rangeSettingList[5].setSwitchText("-te");
		rangeSettingList[5].setMustUse(true);
		
		rangeSettingList[6] = new RangeSettingGridLine(this, "Panel Cold Temp (C)", 80, settingGrid, 7);
		rangeSettingList[6].setLowerRange(0);
		rangeSettingList[6].setUpperRange(100);
		rangeSettingList[6].setSwitchText("-wt");
		rangeSettingList[6].setMustUse(true);

		rangeSettingList[7] = new RangeSettingGridLine(this, "Panel Hot Temp (C)", 25, settingGrid, 8);
		rangeSettingList[7].setLowerRange(0);
		rangeSettingList[7].setUpperRange(100);
		rangeSettingList[7].setSwitchText("-st");
		rangeSettingList[7].setMustUse(true);

		rangeSettingList[8] = new RangeSettingGridLine(this, "Cold Temp Start Day", 120, settingGrid, 9);
		rangeSettingList[8].setLowerRange(1);
		rangeSettingList[8].setUpperRange(365);
		rangeSettingList[8].setSwitchText("-sd");
		rangeSettingList[8].setMustUse(true);

		rangeSettingList[9] = new RangeSettingGridLine(this, "Cold Temp End Day", 273, settingGrid, 10);
		rangeSettingList[9].setLowerRange(1);
		rangeSettingList[9].setUpperRange(365);
		rangeSettingList[9].setSwitchText("-ed");
		rangeSettingList[9].setMustUse(true);

		rangeSettingList[10] = new RangeSettingGridLine(this, "No blocking Day", 50, settingGrid, 11);
		rangeSettingList[10].setLowerRange(1);
		rangeSettingList[10].setUpperRange(365);
		rangeSettingList[10].setEnabled(false);
		rangeSettingList[10].setSwitchText("-nb");
		rangeSettingList[10].setToolTip("When enabled will adjust field utilization for no shadow on noon of this day.");

		rangeSettingList[11] = new RangeSettingGridLine(this, "Maximum Power (MW)", 25, settingGrid, 12);
		rangeSettingList[11].setLowerRange(0);
		rangeSettingList[11].setUpperRange(-1);
		rangeSettingList[11].setEnabled(false);
		rangeSettingList[11].setSwitchText("-mp");
		rangeSettingList[11].setToolTip("When enabled will adjust field area to achieve desired power.");

		rangeSettingList[12] = new RangeSettingGridLine(this, "Latitude (deg)", 55.741, settingGrid, 13);
		rangeSettingList[12].setLowerRange(-180);
		rangeSettingList[12].setUpperRange( 180);
		rangeSettingList[12].setMustUse(true);
		rangeSettingList[12].setSwitchText("-la");
		
		rangeSettingList[13] = new RangeSettingGridLine(this, "Longitude (deg)", 13.263, settingGrid, 14);
		rangeSettingList[13].setLowerRange(-180);
		rangeSettingList[13].setUpperRange( 180);
		rangeSettingList[13].setMustUse(true);
		rangeSettingList[13].setSwitchText("-lo");
		
		String[] dataParkChoices = {"CMSAF", "Classic"};
		dataParkList = new ListSettingGridLine("Data Base", dataParkChoices, true, "-dp", settingGrid, 15);
		dataParkList.setMustUse(true);
		dataParkList.setToolTip("See http://re.jrc.ec.europa.eu/pvgis/download/download.htm");	
		
		CaptionPanel settingsCaptionPanel = new CaptionPanel("Field Setup");
		settingsCaptionPanel.add(settingGrid);

		return settingsCaptionPanel;
	}

	@Override
	public void tabLayoutPanelInterfaceAction(String message) 
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void optionDialogInterfaceAction(String choiceButtonText) 
	{
		// TODO Auto-generated method stub
		
	}
	static class RunButtonClickHandler implements ClickHandler
	{
		SolarFieldSetup solarFieldSetup;
		RunButtonClickHandler(SolarFieldSetup solarFieldSetup)
		{
			this.solarFieldSetup = solarFieldSetup;
		}
		@Override
		public void onClick(ClickEvent event) 
		{
			String[] debugResponse = {"Yes", "No"};
			solarFieldSetup.runButton.setEnabled(false);
			solarFieldSetup.resultCaptionPanel.setVisible(false);
			solarFieldSetup.plotLinkCaptionPanel.setVisible(false);
			solarFieldSetup.dataTableLinkCaptionPanel.setVisible(false);
			try 
			{
				String[][] switchSettings = solarFieldSetup.getSwitchCommandArray();
				String dataPark = solarFieldSetup.dataParkList.getSwitchCommandArray()[1];
				solarFieldSetup.getEntryPointAppService().runSolarCalculator(dataPark, switchSettings, solarFieldSetup.isDebug(), debugResponse, new RunSolarCalculatorAsyncCallback(solarFieldSetup));
			} 
			catch (GskelException e) 
			{
				solarFieldSetup.getMessageDialog().setMessage("Error on Call back", e.getMessage(), true);
			}
		}
	}
	public static class RunSolarCalculatorAsyncCallback implements AsyncCallback<CsvFile[]>
	{
		SolarFieldSetup solarFieldSetup;
		RunSolarCalculatorAsyncCallback(SolarFieldSetup solarFieldSetup)
		{
			this.solarFieldSetup = solarFieldSetup;
		}
		@Override
		public void onFailure(Throwable caught) 
		{
			solarFieldSetup.runButton.setEnabled(true);
			solarFieldSetup.resultCaptionPanel.setVisible(false);
			solarFieldSetup.plotLinkCaptionPanel.setVisible(false);
			solarFieldSetup.dataTableLinkCaptionPanel.setVisible(false);
			solarFieldSetup.getMessageDialog().setMessage("Error on Call back", caught.getMessage(), true);
			
		}
		@Override
		public void onSuccess(CsvFile[] result) 
		{
			solarFieldSetup.resultCaptionPanel.setVisible(true);
			solarFieldSetup.plotLinkCaptionPanel.setVisible(true);
			solarFieldSetup.dataTableLinkCaptionPanel.setVisible(true);
			solarFieldSetup.runButton.setEnabled(true);
			solarFieldSetup.summaryParametersCsvFilePanel.setSourceFileLink("/tempFiles/summaryParameters.csv");
			solarFieldSetup.summaryParametersCsvFilePanel.setCsvFile(result[0]);
			
		}
	}
	static class ImageLinkClickHandler implements ClickHandler
	{
		String link;
		GskelMessageDialog gskelMessageDialog;
		String title;
		int version = 1;
		ImageLinkClickHandler(String link, GskelMessageDialog gskelMessageDialog, String title)
		{
			this.link = link;
			this.gskelMessageDialog = gskelMessageDialog;
			this.title = title;
		}
		@Override
		public void onClick(ClickEvent event) 
		{
			
//			Window.open(link, "_blank", "enabled");
//			Window.open(link, "_blank", "");
			gskelMessageDialog.setImageUrl(link + "?v" + version);
			version = version + 1;
			gskelMessageDialog.setMessage(title, "", true);
			gskelMessageDialog.center();
		}
		
	}
	class DownLoadClickHandler implements ClickHandler
	{
		String link;
		DownLoadClickHandler(String link)
		{
			this.link = link;
		}
		@Override
		public void onClick(ClickEvent event) {
			
//			Window.open(link, "_blank", "enabled");
			Window.open(link, "_blank", "");
		}
		
	}

}
