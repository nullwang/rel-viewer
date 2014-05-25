package com.b.x;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.QuadCurve2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JComponent;

import com.b.g.Generator;

import g.algorithms.layout.Layout;
import g.graph.Graph;
import g.graph.util.Context;
import g.graph.util.EdgeType;
import g.graph.util.Pair;
import g.visualization.Layer;
import g.visualization.RenderContext;
import g.visualization.layout.ObservableCachingLayout;
import g.visualization.renderers.BasicEdgeArrowRenderingSupport;
import g.visualization.renderers.EdgeArrowRenderingSupport;
import g.visualization.renderers.Renderer;
import g.visualization.transform.MutableTransformer;
import g.visualization.transform.shape.GraphicsDecorator;

public class RelEdgeRenderer<V, E> implements Renderer.Edge<V, E> {

	protected EdgeArrowRenderingSupport edgeArrowRenderingSupport = new BasicEdgeArrowRenderingSupport();

	// protected EdgeArrowRenderingSupport edgeArrowRenderingSupport = new
	// CenterEdgeArrowRenderingSupport();
	public void paintEdge(RenderContext<V, E> rc, Layout<V, E> layout, E e) {
		GraphicsDecorator g2d = rc.getGraphicsContext();
		Graph<V, E> graph = layout.getGraph();
		if (!rc.getEdgeIncludePredicate().evaluate(
				Context.<Graph<V, E>, E> getInstance(graph, e)))
			return;

		// don't draw edge if either incident vertex is not drawn
		Pair<V> endpoints = graph.getEndpoints(e);
		V v1 = endpoints.getFirst();
		V v2 = endpoints.getSecond();
		if (!rc.getVertexIncludePredicate().evaluate(
				Context.<Graph<V, E>, V> getInstance(graph, v1))
				|| !rc.getVertexIncludePredicate().evaluate(
						Context.<Graph<V, E>, V> getInstance(graph, v2)))
			return;

		Stroke new_stroke = rc.getEdgeStrokeTransformer().transform(e);
		Stroke old_stroke = g2d.getStroke();
		if (new_stroke != null)
			g2d.setStroke(new_stroke);
		
		 /* if is line draw one line*/
		 Shape edgeShape = rc.getEdgeShapeTransformer().transform(Context.<Graph<V,E>,E>getInstance(graph, e));	       
	    if( !(edgeShape instanceof Line2D && rc.getParallelEdgeIndexFunction().getIndex(graph, e) > 0 ))
	    	  drawSimpleEdge(rc, layout, e);

		// restore paint and stroke
		if (new_stroke != null)
			g2d.setStroke(old_stroke);

	}

