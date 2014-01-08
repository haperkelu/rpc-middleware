/** 
* @Title: DoubleByteBufferInflater.java
* @Package com.pieli.middleware.transfer
* @Description: TODO
* @author Pie.Li
* @date 2013-4-1 上午10:55:27
* @version V1.0 
*/
package org.brilliance.middleware.transfer;

import java.nio.ByteBuffer;

/**
 * @ClassName: DoubleByteBufferInflater
 * @Description: TODO
 * @date 2013-4-1 上午10:55:27
 * 
 */
public class DoubleByteBufferInflaterImp implements ByteBufferInflater {

	/**
	 * @see com.pieli.middleware.transfer.ByteBufferInflater#inflateByteBuffer(java.nio.ByteBuffer)
	 */
	public ByteBuffer inflateByteBuffer(ByteBuffer buffer) {
		
		if(buffer == null){throw new IllegalArgumentException("argument buffer should not be null");}
		
		ByteBuffer clone = ByteBuffer.allocate(buffer.capacity() * 2);
		buffer.flip();
		clone.put(buffer);
		
		return clone;
		
	}

}
