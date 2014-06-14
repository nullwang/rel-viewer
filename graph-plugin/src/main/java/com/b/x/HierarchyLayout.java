package com.b.x;

import java.awt.Dimension;

import g.graph.Forest;

public class HierarchyLayout<V, E> extends RelTreeLayout<V, E> {
		
	public HierarchyLayout(Forest<V,E> g, int len)
	{		
		super(g,len,len);
	}

	public void setSize(Dimension size) {
	       this.size = size;
	       buildTree();
	}
	
	@Override
	protected void buildTree() {
		this.size = new  Dimension(10,10);
		super.buildTree();
	}

}
