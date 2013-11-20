/**
 * 
 */
package com.duowan.yy.titan.cloud.service.request;


/**
 * 
 * @author WangHongfei.Nelson
 *2013-9-17
 */
public class FriendsRecommRequest extends CommonFriendsrecommRequest{

	private Long uid;

	private Integer maxRecommAmount = 28;

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
		builder.append("FriendsRecommRequest [uid=").append(uid).append(", maxRecommAmount=")
				.append(maxRecommAmount).append("]");
		return builder.toString();
	}

}
