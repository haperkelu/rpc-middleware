/**   
* @Title: LocalProxy.java 
* @Package org.brilliance.middleware.core 
* @Description: TODO
* @author Pie.Li   
* @date 2013-12-24 下午5:32:46 
* @version V1.0   
*/
package org.brilliance.middleware.core;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.brilliance.middleware.serialize.CustomEntry;
import org.brilliance.middleware.serialize.SerializerProvider;
import org.brilliance.middleware.transfer.TransferDataContext;
import org.brilliance.middleware.transfer.TransferStandardData;

/**
 * @author Pie.Li
 *
 */
public class LocalProxy implements InvocationHandler {

	/**
	 * Logger
	 */
	private static final Logger logger =  Logger.getLogger(LocalProxy.class);	
	
	private static final int DEFAULT_SIZE = 256;
	
	private String hostName;
	private int port;
	public LocalProxy(String hostName, int port){this.hostName = hostName; this.port = port;}
	
	public Object invoke(Object obj, Method method, Object[] values)
			throws Throwable {
		
		TransferStandardData transferData = new TransferStandardData();
		transferData.setClassFullName(method.getDeclaringClass().getCanonicalName());
		transferData.setMethodName(method.getName());
		Class returnType = method.getReturnType();
		
		if(method.getParameterTypes() != null && values != null){
			if(method.getParameterTypes().length == values.length){
				
				List<CustomEntry<String, Object>> paras = new ArrayList<CustomEntry<String, Object>>();
				for(int i = 0; i < values.length ; i ++){
					CustomEntry<String, Object> entry = new CustomEntry<String, Object>();
					entry.setKey(method.getParameterTypes()[i].getCanonicalName());
					entry.setValue(values[i]);
					paras.add(entry);
				}
				transferData.setParameter(paras);
			}
		}
		
		SocketChannel channel = new NIOClient(hostName, this.port).sendData(transferData);
		if(returnType == void.class){
			channel.close();
			return new Object();
		}
		
		Object result = null;
		ByteBuffer buffer = ByteBuffer.allocate(DEFAULT_SIZE);
		try {

			while(true){
				logger.debug("receiving data from server");
				int readCount = channel.read(buffer);
				logger.debug("receiving data count:" + readCount);
				if(readCount >= DEFAULT_SIZE){
					
					while (true) {

						TransferDataContext context = new TransferDataContext();
						buffer = context.inflateByteBuffer(buffer);

						int size = buffer.capacity() / 2;
						int read = channel.read(buffer);

						if (read < size) {
							break;
						}
						
					}
					
				}
				if(buffer.position() == 0){
					continue;
				} else {
					result = SerializerProvider.deserializedRead(returnType, buffer.array());
					break;
				}				
				
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally{
			if(channel != null && channel.isOpen()){
				logger.debug("channel close");
				channel.close();
			}
		}
		return result;
	}
		
	
}
