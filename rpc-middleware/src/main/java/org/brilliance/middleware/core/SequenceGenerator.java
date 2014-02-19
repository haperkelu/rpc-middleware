/**   
* @Title: SequenceGenerator.java 
* @Package org.brilliance.middleware.core 
* @Description: TODO
* @author Pie.Li   
* @date 2014-2-19 上午7:39:36 
* @version V1.0   
*/
package org.brilliance.middleware.core;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Pie.Li
 *
 */
public class SequenceGenerator {

	private static AtomicLong COUNTER = new AtomicLong(0);
	private static String HOST_IP;
	static {
		try {
			HOST_IP = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			HOST_IP = "unknown address";
		}
	}
	
	/**
	 * 
	 * @return
	 * @throws UnknownHostException
	 */
	public static String getSequenceId() throws UnknownHostException {				
		return HOST_IP + ":" + Calendar.getInstance().getTimeInMillis() + ":" + COUNTER.addAndGet(1);
	}
	
}
