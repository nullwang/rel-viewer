package com.b.x;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ConcurrentModificationException;

import com.b.g.Generator;
import com.b.u.GraphicsUtility;

import g.algorithms.layout.Layout;
import g.graph.Graph;
import g.graph.util.Context;
import g.graph.util.Pair;
import g.visualization.Layer;
import g.visualization.VisualizationServer;
import g.visualization.layout.ObservableCachingLayout;
import g.visualization.picking.ShapePickSupport;

public class RelShapePickSupport<V, E> extends ShapePickSupport<V, E> {

	public RelShapePickSupport(VisualizationServer<V, E> vv) {
		super(vv);
	}

	public RelShapePickSupport(VisualizationServer<V, E> vv, float pickSize) {
		super(vv, pickSize);
	}
	
	@Override
	 public V getVertex(Layout<V, E> layout, double x, double y) {

	        V closest = null;
	        double minDistance = Double.MAX_VALUE;
	        Point2D ip = vv.getRenderContext().getMultiLayerTransformer().inverseTransform(Layer.VIEW, 
	        		new Point2D.Double(x,y));
	        x = ip.getX();
	        y = ip.getY();

	        while(true) {
	            try {
	                for(V v : getFilteredVertices(layout)) {
	                	
	                    Shape shape = vv.getRenderContext().getVertexShapeTransformer().transform(v);
	                   
	                    // get the vertex location
	                    Point2D p = layout.transform(v);
	                    if(p == null) continue;
	                    // transform the vertex location to screen coords
	                    p = vv.getRenderContext().getMultiLayerTransformer().transform(Layer.LAYOUT, p);
	                    
	                    double ox = x - p.getX();
	                    double oy = y - p.getY();

	                    boolean isInLabel = false;		                  
	                    if( vv instanceof RelVisualizationViewer){
	                    	RelVisualizationViewer<V,E> rv = (RelVisualizationViewer<V,E>) vv;
	                    
	                    	Shape labelShape = rv.getVertexLabelShapeTransformer().transform(v);
	                    	if ( labelShape!=null && labelShape.contains(ox,oy)) isInLabel = true;
	                    }
	                    

	                    // Shape vertexShape = Generator.getTransformedVertexShape(vv.getRenderContext(), v, 0, 0);
	                    // if( vertexShape.contains(ox,oy) ){
	                    
	                    if(isInLabel || shape.getBounds().contains(ox, oy))
	                    {
	                    	
	                    	if(style == Style.LOWEST) {
	                    		// return the first match
	                    		return v;
	                    	} else if(style == Style.HIGHEST) {
	                    		// will return the last match
	                    		closest = v;
	                    	} else {
	                    		
	                    		// return the vertex closest to the
	                    		// center of a vertex shape
		                        Rectangle2D bounds = shape.getBounds2D();
		                        double dx = bounds.getCenterX() - ox;
		                        double dy = bounds.getCenterY() - oy;
		                        double dist = dx * dx + dy * dy;
		                        if (dist < minDistance) {
		                        	minDistance = dist;
		                        	closest = v;
		                        }
	                    	}
	                    }
	                }
	                break;
	            } catch(ConcurrentModificationException cme) {}
	        }
	        return closest;
	    }
	
	/**
     * Returns an edge whose shape intersects the 'pickArea' footprint of the passed
     * x,y, coordinates.
     */
    public E getEdge(Layout<V, E> layout, double x, double y) {

        Point2D ip = vv.getRenderContext().getMultiLayerTransformer().inverseTransform(Layer.VIEW, new Point2D.Double(x,y));
        x = ip.getX();
        y = ip.getY();

        // as a Line has no area, we can't always use edgeshape.contains(point) so we
        // make a small rectangular pickArea around the point and check if the
        // edgeshape.intersects(pickArea)
        Rectangle2D pickArea = 
            new Rectangle2D.Float((float)x-pickSize/2,(float)y-pickSize/2,pickSize,pickSize);
        E closest = null;
        double minDistance = Double.MAX_VALUE;
        while(true) {
            try {
                for(E e : getFilteredEdges(layout)) {

                    Shape edgeShape = getTransformedEdgeShape(layout, e);
                    if (edgeShape == null)
                    	continue;                            
                    
                    boolean isInLabel = false;		                  
                    if( vv instanceof RelVisualizationViewer){
                    	RelVisualizationViewer<V,E> rv = (RelVisualizationViewer<V,E>) vv;
                    	//Point2D layoutPoint = vv.getRenderContext().getMultiLayerTransformer().inverseTransform(Layer.LAYOUT,new Point2D.Double(x,y));
                    	                    	
                    	Shape labelShape = rv.getEdgeLabelShapeTransformer().transform(e);
                    	if ( labelShape!= null) isInLabel = labelShape.contains(x,y);
                    }                    
                    
                    // because of the transform, the edgeShape is now a GeneralPath
                    // see if this edge is the closest of any that intersect
                    if(isInLabel || edgeShape.intersects(pickArea) ) {
                        float cx=0;
                        float cy=0;
                        float[] f = new float[6];
                        PathIterator pi = new GeneralPath(edgeShape).getPathIterator(null);
                        if(pi.isDone()==false) {
                            pi.next();
                            pi.currentSegment(f);
                            cx = f[0];
                            cy = f[1];
                            if(pi.isDone()==false) {
                                pi.currentSegment(f);
                                cx = f[0];
                                cy = f[1];
                            }
                        }
                        float dx = (float) (cx - x);
                        float dy = (float) (cy - y);
                        float dist = dx * dx + dy * dy;
                        if (dist <= minDistance) {
                            minDistance = dist;
                            closest = e;
                        }
                    }
		        }
		        break;
		    } catch(ConcurrentModificationException cme) {}
		}
		return closest;
    }

