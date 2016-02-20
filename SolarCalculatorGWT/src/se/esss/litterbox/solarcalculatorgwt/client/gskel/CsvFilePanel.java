/*
Copyright (c) 2014 European Spallation Source

This file is part of LinacLego.
LinacLego is free software: you can redistribute it and/or modify it under the terms of the 
GNU General Public License as published by the Free Software Foundation, either version 2 
of the License, or any newer version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
See the GNU General Public License for more details.
You should have received a copy of the GNU General Public License along with this program. 
If not, see https://www.gnu.org/licenses/gpl-2.0.txt
*/
package se.esss.litterbox.solarcalculatorgwt.client.gskel;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import se.esss.litterbox.solarcalculatorgwt.shared.gskel.CsvFile;

import com.google.gwt.user.client.ui.Grid;

public class CsvFilePanel extends VerticalPanel
{
	private String csvFileType = "";
	private Grid dataGrid;
	private Grid headerGrid;
	private int numHeaderRows;
	private int maxNumDataRowsOnLoad = 50;
	private int lastDataRowLoaded = -1;
	private ScrollPanel dataGridScrollPane;
	private String sourceFileLink = "";
	private int numDataRows;
	private CsvFile csvFile;
	private boolean fileCompletelyLoaded = false;
	private boolean oddDataRow = false;
	private GskelVerticalPanel gskelVerticalPanel;
	private int headerLineHeight = 25;

	
	public boolean isFileCompletelyLoaded() {return fileCompletelyLoaded;}
	public int getLastDataRowLoaded() {return lastDataRowLoaded;}
	public int getNumHeaderRows() {return numHeaderRows;}
	public Grid getDataGrid() {return dataGrid;}
	public int getNumDataRows() {return numDataRows;}
	public CsvFile getCsvFile() {return csvFile;}
	public String getCsvFileType() {return csvFileType;}
	public int getMaxNumDataRowsOnLoad() {return maxNumDataRowsOnLoad;}
	public void setFileCompletelyLoaded(boolean fileCompletelyLoaded) {this.fileCompletelyLoaded = fileCompletelyLoaded;}
	public String getSourceFileLink() {return sourceFileLink;}
	public ScrollPanel getDataGridScrollPane() {return dataGridScrollPane;}

	public void setLastDataRowLoaded(int lastDataRowLoaded) {this.lastDataRowLoaded = lastDataRowLoaded;}
	public void setSourceFileLink(String sourceFileLink) {this.sourceFileLink = sourceFileLink;}

