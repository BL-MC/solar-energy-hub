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

import se.esss.litterbox.solarcalculatorgwt.shared.gskel.GskelException;

public class StringSettingGridLine
{
	private CheckBox enabledCheckBox = new CheckBox();
	private TextBox settingTextBox = new TextBox();
	private String switchCommand;
	
	public StringSettingGridLine(String deviceName, String setting, boolean enabled, String switchCommand, Grid grid, int gridRow)
	{
		settingTextBox.setSize("7.0em", "1.0em");
		enabledCheckBox.setValue(enabled);
		this.switchCommand = switchCommand;
		settingTextBox.setText(setting);
		grid.setWidget(gridRow, 0, new Label(deviceName));
		grid.setWidget(gridRow, 1, settingTextBox);
		grid.setWidget(gridRow, 5, enabledCheckBox);
		
		HTMLTable.CellFormatter formatter = grid.getCellFormatter();
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
	protected String getSwitchCommand() throws GskelException
	{
		if (!enabledCheckBox.getValue()) return "";
		String command = " " + switchCommand + " " + settingTextBox.getText();
		return command;
	}
	protected class EnableSettingClickHandler  implements ClickHandler
	{
		@Override
		public void onClick(ClickEvent event) {enabledCheckBox.setValue(true);}
		
	}
	
}
