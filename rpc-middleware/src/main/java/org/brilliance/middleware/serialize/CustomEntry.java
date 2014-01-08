/** 
* @Title: SerializerProvider.java
* @Package com.pieli.middleware.serialize
* @Description: TODO
* @author Pie.Li
* @date 2013-3-31 上午10:39:27
* @version V1.0 
*/
package org.brilliance.middleware.serialize;


/**
 * @ClassName: SerializerProvider
 * @Description: TODO
 * @date 2013-3-31 上午10:39:27
 * 
 */
public class CustomEntry<K,V> implements java.io.Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8252912385831156955L;
	private K key;
	private V value;
	
	public CustomEntry() {
		super();
	}
	
	public CustomEntry(K key, V value) {
		super();
		this.key = key;
		this.value = value;
	}

	public K getKey() {
		return key;
	}
	
	public void setKey(K key){
		this.key = key;
	}

	public V getValue() {
		return value;
	}

	public void setValue(V value) {
		this.value = value;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {	
		return "{key:" + key.toString() + ",value:" + value.toString() + "}";
	}	

}
