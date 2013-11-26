package com.duowan.yy.titan.cloud.pipe.function;

import java.util.List;

import com.duowan.yy.titan.cloud.constant.VertexEnum;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.pipes.util.PipesFunction;

public class RecommKeyFunction extends PipesFunction<List, Long> {

	@Override
	public Long compute(List path) {
		Vertex end = (Vertex) path.get(path.size() - 1);
		Long friendId = end.getProperty(VertexEnum.USER_UID.getTypeName());
		return friendId;
	}

}
