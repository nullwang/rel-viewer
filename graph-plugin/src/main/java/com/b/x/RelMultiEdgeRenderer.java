package com.b.x;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
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
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;

import javax.swing.JComponent;

import com.b.a.Parameter;
import com.b.g.Generator;
import com.b.u.GraphicsUtility;
import com.b.u.ParameterUtil;

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

public class RelMultiEdgeRenderer<V, E> implements Renderer.Edge<V, E> {

	protected EdgeArrowRenderingSupport<V, E> edgeArrowRenderingSupport = new BasicEdgeArrowRenderingSupport<V, E>();
	

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

		/* if is line draw one line */
		Shape edgeShape = rc.getEdgeShapeTransformer().transform(
				Context.<Graph<V, E>, E> getInstance(graph, e));
		if (!(edgeShape instanceof Line2D && rc.getParallelEdgeIndexFunction()
				.getIndex(graph, e) > 0))
			drawSimpleEdge(rc, layout, e);

		// restore paint and stroke
		if (new_stroke != null)
			g2d.setStroke(old_stroke);

	}

//	protected Shape getTransformedVertexShape(RenderContext<V, E> rc, V v,
//			float x, float y) {
//		Shape shape = rc.getVertexShapeTransformer().transform(v);
//		GeneralPath gp = new GeneralPath(shape);
//
//		Shape labelShape = Generator.getVertexLabelShapeTransformer(rc)
//				.transform(v.toString());
//		if (labelShape != null)
//			gp.append(labelShape, false);
//
//		AffineTransform transform = AffineTransform.getTranslateInstance(x, y);
//		return transform.createTransformedShape(gp);
//	}

	protected Shape getTransformedArrowShape(RenderContext<V, E> rc,
			Layout<V, E> layout, E e, float x1, float y1, float x2, float y2) {
		Graph<V, E> graph = layout.getGraph();
		Shape arrow = rc.getEdgeArrowTransformer().transform(
				Context.<Graph<V, E>, E> getInstance(graph, e));

		Shape edgeShape = getTransformedEdgeShape(rc, layout, e, x1, y1, x2, y2);

		Shape vShape = new Ellipse2D.Double(x2 - 0.5, y2 - 0.5, 1, 1);

		GeneralPath path = new GeneralPath(edgeShape);
		PathIterator iterator = path.getPathIterator(null, 1.0);
		Line2D line = GraphicsUtility.getAdjacentLine(iterator, vShape);
		if (line == null) {
			return null;
		}

		Point2D point1 = line.getP1();
		Point2D point2 = line.getP2();
		double deltaX = point2.getX() - point1.getX();
		double deltaY = point2.getY() - point1.getY();
		float angle = (float) Math.atan2(deltaY, deltaX);

		AffineTransform transform = new AffineTransform();
		transform.concatenate(AffineTransform.getTranslateInstance(point2
				.getX(), point2.getY()));
		transform.concatenate(AffineTransform.getRotateInstance(angle));
		Shape shape = transform.createTransformedShape(arrow);
		return shape;
	}

	private Shape[] getTransformedTimeLineAndInteractionShape(
			RenderContext<V, E> rc, Layout<V, E> layout, E e) {
		Shape[] shapes = new Shape[2];

		Graph<V, E> graph = layout.getGraph();
		Pair<V> endpoints = graph.getEndpoints(e);
		V v1 = endpoints.getFirst();
		V v2 = endpoints.getSecond();

		Point2D p1 = layout.transform(v1);
		Point2D p2 = layout.transform(v2);
		p1 = rc.getMultiLayerTransformer().transform(Layer.LAYOUT, p1);
		p2 = rc.getMultiLayerTransformer().transform(Layer.LAYOUT, p2);

		float x1 = (float) p1.getX();
		float y1 = (float) p1.getY();
		float x2 = (float) p2.getX();
		float y2 = (float) p2.getY();
		float dx = x2 - x1;
		float dy = y2 - y1;

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

		AffineTransform xform = AffineTransform.getTranslateInstance(x1, y1);
		AffineTransform xf = AffineTransform.getTranslateInstance(x1, y1);

		GeneralPath timeLineShape = new GeneralPath(); // theme line
		timeLineShape.moveTo(0.0f, 0.0f);

		if (tsl != null
				&& (tsl.isThemedItem(v1) && tsl.isControllingItem(v2) || (tsl
						.isControllingItem(v1) && tsl.isThemedItem(v2)))) {
			if (tsl.isThemedItem(v1)) {
				timeLineShape.lineTo(dx, 0);
				xf.translate(dx, 0);
			} else {
				timeLineShape.moveTo(0, dy);
				timeLineShape.lineTo(dx, dy);
				xf.translate(0, dy);
			}

			shapes[0] = xform.createTransformedShape(timeLineShape);
			shapes[1] = xf.createTransformedShape(Generator.getInteractionShape());
		}

		return shapes;
	}

	protected Shape getTransformedEdgeShape(RenderContext<V, E> rc,
			Layout<V, E> layout, E e, float x1, float y1, float x2, float y2) {

		Graph<V, E> graph = layout.getGraph();
		Pair<V> endpoints = graph.getEndpoints(e);

		Shape edgeShape = rc.getEdgeShapeTransformer().transform(
				Context.<Graph<V, E>, E> getInstance(graph, e));

		if (edgeShape == null) {
			return null;
		}

		AffineTransform transform = new AffineTransform();

		V v1 = endpoints.getFirst();
		V v2 = endpoints.getSecond();

		if (v1.equals(v2)) {
			Rectangle bounds = rc.getVertexShapeTransformer().transform(v2)
					.getBounds();
			transform.translate(x2, y2 - bounds.getHeight() / 2);
			transform.scale(bounds.getWidth(), bounds.getHeight());
		} else {
			float deltaX = x2 - x1;
			float deltaY = y2 - y1;
			float angle = (float) Math.atan2(deltaY, deltaX);
			float distance = (float) Math.sqrt(deltaX * deltaX + deltaY
					* deltaY);

			transform.translate(x1, y1);
			transform.rotate(angle);
			transform.scale(distance, 1.0);
		}

		return transform.createTransformedShape(edgeShape);
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
		}

		Point2D p1 = layout.transform(v1);
		Point2D p2 = layout.transform(v2);
		p1 = rc.getMultiLayerTransformer().transform(Layer.LAYOUT, p1);
		p2 = rc.getMultiLayerTransformer().transform(Layer.LAYOUT, p2);
		float x1 = (float) p1.getX();
		float y1 = (float) p1.getY();
		float x2 = (float) p2.getX();
		float y2 = (float) p2.getY();

		boolean isLoop = v1.equals(v2);
		boolean edgeHit = true;
		boolean arrowHit = true;
		Rectangle deviceRectangle = null;
		JComponent vv = rc.getScreenDevice();
		if (vv != null) {
			Dimension d = vv.getSize();
			deviceRectangle = new Rectangle(0, 0, d.width, d.height);
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
		
		Shape srcVertexShape = new Ellipse2D.Double(x1 - 0.5, y1 - 0.5, 1, 1);
		Shape destVertexShape = new Ellipse2D.Double(x2 - 0.5, y2 - 0.5, 1, 1);

		Shape edgeShape = this.getTransformedEdgeShape(rc, layout, e, x1, y1,
				x2, y2);
		Shape[] timeLineAndInteractionShapes = this
				.getTransformedTimeLineAndInteractionShape(rc, layout, e);
		Shape timeLineShape = timeLineAndInteractionShapes[0];
		Shape interactPointShape = timeLineAndInteractionShapes[1];

		MutableTransformer vt = rc.getMultiLayerTransformer().getTransformer(
				Layer.VIEW);
//		if (vt instanceof LensTransformer) {
//			vt = ((LensTransformer) vt).getDelegate();
//		}

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
				BasicStroke estroke = new BasicStroke(1.8f,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND);
				Stroke os = g.getStroke();
				g.setStroke(estroke);
				
				g.setPaint(Color.black);
				g.fill(timeLineShape);
				g.draw(timeLineShape);				

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
			
			boolean showBothArrow = 
				( graph.getEdgeType(e) == EdgeType.UNDIRECTED) &&  
				!ParameterUtil.getBoolean(Parameter.PAIR_ARROW_HIDDEN, true);
			
			boolean showFowardArrow = graph.getEdgeType(e) == EdgeType.DIRECTED;

			if (rc.getEdgeArrowPredicate().evaluate(
					Context.<Graph<V, E>, E> getInstance(graph, e))) {

				Stroke new_stroke = rc.getEdgeArrowStrokeTransformer()
						.transform(e);
				Stroke old_stroke = g.getStroke();
				if (new_stroke != null)
					g.setStroke(new_stroke);

				arrowHit = rc.getMultiLayerTransformer().getTransformer(
						Layer.VIEW).transform(s2).intersects(deviceRectangle);
				if (arrowHit && (showFowardArrow || showBothArrow)) {
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
							Layer.VIEW).transform(s1).intersects(
							deviceRectangle);

					if (arrowHit && (showBothArrow)) {
						AffineTransform at = edgeArrowRenderingSupport
								.getReverseArrowTransform(rc, edgeShape, srcVertexShape,
										!isLoop);

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

	public EdgeArrowRenderingSupport<V, E> getEdgeArrowRenderingSupport() {
		return edgeArrowRenderingSupport;
	}

	public void setEdgeArrowRenderingSupport(
			EdgeArrowRenderingSupport edgeArrowRenderingSupport) {
		this.edgeArrowRenderingSupport = edgeArrowRenderingSupport;
	}
}