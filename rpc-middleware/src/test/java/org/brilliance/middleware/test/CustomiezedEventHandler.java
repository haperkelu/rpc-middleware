package org.brilliance.middleware.test;


import java.util.List;

import org.brilliance.middleware.event.RPCEventHandler;
import org.brilliance.middleware.serialize.CustomEntry;

public class CustomiezedEventHandler implements RPCEventHandler {

	public Object onRecieveData(String classFullName, String method,
			List<CustomEntry<Class, Object>> para) {
		
		System.out.println("para:" + para);
		for(CustomEntry<Class, Object> item: para){
		}
		return new Integer(22);
		
	}
	
	
	
}