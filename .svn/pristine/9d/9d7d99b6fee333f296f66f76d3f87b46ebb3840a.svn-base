package com.duowan.yy.titan.cloud.pipe;

import com.tinkerpop.pipes.AbstractPipe;
import com.tinkerpop.pipes.filter.FilterPipe;

import java.util.HashSet;
import java.util.NoSuchElementException;
/**
 * 
 * @author WangHongfei.Nelson
 *2013-9-17
 * @param <S>
 */
public class DuplicateFilterLimitPipe<S> extends AbstractPipe<S, S> implements FilterPipe<S>{

	private final HashSet historySet = new HashSet();
	private final int limit;
	
	public DuplicateFilterLimitPipe() {
		this.limit=28;
    }

    public DuplicateFilterLimitPipe(final int limit) {
        this.limit=limit;
    }
	@Override
	protected S processNextStart() throws NoSuchElementException {
		while (true) {
            final S s = this.starts.next();
            if (!this.historySet.contains(s)&&this.historySet.size()<limit) {
                this.historySet.add(s);
                return s;
            }
        }
	}
	
	public void reset() {
        this.historySet.clear();
        super.reset();
    }

}
