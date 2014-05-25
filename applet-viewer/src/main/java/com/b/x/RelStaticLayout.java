package com.b.x;

import java.awt.Dimension;
import java.awt.geom.Point2D;

import o.c15.Transformer;


import g.algorithms.layout.AbstractLayout;
import g.graph.Graph;

public class RelStaticLayout<V, E> extends AbstractLayout<V,E> {

	public RelStaticLayout(Graph<V, E> graph) {
		super(graph);
	}

	public RelStaticLayout(Graph<V, E> graph, Dimension size) {
		super(graph, size);
	}

	public RelStaticLayout(Graph<V, E> graph,
			Transformer<V, Point2D> initializer, Dimension size) {
		super(graph, initializer, size);
	}

	public RelStaticLayout(Graph<V, E> graph,
			Transformer<V, Point2D> initializer) {
		super(graph, initializer);
	}
	
	@Override
	public void setLocation(V v, Point2D location)
	{
		if( ! locations.containsKey(v))
		{
			this.locations.put(v, location);
		}
		else {
			Point2D coord = transform(v);
			if( coord != null)
			coord.setLocation(location);
		}		
	}

	public void initialize() {
		
	}

	public void reset() {
		
	}

}
