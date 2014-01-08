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
import java.nio.ByteBuffer;
import org.apache.log4j.Logger;
import com.esotericsoftware.kryo.Kryo;
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
	public static ByteBuffer  serializedWriteBuffer(Class<?> c, Object targetObj) throws InstantiationException, IllegalAccessException, IOException{
				
		ByteBuffer buffer = ByteBuffer.allocate(_defaultByteSize);
		ByteBufferOutputStream outStream = new ByteBufferOutputStream(buffer);
		Output out = new Output(outStream);		
		_kryoMap.get().register(c);
		_kryoMap.get().writeObject(out, targetObj);
				
		if(out.position() < _defaultByteSize - 1){
			out.flush();
			return buffer;
		} else {
			
			while(true){			
				buffer = ByteBuffer.allocate(buffer.capacity() * 2);
				outStream = new ByteBufferOutputStream(buffer);
				out = new Output(outStream);	
				_kryoMap.get().writeObject(out, targetObj);
				if(out.position() < buffer.capacity() - 1){
					out.flush();
					break;
				}
			}

			return buffer;
		}				
	
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
