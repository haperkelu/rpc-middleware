/**   
* @Title: Client.java 
* @Package org.brilliance.middleware.core 
* @Description: TODO
* @author Pie.Li   
* @date 2013-12-24 下午4:32:27 
* @version V1.0   
*/
package org.brilliance.middleware.core;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;
import org.brilliance.middleware.serialize.SerializerProvider;
import org.brilliance.middleware.transfer.TransferStandardData;

/**
 * @author Pie.Li
 *
 */
public class NIOClientUtils {
	
	/**
	 * Logger
	 */
	private static final Logger NIO_CLIENT_LOGGER =  Logger.getLogger(NIOClientUtils.class);		
	
	private static final int TIME_OUT = 3000;
	
	private static final Lock lock_create_socket = new ReentrantLock();  
	
	private static final Lock lock_connect_socket = new ReentrantLock();  
	
	private static Map<InetSocketAddress, SocketChannel> _map = new HashMap<InetSocketAddress, SocketChannel>();
	
	@SuppressWarnings("static-access")
	public static SocketChannel sendData(String hostname, int port, TransferStandardData data) throws IOException, InstantiationException, IllegalAccessException, InterruptedException {
					
		InetSocketAddress addr = hostname == null ? new InetSocketAddress(port):new InetSocketAddress(hostname, port); 
		
		SocketChannel channel = _map.get(addr);
		if(channel == null){
			if(lock_create_socket.tryLock()) {
				try {
					channel = _map.get(addr);
					if(channel == null){
						channel = SocketChannel.open();
						channel.configureBlocking(false);
						channel.connect(addr);
						_map.put(addr, channel);
					}
				} catch (Exception e) {
					NIO_CLIENT_LOGGER.error(e.getMessage(), e);
				} finally {
					lock_create_socket.unlock();
				}
			}			
		}
		
		if(!channel.isOpen()) {
			throw new RuntimeException("NIOClient's socket is closed!hostname: " + hostname + ",port:" + port);
		}
		if(!channel.isConnected()){
			
			if(lock_connect_socket.tryLock()){
				
				boolean timeout = false;
				try {
					long timeBegin = Calendar.getInstance().getTime().getTime();
					while(!channel.finishConnect()){
						long current = Calendar.getInstance().getTime().getTime();
						if((current - timeBegin) >= TIME_OUT){
							timeout = true;
							break;
						}
						Thread.currentThread().sleep(100);
					}
				} catch (Exception e) {
					NIO_CLIENT_LOGGER.error(e.getMessage(), e);
				} finally {
					lock_connect_socket.unlock();
				}
				if(timeout == true){
					if(channel != null){
						_map.remove(addr);
						channel.close();
					}
					throw new RuntimeException("Timeout on NIOClient[send data]; hostname " + hostname + ",port:" + port);
				} 
			}
			
		}
		
		ByteBuffer buffer = SerializerProvider.serializedWriteBuffer(TransferStandardData.class, data);
		buffer.flip();
		channel.write(buffer);
		
		return channel;
	}
	
}
