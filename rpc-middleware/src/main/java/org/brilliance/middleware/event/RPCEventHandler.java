/**   
* @Title: Dummy.java 
* @Package org.brilliance.middleware 
* @Description: TODO
* @author Pie.Li   
* @date 2013-12-24 下午5:13:52 
* @version V1.0   
*/
package org.brilliance.middleware.event;

import java.util.List;
import org.brilliance.middleware.serialize.CustomEntry;

/**
 * 
 * @author Pie.Li
 *
 */
public interface RPCEventHandler {
	
	@SuppressWarnings("rawtypes")
	Object onRecieveData(String classFullName, String method, List<CustomEntry<Class, Object>> para);
	
}
