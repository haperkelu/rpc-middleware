/**   
* @Title: EmbeddedServer.java 
* @Package org.brilliance.middleware.core 
* @Description: TODO
* @author Pie.Li   
* @date 2013-12-23 下午2:47:07 
* @version V1.0   
*/
package org.brilliance.middleware.core;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.brilliance.middleware.event.RPCEventHandler;
import org.brilliance.middleware.serialize.CustomEntry;
import org.brilliance.middleware.serialize.SerializerProvider;
import org.brilliance.middleware.transfer.TransferDataContext;
import org.brilliance.middleware.transfer.TransferResult;
import org.brilliance.middleware.transfer.TransferStandardData;

/**
 * 
 * @author Pie.Li
 *
 */
public class EmbeddedServer {
	
	/**
	 * Logger
	 */
	private static final Logger logger =  Logger.getLogger(EmbeddedServer.class);		
	
	private static final int DEFAULT_SIZE = 1024;
	
	/**
	 * NIO Selector
	 */
	private static Selector _selector;
	
	static {
		// open selector
		try {
			_selector = Selector.open();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		// add jvm hook
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {

			public void run() {
				if (_selector != null) {
					if (_selector.keys() != null) {
						for (SelectionKey k : _selector.keys()) {
							k.cancel();
							try {
								k.channel().close();
							} catch (Exception e) {
								logger.error(e.getMessage(), e);
							}
						}
					}
					try {
						_selector.close();
					} catch (IOException e) {
						logger.error(e.getMessage(), e);
					}
				}
			}}));
	}
	

	private static List<RPCEventHandler> eventList = new ArrayList<RPCEventHandler>();
	public static void registerEvent(RPCEventHandler handler) {		
		logger.debug(handler);
		eventList.add(handler);	
	}
	
	/**
	 * @throws Exception 
	 * 
	 */
	public static void startServer(final int port) throws Exception {
		
		if (_selector == null) {
			throw new IllegalArgumentException("selector should not be null");
		}
		
		try {
			ServerSocketChannel server = ServerSocketChannel.open();
			server.configureBlocking(false);
			server.socket().bind(new InetSocketAddress(port));
			SelectionKey serverKey = server.register(_selector, SelectionKey.OP_ACCEPT);
			
			while (true) {
				
				if(!_selector.isOpen()){
					break;
				}
				int readChannels = _selector.select(); //select channels for io operation
				if(readChannels == 0){
					continue;
				}
				logger.debug("selectedkeys count:" + readChannels);
				Iterator<SelectionKey> keyIterator = _selector.selectedKeys().iterator();
				while (keyIterator.hasNext()) {
					
					SelectionKey key = keyIterator.next();			
					keyIterator.remove();
					
					if(!key.isValid()){
						continue;
					}
					
					//读取
					if (key.isReadable()) {		
						logger.debug("key is readable");
						SocketChannel channel = (SocketChannel) key.channel();
						try {
							TransferResult result = new TransferResult();
							Object readReturn = Handler.processRead(serverKey, key, channel, result);								
							if(readReturn == null){
								result.setMetaType(String.class);
								result.setResult("void");
							}else {
								result.setMetaType(readReturn.getClass());
								result.setResult(readReturn);
							}
							channel.register(_selector, SelectionKey.OP_WRITE, result);
						} catch (Exception e) {
							logger.error(e.getMessage(), e);
							if(channel != null && channel.isOpen()){
								channel.close();
							}
						} 		
					} else if (key.isAcceptable()) { // 接收
						logger.debug("key is acceptable");
						SocketChannel channel = ((ServerSocketChannel) key.channel()).accept().socket().getChannel();
						channel.configureBlocking(false);
						channel.register(_selector, SelectionKey.OP_READ);
					} else if(key.isWritable()){	//写入	
						logger.debug("key is writable");
						SocketChannel channel = (SocketChannel) key.channel();
						try {
							Handler.processWrite(key, channel);
							channel.register(_selector, SelectionKey.OP_READ);
						} catch (Exception e) {
							logger.error(e.getMessage(), e);
						} 		
					}														
										
				}
				
			}
			
			
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
		
	}
	
	/**
	 * 
	 * @author Pie.Li
	 *
	 */
	private static class Handler {

		/**
		 * 
		 * @param channel
		 * @throws IOException
		 * @throws ClassNotFoundException
		 */
		@SuppressWarnings("rawtypes")
		public static Object processRead(SelectionKey serverKey, SelectionKey key, SocketChannel channel, TransferResult transferResult) throws IOException, ClassNotFoundException {
			
			ByteBuffer buffer = ByteBuffer.allocate(DEFAULT_SIZE);
			if (channel.read(buffer) >= DEFAULT_SIZE){
				
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
			if(buffer.position() == 0) {return null;}
			logger.debug("read content:" + new String(buffer.array()) + ";position:" + buffer.position());
			TransferStandardData bizData = (TransferStandardData) SerializerProvider.deserializedRead(TransferStandardData.class, buffer.array());
			transferResult.setSequence(bizData.getSequenceId());
			logger.debug(bizData.getClassFullName());
			logger.debug(bizData.getParameter());
			logger.debug(eventList);
			Object result = null;
			if(eventList != null && eventList.size() > 0){
				boolean isFirstElement = true;
				for(RPCEventHandler handler: eventList){
					if(handler != null){
						logger.debug("start invoking event handler");
						List<CustomEntry<Class, Object>> tempList = null;
						if(bizData.getParameter() != null){
							tempList = new ArrayList<CustomEntry<Class, Object>>();
							for(CustomEntry<String, Object> item: bizData.getParameter()){
								Class c = loadPrimitiveType(item.getKey());
								if(c == null){
									c = Class.forName(item.getKey());
								}
								CustomEntry<Class, Object> entry = new CustomEntry<Class, Object>();
								entry.setKey(c);
								entry.setValue(item.getValue());
								tempList.add(entry);
							}
						}
						if(isFirstElement){
							result = handler.onRecieveData(bizData.getClassFullName(), bizData.getMethodName(), tempList);
							isFirstElement = !isFirstElement;
						} else {
							handler.onRecieveData(bizData.getClassFullName(), bizData.getMethodName(), tempList);
						}
						
						logger.debug("result " + result);
						/**
						if(isFirstElement){
							logger.debug("is first element:" + result);
							
							Iterator<SelectionKey> keyIterator = _selector.keys().iterator();
							while (keyIterator.hasNext()) {
								SelectionKey target = keyIterator.next();
								logger.debug("target:" + target);
								if(target != serverKey){
									logger.debug("attach key:" + target);
									target.attach(result);
								}
							}

							isFirstElement = !isFirstElement;
						}
						**/	
					}
				}
			}
			return result;
			
		}
		
		/**
		 * 
		 * @param key
		 * @param channel
		 * @throws InstantiationException
		 * @throws IllegalAccessException
		 * @throws IOException
		 */
		public static void processWrite(SelectionKey key, SocketChannel channel) throws InstantiationException, IllegalAccessException, IOException{
			Object attachment = key.attachment();
			
			logger.debug("process writer");
			logger.debug("writer key:" + key);
			logger.debug("attachment:" + attachment);
			ByteBuffer buffer = null;
			buffer = SerializerProvider.serializedWriteBuffer(attachment.getClass(), attachment);
			
			logger.debug("buffer:" + new String(buffer.array()) + ";count" + buffer.position());
			buffer.flip();
			channel.write(buffer);
			

		}		
		
		/**
		 * Load Primitive Java Type Class
		 * @param name
		 * @return
		 */
		@SuppressWarnings("rawtypes")
		public static Class loadPrimitiveType(String name) {
			if (name.equals("byte")) return byte.class;
			if (name.equals("short")) return short.class;
			if (name.equals("int")) return int.class;
			if (name.equals("long")) return long.class;
			if (name.equals("char")) return char.class;
			if (name.equals("float")) return float.class;
			if (name.equals("double")) return double.class;
			if (name.equals("boolean")) return boolean.class;
			if (name.equals("void")) return void.class;
			return null;
		}	
		
	}
	
}