	protected Shape[] constructAllShape(RenderContext<V, E> rc,
			Layout<V, E> layout, E e, Point2D p1, Point2D p2) {
		Shape[] shapes = new Shape[5];

		Graph<V, E> graph = layout.getGraph();
		Pair<V> endpoints = graph.getEndpoints(e);
		V v1 = endpoints.getFirst();
		V v2 = endpoints.getSecond();

		boolean isLoop = v1.equals(v2);
		Shape s2 = rc.getVertexShapeTransformer().transform(v2);
		Shape edgeShape = rc.getEdgeShapeTransformer().transform(
				Context.<Graph<V, E>, E> getInstance(graph, e));

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

		AffineTransform xform = AffineTransform.getTranslateInstance(x1, y1);
		GeneralPath timeLineShape = null; // theme line
		Ellipse2D interactPointShape = null; // interact point

		if (isLoop) {
			// this is a self-loop. scale it is larger than the vertex
			// it decorates and translate it so that its nadir is
			// at the center of the vertex.
			Rectangle2D s2Bounds = s2.getBounds2D();
			xform.scale(s2Bounds.getWidth(), s2Bounds.getHeight());
			xform.translate(0, -edgeShape.getBounds2D().getWidth() / 2);
		} else if (tsl != null
				&& (tsl.isThemedItem(v1) && tsl.isControllingItem(v2) || (tsl
						.isControllingItem(v1) && tsl.isThemedItem(v2)))) {
			float dx = x2 - x1;
			float dy = y2 - y1;

			GeneralPath gp = new GeneralPath();
			gp.moveTo(0, 0);// the xform will do the translation to x1,y1
			timeLineShape = new GeneralPath();
			timeLineShape.moveTo(0, 0);
			if (tsl.isThemedItem(v1)) {
				timeLineShape.lineTo(dx, 0);
				interactPointShape = new Ellipse2D.Double(dx - 2, -2, 4, 4);
				gp.moveTo(dx, 0);
				if( edgeShape instanceof QuadCurve2D){
					QuadCurve2D curve = (QuadCurve2D) edgeShape;
					gp.quadTo(dx-(float)curve.getCtrlY(), dy/2, dx, dy);
				}
				else{
					gp.lineTo(dx, dy);
				}
				
			} else {
				timeLineShape.moveTo(0, dy);
				timeLineShape.lineTo(dx, dy);
				//gp.lineTo(0, dy);
				if( edgeShape instanceof QuadCurve2D){
					QuadCurve2D curve = (QuadCurve2D) edgeShape;
					gp.quadTo(-(float)curve.getCtrlY(), dy/2, 0, dy);
				}
				else{
					gp.lineTo(0, dy);
				}				
				interactPointShape = new Ellipse2D.Double(-2, dy - 2, 4, 4);
			}
			edgeShape = gp;

		} else {
			// this is a normal edge. Rotate it to the angle between
			// vertex endpoints, then scale it to the distance between
			// the vertices
			float dx = x2 - x1;
			float dy = y2 - y1;
			float thetaRadians = (float) Math.atan2(dy, dx);
			xform.rotate(thetaRadians);
			float dist = (float) Math.sqrt(dx * dx + dy * dy);
			xform.scale(dist, 1.0);
		}

		edgeShape = xform.createTransformedShape(edgeShape);
		shapes[0] = edgeShape;

		if (timeLineShape != null) {
			shapes[1] = xform.createTransformedShape(timeLineShape);			
		} else {
			shapes[1] = timeLineShape;
		}

		if(interactPointShape != null ) {
			shapes[2] = xform.createTransformedShape(interactPointShape);
		}else {
			shapes[2] = interactPointShape;
		}

		Shape vertexShape = rc.getVertexShapeTransformer().transform(v1);
		AffineTransform xf = AffineTransform.getTranslateInstance(x1, y1);

		if (tsl != null && (tsl.isThemedItem(v1) && tsl.isControllingItem(v2))) {
			vertexShape = new Ellipse2D.Double(-2, -2, 4, 4);
			xf = AffineTransform.getTranslateInstance(x2, y1);
		}

		vertexShape = xf.createTransformedShape(vertexShape);
		shapes[3] = vertexShape;

		Shape destVertexShape = rc.getVertexShapeTransformer().transform(v2);
		xf = AffineTransform.getTranslateInstance(x2, y2);
		if (tsl != null && (tsl.isThemedItem(v2)) && tsl.isControllingItem(v1)) {
			destVertexShape = new Ellipse2D.Double(-2, -2, 4, 4);
			xf = AffineTransform.getTranslateInstance(x1, y2);
		}
		destVertexShape = xf.createTransformedShape(destVertexShape);

		shapes[4] = destVertexShape;

		return shapes;
	}

