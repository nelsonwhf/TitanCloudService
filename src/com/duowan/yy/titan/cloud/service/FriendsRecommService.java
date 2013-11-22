/**
 * 
 */
package com.duowan.yy.titan.cloud.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import com.duowan.yy.titan.cloud.constant.RelationEnum;
import com.duowan.yy.titan.cloud.constant.VertexEnum;
import com.duowan.yy.titan.cloud.pipe.InEdgesPipe;
import com.duowan.yy.titan.cloud.pipe.OutEdgesPipe;
import com.duowan.yy.titan.cloud.redis.RedisUtils;
import com.duowan.yy.titan.cloud.serde.JsonObjectMapper;
import com.duowan.yy.titan.cloud.service.request.FriendsRecommRequest;
import com.duowan.yy.titan.cloud.service.response.ServiceResponse;
import com.duowan.yy.titan.cloud.service.vo.FriendsRecommServiceResult;
import com.duowan.yy.titan.cloud.service.vo.RecommReason;
import com.duowan.yy.titan.cloud.service.vo.SingleUserRecommReason;
import com.thinkaurelius.titan.core.TitanGraph;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.gremlin.java.GremlinPipeline;
import com.tinkerpop.gremlin.pipes.transform.InVertexPipe;
import com.tinkerpop.gremlin.pipes.transform.OutVertexPipe;

/**
 * 
 * @author WangHongfei.Nelson 2013-9-17
 */
