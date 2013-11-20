/**
 * 
 */
package com.duowan.yy.titan.cloud.service.response;

import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * 
 * @author WangHongfei.Nelson
 *2013-9-17
 * @param <T>
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class ServiceResponse<T extends Object> {

	private String serviceName;
	private String serviceErrorInfo;
	private T serviceResult;
	//private String code;//服务响应状态码（目前用于判断appid权限）

	/**
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
	**/

	/**
	 * @return the serviceName
	 */
	public String getServiceName() {
		return serviceName;
	}

	/**
	 * @param serviceName
	 *            the serviceName to set
	 */
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	/**
	 * @return the serviceErrorInfo
	 */
	public String getServiceErrorInfo() {
		return serviceErrorInfo;
	}

	/**
	 * @param serviceErrorInfo
	 *            the serviceErrorInfo to set
	 */
	public void setServiceErrorInfo(String serviceErrorInfo) {
		this.serviceErrorInfo = serviceErrorInfo;
	}

	/**
	 * @return the serviceResult
	 */
	public T getServiceResult() {
		return serviceResult;
	}

	/**
	 * @param serviceResult
	 *            the serviceResult to set
	 */
	public void setServiceResult(T serviceResult) {
		this.serviceResult = serviceResult;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ServiceResponseObject [serviceName=").append(serviceName)
				.append(", serviceErrorInfo=").append(serviceErrorInfo).append(", serviceResult=")
				.append(serviceResult).append("]");
		return builder.toString();
	}

}