	/**
	 * Draws the edge <code>e</code>, whose endpoints are at
	 * <code>(x1,y1)</code> and <code>(x2,y2)</code>, on the graphics context
	 * <code>g</code>. The <code>Shape</code> provided by the
	 * <code>EdgeShapeFunction</code> instance is scaled in the x-direction so
	 * that its width is equal to the distance between <code>(x1,y1)</code> and
	 * <code>(x2,y2)</code>.
	 */
	@SuppressWarnings("unchecked")
	protected void drawSimpleEdge(RenderContext<V, E> rc, Layout<V, E> layout,
			E e) {

		GraphicsDecorator g = rc.getGraphicsContext();
		Graph<V, E> graph = layout.getGraph();
		Pair<V> endpoints = graph.getEndpoints(e);
		V v1 = endpoints.getFirst();
		V v2 = endpoints.getSecond();

		Layout relLayout = layout;
		TimeSeqLayout tsl = null;

		if (layout instanceof ObservableCachingLayout) {
			ObservableCachingLayout ol = (ObservableCachingLayout) layout;
			relLayout = ol.getDelegate();
		}
		// time layout
		if (relLayout instanceof TimeSeqLayout) {
			tsl = (TimeSeqLayout) relLayout;
//			if (tsl.isControllingItem(v1) && tsl.isControllingItem(v2))
//				return;
		}

		Point2D p1 = layout.transform(v1);
		Point2D p2 = layout.transform(v2);
		p1 = rc.getMultiLayerTransformer().transform(Layer.LAYOUT, p1);
		p2 = rc.getMultiLayerTransformer().transform(Layer.LAYOUT, p2);
	
		boolean isLoop = v1.equals(v2);

		boolean edgeHit = true;
		boolean arrowHit = true;
		Rectangle deviceRectangle = null;
		JComponent vv = rc.getScreenDevice();
		if (vv != null) {
			Dimension d = vv.getSize();
			deviceRectangle = new Rectangle(0, 0, d.width, d.height);
		}

		Shape[] shapes = constructAllShape(rc, layout, e, p1, p2);

		MutableTransformer vt = rc.getMultiLayerTransformer().getTransformer(
				Layer.VIEW);
//		if (vt instanceof LensTransformer) {
//			vt = ((LensTransformer) vt).getDelegate();
//		}

		Shape edgeShape = shapes[0];
		Shape timeLineShape = shapes[1];
		Shape interactPointShape = shapes[2];
		Shape srcVertexShape = shapes[3];
		Shape destVertexShape = shapes[4];

		edgeHit = vt.transform(edgeShape).intersects(deviceRectangle);

		if (edgeHit == true) {

			Composite oldComposite = g.getComposite();
			float alphaFactor = Generator.getAlphaFactor();
			Composite ac1 = AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
					alphaFactor);
			if (tsl != null && (tsl.isFadedItem(v1) || tsl.isFadedItem(v2)))
				g.setComposite(ac1);

			Paint oldPaint = g.getPaint();

			// get Paints for filling and drawing
			// (filling is done first so that drawing and label use same Paint)
			Paint fill_paint = rc.getEdgeFillPaintTransformer().transform(e);
			if (fill_paint != null) {
				g.setPaint(fill_paint);
				g.fill(edgeShape);
			}
			Paint draw_paint = rc.getEdgeDrawPaintTransformer().transform(e);
			if (draw_paint != null) {
				g.setPaint(draw_paint);
				g.draw(edgeShape);
			}

			if (timeLineShape != null) {
				BasicStroke estroke = new BasicStroke(1.8f);
				Stroke os = g.getStroke();
				g.setStroke(estroke);

				fill_paint = rc.getEdgeFillPaintTransformer().transform(e);
				if (fill_paint != null) {
					g.setPaint(fill_paint);
					g.fill(timeLineShape);
				}
				draw_paint = rc.getEdgeDrawPaintTransformer().transform(e);
				if (draw_paint != null) {
					g.setPaint(draw_paint);
					g.draw(timeLineShape);
				}

				g.setStroke(os);

				if (interactPointShape != null) {
					g.fill(interactPointShape);
					g.draw(interactPointShape);
				}
			}

			float scalex = (float) g.getTransform().getScaleX();
			float scaley = (float) g.getTransform().getScaleY();
			// see if arrows are too small to bother drawing
			if (scalex < .3 || scaley < .3)
				return;

			if (rc.getEdgeArrowPredicate().evaluate(
					Context.<Graph<V, E>, E> getInstance(graph, e))) {

				Stroke new_stroke = rc.getEdgeArrowStrokeTransformer()
						.transform(e);
				Stroke old_stroke = g.getStroke();
				if (new_stroke != null)
					g.setStroke(new_stroke);
				
				arrowHit = rc.getMultiLayerTransformer().getTransformer(
						Layer.VIEW).transform(destVertexShape).intersects(
						deviceRectangle);
				if (arrowHit) {
					AffineTransform at = edgeArrowRenderingSupport
							.getArrowTransform(rc, edgeShape, destVertexShape);

					if (at == null)
						return;
					Shape arrow = rc.getEdgeArrowTransformer().transform(
							Context.<Graph<V, E>, E> getInstance(graph, e));
					arrow = at.createTransformedShape(arrow);
					g.setPaint(rc.getArrowFillPaintTransformer().transform(e));
					g.fill(arrow);
					g.setPaint(rc.getArrowDrawPaintTransformer().transform(e));
					g.draw(arrow);
				}
				if (graph.getEdgeType(e) == EdgeType.UNDIRECTED) {
					
					arrowHit = rc.getMultiLayerTransformer().getTransformer(
							Layer.VIEW).transform(srcVertexShape).intersects(
							deviceRectangle);

					if (arrowHit) {
						AffineTransform at = edgeArrowRenderingSupport
								.getReverseArrowTransform(rc, edgeShape,
										srcVertexShape, !isLoop);

						if (at == null)
							return;
						Shape arrow = rc.getEdgeArrowTransformer().transform(
								Context.<Graph<V, E>, E> getInstance(graph, e));
						arrow = at.createTransformedShape(arrow);
						g.setPaint(rc.getArrowFillPaintTransformer().transform(
								e));
						g.fill(arrow);
						g.setPaint(rc.getArrowDrawPaintTransformer().transform(
								e));
						g.draw(arrow);
					}
				}
				// restore paint and stroke
				if (new_stroke != null)
					g.setStroke(old_stroke);

			}

			// restore old paint
			g.setPaint(oldPaint);
			g.setComposite(oldComposite);
		}
	}

	public EdgeArrowRenderingSupport getEdgeArrowRenderingSupport() {
		return edgeArrowRenderingSupport;
	}

	public void setEdgeArrowRenderingSupport(
			EdgeArrowRenderingSupport edgeArrowRenderingSupport) {
		this.edgeArrowRenderingSupport = edgeArrowRenderingSupport;
	}
}
