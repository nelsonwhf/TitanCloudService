package com.duowan.yy.titan.cloud.pipe.function;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.duowan.yy.titan.cloud.constant.RelationEnum;
import com.duowan.yy.titan.cloud.constant.VertexEnum;
import com.duowan.yy.titan.cloud.service.vo.RecommReason;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.pipes.util.PipesFunction;

public class RecommValueFunction extends PipesFunction<List, RecommReason> {

	@Override
	public RecommReason compute(List path) {
		Edge endEdge = (Edge) path.get(path.size() - 2);
		Vertex middle = (Vertex) path.get(path.size() - 3);
		Set<String> addition = new HashSet<String>();
		if (endEdge.getLabel().equals(
				RelationEnum.GAME_BINDING.getTypeName())) {
			addition.add(middle.getProperty(VertexEnum.GAME_REGION.getTypeName()).toString());
			return new RecommReason().newGameBindinglReason(1, addition);
		} else if (endEdge.getLabel().equals(
				RelationEnum.CHANNEL_ROLE_LEVEL.getTypeName())) {
			addition.add(middle.getProperty(VertexEnum.CHANNEL_TID.getTypeName()).toString());
			return new RecommReason().newChannelLevelReason(1, addition);
		} else if (endEdge.getLabel().equals(
				RelationEnum.USER_FOLLOW.getTypeName())) {
			return new RecommReason().USER_FOLLOW_REASON;
		}
		return null;
	}

}
