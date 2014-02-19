package org.brilliance.middleware.test;

public class Pair1 {
	public Pair1() {
		
	}
	public Pair1(String key, String value) {
		this.key = key;
		this.value = value;
	}
	private String key;
	private String value;
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