package com.b.x;

import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.Map;

import g.algorithms.layout.PolarPoint;
import g.graph.Forest;

public class RelRadialTreeLayout<V,E> extends RelTreeLayout<V, E> {
	
	 protected Map<V,PolarPoint> polarLocations;

	public RelRadialTreeLayout(Forest<V, E> g, int distx) {
		super(g, distx, distx);
	}
	
	@Override
    protected void buildTree() {
		this.size = new  Dimension(10,10);
	    super.buildTree();
	    this.polarLocations = new HashMap<V, PolarPoint>();
        setRadialLocations();
    }

    @Override
    public void setSize(Dimension size) {
    	this.size = size;
        buildTree();
    }

//    @Override
//    protected void setCurrentPositionFor(V vertex) {
//    	locations.get(vertex).setLocation(m_currentPoint);
//    }

	@Override
    public void setLocation(V v, Point2D location)
    {
        Point2D c = getCenter();
        Point2D pv = new Point2D.Double(location.getX() - c.getX(), 
                location.getY() - c.getY());
        PolarPoint newLocation = PolarPoint.cartesianToPolar(pv);
        PolarPoint currentLocation = polarLocations.get(v);
        if (currentLocation == null)
        	polarLocations.put(v, newLocation);
        else
        	currentLocation.setLocation(newLocation);
     }
	
	/**
	 * Returns the map from vertices to their locations in polar coordinates.
	 */
	public Map<V,PolarPoint> getPolarLocations() {
		return polarLocations;
	}

	@Override
    public Point2D transform(V v) {
		PolarPoint pp = polarLocations.get(v);
		
		if( pp == null) return null;
		
		double centerX = getSize().getWidth()/2;
		double centerY = getSize().getHeight()/2;
		//double centerX = 0, centerY = 0;
		Point2D cartesian = PolarPoint.polarToCartesian(pp);
		cartesian.setLocation(cartesian.getX()+centerX,cartesian.getY()+centerY);
		return cartesian;
	}
	
	private Point2D getMaxXY() {
		double maxx = 0;
		double maxy = 0;
		for(Point2D p : locations.values()) {
			maxx = Math.max(maxx, p.getX());
			maxy = Math.max(maxy, p.getY());
		}
		return new Point2D.Double(maxx,maxy);
	}
	
	private void setRadialLocations() {
		Point2D max = getMaxXY();
		double maxx = max.getX();
		//double maxy = max.getY();
		maxx = Math.max(maxx, size.width);
		double theta = 2*Math.PI/(maxx+this.distX);

		double snap = Math.sqrt(this.distX * this.distX / Math.PI);
		
		//double deltaRadius = size.width/2/maxy;//distX; 
		for(Map.Entry<V, Point2D> entry : locations.entrySet()) {
			V v = entry.getKey();
			Point2D p = entry.getValue();
			PolarPoint polarPoint = new PolarPoint(p.getX()*theta, p.getY() + snap);//-this.distX));//*deltaRadius);
			polarLocations.put(v, polarPoint);
		}
	}

}