	public CsvFilePanel(GskelVerticalPanel gskelVerticalPanel, String csvFileType, int numHeaderRows) 
	{
		super();
		this.gskelVerticalPanel = gskelVerticalPanel;
		
		this.csvFileType = csvFileType;
		this.numHeaderRows = numHeaderRows;
		setWidth("100%");
		setHeight("100%");
	}
	public void setCsvFile(CsvFile csvFile)
	{
		if (getWidgetCount() > 0) clear();
		this.csvFile = csvFile;
		Anchor sourceFileAnchor = new Anchor("Source File");
		sourceFileAnchor.addClickHandler(new DownLoadClickHandler(sourceFileLink));
		add(sourceFileAnchor);
		numDataRows = csvFile.numOfRows() - numHeaderRows;
		
		dataGrid = new Grid(numDataRows, csvFile.numOfCols());
		headerGrid = new Grid(numHeaderRows, csvFile.numOfCols());
		dataGridScrollPane = new ScrollPanel();
		dataGridScrollPane.add(dataGrid);
		add(headerGrid);
		add(dataGridScrollPane);

		for (int irow = 0; irow < numHeaderRows; ++irow)
		{
			for ( int icol = 0; icol < csvFile.numOfCols(); ++icol)
			{
				headerGrid.setText(irow, icol, csvFile.getLine(irow).getCell(icol));
			}
		}
		int numDataRowsOnLoad = numDataRows;
		fileCompletelyLoaded = true;
		if (numDataRowsOnLoad > getMaxNumDataRowsOnLoad())
		{
			numDataRowsOnLoad = getMaxNumDataRowsOnLoad();
			fileCompletelyLoaded = false;
			Scheduler.get().scheduleIncremental(new CsvFilePanelIncrementalExtraRowLoader(this));
		}
		for (int irow = 0; irow < numDataRowsOnLoad; ++irow)
		{
			for ( int icol = 0; icol < csvFile.numOfCols(); ++icol)
			{
				dataGrid.setText(irow, icol, csvFile.getLine(irow + numHeaderRows).getCell(icol));
			}
			if (oddDataRow) dataGrid.getRowFormatter().setStyleName(irow, "csvFilePanelOddRow");
			oddDataRow = !oddDataRow;
		}
		lastDataRowLoaded = numDataRowsOnLoad - 1;
		resizeMe();
		for (int ih = 0; ih < numHeaderRows; ++ih)
			headerGrid.getRowFormatter().setStyleName(ih, "csvFilePanelHeader");
		headerGrid.setBorderWidth(0);
		headerGrid.setCellSpacing(0);
		headerGrid.setCellPadding(0);
		dataGrid.setBorderWidth(0);
		dataGrid.setCellSpacing(0);
		dataGrid.setCellPadding(0);
		dataGrid.addClickHandler(new DataGridClickHandler(dataGrid));

		if (fileCompletelyLoaded) gskelVerticalPanel.getStatusTextArea().addStatus("Finished building " + csvFileType + " Spreadsheet.");
		if (!fileCompletelyLoaded) gskelVerticalPanel.getStatusTextArea().addStatus("Still building " + csvFileType + " Spreadsheet.");
	}
	public void loadExtraRows()
	{
		if (fileCompletelyLoaded) return;
		int startRow = getLastDataRowLoaded() + 1;
		int stopRow = startRow + getMaxNumDataRowsOnLoad();
		if (stopRow > (getNumDataRows() - 1))  stopRow = getNumDataRows() - 1;
		for (int irow = startRow; irow <= stopRow; ++irow)
		{
			for ( int icol = 0; icol < getCsvFile().numOfCols(); ++icol)
			{
				getDataGrid().setText(irow, icol, getCsvFile().getLine(irow + getNumHeaderRows()).getCell(icol));
			}
			if (oddDataRow) dataGrid.getRowFormatter().setStyleName(irow, "csvFilePanelOddRow");
			oddDataRow = !oddDataRow;
		}
		setLastDataRowLoaded(stopRow);
		if (stopRow == (getNumDataRows() - 1))  fileCompletelyLoaded = true;
		if (fileCompletelyLoaded) gskelVerticalPanel.getStatusTextArea().addStatus("Finished building " + csvFileType + " Spreadsheet.");
	}
	public void resizeMe()
	{	
		Scheduler.get().scheduleDeferred(new ScheduledCommand() 
		{

		    @Override
		    public void execute() 
		    {
				int parentWidth = getOffsetWidth();
				int parentHeight = getOffsetHeight();
				
				if (parentWidth < 10) return;
				headerGrid.setWidth(parentWidth - 50 + "px");
				dataGrid.setWidth(parentWidth - 50  + "px");
				dataGrid.setHeight(parentHeight - 50  + "px");
				dataGridScrollPane.setHeight(parentHeight - headerLineHeight * numHeaderRows - 20 + "px");
				dataGridScrollPane.setWidth(parentWidth   - 20 + "px");
				headerGrid.setHeight(headerLineHeight * numHeaderRows + "px");

				for ( int icol = 0; icol < csvFile.numOfCols(); ++icol)
				{
					double colPer = 100.0 * ((double) csvFile.getColWidth(icol) ) / ((double) csvFile.getTableWidth());
					headerGrid.getColumnFormatter().setWidth(icol, NumberFormat.getFormat("0.0").format(colPer) + "%");
					dataGrid.getColumnFormatter().setWidth(icol, NumberFormat.getFormat("0.0").format(colPer) + "%");
				}
		    }
		});	
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
	static class DataGridClickHandler implements ClickHandler
	{
		Grid dataGrid;
		DataGridClickHandler(Grid dataGrid)
		{
			this.dataGrid = dataGrid;
		}

		@Override
		public void onClick(ClickEvent event) 
		{
			int irow = dataGrid.getCellForEvent(event).getRowIndex();
			String styleName = dataGrid.getRowFormatter().getStyleName(irow);
			if (styleName.equals("csvFilePanelSelectedRow"))
			{
				if (irow == 2 * (irow / 2) ) 
				{
					dataGrid.getRowFormatter().setStyleName(irow, "csvFilePanelEvenRow");
				}
				else
				{
					dataGrid.getRowFormatter().setStyleName(irow, "csvFilePanelOddRow");
				}
			}
			else
			{
				dataGrid.getRowFormatter().setStyleName(irow, "csvFilePanelSelectedRow");
			}
			
		}
		
	}
	public static class CsvFilePanelIncrementalExtraRowLoader implements RepeatingCommand
	{
		CsvFilePanel csvFilePanel;

		public CsvFilePanelIncrementalExtraRowLoader(CsvFilePanel csvFilePanel) 
		{
			this.csvFilePanel = csvFilePanel;
		}

		@Override
		public boolean execute() 
		{
			csvFilePanel.loadExtraRows();
// Returning true causes command to be executed again			
			return !csvFilePanel.isFileCompletelyLoaded();
		}

	}
}
