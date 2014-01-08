package org.brilliance.middleware.test;

import java.io.IOException;

import org.apache.log4j.BasicConfigurator;
import org.brilliance.middleware.client.ClientWrapper;
import org.brilliance.middleware.core.EmbeddedServer;
import org.brilliance.middleware.event.RPCEventHandler;
import org.junit.BeforeClass;
import org.junit.Test;

/**   
 * @Title: Tester.java 
 * @Package  
 * @Description: TODO
 * @author Pie.Li   
 * @date 2013-12-28 下午2:16:03 
 * @version V1.0   
 */

/**
 * @author Pie.Li
 *
 */
public class Tester {

	@BeforeClass
	public  static void init(){
		BasicConfigurator.configure();
	}
	
	
	@Test
	public void fn() throws InterruptedException, IOException {
		
		
		final int port = 8088;
		
		Thread t = new Thread(new Runnable(){

			public void run() {
				try {
					RPCEventHandler handler = new CustomiezedEventHandler();
					EmbeddedServer.registerEvent(handler);
					EmbeddedServer.startServer(port);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
		});
		t.start();
		
		Thread.sleep(1000);
		Mock proxy = (Mock) ClientWrapper.powerStub(Mock.class, null, port);
		Class[] paras = new Class[2];
		paras[0] = int.class;
		paras[1] = int.class;
		//Method m = proxy.getClass().getMethod("fn", paras);
		//System.out.println(m.getName());
		
		int result = proxy.fn(1, 2);	
		
		System.out.println("final result :" + result);
		proxy.fn1("ddd");
		
		System.out.println("final result :" + result);
		System.in.read();
		
	}
}
