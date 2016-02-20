package se.esss.litterbox.solarcalculatorgwt.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import se.esss.litterbox.solarcalculatorgwt.shared.gskel.CsvFile;
import se.esss.litterbox.solarcalculatorgwt.shared.gskel.GskelException;

@RemoteServiceRelativePath("entrypointapp")
public interface EntryPointAppService extends RemoteService 
{
	String[] gskelServerTest(String name, boolean debug, String[] debugResponse) throws GskelException;
	CsvFile[] runSolarCalculator(String dataPark, String[][] switchSettings, boolean debug, String[] debugResponse) throws GskelException;
}
