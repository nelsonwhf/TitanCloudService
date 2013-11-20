package com.duowan.yy.titan.cloud.listeners;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.apache.commons.configuration.web.ServletContextConfiguration;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import com.thinkaurelius.titan.core.TitanFactory;
import com.thinkaurelius.titan.core.TitanGraph;

@WebListener(value = "TitanConfigListener")
public class ConfigListener implements ServletContextListener {

	private static final String TITAN_CONF_FILE = "profile";
	private static final String REDIS_HOST = "redisHost";
	private static final String REDIS_PORT = "redisPort";
	private static final String REDIS_DB = "redisDb";
	private static final String REDIS_TIMEOUT = "redisTimeout";

	@Override
	public void contextDestroyed(ServletContextEvent event) {
		TitanGraph graphDb = (TitanGraph) event.getServletContext().getAttribute("titan");
		if (graphDb != null)
		{
			graphDb.commit();
			graphDb.shutdown();
		}
		JedisPool redisPool = (JedisPool) event.getServletContext().getAttribute("redisPool");
		if (redisPool != null)
			redisPool.destroy();
	}

	@Override
	public void contextInitialized(ServletContextEvent event) {
		ServletContext context = event.getServletContext();
		// String conffile = context.getInitParameter(TITAN_CONF_FILE);
		// conffile = context.getRealPath(conffile);
		// 2013-11-18 change config from File to ServletContextConfig
		ServletContextConfiguration conf = new ServletContextConfiguration(context);
		TitanGraph graphDb = TitanFactory.open(conf);
		context.setAttribute("titan", graphDb);

		String host = context.getInitParameter(REDIS_HOST);
		String port = context.getInitParameter(REDIS_PORT);
		String db = context.getInitParameter(REDIS_DB);
		String timeout = context.getInitParameter(REDIS_TIMEOUT);
		JedisPool redisPool = new JedisPool(new JedisPoolConfig(), host, Integer.parseInt(port),
				Integer.parseInt(timeout), null, Integer.parseInt(db));
		context.setAttribute("redisPool", redisPool);
	}

}
