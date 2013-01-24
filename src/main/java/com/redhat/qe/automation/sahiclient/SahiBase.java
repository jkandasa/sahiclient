package com.redhat.qe.automation.sahiclient;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.sahi.client.Browser;
import net.sf.sahi.client.ElementStub;
import net.sf.sahi.config.Configuration;

/**
 * @author jkandasa (Jeeva Kandasamy)
 * Jan 24, 2013
 */
public class SahiBase extends Browser {
	public static Logger _logger = Logger.getLogger(SahiBase.class.getName());

	public SahiBase(String browserName, String basePath, String userDataDirectory){
		super(browserName);
		initSahi(basePath, userDataDirectory);
	}

	public SahiBase(String browserPath, String browserType, String browserOption, String basePath, String userDataDirectory){
		super(browserPath, browserType, browserOption);
		initSahi(basePath, userDataDirectory);
	}

	private void initSahi(String basePath, String userDataDirectory){
		Configuration.initJava(basePath, userDataDirectory);
		_logger.log(Level.FINE, "Sahi configuration init process done...");
	}

	/*
	 * Waiting for the element on the browser.
	 */
	public boolean waitForElementExists(ElementStub elementStub, String element, long maximunWaitTime){
		_logger.info("Waiting for the element: ["+element+"], Remaining wait time: "+(maximunWaitTime/1000)+" Second(s)...");
		while(maximunWaitTime >=  0){
			if(elementStub.exists()){
				_logger.info("Element ["+element+"] exists.");
				return true;
			}else{
				browser.waitFor(500);
				maximunWaitTime -= 500;
				if((maximunWaitTime%(1000*5)) <= 0){
					_logger.info("Waiting for the element: ["+element+"], Remaining wait time: "+(maximunWaitTime/1000)+" Second(s)...");
				}
			}
		}		
		_logger.warning("Failed to get the element! ["+element+"]");
		return false;
	}
	
	/*
	 * base table method for Common Id
	 */
	public LinkedList<HashMap<String, String>> getTable(String cellReference, LinkedList<String> keys, ElementStub nearReference){
		LinkedList<HashMap<String, String>> table = new LinkedList<HashMap<String,String>>();
		HashMap<String,String> row = new HashMap<String,String>();
		int cellCount = this.div("/"+cellReference+"/").countSimilar();
		if(cellCount%keys.size() != 0){
			_logger.log(Level.WARNING, "Column count missmatch with actual column count!!");
		}
		int cellNo=0;
		while(cellNo < cellCount){
			for(String key : keys){
				if(nearReference == null){
					row.put(key, this.div("/"+cellReference+"/["+cellNo+"]").getText().trim());
				}else{
					row.put(key, this.div("/"+cellReference+"/["+cellNo+"]").near(nearReference).getText().trim());
				}
				cellNo++;
			}
			table.add(row);
			row = new HashMap<String,String>();
		}
		return table;		
	}
	
	/*
	 * base table method for different ID
	 */
	public LinkedList<HashMap<String, String>> getTable(LinkedList<String> cellReferences, LinkedList<String> keys, ElementStub nearReference){
		LinkedList<HashMap<String, String>> table = new LinkedList<HashMap<String,String>>();
		HashMap<String,String> row = new HashMap<String,String>();
		int cellCount = this.div("/"+cellReferences.getFirst()+"/").countSimilar();
		int cellNo = 0;
		while(cellNo < cellCount){
			for(int i=0; i<cellReferences.size(); i++){
				if(nearReference == null){
					row.put(keys.get(0), this.div("/"+cellReferences.get(i)+"/["+(cellNo % cellReferences.size())+"]").getText().trim());
				}else{
					row.put(keys.get(0), this.div("/"+cellReferences.get(i)+"/["+(cellNo % cellReferences.size())+"]").near(nearReference).getText().trim());
				}
			}
			cellNo++;
			table.add(row);
			row = new HashMap<String,String>();
		}
		return table;		
	}

}
