package se.esss.litterbox.solarcalculatorgwt.client.gskel.settinggridline;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;

import se.esss.litterbox.solarcalculatorgwt.shared.gskel.GskelException;

public class ListSettingGridLine
{
	private ListBox choiceListBox = new ListBox();
	private CheckBox enabledCheckBox = new CheckBox();
	private String switchCommand;
	private boolean mustUse = false;
	private Label deviceNameLabel = new Label();
	public boolean isMustUse() {return mustUse;}
	
	public ListSettingGridLine(String deviceName, String[] choices, boolean enabled, String switchCommand, Grid grid, int gridRow)
	{
		enabledCheckBox.setValue(enabled);
		this.switchCommand = switchCommand;
		choiceListBox.setVisibleItemCount(1);
		choiceListBox.setSize("8.0em", "2.0em");
		for (int ii = 0; ii < choices.length; ++ii) {choiceListBox.addItem(choices[ii]);}
		deviceNameLabel.setText(deviceName);
		grid.setWidget(gridRow, 0, deviceNameLabel);
		grid.setWidget(gridRow, 1, choiceListBox);
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
		
		choiceListBox.addClickHandler(new EnableListClickHandler());
	}
	public void setMustUse(boolean mustUse) 
	{
		this.mustUse = mustUse;
		if (mustUse)
		{
			enabledCheckBox.setValue(true);
			enabledCheckBox.setEnabled(false);
		}
	}
	protected String getSwitchCommand() throws GskelException
	{
		if (!enabledCheckBox.getValue()) return "";
		String command = " " + switchCommand + " " + choiceListBox.getItemText(choiceListBox.getSelectedIndex());
		return command;
	}
	public String[] getSwitchCommandArray() throws GskelException
	{
		String[] command = {"",""};
		if (!enabledCheckBox.getValue()) return command;
		command[0] = switchCommand;
		command[1] = choiceListBox.getItemText(choiceListBox.getSelectedIndex());
		return command;
	}
	public void setToolTip(String tip)
	{
		deviceNameLabel.setTitle(tip);
		choiceListBox.setTitle(tip);
		enabledCheckBox.setTitle(tip);
	}
	protected class EnableListClickHandler  implements ClickHandler
	{
		@Override
		public void onClick(ClickEvent event) {enabledCheckBox.setValue(true);}
		
	}
	
}
