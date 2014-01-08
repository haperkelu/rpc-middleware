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
	
	private static Map<InetSocketAddress, SocketChannel> _map = new HashMap<InetSocketAddress, SocketChannel>();
	
	public NIOClient(String hostName, int port) {
		this.hostName = hostName;
		this.port = port;
	}
	
	public SocketChannel sendData(TransferStandardData data) throws IOException, InstantiationException, IllegalAccessException {
			
		
		InetSocketAddress addr = hostName == null ? new InetSocketAddress(port):new InetSocketAddress(hostName, port); 
		
		
		SocketChannel  channel  = null;
		channel = SocketChannel.open(); 
		channel.configureBlocking(false);
		channel.connect(addr);               
		while(!channel.finishConnect()){
			
		}

		ByteBuffer buffer = SerializerProvider.serializedWriteBuffer(TransferStandardData.class, data);
		buffer.flip();
		channel.write(buffer);

		return channel;
	}
	
}
