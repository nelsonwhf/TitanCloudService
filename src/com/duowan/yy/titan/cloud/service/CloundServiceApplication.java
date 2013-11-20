/**
 * 
 */
package com.duowan.yy.titan.cloud.service;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 * @author zhangtao.robin
 * 
 */
@ApplicationPath("/service")
public class CloundServiceApplication extends Application {

	/* (non-Javadoc)
	 * @see javax.ws.rs.core.Application#getClasses()
	 */
	@Override
	public Set<Class<?>> getClasses() {
		Set<Class<?>> s = new HashSet<Class<?>>();
		s.add(FriendsRecommService.class);
		s.add(PojoJacksonJsonProvider.class);
		return s;
	}

}
