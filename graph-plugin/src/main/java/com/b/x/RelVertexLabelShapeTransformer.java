package com.b.x;

import java.awt.AlphaComposite;
import java.awt.Component;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javax.swing.JComponent;

import o.c15.Transformer;


import com.b.g.Generator;

import g.algorithms.layout.Layout;
import g.graph.Graph;
import g.graph.util.Context;
import g.visualization.Layer;
import g.visualization.RenderContext;
import g.visualization.layout.ObservableCachingLayout;
import g.visualization.renderers.Renderer;
import g.visualization.renderers.VertexLabelRenderer;
import g.visualization.transform.BidirectionalTransformer;
import g.visualization.transform.MutableTransformer;
import g.visualization.transform.MutableTransformerDecorator;
import g.visualization.transform.shape.GraphicsDecorator;
import g.visualization.transform.shape.ShapeTransformer;
import g.visualization.transform.shape.TransformingGraphics;

public class RelVertexLabelShapeTransformer<V, E> implements
		Renderer.VertexLabel<V, E> , Transformer<V,Shape>{
	protected Map<V, Shape> shapes = new HashMap<V, Shape>();
	protected Map<V, Point> anchors = new HashMap<V, Point>();
	protected Map<V, Point2D> centers = new HashMap<V, Point2D>();

	public Boolean getEmphasised() {
		return Generator.getEmphasised();
	}

	protected RenderContext renderContext;
	
	public Set getEmphasisedVertex() {
		return Generator.getEmphasisedEnds();
	}

	public RenderContext getRenderContext() {
		return renderContext;
	}

	public void setRenderContext(RenderContext renderContext) {
		this.renderContext = renderContext;
	}

	public Component prepareRenderer(RenderContext<V, E> rc,
			VertexLabelRenderer graphLabelRenderer, Object value,
			boolean isSelected, V vertex) {
		return rc.getVertexLabelRenderer().<V> getVertexLabelRendererComponent(
				rc.getScreenDevice(), value,
				rc.getVertexFontTransformer().transform(vertex), isSelected,
				vertex);
	}
	
	//return the label shape
	public Shape calcLabelShape(RenderContext<V, E> rc, String value, 
			 V v)
	{
		float y = 0;
		float lasty = y;
		GeneralPath gp = new GeneralPath();
		float wrappingWidth = rc.getScreenDevice().getWidth();
		
		Font font =  rc.getVertexFontTransformer().transform(v);		
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
			          y += textLayout.getAscent(); 
			          y += textLayout.getDescent() + textLayout.getLeading();
			          
			          gp.append(new Rectangle2D.Float(0,lasty,textLayout.getAdvance(),y-lasty), false);
			          lasty = y;
				 }
			}
		}
		
		//AffineTransform af = AffineTransform.getTranslateInstance(sx, sy);
		
		return gp;
	}
	
	//draw label and return label shape
	public void drawLabel(RenderContext<V, E> rc, String value, 
			 V v, float sx, float sy)
	{
		float x = sx, y = sy;
		//GeneralPath gp = new GeneralPath();
		float wrappingWidth = rc.getScreenDevice().getWidth();
		boolean isSelected = rc.getPickedVertexState().isPicked(v);
		Font font =  rc.getVertexFontTransformer().transform(v);		
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
			          //AffineTransform af = AffineTransform.getTranslateInstance(x, y);
			          //Shape s = textLayout.getOutline(af).getBounds2D();
			          //gp.append(s, true);  
			          y += textLayout.getDescent() + textLayout.getLeading();
				 }
			}
		}
		
		//return gp;
		//return new Rectangle2D.Double(0, 0, gp.getBounds2D().getMaxX(), gp.getBounds2D().getMaxY());
	}

	public Shape transform(V v) {
		Shape shape = shapes.get(v);
		return shape;
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
	 * @param position
	 *            the position to set
	 */
	public void setPosition(Position position) {
		this.position = position;
	}

	/**
	 * Labels the specified vertex with the specified label. Uses the font
	 * specified by this instance's <code>VertexFontFunction</code>. (If the
	 * font is unspecified, the existing font for the graphics context is used.)
	 * If vertex label centering is active, the label is centered on the
	 * position of the vertex; otherwise the label is offset slightly.
	 */
	public void labelVertex(RenderContext<V, E> rc, Layout<V, E> layout, V v,
			String label) {
		Graph<V, E> graph = layout.getGraph();
		if (rc.getVertexIncludePredicate().evaluate(
				Context.<Graph<V, E>, V> getInstance(graph, v)) == false) {
			return;
		}
		Point2D pt = layout.transform(v);
		pt = rc.getMultiLayerTransformer().transform(Layer.LAYOUT, pt);

		float x = (float) pt.getX();
		float y = (float) pt.getY();
		
		Shape shape = rc.getVertexShapeTransformer().transform(v);
		AffineTransform xform = AffineTransform.getTranslateInstance(x, y);
		
		// user icon shape
		// ape shape = super.transform(v);

		shape = xform.createTransformedShape(shape);
				
		//Component component = prepareRenderer(rc, rc.getVertexLabelRenderer(),
		//		label, rc.getPickedVertexState().isPicked(v), v);
		//Dimension d = component.getPreferredSize();
		Shape labelShape = calcLabelShape(rc,label,v);
		//Dimension d = drawLabel(rc,label,v,0,0,false).getBounds().getSize();
		Dimension d = labelShape.getBounds().getSize();

		GraphicsDecorator g = rc.getGraphicsContext();
		
		if (rc.getGraphicsContext() instanceof TransformingGraphics) {
			BidirectionalTransformer transformer = ((TransformingGraphics) rc
					.getGraphicsContext()).getTransformer();
			if (transformer instanceof ShapeTransformer) {
				ShapeTransformer shapeTransformer = (ShapeTransformer) transformer;
				shape = shapeTransformer.transform(shape);
			}
		}
		Rectangle2D bounds = shape.getBounds2D();

		Point p = null;
		if (position == Position.AUTO) {
			Dimension vvd = rc.getScreenDevice().getSize();
			if (vvd.width == 0 || vvd.height == 0) {
				vvd = rc.getScreenDevice().getPreferredSize();
			}
			p = getAnchorPoint(bounds, d, positioner.getPosition(x, y, vvd));
		} else {
			p = getAnchorPoint(bounds, d, position);
		}
		
		AffineTransform afLabel = AffineTransform.getTranslateInstance(p.x,p.y);
		if(!labelHit(rc,afLabel.createTransformedShape(labelShape)))
			return;

		Layout relLayout = layout;
		TimeSeqLayout tsl = null;

		if (layout instanceof ObservableCachingLayout) {
			ObservableCachingLayout ol = (ObservableCachingLayout) layout;
			relLayout = ol.getDelegate();
		}
		// time layout
		if (relLayout instanceof TimeSeqLayout) {
			tsl = (TimeSeqLayout) relLayout;
		}

		Composite oldComposite = g.getComposite();
		float alphaFactor = Generator.getAlphaFactor();
		Composite ac1 = AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
				alphaFactor);
		if (tsl != null && (tsl.isFadedItem(v)))
			g.setComposite(ac1);
		
		if( this.getEmphasisedVertex().contains(v) && this.getEmphasised()){
			 MutableTransformer viewTransformer = rc.getMultiLayerTransformer().getTransformer(Layer.VIEW);
				double scalex = 1/viewTransformer.getScaleX();
				double scaley = 1/viewTransformer.getScaleY();
				AffineTransform af = AffineTransform.getScaleInstance(scalex,scaley);
				AffineTransform oldXform = g.getTransform();
		        AffineTransform newXform = new AffineTransform(oldXform);
		        AffineTransform pXform = AffineTransform.getTranslateInstance(p.x,p.y);
		        newXform.concatenate(pXform);
		        newXform.concatenate(af);
		        g.setTransform(newXform);
		        
		        //labelShape = calcLabelShape(rc,label,v,0,0); 
		        drawLabel(rc,label,v,0,0); //drawLabel(rc,label,v,0,0,true);
		        
		        newXform = new AffineTransform();
		        pXform = AffineTransform.getTranslateInstance(p.x-pt.getX(),p.y-pt.getY());
		        newXform.concatenate(pXform);
		        newXform.concatenate(af); 
		        labelShape = newXform.createTransformedShape(labelShape);
		        g.setTransform(oldXform);
		        
//		        g.draw(component, rc.getRendererPane(), 0, 0, d.width, d.height,
//						true);
//		        g.setTransform(oldXform);
//		        
//		        newXform = new AffineTransform();
//		        pXform = AffineTransform.getTranslateInstance(p.x-pt.getX(),p.y-pt.getY());
//		        newXform.concatenate(pXform);
//		        newXform.concatenate(af);    
//		        Rectangle labelBounds = new Rectangle(0,0, d.width, d.height);
//		        Shape labelShape = newXform.createTransformedShape(labelBounds);
		        shapes.put(v, labelShape);
		       
		}
		else{
			//labelShape = calcLabelShape(rc,label,v,p.x,p.y);
			drawLabel(rc,label,v,p.x,p.y);
			//Shape labelShape = drawLabel(rc,label,v,p.x,p.y,true);
			AffineTransform pXform = AffineTransform.getTranslateInstance(p.x-pt.getX(),p.y-pt.getY());
			labelShape = pXform.createTransformedShape(labelShape);
			
//			g.draw(component, rc.getRendererPane(), p.x, p.y, d.width, d.height,		
//				true);
//		
//			Shape labelShape = new Rectangle((int) (p.x - pt.getX()),
//				(int) (p.y - pt.getY()), d.width, d.height);
		
			shapes.put(v, labelShape);
		}
		g.setComposite(oldComposite);
	}

	protected Point getAnchorPoint(Rectangle2D vertexBounds,
			Dimension labelSize, Position position) {
		double x;
		double y;
		int offset = 5;
		switch (position) {

		case N:
			x = vertexBounds.getCenterX() - labelSize.width / 2;
			y = vertexBounds.getMinY() - offset - labelSize.height;
			return new Point((int) x, (int) y);

		case NE:
			x = vertexBounds.getMaxX() + offset;
			y = vertexBounds.getMinY() - offset - labelSize.height;
			return new Point((int) x, (int) y);

		case E:
			x = vertexBounds.getMaxX() + offset;
			y = vertexBounds.getCenterY() - labelSize.height / 2;
			return new Point((int) x, (int) y);

		case SE:
			x = vertexBounds.getMaxX() + offset;
			y = vertexBounds.getMaxY() + offset;
			return new Point((int) x, (int) y);

		case S:
			x = vertexBounds.getCenterX() - labelSize.width / 2;
			y = vertexBounds.getMaxY() + offset;
			return new Point((int) x, (int) y);

		case SW:
			x = vertexBounds.getMinX() - offset - labelSize.width;
			y = vertexBounds.getMaxY() + offset;
			return new Point((int) x, (int) y);

		case W:
			x = vertexBounds.getMinX() - offset - labelSize.width;
			y = vertexBounds.getCenterY() - labelSize.height / 2;
			return new Point((int) x, (int) y);

		case NW:
			x = vertexBounds.getMinX() - offset - labelSize.width;
			y = vertexBounds.getMinY() - offset - labelSize.height;
			return new Point((int) x, (int) y);

		case CNTR:
			x = vertexBounds.getCenterX() - labelSize.width / 2;
			y = vertexBounds.getCenterY() - labelSize.height / 2;
			return new Point((int) x, (int) y);

		default:
			return new Point();
		}

	}

	public static class InsidePositioner implements Positioner {
		public Position getPosition(float x, float y, Dimension d) {
			int cx = d.width / 2;
			int cy = d.height / 2;
			if (x > cx && y > cy)
				return Position.NW;
			if (x > cx && y < cy)
				return Position.SW;
			if (x < cx && y > cy)
				return Position.NE;
			return Position.SE;
		}
	}

	public static class OutsidePositioner implements Positioner {
		public Position getPosition(float x, float y, Dimension d) {
			int cx = d.width / 2;
			int cy = d.height / 2;
			if (x > cx && y > cy)
				return Position.SE;
			if (x > cx && y < cy)
				return Position.NE;
			if (x < cx && y > cy)
				return Position.SW;
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
	 * @param positioner
	 *            the positioner to set
	 */
	public void setPositioner(Positioner positioner) {
		this.positioner = positioner;
	}
	
	protected boolean labelHit(RenderContext<V,E> rc, Shape s) {
        JComponent vv = rc.getScreenDevice();
        Rectangle deviceRectangle = null;
        if(vv != null) {
            Dimension d = vv.getSize();
            deviceRectangle = new Rectangle(
                    0,0,
                    d.width,d.height);
        }
        MutableTransformer vt = rc.getMultiLayerTransformer().getTransformer(Layer.VIEW);
        if(vt instanceof MutableTransformerDecorator) {
        	vt = ((MutableTransformerDecorator)vt).getDelegate();
        }
        return vt.transform(s).intersects(deviceRectangle);
    }

}
