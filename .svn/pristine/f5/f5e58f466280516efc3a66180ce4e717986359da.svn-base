package com.duowan.yy.titan.cloud.pipe;

import java.util.Arrays;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.pipes.util.PipeHelper;

/**
 * BothPipe will emit those vertices adjacent to the incoming and outgoing edges
 * of the incoming vertex.
 * 
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public class BothPipe extends VerticesVerticesPipe {

	public BothPipe(final String... labels) {
		super(Direction.BOTH, labels);
	}

	public BothPipe(final int branchFactor, final String... labels) {
		super(Direction.BOTH, branchFactor, labels);
	}

	public String toString() {
		return (this.branchFactor == Integer.MAX_VALUE) ?
				PipeHelper.makePipeString(this, Arrays.asList(this.labels)) :
				PipeHelper.makePipeString(this, this.branchFactor, Arrays.asList(this.labels));
	}
}
