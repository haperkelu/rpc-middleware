/** 
* @Title: SerializerProvider.java
* @Package com.pieli.middleware.serialize
* @Description: TODO
* @author Pie.Li
* @date 2013-3-31 上午10:39:27
* @version V1.0 
*/
package org.brilliance.middleware.serialize;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import org.apache.log4j.Logger;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoException;
import com.esotericsoftware.kryo.io.ByteBufferOutputStream;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;


/**
 * @ClassName: SerializerProvider
 * @Description: TODO
 * @date 2013-3-31 上午10:39:27
 * 
 */
public class SerializerProvider {

	/**
	 * Logger
	 */
	private static final Logger logger =  Logger.getLogger(SerializerProvider.class);		
	
	/**默认byte大小**/
	private final static int  _defaultByteSize = 1024;
	
	/**序列化对象Map，线程安全**/
	private static ThreadLocal<Kryo> _kryoMap = new ThreadLocal<Kryo>(){
		public Kryo initialValue(){
			return new Kryo();
		}
	};
	
	/**
	 * @throws IOException 
	 * 序列化写
	* @Title: serializedWriteBuffer
	* @Description: TODO
	* @param @param c
	* @param @param targetObj
	* @param @return
	* @param @throws InstantiationException
	* @param @throws IllegalAccessException
	* @return ByteBuffer
	* @throws
	 */
	public static ByteBuffer serializedWriteBuffer(Class<?> c, Object targetObj) throws InstantiationException, IllegalAccessException, IOException{
				
		ByteBuffer buffer = ByteBuffer.allocate(_defaultByteSize);
		Output out = null;
		_kryoMap.get().register(c);
		
		int retry = 10;
		while(retry-- != 0){
			
			logger.debug("Action Once");
			try {
				out = generateOutput(buffer);
				_kryoMap.get().writeObject(out, targetObj);
				break;
			} catch (Exception e) {
				if(e instanceof BufferOverflowException) {
					buffer = ByteBuffer.allocate(buffer.capacity() * 2);
					continue;
				}
				if(e instanceof KryoException){
					KryoException kryoExcpetion = (KryoException) e;
					if(kryoExcpetion.getCause() instanceof BufferOverflowException){
						buffer = ByteBuffer.allocate(buffer.capacity() * 2);
						continue;
					}
				}
				logger.error("[serializedWriteBuffer] Error!", e);
			}			
			
		}
		if(buffer.capacity() == buffer.position()){
			ByteBuffer tempBuffer = ByteBuffer.allocate(buffer.capacity());
			buffer.flip();
			tempBuffer.put(buffer);
			buffer.clear();
			out.flush();
			if(buffer.position() > 0){
				ByteBuffer newBuffer = ByteBuffer.allocate(buffer.capacity() + buffer.position());
				tempBuffer.flip();
				newBuffer.put(tempBuffer);
				buffer.flip();
				newBuffer.put(buffer);
				buffer = newBuffer;
			}
		} else {
			out.flush();
		}
	
		return buffer;
	}
	
	/**
	 * 
	 * @param buffer
	 * @return
	 */
	private static Output generateOutput(ByteBuffer buffer){
		ByteBufferOutputStream outStream = new ByteBufferOutputStream(buffer);
		Output out = new Output(outStream, buffer.capacity());		
		return out;
	} 
	
	/**
	 * 
	* @Title: serializedRead
	* @Description: TODO
	* @param @param c
	* @param @param bytes
	* @param @return
	* @return Object
	* @throws
	 */
	public static <T> Object deserializedRead(Class<T> c, byte[] bytes) {
		
		Input input = new Input(new ByteArrayInputStream(bytes));
		Object obj =  _kryoMap.get().readObject(input, c);
		return obj;
		
	}
	
}
