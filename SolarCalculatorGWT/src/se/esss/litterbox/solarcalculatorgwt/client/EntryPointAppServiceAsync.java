package se.esss.litterbox.solarcalculatorgwt.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

import se.esss.litterbox.solarcalculatorgwt.shared.gskel.CsvFile;

public interface EntryPointAppServiceAsync 
{
	void gskelServerTest(String name, boolean debug, String[] debugResponse,AsyncCallback<String[]> callback);
	void runSolarCalculator(String dataPark, String[][] switchSettings, boolean debug, String[] debugResponse, AsyncCallback<CsvFile[]> callback);
}
