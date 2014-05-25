package com.b.x;

import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import o.c15.Transformer;
import o.c15.map.LazyMap;


import g.algorithms.layout.PolarPoint;
import g.graph.Forest;
import g.graph.util.TreeUtils;

public class RelBallonLayout<V, E> extends HierarchyLayout<V, E> {

	public RelBallonLayout(Forest<V, E> g, int len) {
		super(g, len);
	}
	
	 protected Map<V,PolarPoint> polarLocations =
	    	LazyMap.decorate(new HashMap<V, PolarPoint>(),
	    			new Transformer<V,PolarPoint>() {
						public PolarPoint transform(V arg0) {
							return new PolarPoint();
						}});
	    
	    protected Map<V,Double> radii = new HashMap<V,Double>();
	    
	    protected void setRootPolars() 
	    {
	        List<V> roots = TreeUtils.getRoots(graph);
	        if(roots.size() == 1) {
	    		// its a Tree
	    		V root = roots.get(0);
	    		setRootPolar(root);
	            setPolars(new ArrayList<V>(graph.getChildren(root)),
	                    getCenter(), getSize().width/2);
	    	} else if (roots.size() > 1) {
	    		// its a Forest
	    		setPolars(roots, getCenter(), getSize().width/2);
	    	}
	    }
	    
	    protected void setRootPolar(V root) {
	    	PolarPoint pp = new PolarPoint(0,0);
	    	Point2D p = getCenter();
	    	polarLocations.put(root, pp);
	    	locations.put(root, p);
	    }
	    

	    protected void setPolars(List<V> kids, Point2D parentLocation, double parentRadius) {

	    	int childCount = kids.size();
	    	if(childCount == 0) return;
	    	// handle the 1-child case with 0 limit on angle.
	    	double angle = Math.max(0, Math.PI / 2 * (1 - 2.0/childCount));
	    	double childRadius = parentRadius*Math.cos(angle) / (1 + Math.cos(angle));
	    	double radius = parentRadius - childRadius;

	    	double rand = Math.random();

	    	for(int i=0; i< childCount; i++) {
	    		V child = kids.get(i);
	    		double theta = i* 2*Math.PI/childCount + rand;
	    		radii.put(child, childRadius);
	    		
	    		PolarPoint pp = new PolarPoint(theta, radius);
	    		polarLocations.put(child, pp);
	    		
	    		Point2D p = PolarPoint.polarToCartesian(pp);
	    		p.setLocation(p.getX()+parentLocation.getX(), p.getY()+parentLocation.getY());
	    		locations.put(child, p);
	    		setPolars(new ArrayList<V>(graph.getChildren(child)), p, childRadius);
	    	}
	    }

	    @Override
	    public void setSize(Dimension size) {
	    	this.size = size;
	    	setRootPolars();
	    }

		/**
		 * Returns the coordinates of {@code v}'s parent, or the
		 * center of this layout's area if it's a root.
		 */
		public Point2D getCenter(V v) {
			V parent = graph.getParent(v);
			if(parent == null) {
				return getCenter();
			}
			return locations.get(parent);
		}

		@Override
	    public void setLocation(V v, Point2D location) {
			Point2D c = getCenter(v);
			Point2D pv = new Point2D.Double(location.getX()-c.getX(),location.getY()-c.getY());
			PolarPoint newLocation = PolarPoint.cartesianToPolar(pv);
			polarLocations.get(v).setLocation(newLocation);
			
			Point2D center = getCenter(v);
			pv.setLocation(pv.getX()+center.getX(), pv.getY()+center.getY());
			locations.put(v, pv);
		}

		@Override
	    public Point2D transform(V v) {
			return locations.get(v);
		}

		/**
		 * @return the radii
		 */
		public Map<V, Double> getRadii() {
			return radii;
		}

}
