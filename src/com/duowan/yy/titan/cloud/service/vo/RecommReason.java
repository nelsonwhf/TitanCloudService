package com.duowan.yy.titan.cloud.service.vo;

import java.io.Serializable;
import java.util.Set;

import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public final class RecommReason {

	public static final Integer USER_FOLLOW = 1;//用户关注原因代码
	public static final Integer GAME_BINDING = 2;//游戏绑定原因代码
	public static final Integer CHANNEL_LEVEL = 3;//频道原因代码

	public static final RecommReason USER_FOLLOW_REASON = new RecommReason(USER_FOLLOW, null, null);

	public static RecommReason newChannelLevelReason(Integer value, Set<String> channels) {
		return new RecommReason(CHANNEL_LEVEL, value, channels);
	}

	public static RecommReason newGameBindinglReason(Integer value, Set<String> gameRegions) {
		return new RecommReason(GAME_BINDING, value, gameRegions);
	}

	private Integer reason;//推荐原因
	private Integer value;//绑定相同游戏区服的个数或同一频道个数
	private Set<String> additions;//相同游戏区服或频道集合

	public RecommReason() {
	}

	public RecommReason(Integer reason, Integer value, Set<String> additions) {
		super();
		this.reason = reason;
		this.value = value;
		this.additions = additions;
	}

	/**
	 * 得到推荐原因代码
	 * @return the reason
	 */
	public Integer getReason() {
		return reason;
	}

	/**
	 * @param reason
	 *            the reason to set
	 */
	public void setReason(Integer reason) {
		this.reason = reason;
	}

	/**
	 * 得到绑定相同游戏区服的个数或相同频道个数
	 * @return the value
	 */
	public Integer getValue() {
		return value;
	}

	/**
	 * @param value
	 *            the value to set
	 */
	public void setValue(Integer value) {
		this.value = value;
	}

	/**
	 * 得到相同游戏区服或频道集合
	 * @return the additions
	 */
	public Set<String> getAdditions() {
		return additions;
	}

	/**
	 * @param additions
	 *            the additions to set
	 */
	public void setAdditions(Set<String> additions) {
		this.additions = additions;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("RecommReason [reason=").append(reason).append(", value=").append(value)
				.append(", additions=").append(additions).append("]");
		return builder.toString();
	}

}
