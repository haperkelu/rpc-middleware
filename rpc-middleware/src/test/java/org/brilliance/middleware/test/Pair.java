package org.brilliance.middleware.test;

public class Pair {
	public Pair() {
		
	}
	public Pair(String key, String value) {
		this.key = key;
		this.value = value;
		this.pair1 = new Pair1("key1", "key2");
	}
	private String key;
	private String value;
	private Pair1 pair1;
	/**
	 * @return the key
	 */
	public String getKey() {
		return key;
	}
	/**
	 * @param key the key to set
	 */
	public void setKey(String key) {
		this.key = key;
	}
	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}
	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		
		return this.key + this.value;
	}
	
}