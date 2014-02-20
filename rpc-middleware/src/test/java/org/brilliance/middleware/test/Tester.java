package org.brilliance.middleware.test;

import java.io.ByteArrayInputStream;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;

import org.apache.log4j.BasicConfigurator;
import org.brilliance.middleware.client.ClientWrapper;
import org.brilliance.middleware.core.EmbeddedServer;
import org.brilliance.middleware.event.RPCEventHandler;
import org.junit.BeforeClass;
import org.junit.Test;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoException;
import com.esotericsoftware.kryo.io.ByteBufferOutputStream;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

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
	public void fn() throws Exception {
		
		
		final int port = 8888;
		
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
		final Mock proxy = (Mock) ClientWrapper.powerStub(Mock.class, null, port);
		Class[] paras = new Class[2];
		paras[0] = int.class;
		paras[1] = int.class;
		//Method m = proxy.getClass().getMethod("fn", paras);
		//System.out.println(m.getName());
		
		int result = proxy.fn(1, 2);	
		
		//System.out.println("final result :" + result);
		/**
		StringBuilder builder = new StringBuilder();
		for(int i = 0; i <= 1000; i ++){
			builder.append("ffddddddd");
		}
		proxy.fn1(builder.toString());
		**/
		for(int i = 0; i <= 10 ; i ++){
			final int tag = i;
			Thread tt = new Thread(new Runnable() {

				@Override
				public void run() {
					int result = proxy.fn(1, 2);	
					System.out.println("thread:" + tag + ":" +result);
				}
				
			});
			tt.start();
		}
		proxy.fn(1, 2);	
		System.out.println("final result :" + result);
		System.in.read();
		
	}
	
	
	public void fn2() {
		
		Kryo k = new Kryo();
		k.register(Pair.class);
		Pair p = new Pair("key", "valuevvvvvvvvvvvvvvvvvvvvvvv");

		ByteBuffer buffer = ByteBuffer.allocate(128);
		ByteBufferOutputStream outStream = new ByteBufferOutputStream(buffer);
		Output out = new Output(outStream, buffer.capacity());	

		try {
			k.writeObject(out, p);
		} catch (Exception e) {
			if(e instanceof KryoException){
				KryoException kryoExcpetion = (KryoException) e;
				if(kryoExcpetion.getCause() instanceof BufferOverflowException){
					
				}
			}
		}
		System.out.println(buffer.position());
		out.flush();
		System.out.println(buffer.position());
		System.out.println(new String(buffer.array()));		
		
		Object newList = k.readObject(new Input(new ByteArrayInputStream(buffer.array())), Pair.class);
		System.out.println("new:" + newList);
		
	}
	
}