@Path("/friends_recomm")
public class FriendsRecommService extends
		AbstractCloudService<FriendsRecommRequest, FriendsRecommServiceResult> {

	private static final String FRIENDS_RECOMM = "friends_recomm";
	private static final String RECOMM_CACHE = "recomm_cache";

	public static final String COLON = ":";

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public ServiceResponse<FriendsRecommServiceResult> getRecommForTest(
			@QueryParam("uid") long uid, @DefaultValue("28") @QueryParam("max") int max,
			@DefaultValue("test") @QueryParam("appid") String appid,
			@Context ServletContext context,
			@Context HttpServletRequest httpreq)
			throws Exception {
		TitanGraph graphDb = (TitanGraph) context.getAttribute("titan");

		ServiceResponse<FriendsRecommServiceResult> response = new ServiceResponse<FriendsRecommServiceResult>();
		FriendsRecommRequest request = new FriendsRecommRequest();
		request.setUid(uid);
		request.setMaxRecommAmount(max);
		request.setAppid(appid);
		FriendsRecommServiceResult serviceResult = doRealService(request, graphDb, context, httpreq);
		response.setServiceResult(serviceResult);
		response.setServiceName(FRIENDS_RECOMM);
		return response;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.duowan.yy.relations.cloud.service.AbstractCloudService#getServiceName
	 * ()
	 */
	@Override
	public String getServiceName() {
		return FRIENDS_RECOMM;
	}

	/* (non-Javadoc)
	 * @see com.duowan.yy.titan.cloud.service.AbstractCloudService#doRealService(java.lang.Object, com.thinkaurelius.titan.core.TitanGraph, javax.servlet.ServletContext, javax.servlet.http.HttpServletRequest)
	 */
	@Override
	protected FriendsRecommServiceResult doRealService(FriendsRecommRequest request,
			TitanGraph graphDb, ServletContext context,
			HttpServletRequest httpreq) throws Exception {

		JedisPool redisPool = (JedisPool) context.getAttribute("redisPool");

		// check app rights
		String appid = request.getAppid();
		String ip = this.getRemoteAddress(httpreq);
		if (!isAppHasRight(appid, ip, redisPool)) {
			log.warn("Appid denied: " + appid + ", ip: " + ip);
			Response rsp = Response.status(403).entity(null).build();
			throw new WebApplicationException(rsp);
		}

		// check cache exist
		Long uid = request.getUid();
		int max = request.getMaxRecommAmount();

		FriendsRecommServiceResult result = getRecommFromCache(uid, redisPool);
		if (result != null) {
			log.info("Uid:" + uid + ", Cache Hit");
			return result;
		}

		result = new FriendsRecommServiceResult();
		result.setMaxRecommAmount(max);
		result.setUid(uid);

		int limit = max * 2;
		try {
			Vertex node = this.findUserNode(uid, graphDb);
			if (node != null) {
				long startRunTime = System.currentTimeMillis();

				/*
				userFollows(node, uid, max, result);
				long endRunTime = System.currentTimeMillis();
				long cost1 = (endRunTime - startRunTime);

				startRunTime = endRunTime;
				userGameBindingAndChannelLevel(node, uid, limit, result);

				// recommFriends(node, uid, limit, result);
				endRunTime = System.currentTimeMillis();
				long cost2 = (endRunTime - startRunTime);
				*/

				startRunTime = System.currentTimeMillis();
				recommFriends(node, uid, max, result);
				long endRunTime = System.currentTimeMillis();
				long cost3 = endRunTime - startRunTime;

				// log.info("Split Method --- " + "Uid:" + uid +
				// ", UserFollow time:" + cost1
				// + ", GameBindingAndChannelLevel time:" + cost2 +
				// ", result numbers:"
				// + (result.getFriends() == null ? 0 :
				// result.getFriends().size()));
				log.info("Combine Method --- " + "Uid:" + uid +
						",Total Time:" + cost3 + ", result numbers:"
						+ (result.getFriends() == null ? 0 :
								result.getFriends().size()));
			} else {
				log.info("Uid:" + uid + ", Cannot find node");
			}
		} finally {
			// must commit, because titan cache is in ThreadLocal variable
			graphDb.commit();
		}

		// add result to cache
		addRecommToCache(uid, result, 900, redisPool);
		return result;
	}

	/**
	 * 获取客户端ip地址
	 * 
	 * @author WangHongfei.Nelson 2013-9-30
	 * @param request
	 * @return
	 */
	public String getRemoteAddress(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || ip.equalsIgnoreCase("unknown"))
			ip = request.getHeader("Proxy-Client-IP");
		if (ip == null || ip.length() == 0 || ip.equalsIgnoreCase("unknown"))
			ip = request.getHeader("WL-Proxy-Client-IP");
		if (ip == null || ip.length() == 0 || ip.equalsIgnoreCase("unknown"))
			ip = request.getRemoteAddr();
		return ip;
	}

	private boolean isAppHasRight(String appid, String ip, JedisPool redisPool)
	{
		String key = RedisUtils.createKey(FRIENDS_RECOMM, appid);
		Jedis jedis = redisPool.getResource();
		try {
			return (jedis.sismember(key, ip) || jedis.sismember(key, "*"));
		} finally {
			redisPool.returnResource(jedis);
		}
	}

	private Vertex findUserNode(Long uid, TitanGraph graphDb) {
		for (Vertex v : graphDb.getVertices(VertexEnum.USER_UID.getTypeName(),
				uid)) {
			return v;
		}
		return null;
	}

	private void recommFriends(Vertex userNode, Long userid, int limit,
			FriendsRecommServiceResult result) {
		GremlinPipeline[] pipes = { userFollowPipe(userNode)
				, userGameAndChannelPipe(userNode, userid, limit) };
		List<List> pathList = (List<List>) new GremlinPipeline<Vertex, Vertex>().start(userNode)
				.copySplit(pipes).enablePath().exhaustMerge().next(limit * 3);
		List<SingleUserRecommReason> reasonsListGame = new ArrayList<SingleUserRecommReason>();
		List<SingleUserRecommReason> reasonsListChannel = new ArrayList<SingleUserRecommReason>();
		for (List<?> path : pathList) {
			if (path.size() == 3) {
				// v-v-v
				addUserFollowReason(path, result);
			} else if (path.size() == 5) {
				// v-e-v-e-v
				addUserChannelAndGameReason(path, reasonsListGame, reasonsListChannel);
			} else {
				log.warn("Path size should be 3 or 5:" + path.size());
			}
		}
		computeGameChannelBinding(reasonsListGame, RelationEnum.GAME_BINDING, result);
		computeGameChannelBinding(reasonsListChannel, RelationEnum.CHANNEL_ROLE_LEVEL, result);
	}

	private void addUserFollowReason(List<?> path, FriendsRecommServiceResult result) {
		Vertex middle = (Vertex) path.get(1);
		Long uid = (Long) middle.getProperty(VertexEnum.USER_UID.getTypeName());
		result.addRecommUser(uid, RecommReason.USER_FOLLOW_REASON);
	}

	private void addUserChannelAndGameReason(List<?> path
			, List<SingleUserRecommReason> reasonsListGame,
			List<SingleUserRecommReason> reasonsListChannel) {
		Vertex end = (Vertex) path.get(path.size() - 1);
		Long friendId = end.getProperty(VertexEnum.USER_UID.getTypeName());
		Integer reason = null;
		String addition = null;
		Vertex middle = (Vertex) path.get(path.size() - 3);
		Edge endEdge = (Edge) path.get(path.size() - 2);
		if (endEdge.getLabel().equals(
				RelationEnum.GAME_BINDING.getTypeName())) {
			reason = RecommReason.GAME_BINDING;
			addition = middle.getProperty(VertexEnum.GAME_REGION
					.getTypeName());
			reasonsListGame.add(new SingleUserRecommReason(friendId, reason, addition));
		} else if (endEdge.getLabel().equals(
				RelationEnum.CHANNEL_ROLE_LEVEL.getTypeName())) {
			reason = RecommReason.CHANNEL_LEVEL;
			addition = middle.getProperty(VertexEnum.CHANNEL_TID
					.getTypeName()).toString();
			reasonsListChannel.add(new SingleUserRecommReason(friendId, reason, addition));
		}
	}

	private GremlinPipeline userFollowPipe(Vertex userNode) {
		GremlinPipeline followPipe = new GremlinPipeline<Vertex, Vertex>()
				.out(RelationEnum.USER_FOLLOW.getTypeName())
				.out(RelationEnum.USER_FOLLOW.getTypeName())
				.has("id", userNode.getId())
				.path();
		return followPipe;
	}

	private GremlinPipeline userGameAndChannelPipe(Vertex userNode, Long userid, int limit) {
		GremlinPipeline pipe = new GremlinPipeline<Vertex, Vertex>()
				.add(new OutEdgesPipe(limit, RelationEnum.GAME_BINDING.getTypeName(),
						RelationEnum.CHANNEL_ROLE_LEVEL.getTypeName()))
				.add(new InVertexPipe())
				.add(new InEdgesPipe(limit, RelationEnum.GAME_BINDING.getTypeName(),
						RelationEnum.CHANNEL_ROLE_LEVEL.getTypeName()))
				.add(new OutVertexPipe())
				.hasNot(VertexEnum.USER_UID.getTypeName(), userid)
				.path();
		return pipe;
	}

	private void userFollows(Vertex userNode, Long userid, int limit,
			FriendsRecommServiceResult result) {
		List<List> pathList = new GremlinPipeline<Vertex, Vertex>().start(userNode)
				.out(RelationEnum.USER_FOLLOW.getTypeName())
				.out(RelationEnum.USER_FOLLOW.getTypeName())
				.has("id", userNode.getId())
				.path()
				.next(limit);
		for (List<?> path : pathList) {
			// v-v-v
			Vertex middle = (Vertex) path.get(1);
			Long uid = (Long) middle.getProperty(VertexEnum.USER_UID.getTypeName());
			result.addRecommUser(uid, RecommReason.USER_FOLLOW_REASON);
		}

	}

	private void userGameBindingAndChannelLevel(Vertex userNode, Long userid, int limit,
			FriendsRecommServiceResult result) {
		List<List> pathList = new GremlinPipeline<Vertex, Vertex>()
				.start(userNode)
				.add(new OutEdgesPipe(limit, RelationEnum.GAME_BINDING.getTypeName(),
						RelationEnum.CHANNEL_ROLE_LEVEL.getTypeName()))
				.add(new InVertexPipe())
				.add(new InEdgesPipe(limit, RelationEnum.GAME_BINDING.getTypeName(),
						RelationEnum.CHANNEL_ROLE_LEVEL.getTypeName()))
				.add(new OutVertexPipe())
				.hasNot(VertexEnum.USER_UID.getTypeName(), userid)
				.path().next(limit);

		List<SingleUserRecommReason> reasonsListGame = new ArrayList<SingleUserRecommReason>();
		List<SingleUserRecommReason> reasonsListChannel = new ArrayList<SingleUserRecommReason>();

		for (List<?> path : pathList) {
			// v-e-v-e-v
			Vertex end = (Vertex) path.get(path.size() - 1);
			Long friendId = end.getProperty(VertexEnum.USER_UID.getTypeName());
			Integer reason = null;
			String addition = null;
			Vertex middle = (Vertex) path.get(path.size() - 3);
			Edge endEdge = (Edge) path.get(path.size() - 2);
			if (endEdge.getLabel().equals(
					RelationEnum.GAME_BINDING.getTypeName())) {
				reason = RecommReason.GAME_BINDING;
				addition = middle.getProperty(VertexEnum.GAME_REGION
						.getTypeName());
				reasonsListGame.add(new SingleUserRecommReason(friendId, reason, addition));
			} else if (endEdge.getLabel().equals(
					RelationEnum.CHANNEL_ROLE_LEVEL.getTypeName())) {
				reason = RecommReason.CHANNEL_LEVEL;
				addition = middle.getProperty(VertexEnum.CHANNEL_TID
						.getTypeName()).toString();
				reasonsListChannel.add(new SingleUserRecommReason(friendId, reason, addition));
			}
		}

		computeGameChannelBinding(reasonsListGame, RelationEnum.GAME_BINDING, result);
		computeGameChannelBinding(reasonsListChannel, RelationEnum.CHANNEL_ROLE_LEVEL, result);
	}

	private void computeGameChannelBinding(List<SingleUserRecommReason> reasonsList,
			RelationEnum relationEnum,
			FriendsRecommServiceResult result) {
		Map<Long, Integer> friendsReasonCount = new HashMap<Long, Integer>();
		Map<Long, Set<String>> friendsAddtion = new HashMap<Long, Set<String>>();
		for (SingleUserRecommReason sreason : reasonsList) {
			Long uid = sreason.getUid();
			String addition = sreason.getAddition();
			Set<String> set = friendsAddtion.get(uid);
			if (set == null) {
				set = new HashSet<String>(3);
				set.add(addition);
				friendsAddtion.put(uid, set);
			} else if (set.size() < 3) {
				set.add(addition);
			}

			Integer value = friendsReasonCount.get(uid);
			if (value == null) {
				friendsReasonCount.put(uid, Integer.valueOf(1));
			} else {
				friendsReasonCount.put(uid, Integer.valueOf(value + 1));
			}
		}

		for (Entry<Long, Integer> entry : friendsReasonCount.entrySet()) {
			if (relationEnum == RelationEnum.GAME_BINDING) {
				result.addRecommUser(
						entry.getKey(),
						RecommReason.newGameBindinglReason(entry.getValue(),
								friendsAddtion.get(entry.getKey())));
			} else if (relationEnum == RelationEnum.CHANNEL_ROLE_LEVEL) {
				result.addRecommUser(
						entry.getKey(),
						RecommReason.newChannelLevelReason(entry.getValue(),
								friendsAddtion.get(entry.getKey())));
			}
		}
	}

	private FriendsRecommServiceResult getRecommFromCache(Long uid, JedisPool jedisPool) {
		String cacheKey = RedisUtils.createKey(RECOMM_CACHE, uid.toString());
		Jedis jedis = jedisPool.getResource();
		try {
			String json = jedis.get(cacheKey);
			if (json != null) {
				try {
					return JsonObjectMapper.getObjectMapper().readValue(json,
							FriendsRecommServiceResult.class);
				} catch (Exception e) {
				}
			}
			return null;
		} finally {
			jedisPool.returnResource(jedis);
		}
	}

	private void addRecommToCache(Long uid, FriendsRecommServiceResult result, int timeOut,
			JedisPool jedisPool) {
		String cacheKey = RedisUtils.createKey(RECOMM_CACHE, uid.toString());
		Jedis jedis = jedisPool.getResource();
		try {
			String json;
			try {
				json = JsonObjectMapper.getObjectMapper().writeValueAsString(result);
			} catch (Exception e) {
				return;
			}
			jedis.set(cacheKey, json);
			jedis.expire(cacheKey, timeOut);
		} finally {
			jedisPool.returnResource(jedis);
		}
	}
}
