package com.b.x;

import java.awt.AlphaComposite;
import java.awt.Component;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Shape;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import o.c15.Transformer;


import com.b.g.Generator;
import com.b.u.GraphicsUtility;

import g.algorithms.layout.Layout;
import g.graph.Graph;
import g.graph.util.Context;
import g.graph.util.Pair;
import g.visualization.Layer;
import g.visualization.RenderContext;
import g.visualization.layout.ObservableCachingLayout;
import g.visualization.renderers.EdgeLabelRenderer;
import g.visualization.renderers.Renderer;
import g.visualization.transform.shape.GraphicsDecorator;

public class RelEdgeLabelShapeTransformer<V, E> implements Renderer.EdgeLabel<V,E>,Transformer<E,Shape>{
	Map<E,Shape> shapeMap = new HashMap<E,Shape>();

	public Shape transform(E e) {		
		return shapeMap.get(e);
	}
	
	public Component prepareRenderer(RenderContext<V,E> rc, EdgeLabelRenderer graphLabelRenderer, Object value, 
			boolean isSelected, E edge) {
		return rc.getEdgeLabelRenderer().<E>getEdgeLabelRendererComponent(rc.getScreenDevice(), value, 
				rc.getEdgeFontTransformer().transform(edge), isSelected, edge);
	}
	
	private Shape calcLabelShape(RenderContext<V, E> rc, String value, 
			 E e)
	{
		float maxx = 0,maxy =0;
		
		float wrappingWidth = rc.getScreenDevice().getWidth();
		Font font =  rc.getEdgeFontTransformer().transform(e);		
		if( font == null ) font = rc.getScreenDevice().getFont();		
		GraphicsDecorator g = rc.getGraphicsContext();
		if( value != null && !"".equals(value)){
			StringTokenizer st = new StringTokenizer(value,"\n\r\f",false);
			while(st.hasMoreTokens()){
				AttributedString as = new AttributedString(st.nextToken()); 
				as.addAttribute(TextAttribute.FONT, font); 
				AttributedCharacterIterator aci = as.getIterator(); 
				LineBreakMeasurer lbm = new LineBreakMeasurer(aci, g.getFontRenderContext()); 
				 while (lbm.getPosition() < aci.getEndIndex()) { 
			          TextLayout textLayout = lbm.nextLayout(wrappingWidth);
			          maxy += textLayout.getAscent();
			          maxy += textLayout.getDescent() + textLayout.getLeading();
			         
			          if( textLayout.getAdvance() > maxx ) maxx = textLayout.getAdvance();
				 }
			}
		}
		
		return new Rectangle2D.Float(0, 0, maxx, maxy);
		
	}
	
	private void drawLabelShape(RenderContext<V, E> rc, String value, 
			 E e)
	{
		float x = 0, y = 0;
		float wrappingWidth = rc.getScreenDevice().getWidth();
		boolean isSelected = rc.getPickedEdgeState().isPicked(e);
		
		Font font =  rc.getEdgeFontTransformer().transform(e);		
		if( font == null ) font = rc.getScreenDevice().getFont();		
		GraphicsDecorator g = rc.getGraphicsContext();
		
		if( value != null && !"".equals(value)){
			StringTokenizer st = new StringTokenizer(value,"\n\r\f",false);
			while(st.hasMoreTokens()){
				AttributedString as = new AttributedString(st.nextToken()); 
				as.addAttribute(TextAttribute.FONT, font); 
				if( isSelected){
					as.addAttribute(TextAttribute.BACKGROUND, Generator.getTextPickingBgColor());
					as.addAttribute(TextAttribute.FOREGROUND, Generator.getTextPickingColor());
				}else {
					as.addAttribute(TextAttribute.BACKGROUND, rc.getScreenDevice().getBackground());
					as.addAttribute(TextAttribute.FOREGROUND, rc.getScreenDevice().getForeground());
				}
				AttributedCharacterIterator aci = as.getIterator(); 
				LineBreakMeasurer lbm = new LineBreakMeasurer(aci, g.getFontRenderContext()); 
				 while (lbm.getPosition() < aci.getEndIndex()) { 
			          TextLayout textLayout = lbm.nextLayout(wrappingWidth); 
			          y += textLayout.getAscent();
			          
			          textLayout.draw(g.getDelegate(),x,y);
			        
			          y += textLayout.getDescent() + textLayout.getLeading();
				 }
			}
		}
	}

	public void labelEdge(RenderContext<V, E> rc, Layout<V, E> layout, E e,
			String label) {
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
        
        Graph<V,E> graph = layout.getGraph();
        Pair<V> endpoints = graph.getEndpoints(e);
        V v1 = endpoints.getFirst();
        V v2 = endpoints.getSecond();
        
        if(tsl != null && (tsl.isFadedItem(v1) || tsl.isFadedItem(v2)) )
        		g.setComposite(ac1);
        
        Shape edgeShape = rc.getEdgeShapeTransformer().transform(Context.<Graph<V,E>,E>getInstance(graph, e));
        /* if is line draw one label*/     
       if( !(edgeShape instanceof Line2D && rc.getParallelEdgeIndexFunction().getIndex(graph, e) > 0 ))
    	   drawLabelEdge(rc, layout, e, label);
   
		
		g.setComposite(oldComposite);
	}

