package com.b.x;

import java.awt.AlphaComposite;
import java.awt.Component;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Map;

import o.c15.Transformer;


import com.b.g.Generator;

import g.algorithms.layout.Layout;
import g.graph.Graph;
import g.graph.util.Context;
import g.visualization.Layer;
import g.visualization.RenderContext;
import g.visualization.decorators.VertexIconShapeTransformer;
import g.visualization.layout.ObservableCachingLayout;
import g.visualization.renderers.Renderer;
import g.visualization.renderers.VertexLabelRenderer;
import g.visualization.transform.BidirectionalTransformer;
import g.visualization.transform.shape.GraphicsDecorator;
import g.visualization.transform.shape.ShapeTransformer;
import g.visualization.transform.shape.TransformingGraphics;

public class RelVertexIconLabelShapeTransformer<V,E> extends VertexIconShapeTransformer<V> implements Renderer.VertexLabel<V,E> {
	protected RenderContext<V,E> rc;
	protected Map<V,Shape> shapes = new HashMap<V,Shape>();
	protected Map<V,Point> anchors = new HashMap<V,Point>();
	protected Map<V,Point2D> centers = new HashMap<V,Point2D>();
	
	public RelVertexIconLabelShapeTransformer(RenderContext<V,E> rc,Transformer<V, Shape> delegate) {
		super(delegate);
		this.rc = rc;		
	}
	
	public Component prepareRenderer(RenderContext<V,E> rc, VertexLabelRenderer graphLabelRenderer, Object value, 
			boolean isSelected, V vertex) {
		return rc.getVertexLabelRenderer().<V>getVertexLabelRendererComponent(rc.getScreenDevice(), value, 
				rc.getVertexFontTransformer().transform(vertex), isSelected, vertex);
	}
	
	@Override
	public Shape transform(V v) {
		Shape s = super.transform(v);
		
		Component component = prepareRenderer(rc, rc.getVertexLabelRenderer(), rc.getVertexLabelTransformer().transform(v),
				rc.getPickedVertexState().isPicked(v), v);
		
        Dimension size = component.getPreferredSize();
        
        GeneralPath gp = new GeneralPath(s);
        //Rectangle bounds = new Rectangle(-size.width/2 -2, -size.height/2 -2, size.width+4, size.height);
        
       Point p = anchors.get(v);
       Point2D pc = centers.get(v);
       if( p != null && pc != null){  
           
        Rectangle bounds = new Rectangle((int)(p.x-pc.getX()),(int)(p.y-pc.getY()), size.width, size.height);
        gp.append(bounds, true);
       }
		
	return gp;
	}
	
	protected Position position = Position.SE;
	private Positioner positioner = new OutsidePositioner();	
	
	/**
	 * @return the position
	 */
	public Position getPosition() {
		return position;
	}

	/**
	 * @param position the position to set
	 */
	public void setPosition(Position position) {
		this.position = position;
	}


	/**
	 * Labels the specified vertex with the specified label.  
	 * Uses the font specified by this instance's 
	 * <code>VertexFontFunction</code>.  (If the font is unspecified, the existing
	 * font for the graphics context is used.)  If vertex label centering
	 * is active, the label is centered on the position of the vertex; otherwise
     * the label is offset slightly.
     */
    public void labelVertex(RenderContext<V,E> rc, Layout<V,E> layout, V v, String label) {
    	Graph<V,E> graph = layout.getGraph();
        if (rc.getVertexIncludePredicate().evaluate(Context.<Graph<V,E>,V>getInstance(graph,v)) == false) {
        	return;
        }
        Point2D pt = layout.transform(v);
        pt = rc.getMultiLayerTransformer().transform(Layer.LAYOUT, pt);

        centers.put(v, pt);
        
        float x = (float) pt.getX();
        float y = (float) pt.getY();

        Component component = prepareRenderer(rc, rc.getVertexLabelRenderer(), label,
        		rc.getPickedVertexState().isPicked(v), v);
        GraphicsDecorator g = rc.getGraphicsContext();
        Dimension d = component.getPreferredSize();
        AffineTransform xform = AffineTransform.getTranslateInstance(x, y);
        
    	//Shape shape = rc.getVertexShapeTransformer().transform(v);
        //user icon shape
        Shape shape = super.transform(v);
        
    	shape = xform.createTransformedShape(shape);
    	if(rc.getGraphicsContext() instanceof TransformingGraphics) {
    		BidirectionalTransformer transformer = ((TransformingGraphics)rc.getGraphicsContext()).getTransformer();
    		if(transformer instanceof ShapeTransformer) {
    			ShapeTransformer shapeTransformer = (ShapeTransformer)transformer;
    			shape = shapeTransformer.transform(shape);
    		}
    	}
    	Rectangle2D bounds = shape.getBounds2D();

    	Point p = null;
    	if(position == Position.AUTO) {
    		Dimension vvd = rc.getScreenDevice().getSize();
    		if(vvd.width == 0 || vvd.height == 0) {
    			vvd = rc.getScreenDevice().getPreferredSize();
    		}
    		p = getAnchorPoint(bounds, d, positioner.getPosition(x, y, vvd));
    	} else {
    		p = getAnchorPoint(bounds, d, position);
    	}
    	
    	anchors.put(v, p);
    	
    	Layout relLayout= layout;
        TimeSeqLayout tsl = null;
        
        if( layout instanceof ObservableCachingLayout){
        	ObservableCachingLayout ol = (ObservableCachingLayout) layout;
        	relLayout = ol.getDelegate();        	
        }
        //time layout
        if( relLayout instanceof TimeSeqLayout ){
        	tsl = (TimeSeqLayout) relLayout;     
        }
        
        Composite oldComposite = g.getComposite();
        float alphaFactor = Generator.getAlphaFactor();
        Composite ac1 = AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
        		alphaFactor);
        if(tsl != null && (tsl.isFadedItem(v) ) )
        		g.setComposite(ac1);
        
