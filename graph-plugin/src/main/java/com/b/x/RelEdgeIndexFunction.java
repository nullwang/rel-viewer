package com.b.x;

import g.graph.Graph;
import g.graph.util.EdgeIndexFunction;

public interface RelEdgeIndexFunction<V,E>  extends EdgeIndexFunction<V,E>{

	int getCommonEdgeNumber(Graph<V,E> graph, E e);
}
