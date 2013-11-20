/**
 * 
 */
package com.duowan.yy.titan.cloud.service;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.duowan.yy.titan.cloud.service.response.ServiceResponse;
import com.thinkaurelius.titan.core.TitanGraph;

/**
 * 
 * @author WangHongfei.Nelson 2013-9-17
 * @param <REQ>
 * @param <RST>
 */
public abstract class AbstractCloudService<REQ, RST> {

	protected final Logger log = LoggerFactory.getLogger(this.getClass());

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response doCloudService(REQ request, @Context ServletContext context,
			@Context HttpServletRequest httpreq) {

		TitanGraph graphDb = (TitanGraph) context.getAttribute("titan");

		try {
			RST result = doRealService(request, graphDb, context, httpreq);
			ServiceResponse<RST> response = new ServiceResponse<RST>();
			response.setServiceName(getServiceName());
			response.setServiceResult(result);
			if (log.isDebugEnabled()) {
				log.debug("doCloudService() request=" + request + ", response=" + response);
			}
			return Response.status(200).entity(response).build();
		} catch (Exception e) {
			log.error("doCloudService() error: " + e.toString() + ", request=" + request, e);
			if (e instanceof WebApplicationException) {
				return ((WebApplicationException) e).getResponse();
			}
			ServiceResponse<Object> error = new ServiceResponse<Object>();
			error.setServiceName(getServiceName());
			error.setServiceErrorInfo(e.toString());
			return Response.status(500).entity(error).build();
		}
	}

	protected abstract RST doRealService(REQ request, TitanGraph graphDb, ServletContext context,
			HttpServletRequest httpreq) throws Exception;

	public abstract String getServiceName();
}
