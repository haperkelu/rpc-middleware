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

import org.brilliance.middleware.serialize.SerializerProvider;
import org.brilliance.middleware.transfer.TransferStandardData;

/**
 * @author Pie.Li
 *
 */
public class NIOClient {

	private String hostName;
	private int port;
	
	private static final int TIME_OUT = 3000;
	
	private static Map<InetSocketAddress, SocketChannel> _map = new HashMap<InetSocketAddress, SocketChannel>();
	
	public NIOClient(String hostName, int port) {
		this.hostName = hostName;
		this.port = port;
	}
	
	@SuppressWarnings("static-access")
	public SocketChannel sendData(TransferStandardData data) throws IOException, InstantiationException, IllegalAccessException, InterruptedException {
					
		InetSocketAddress addr = hostName == null ? new InetSocketAddress(port):new InetSocketAddress(hostName, port); 
		
		SocketChannel channel = _map.get(addr);
		if(channel == null || !channel.isOpen() || !channel.isConnected()){
			channel = SocketChannel.open(); 
			channel.configureBlocking(false);
			channel.connect(addr);    
			long timeBegin = Calendar.getInstance().getTime().getTime();
			boolean timeout = false;
			while(!channel.finishConnect()){
				long current = Calendar.getInstance().getTime().getTime();
				if((current - timeBegin) >= TIME_OUT){
					timeout = true;
					break;
				}
				Thread.currentThread().sleep(100);
			}
			if(timeout == true){
				if(channel != null){
					channel.close();
				}
				throw new RuntimeException("Timeout on NIOClient[send data]; hostname " + this.hostName + ",port:" + this.port);
			} 
			_map.put(addr, channel);
		}
		
		ByteBuffer buffer = SerializerProvider.serializedWriteBuffer(TransferStandardData.class, data);
		buffer.flip();
		channel.write(buffer);
		
		return channel;
	}
	
}
