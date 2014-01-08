/**   
* @Title: ClientWrapper.java 
* @Package org.brilliance.middleware 
* @Description: TODO
* @author Pie.Li   
* @date 2013-12-26 下午10:15:06 
* @version V1.0   
*/
package org.brilliance.middleware.client;

import java.lang.reflect.Proxy;

import org.brilliance.middleware.core.LocalProxy;

/**
 * @author Pie.Li
 *
 */
public class ClientWrapper {

	
	public static Object powerStub(Class type, String hostName, int port){
		
		Object proxy = Proxy.newProxyInstance(ClientWrapper.class.getClassLoader(), 
				new Class[]{type}, new LocalProxy(hostName, port));
		return proxy;
	}
	
}
