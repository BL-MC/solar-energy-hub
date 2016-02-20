package se.esss.litterbox.solarcalculatorgwt.client.gskel.settinggridline;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;

import se.esss.litterbox.solarcalculatorgwt.client.gskel.GskelVerticalPanel;
import se.esss.litterbox.solarcalculatorgwt.shared.gskel.GskelException;

public class RangeSettingGridLine
{
	private String deviceName;
	private double lowerRange = 0;
	private double upperRange = 0;
	private CheckBox enabledCheckBox = new CheckBox();
	private TextBox settingTextBox = new TextBox();
	private String switchText = "";
	private GskelVerticalPanel gskelVerticalPanel;
	private double multiplier = 1;
	private boolean enabled = false;
	private boolean mustUse = false;
	private int gridRow = -1;
	private Grid settingGrid;
	private Label deviceNameLabel;
		
	public boolean isEnabled() {return enabled;}
	public boolean isMustUse() {return mustUse;}
	public double getLowerRange() {return lowerRange;}
	public double getUpperRange() {return upperRange;}
	public String getSwitchText() {return switchText;}
	public double getMultiplier() {return multiplier;}
	
	
	public RangeSettingGridLine(GskelVerticalPanel gskelVerticalPanel, String deviceName, double setting, Grid settingGrid, int gridRow)
	{
		this.gskelVerticalPanel = gskelVerticalPanel;
		settingTextBox.setSize("7.0em", "1.0em");
		this.deviceName = deviceName;
		enabledCheckBox.setValue(enabled);
		settingTextBox.setText(Double.toString(setting));
		this.settingGrid = settingGrid;
		this.gridRow = gridRow;
		deviceNameLabel = new Label(deviceName);
		settingGrid.setWidget(gridRow, 0, deviceNameLabel);
		settingGrid.setWidget(gridRow, 1, settingTextBox);
		settingGrid.setWidget(gridRow, 5, enabledCheckBox);
		
		HTMLTable.CellFormatter formatter = settingGrid.getCellFormatter();
		formatter.setHorizontalAlignment(gridRow, 1, HasHorizontalAlignment.ALIGN_CENTER);
		formatter.setVerticalAlignment(gridRow, 1, HasVerticalAlignment.ALIGN_MIDDLE);		
		formatter.setHorizontalAlignment(gridRow, 2, HasHorizontalAlignment.ALIGN_LEFT);
		formatter.setVerticalAlignment(gridRow, 2, HasVerticalAlignment.ALIGN_MIDDLE);		
		formatter.setHorizontalAlignment(gridRow, 3, HasHorizontalAlignment.ALIGN_CENTER);
		formatter.setVerticalAlignment(gridRow, 3, HasVerticalAlignment.ALIGN_MIDDLE);		
		formatter.setHorizontalAlignment(gridRow, 4, HasHorizontalAlignment.ALIGN_RIGHT);
		formatter.setVerticalAlignment(gridRow, 4, HasVerticalAlignment.ALIGN_MIDDLE);		
		formatter.setHorizontalAlignment(gridRow, 5, HasHorizontalAlignment.ALIGN_CENTER);
		formatter.setVerticalAlignment(gridRow, 5, HasVerticalAlignment.ALIGN_MIDDLE);
		settingTextBox.addClickHandler(new EnableSettingClickHandler());
	}
	public void setLowerRange(double lowerRange) 
	{
		this.lowerRange = lowerRange;
		setRange();
	}
	public void setUpperRange(double upperRange) 
	{
		this.upperRange = upperRange;
		setRange();
	}
	public void setMultiplier(double multiplier) {this.multiplier = multiplier;}
	public void setSwitchText(String switchText) {this.switchText = switchText;}
	public void setEnabled(boolean enabled) 
	{
		this.enabled = enabled;
		if (enabled) enabledCheckBox.setValue(enabled);
	}
	public void setMustUse(boolean mustUse) 
	{
		this.mustUse = mustUse;
		if (mustUse)
		{
			setEnabled(true);
			enabledCheckBox.setEnabled(false);
		}
	}
	private void setRange()
	{
		if (lowerRange < upperRange)
		{
			settingGrid.setWidget(gridRow, 2, new Label(Double.toString(lowerRange)));
			settingGrid.setWidget(gridRow, 3, new Label("to"));
			settingGrid.setWidget(gridRow, 4, new Label(Double.toString(upperRange)));
		}
		if (lowerRange > upperRange)
		{
			settingGrid.setWidget(gridRow, 2, new Label(Double.toString(lowerRange)));
			settingGrid.setWidget(gridRow, 3, new Label("or greater"));
		}
	}
	private void checkRange() throws GskelException
	{
		if (lowerRange == upperRange) return;
		double setting;
		String settingString = settingTextBox.getText().replaceAll("\\s+","");
		try {setting = Double.parseDouble(settingString);}
		catch (Exception e) 
		{
			gskelVerticalPanel.getStatusTextArea().addStatus("ERROR: Bad setting value for " + deviceName);
			enabledCheckBox.setValue(false);
			throw new GskelException(e);
		}
		if (setting < lowerRange) setting = lowerRange;
		if (lowerRange < upperRange)
			if (setting > upperRange) setting = upperRange;
		settingTextBox.setText(Double.toString(setting));
	}
	public String getSwitchCommand() throws GskelException
	{
		if (switchText == null) return "";
		if (!enabledCheckBox.getValue()) return "";
		checkRange();
		double setting = Double.parseDouble(settingTextBox.getText());
		setting = setting * multiplier;
		String command = " " + switchText + " " + Double.toString(setting);
		return command;
	}
	public String[] getSwitchCommandArray() throws GskelException
	{
		String[] command = {"",""};
		if (switchText == null) return command;
		if (!enabledCheckBox.getValue()) return command;
		checkRange();
		double setting = Double.parseDouble(settingTextBox.getText());
		setting = setting * multiplier;
		command[0] = switchText;
		command[1] = Double.toString(setting);
		return command;
	}
	private class EnableSettingClickHandler  implements ClickHandler
	{
		@Override
		public void onClick(ClickEvent event) {enabledCheckBox.setValue(true);}
		
	}
	public double getSetting() throws GskelException
	{
		checkRange();
		return Double.parseDouble(settingTextBox.getText().replaceAll("\\s+",""));
	}
	public void changeSetting(double setting)
	{
		settingTextBox.setText(Double.toString(setting));
	}
	public void setToolTip(String tip)
	{
		settingTextBox.setTitle(tip);
		enabledCheckBox.setTitle(tip);
		deviceNameLabel.setTitle(tip);
	}
	
}
