/**
 * 
 */
package com.duowan.yy.titan.cloud.serde;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * @author zhangtao.robin
 * 
 */
public class JsonObjectMapper {

	// disable FAIL_ON_UNKNOWN_PROPERTIES
	private static final ObjectMapper objectMapper = new ObjectMapper().configure(
			DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);

	private JsonObjectMapper() {
		// none
	}

	public static final ObjectMapper getObjectMapper() {
		return objectMapper;
	}
}