    	g.draw(component, rc.getRendererPane(), p.x, p.y, d.width, d.height, true);
    	
    	g.setComposite(oldComposite);
    }
    
    protected Point getAnchorPoint(Rectangle2D vertexBounds, Dimension labelSize, Position position) {
    	double x;
    	double y;
    	int offset = 5;
    	switch(position) {
    	
    	case N:
    		x = vertexBounds.getCenterX()-labelSize.width/2;
    		y = vertexBounds.getMinY()-offset - labelSize.height;
    		return new Point((int)x,(int)y);

    	case NE:
    		x = vertexBounds.getMaxX()+offset;
    		y = vertexBounds.getMinY()-offset-labelSize.height;
    		return new Point((int)x,(int)y);

    	case E:
    		x = vertexBounds.getMaxX()+offset;
    		y = vertexBounds.getCenterY()-labelSize.height/2;
    		return new Point((int)x,(int)y);

    	case SE:
    		x = vertexBounds.getMaxX()+offset;
    		y = vertexBounds.getMaxY()+offset;
    		return new Point((int)x,(int)y);

    	case S:
    		x = vertexBounds.getCenterX()-labelSize.width/2;
    		y = vertexBounds.getMaxY()+offset;
    		return new Point((int)x,(int)y);

    	case SW:
    		x = vertexBounds.getMinX()-offset-labelSize.width;
    		y = vertexBounds.getMaxY()+offset;
    		return new Point((int)x,(int)y);

    	case W:
    		x = vertexBounds.getMinX()-offset-labelSize.width;
    		y = vertexBounds.getCenterY()-labelSize.height/2;
    		return new Point((int)x,(int)y);

    	case NW:
    		x = vertexBounds.getMinX()-offset-labelSize.width;
    		y = vertexBounds.getMinY()-offset-labelSize.height;
    		return new Point((int)x,(int)y);

    	case CNTR:
    		x = vertexBounds.getCenterX()-labelSize.width/2;
    		y = vertexBounds.getCenterY()-labelSize.height/2;
    		return new Point((int)x,(int)y);

    	default:
    		return new Point();
    	}
    	
    }
    public static class InsidePositioner implements Positioner {
    	public Position getPosition(float x, float y, Dimension d) {
    		int cx = d.width/2;
    		int cy = d.height/2;
    		if(x > cx && y > cy) return Position.NW;
    		if(x > cx && y < cy) return Position.SW;
    		if(x < cx && y > cy) return Position.NE;
    		return Position.SE;
    	}
    }
    public static class OutsidePositioner implements Positioner {
    	public Position getPosition(float x, float y, Dimension d) {
    		int cx = d.width/2;
    		int cy = d.height/2;
    		if(x > cx && y > cy) return Position.SE;
    		if(x > cx && y < cy) return Position.NE;
    		if(x < cx && y > cy) return Position.SW;
    		return Position.NW;
    	}
    }
	/**
	 * @return the positioner
	 */
	public Positioner getPositioner() {
		return positioner;
	}

	/**
	 * @param positioner the positioner to set
	 */
	public void setPositioner(Positioner positioner) {
		this.positioner = positioner;
	}
	
}
