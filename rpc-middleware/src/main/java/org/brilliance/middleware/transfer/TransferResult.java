/**   
* @Title: TransferResult.java 
* @Package org.brilliance.middleware.transfer 
* @Description: TODO
* @author Pie.Li   
* @date 2014-2-20 下午8:27:32 
* @version V1.0   
*/
package org.brilliance.middleware.transfer;

import java.io.Serializable;

/**
 * @author Pie.Li
 *
 */
public class TransferResult implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1244626262817888105L;
	
	private String sequence;
	private Class metaType;
	private Object result;
	/**
	 * @return the sequence
	 */
	public String getSequence() {
		return sequence;
	}
	/**
	 * @param sequence the sequence to set
	 */
	public void setSequence(String sequence) {
		this.sequence = sequence;
	}
	/**
	 * @return the result
	 */
	public Object getResult() {
		return result;
	}
	/**
	 * @param result the result to set
	 */
	public void setResult(Object result) {
		this.result = result;
	}
	/**
	 * @return the metaType
	 */
	public Class getMetaType() {
		return metaType;
	}
	/**
	 * @param metaType the metaType to set
	 */
	public void setMetaType(Class metaType) {
		this.metaType = metaType;
	}
	
}
