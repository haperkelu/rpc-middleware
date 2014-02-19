/**   
* @Title: TransferStandardData.java 
* @Package org.brilliance.middleware.entity 
* @Description: TODO
* @author Pie.Li   
* @date 2013-12-24 下午2:50:25 
* @version V1.0   
*/
package org.brilliance.middleware.transfer;

import java.io.Serializable;
import java.util.List;
import org.brilliance.middleware.serialize.CustomEntry;

/**
 * 
 * @author Pie.Li
 *
 */
public class TransferStandardData implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2490584516803696012L;
	
	private String sequenceId;
	
	private String classFullName;
	
	private String methodName;
	
	private List<CustomEntry<String, Object>> parameter;

	/**
	 * @return the classFullName
	 */
	public String getClassFullName() {
		return classFullName;
	}

	/**
	 * @param classFullName the classFullName to set
	 */
	public void setClassFullName(String classFullName) {
		this.classFullName = classFullName;
	}

	/**
	 * @return the methodName
	 */
	public String getMethodName() {
		return methodName;
	}

	/**
	 * @param methodName the methodName to set
	 */
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	/**
	 * @return the parameter
	 */
	public List<CustomEntry<String, Object>> getParameter() {
		return parameter;
	}

	/**
	 * @param parameter the parameter to set
	 */
	public void setParameter(List<CustomEntry<String, Object>> parameter) {
		this.parameter = parameter;
	}

	/**
	 * @return the sequenceId
	 */
	public String getSequenceId() {
		return sequenceId;
	}

	/**
	 * @param sequenceId the sequenceId to set
	 */
	public void setSequenceId(String sequenceId) {
		this.sequenceId = sequenceId;
	}
}
