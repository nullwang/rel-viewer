package com.b.x;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.Set;

import javax.swing.Icon;

import com.b.g.Generator;

import g.algorithms.layout.Layout;
import g.visualization.Layer;
import g.visualization.RenderContext;
import g.visualization.layout.ObservableCachingLayout;
import g.visualization.renderers.BasicVertexRenderer;
import g.visualization.transform.MutableTransformer;
import g.visualization.transform.shape.GraphicsDecorator;

public class RelVertexRenderer<V,E> extends BasicVertexRenderer<V, E> {
	public Set getEmphasisedVertex() {
		return Generator.getEmphasisedEnds();
	}
	
	public Boolean getEmphasised() {
		return Generator.getEmphasised();
	}
	
	@Override
	protected void paintShapeForVertex(RenderContext<V,E> rc, V v, Shape shape) {
		
		super.paintShapeForVertex(rc, v, shape);
	}
	
	@Override
	protected void paintIconForVertex(RenderContext<V,E> rc, V v, Layout<V,E> layout)
	{
			GraphicsDecorator g = rc.getGraphicsContext();
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
            
            super_paintIconForVertex(rc, v, layout);
            
            g.setComposite(oldComposite);
	}
	
	 /**
     * Paint <code>v</code>'s icon on <code>g</code> at <code>(x,y)</code>.
     */
    protected void super_paintIconForVertex(RenderContext<V,E> rc, V v, Layout<V,E> layout) {
        GraphicsDecorator g = rc.getGraphicsContext();
        boolean vertexHit = true;
        // get the shape to be rendered
        Shape shape = rc.getVertexShapeTransformer().transform(v);
        
        Point2D p = layout.transform(v);
        p = rc.getMultiLayerTransformer().transform(Layer.LAYOUT, p);
        float x = (float)p.getX();
        float y = (float)p.getY();
        // create a transform that translates to the location of
        // the vertex to be rendered
        AffineTransform xform = AffineTransform.getTranslateInstance(x,y);
        // transform the vertex shape with xtransform
        shape = xform.createTransformedShape(shape);
        
        vertexHit = vertexHit(rc, shape);
            //rc.getViewTransformer().transform(shape).intersects(deviceRectangle);

        if (vertexHit) {
        	if(rc.getVertexIconTransformer() != null) {
        		Icon icon = rc.getVertexIconTransformer().transform(v);
        		if(icon != null) {
        		 if( this.getEmphasisedVertex().contains(v) && this.getEmphasised()){
        			 MutableTransformer viewTransformer = rc.getMultiLayerTransformer().getTransformer(Layer.VIEW);
        				double scalex = 1/viewTransformer.getScaleX();
        				double scaley = 1/viewTransformer.getScaleY();
        				AffineTransform af = AffineTransform.getScaleInstance(scalex,scaley);
        				AffineTransform oldXform = g.getTransform();
        		        AffineTransform newXform = new AffineTransform(oldXform);
        		        newXform.concatenate(xform);
        		        newXform.concatenate(af);
        		        g.setTransform(newXform);        		       
        		        
        		        //g.draw(icon, rc.getScreenDevice(), shape, (int)(x), (int)(y));
        		        g.draw(icon, rc.getScreenDevice(), shape, 0, 0);
        		    	g.setTransform(oldXform);
        		 }
        		 else {
        			 g.draw(icon, rc.getScreenDevice(), shape, (int)x, (int)y);
        		 }

        		} else {
        			paintShapeForVertex(rc, v, shape);
        		}
        	} else {
        		paintShapeForVertex(rc, v, shape);
        	}
        }
    }
	
}