    /**
     * Retrieves the shape template for <code>e</code> and
     * transforms it according to the positions of its endpoints
     * in <code>layout</code>.
     * @param layout the <code>Layout</code> which specifies
     * <code>e</code>'s endpoints' positions
     * @param e the edge whose shape is to be returned
     * @return
     */
	protected Shape getTransformedEdgeShape(Layout<V, E> layout, E e) {
		Pair<V> pair = layout.getGraph().getEndpoints(e);
		V v1 = pair.getFirst();
		V v2 = pair.getSecond();
		boolean isLoop = v1.equals(v2);
		Point2D p1 = vv.getRenderContext().getMultiLayerTransformer().transform(Layer.LAYOUT, layout.transform(v1));
		Point2D p2 = vv.getRenderContext().getMultiLayerTransformer().transform(Layer.LAYOUT, layout.transform(v2));
        if(p1 == null || p2 == null) 
        	return null;
        
        Layout<V, E> relLayout = layout;
		TimeSeqLayout<V, E> tsl = null;

		if (layout instanceof ObservableCachingLayout) {
			ObservableCachingLayout<V, E> ol = (ObservableCachingLayout<V, E>) layout;
			relLayout = ol.getDelegate();
		}

		// time layout
		if (relLayout instanceof TimeSeqLayout) {
			tsl = (TimeSeqLayout) relLayout;
		}
		
		float x1 = (float) p1.getX();
		float y1 = (float) p1.getY();
		float x2 = (float) p2.getX();
		float y2 = (float) p2.getY();
		
		Shape s1 = Generator.getTransformedVertexShape(vv.getRenderContext(),v1, x1, y1);
		Shape s2 = Generator.getTransformedVertexShape(vv.getRenderContext(),v2, x2, y2);
		
		if (tsl != null
				&& (tsl.isThemedItem(v1) && tsl.isControllingItem(v2) || (tsl
						.isControllingItem(v1) && tsl.isThemedItem(v2)))) {
			if (tsl.isThemedItem(v1)) {
				x1 = x2;
				AffineTransform at = AffineTransform.getTranslateInstance(x1,
						y1);
				s1 = at.createTransformedShape(Generator.getInteractionShape());
			} else {
				x2 = x1;
				AffineTransform at = AffineTransform.getTranslateInstance(x2,
						y2);
				s2 = at.createTransformedShape(Generator.getInteractionShape());
			}
		}

		Line2D centerLine = new Line2D.Double(x1, y1, x2, y2);

		// calc interaction point
		Point2D ps = GraphicsUtility.getPointOnBoundary(centerLine, s1);
		Point2D pe = GraphicsUtility.getPointOnBoundary(centerLine, s2);
		if (ps != null) {
			x1 = (float) ps.getX();
			y1 = (float) ps.getY();
		}
		if (pe != null) {
			x2 = (float) pe.getX();
			y2 = (float) pe.getY();
		}

		// translate the edge to the starting vertex
		AffineTransform xform = AffineTransform.getTranslateInstance(x1, y1);

		Shape edgeShape = 
			vv.getRenderContext().getEdgeShapeTransformer().transform(Context.<Graph<V,E>,E>getInstance(vv.getGraphLayout().getGraph(),e));
		if(isLoop) {
		    // make the loops proportional to the size of the vertex
		    Shape s = vv.getRenderContext().getVertexShapeTransformer().transform(v2);
		    Rectangle2D sBounds = s.getBounds2D();
		    xform.scale(sBounds.getWidth(),sBounds.getHeight());
		    // move the loop so that the nadir is centered in the vertex
		    xform.translate(0, -edgeShape.getBounds2D().getHeight()/2);
		}  else {
		    float dx = x2 - x1;
		    float dy = y2 - y1;
		    // rotate the edge to the angle between the vertices
		    double theta = Math.atan2(dy,dx);
		    xform.rotate(theta);
		    // stretch the edge to span the distance between the vertices
		    float dist = (float) Math.sqrt(dx*dx + dy*dy);
		    xform.scale(dist, 1.0f);
		}

		// transform the edge to its location and dimensions
		edgeShape = xform.createTransformedShape(edgeShape);
		return edgeShape;
	}

}
