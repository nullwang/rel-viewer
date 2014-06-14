/*
 * Copyright (c) 2005, the JUNG Project and the Regents of the University of
 * California All rights reserved.
 *
 * This software is open-source under the BSD license; see either "license.txt"
 * or http://jung.sourceforge.net/license.txt for a description.
 *
 * Created on Aug 23, 2005
 */

package g.visualization.layout;

import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.Map;

import javax.swing.event.ChangeListener;

import o.c15.Transformer;
import o.c15.functors.ChainedTransformer;
import o.c15.functors.CloneTransformer;
import o.c15.map.LazyMap;


import g.algorithms.layout.Layout;
import g.algorithms.layout.LayoutDecorator;
import g.algorithms.util.IterativeContext;
import g.graph.Graph;
import g.visualization.util.Caching;
import g.visualization.util.ChangeEventSupport;
import g.visualization.util.DefaultChangeEventSupport;

/**
 * A LayoutDecorator that fires ChangeEvents when certain methods 
 * are called. Used to wrap a Layout so that the visualization
 * components can be notified of changes.
 * 
 * @see LayoutDecorator
 * @author Tom Nelson 
 *
 */
public class ObservableCachingLayout<V, E> extends LayoutDecorator<V,E> implements ChangeEventSupport, Caching {
    
    protected ChangeEventSupport changeSupport =
        new DefaultChangeEventSupport(this);
    
    protected Map<V,Point2D> locationMap;

    public ObservableCachingLayout(Layout<V, E> delegate) {
    	super(delegate);
    	this.locationMap = LazyMap.<V,Point2D>decorate(new HashMap<V,Point2D>(), 
    			new ChainedTransformer<V, Point2D>(new Transformer[]{delegate, CloneTransformer.<Point2D>getInstance()}));
    }
    
    /**
     * @see g.algorithms.layout.Layout#step()
     */
    @Override
    public void step() {
    	super.step();
    	fireStateChanged();
    }

    /**
	 * 
	 * @see g.algorithms.layout.Layout#initialize()
	 */
	@Override
    public void initialize() {
		super.initialize();
		fireStateChanged();
	}
	
    /**
     * @see g.algorithms.util.IterativeContext#done()
     */
    @Override
    public boolean done() {
    	if(delegate instanceof IterativeContext) {
    		return ((IterativeContext)delegate).done();
    	}
    	return true;
    }


	/**
	 * @param v
	 * @param location
	 * @see g.algorithms.layout.Layout#setLocation(java.lang.Object, java.awt.geom.Point2D)
	 */
	@Override
    public void setLocation(V v, Point2D location) {
		super.setLocation(v, location);
		fireStateChanged();
	}

    public void addChangeListener(ChangeListener l) {
        changeSupport.addChangeListener(l);
    }

    public void removeChangeListener(ChangeListener l) {
        changeSupport.removeChangeListener(l);
    }

    public ChangeListener[] getChangeListeners() {
        return changeSupport.getChangeListeners();
    }

    public void fireStateChanged() {
        changeSupport.fireStateChanged();
    }
    
    @Override
    public void setGraph(Graph<V, E> graph) {
        delegate.setGraph(graph);
    }

	public void clear() {
		this.locationMap.clear();
	}

	public void init() {
	}

	/* (non-Javadoc)
	 * @see edu.uci.ics.jung.visualization.layout.LayoutDecorator#transform(java.lang.Object)
	 */
	@Override
	public Point2D transform(V v) {
		return locationMap.get(v);
	}
}