	public void  drawLabelEdge(RenderContext<V,E> rc, Layout<V,E> layout, E e, String label) {
    	if(label == null || label.length() == 0) return;
    	
    	Graph<V,E> graph = layout.getGraph();
        // don't draw edge if either incident vertex is not drawn
        Pair<V> endpoints = graph.getEndpoints(e);
        V v1 = endpoints.getFirst();
        V v2 = endpoints.getSecond();
        if (!rc.getEdgeIncludePredicate().evaluate(Context.<Graph<V,E>,E>getInstance(graph,e)))
            return;

        if (!rc.getVertexIncludePredicate().evaluate(Context.<Graph<V,E>,V>getInstance(graph,v1)) || 
            !rc.getVertexIncludePredicate().evaluate(Context.<Graph<V,E>,V>getInstance(graph,v2)))
            return;

        Point2D p1 = layout.transform(v1);
        Point2D p2 = layout.transform(v2);
        p1 = rc.getMultiLayerTransformer().transform(Layer.LAYOUT, p1);
        p2 = rc.getMultiLayerTransformer().transform(Layer.LAYOUT, p2);
        float x1 = (float) p1.getX();
        float y1 = (float) p1.getY();
        float x2 = (float) p2.getX();
        float y2 = (float) p2.getY();
        
        Layout<V, E> relLayout = layout;
		TimeSeqLayout<V, E> tsl = null;

		if (layout instanceof ObservableCachingLayout) {
			ObservableCachingLayout<V, E> ol = (ObservableCachingLayout<V, E>) layout;
			relLayout = ol.getDelegate();
		}
		
		//time layout
		if (relLayout instanceof TimeSeqLayout) {
			tsl = (TimeSeqLayout) relLayout;
		}		
		Shape s1 = Generator.getTransformedVertexShape(rc, v1, x1, y1);
		Shape s2 = Generator.getTransformedVertexShape(rc, v2, x2, y2);
				
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
		
        GraphicsDecorator g = rc.getGraphicsContext();
        float distX = x2 - x1;
        float distY = y2 - y1;
        double totalLength = Math.sqrt(distX * distX + distY * distY);

        double closeness = rc.getEdgeLabelClosenessTransformer().transform(Context.<Graph<V,E>,E>getInstance(graph, e)).doubleValue();

        double posX = (x1 + (closeness) * distX);
        double posY = (y1 + (closeness) * distY);

        float xDisplacement = (int) (rc.getLabelOffset() * (distY / totalLength));
        float yDisplacement = (int) (rc.getLabelOffset() * (-distX / totalLength));
              
//        Component component = prepareRenderer(rc, rc.getEdgeLabelRenderer(), label, 
//                rc.getPickedEdgeState().isPicked(e), e);
//        
//        Dimension d = component.getPreferredSize(); 
        Shape labelShape = calcLabelShape(rc,label,e);
        Dimension d = labelShape.getBounds().getSize();
                        
        Shape edgeShape = rc.getEdgeShapeTransformer().transform(Context.<Graph<V,E>,E>getInstance(graph, e));
        
        //double parallelOffset = 1;
        double parallelOffset = 0;
        
        parallelOffset += rc.getParallelEdgeIndexFunction().getIndex(graph, e);

        parallelOffset *= d.height;
        if(edgeShape instanceof Ellipse2D) {
        	parallelOffset += rc.getVertexShapeTransformer().transform(v1).getBounds2D().getHeight()/2;
        	//parallelOffset += edgeShape.getBounds().getHeight();
            parallelOffset += d.height;
            parallelOffset = -parallelOffset;
        }
        
        AffineTransform old = g.getTransform();
        AffineTransform gform =  new AffineTransform(old);
        AffineTransform xform = new AffineTransform();
        
        //AffineTransform xform = new AffineTransform();
        xform.translate(posX+xDisplacement, posY+yDisplacement);
        double dx = x2 - x1;
        double dy = y2 - y1;
        if(rc.getEdgeLabelRenderer().isRotateEdgeLabels()) {
            double theta = Math.atan2(dy, dx);
            if(dx < 0) {
                theta += Math.PI;
            }
            xform.rotate(theta);
        }
        if(dx < 0) {
            parallelOffset = -parallelOffset;
        }
       
       xform.translate(-d.width/2, -(d.height/2-parallelOffset));      
       gform.concatenate(xform);
       
       g.setTransform(gform);

       drawLabelShape(rc,label,e);
        //shapeMap.put(e, xform.createTransformedShape(new Rectangle2D.Double(0,0,d.width,d.height)));
        shapeMap.put(e, xform.createTransformedShape(labelShape));
        //g.draw(component, rc.getRendererPane(), 0, 0, d.width, d.height, true);
        //drawLabelShape(rc, label, e, 0, 0, true);

        g.setTransform(old);
    }

}
