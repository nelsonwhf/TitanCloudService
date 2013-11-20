/**
 * 
 */
package com.duowan.yy.titan.cloud.service.vo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * 
 * @author WangHongfei.Nelson
 *2013-9-17
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class FriendsRecommServiceResult {

	private Long uid;
	private Integer maxRecommAmount;

	private Map<Long, List<RecommReason>> friends;

	public void addRecommUser(Long uid, RecommReason reason) {
		List<RecommReason> reasons = null;

		if (friends == null) {
			friends = new HashMap<Long, List<RecommReason>>();
		} else {
			reasons = friends.get(uid);
		}

		if (reasons == null && friends.size() < maxRecommAmount) {
			reasons = new ArrayList<RecommReason>();
			reasons.add(reason);
			friends.put(uid, reasons);
		} else if (reasons != null) {
			reasons.add(reason);
		}
	}

	/**
	 * @return the friends
	 */
	public Map<Long, List<RecommReason>> getFriends() {
		if (friends == null) {
			friends = new HashMap<Long, List<RecommReason>>();
		}
		return friends;
	}

	/**
	 * @return the uid
	 */
	public Long getUid() {
		return uid;
	}

	/**
	 * @param uid
	 *            the uid to set
	 */
	public void setUid(Long uid) {
		this.uid = uid;
	}

	/**
	 * @return the maxRecommAmount
	 */
	public Integer getMaxRecommAmount() {
		return maxRecommAmount;
	}

	/**
	 * @param maxRecommAmount
	 *            the maxRecommAmount to set
	 */
	public void setMaxRecommAmount(Integer maxRecommAmount) {
		this.maxRecommAmount = maxRecommAmount;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("FriendsRecommServiceResult [uid=").append(uid).append(", maxRecommAmount=")
				.append(maxRecommAmount).append(", friends=").append(friends).append("]");
		return builder.toString();
	}

}
