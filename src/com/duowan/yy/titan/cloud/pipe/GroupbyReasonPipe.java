package com.duowan.yy.titan.cloud.pipe;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.duowan.yy.titan.cloud.service.vo.RecommReason;
import com.tinkerpop.pipes.PipeFunction;
import com.tinkerpop.pipes.sideeffect.GroupByPipe;

public class GroupbyReasonPipe extends
		GroupByPipe<List, Long, RecommReason> {

	protected int maxfriends;
	private int count = 0;

	public GroupbyReasonPipe(Map<Long, List<RecommReason>> byMap,
			PipeFunction<List, Long> keyFunction, PipeFunction<List, RecommReason> valueFunction) {
		super(byMap, keyFunction, valueFunction);
	}

	public GroupbyReasonPipe(int maxfriends, Map<Long, List<RecommReason>> byMap,
			PipeFunction<List, Long> keyFunction, PipeFunction<List, RecommReason> valueFunction) {
		super(byMap, keyFunction, valueFunction);
		this.maxfriends = maxfriends;
	}

	@Override
	protected List processNextStart() {

		while (true) {
			List s = this.starts.next();

			if (count < maxfriends * 3) {
				Long key = getKey(s);
				RecommReason value = getValue(s);

				if (value == null)
					continue;

				List<RecommReason> list = this.byMap.get(key);
				if (null == list) {
					if (this.byMap.size() < maxfriends) {
						list = new ArrayList<RecommReason>();
						this.byMap.put(key, list);
						addValue(value, list);
					}
				} else {
					for (RecommReason reason : list) {
						if (reason.getReason() != 1) {
							addValue(value, list);
						} else if (reason.getReason() == value.getReason()) {
							reason.setValue(reason.getValue() + 1);
							Set<String> additions = reason.getAdditions();
							if (additions.size() < 3)
								additions.addAll(value.getAdditions());
							reason.setAdditions(additions);
						}
					}
				}
				count++;
				return s;
			}
		}
	}

	public int getMaxfriends() {
		return maxfriends;
	}

	public void setMaxfriends(int maxfriends) {
		this.maxfriends = maxfriends;
	}

}
