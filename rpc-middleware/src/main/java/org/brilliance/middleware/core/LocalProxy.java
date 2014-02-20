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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;
import org.brilliance.middleware.serialize.CustomEntry;
import org.brilliance.middleware.serialize.SerializerProvider;
import org.brilliance.middleware.transfer.TransferDataContext;
import org.brilliance.middleware.transfer.TransferResult;
import org.brilliance.middleware.transfer.TransferStandardData;

/**
 * @author Pie.Li
 *
 */
public class LocalProxy implements InvocationHandler {

	private final Lock lock = new ReentrantLock();  
	private final Condition wait_result_condition = lock.newCondition();
	
	private static final ThreadLocal<String> sequence_pool = new ThreadLocal<String>();
	
	private static final Map<String, TransferResult> result_pool = new HashMap<String,TransferResult>();
	
	/**
	 * Logger
	 */
	private static final Logger logger =  Logger.getLogger(LocalProxy.class);	
	
	private static final int DEFAULT_SIZE = 256;
	
	private String hostName;
	private int port;
	public LocalProxy(String hostName, int port){this.hostName = hostName; this.port = port;}
	
	@SuppressWarnings("rawtypes")
	public Object invoke(Object obj, Method method, Object[] values)
			throws Throwable {
		
		TransferStandardData transferData = new TransferStandardData();
		transferData.setClassFullName(method.getDeclaringClass().getCanonicalName());
		transferData.setMethodName(method.getName());
		transferData.setSequenceId(SequenceGenerator.getSequenceId());
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
		// set thread-local sequence
		sequence_pool.set(transferData.getSequenceId());
		SocketChannel channel = new NIOClient(hostName, this.port).sendData(transferData);
		if(returnType == void.class){
			channel.close();
			return new Object();
		}
		
		TransferResult result = null;
		ByteBuffer buffer = ByteBuffer.allocate(DEFAULT_SIZE);
		lock.lock();
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
					result = (TransferResult) SerializerProvider.deserializedRead(TransferResult.class, buffer.array());
					result_pool.put(sequence_pool.get(), result);
					if(result != null && result.getSequence() != null && !result.getSequence().equalsIgnoreCase(sequence_pool.get())){
						wait_result_condition.signalAll();
					}
					break;
				}				
				
			}
			
			if(result != null && result.getSequence() != null && !result.getSequence().equalsIgnoreCase(sequence_pool.get())){
				while(result_pool.get(sequence_pool.get()) == null){
					wait_result_condition.await(2000, TimeUnit.MILLISECONDS);
				}
			}
			result_pool.remove(sequence_pool.get());
			return result.getResult();
			
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally{
			lock.unlock();
			/**
			if(channel != null && channel.isOpen()){
				logger.debug("channel close");
				channel.close();
			}
			**/
		}

		return result.getResult();
	}
		
	
}
