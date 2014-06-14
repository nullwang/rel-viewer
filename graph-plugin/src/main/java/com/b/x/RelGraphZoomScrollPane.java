package com.b.x;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import javax.swing.BoundedRangeModel;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.b.g.Generator;
import com.b.u.GraphicsUtility;

import g.algorithms.layout.Layout;
import g.visualization.Layer;
import g.visualization.VisualizationViewer;
import g.visualization.transform.BidirectionalTransformer;

public class RelGraphZoomScrollPane extends JPanel {
	
	protected VisualizationViewer vv;
	protected JScrollBar horizontalScrollBar;
	protected JScrollBar verticalScrollBar;
	protected JComponent corner;
	protected boolean scrollBarsMayControlAdjusting = true;
	protected JPanel south;

	/**
	 * Create an instance of the GraphZoomScrollPane to contain the
	 * VisualizationViewer
	 * 
	 * @param vv
	 */
	public RelGraphZoomScrollPane(VisualizationViewer vv) {
		super(new BorderLayout());
		this.vv = vv;
		addComponentListener(new ResizeListener());
		Dimension d = vv.getGraphLayout().getSize();
		verticalScrollBar = new JScrollBar(JScrollBar.VERTICAL, 0, d.height, 0,
				d.height);
		horizontalScrollBar = new JScrollBar(JScrollBar.HORIZONTAL, 0, d.width,
				0, d.width);
		verticalScrollBar
				.addAdjustmentListener(new VerticalAdjustmentListenerImpl());
		horizontalScrollBar
				.addAdjustmentListener(new HorizontalAdjustmentListenerImpl());
		verticalScrollBar.setUnitIncrement(20);
		horizontalScrollBar.setUnitIncrement(20);
		// respond to changes in the VisualizationViewer's transform
		// and set the scroll bar parameters appropriately
		vv.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent evt) {
				RelVisualizationViewer vv = (RelVisualizationViewer) evt.getSource();
				setScrollBars(vv);
			}
		});
		add(vv);
		add(verticalScrollBar, BorderLayout.EAST);
		south = new JPanel(new BorderLayout());
		south.add(horizontalScrollBar);
		setCorner(new JPanel());
		add(south, BorderLayout.SOUTH);
	}

	/**
	 * listener for adjustment of the horizontal scroll bar. Sets the
	 * translation of the VisualizationViewer
	 */
	class HorizontalAdjustmentListenerImpl implements AdjustmentListener {
		int previous = 0;

		public void adjustmentValueChanged(AdjustmentEvent e) {
			int hval = e.getValue();
			float dh = previous - hval;
			previous = hval;
			if (dh != 0 && scrollBarsMayControlAdjusting) {
				// get the uniform scale of all transforms
				float layoutScale = (float) vv.getRenderContext()
						.getMultiLayerTransformer()
						.getTransformer(Layer.LAYOUT).getScale();
				dh *= layoutScale;
				AffineTransform at = AffineTransform
						.getTranslateInstance(dh, 0);
				vv.getRenderContext().getMultiLayerTransformer()
						.getTransformer(Layer.LAYOUT).preConcatenate(at);
			}
		}
	}

	/**
	 * Listener for adjustment of the vertical scroll bar. Sets the translation
	 * of the VisualizationViewer
	 */
	class VerticalAdjustmentListenerImpl implements AdjustmentListener {
		int previous = 0;

		public void adjustmentValueChanged(AdjustmentEvent e) {
			JScrollBar sb = (JScrollBar) e.getSource();
			BoundedRangeModel m = sb.getModel();
			int vval = m.getValue();
			float dv = previous - vval;
			previous = vval;
			if (dv != 0 && scrollBarsMayControlAdjusting) {

				// get the uniform scale of all transforms
				float layoutScale = (float) vv.getRenderContext()
						.getMultiLayerTransformer()
						.getTransformer(Layer.LAYOUT).getScale();
				dv *= layoutScale;
				AffineTransform at = AffineTransform
						.getTranslateInstance(0, dv);
				vv.getRenderContext().getMultiLayerTransformer()
						.getTransformer(Layer.LAYOUT).preConcatenate(at);
			}
		}
	}

	/**
	 * use the supplied vv characteristics to set the position and dimensions of
	 * the scroll bars. Called in response to a ChangeEvent from the
	 * VisualizationViewer
	 * 
	 * @param xform
	 *            the transform of the VisualizationViewer
	 */
	private synchronized void setScrollBars(VisualizationViewer vv) {
		//sometimes node move lead to size change
		//Dimension d = vv.getGraphLayout().getSize();
		Layout layout = vv.getGraphLayout();
		
		double minX = Integer.MAX_VALUE;
		double minY = Integer.MAX_VALUE;
		double maxX = Integer.MIN_VALUE;
		double maxY = Integer.MIN_VALUE;
		
		for ( Object v: layout.getGraph().getVertices())
		{
			Point2D vp =  (Point2D) layout.transform(v);
			if( vp == null) return;
			
			Rectangle2D r2d = Generator.getTransformedVertexShape(vv.getRenderContext(),v,(float)vp.getX(),(float)vp.getY()).getBounds();
			double x1 = r2d.getMinX();
			double y1 = r2d.getMinY();
			double x2 = r2d.getMaxX();
			double y2 = r2d.getMaxY();
			
			if( x1 < minX) minX = x1;
			if( y1 < minY) minY = y1;
			if( x2 > maxX) maxX = x2;
			if(y2 > maxY) maxY = y2;
			
			
		}
		if( maxX < minX ) maxX = minX;
		if( maxY < minY) maxY = minY;
		
		minX -= GraphicsUtility.LEFTPADDING;
		maxX += GraphicsUtility.RIGHTPADDING;
		minY -= GraphicsUtility.TOPPADDING;
		maxY += GraphicsUtility.BOTTOMPADDING;
		
		//d.setSize(maxX-minX, maxY-minY);
		
		Rectangle2D vvBounds = vv.getBounds();

		// a rectangle representing the layout
		//Rectangle layoutRectangle = new Rectangle(0, 0, d.width, d.height);
		Rectangle layoutRectangle = new Rectangle();
		layoutRectangle.setRect(minX, minY, maxX-minX, maxY-minY);
				
		// -d.width/2, -d.height/2, 2*d.width, 2*d.height);

		BidirectionalTransformer viewTransformer = vv.getRenderContext()
				.getMultiLayerTransformer().getTransformer(Layer.VIEW);
		BidirectionalTransformer layoutTransformer = vv.getRenderContext()
				.getMultiLayerTransformer().getTransformer(Layer.LAYOUT);

		Point2D h0 = new Point2D.Double(vvBounds.getMinX(), vvBounds
				.getCenterY());
		Point2D h1 = new Point2D.Double(vvBounds.getMaxX(), vvBounds
				.getCenterY());
		Point2D v0 = new Point2D.Double(vvBounds.getCenterX(), vvBounds
				.getMinY());
		Point2D v1 = new Point2D.Double(vvBounds.getCenterX(), vvBounds
				.getMaxY());

		h0 = viewTransformer.inverseTransform(h0);
		h0 = layoutTransformer.inverseTransform(h0);
		h1 = viewTransformer.inverseTransform(h1);
		h1 = layoutTransformer.inverseTransform(h1);
		v0 = viewTransformer.inverseTransform(v0);
		v0 = layoutTransformer.inverseTransform(v0);
		v1 = viewTransformer.inverseTransform(v1);
		v1 = layoutTransformer.inverseTransform(v1);

		scrollBarsMayControlAdjusting = false;
		setScrollBarValues(layoutRectangle, h0, h1, v0, v1);
		scrollBarsMayControlAdjusting = true;
	}

	@SuppressWarnings("unchecked")
	protected void setScrollBarValues(Rectangle rectangle, Point2D h0,
			Point2D h1, Point2D v0, Point2D v1)
	{		
		int min,max,visible,value;
		min = (int) (rectangle.getMinX() < h0.getX()? rectangle.getMinX(): h0.getX());
		max = (int) (rectangle.getMaxX() > h1.getX() ? rectangle.getMaxX(): h1.getX());
		visible = (int) (h1.getX()-h0.getX());
		value = (int) h0.getX();
				
		horizontalScrollBar.setValues(value, visible, min, max);
		
		min = (int) (rectangle.getMinY() < v0.getY() ? rectangle.getMinY():v0.getY());
		max = (int) (rectangle.getMaxY() > v1.getY() ? rectangle.getMaxY():v1.getY());
		visible = (int) (v1.getY() - v0.getY());
		value = (int) v0.getY();

		// vertical scroll bar		
		verticalScrollBar.setValues(value, visible, min, max);
		
	}

	/**
	 * Listener to adjust the scroll bar parameters when the window is resized
	 */
	protected class ResizeListener extends ComponentAdapter {

		public void componentHidden(ComponentEvent e) {
		}

		public void componentResized(ComponentEvent e) {
			setScrollBars(vv);
		}

		public void componentShown(ComponentEvent e) {
		}
	}

	/**
	 * @return Returns the corner component.
	 */
	public JComponent getCorner() {
		return corner;
	}

	/**
	 * @param corner
	 *            The cornerButton to set.
	 */
	public void setCorner(JComponent corner) {
		this.corner = corner;
		corner.setPreferredSize(new Dimension(verticalScrollBar
				.getPreferredSize().width, horizontalScrollBar
				.getPreferredSize().height));
		south.add(this.corner, BorderLayout.EAST);
	}

	public JScrollBar getHorizontalScrollBar() {
		return horizontalScrollBar;
	}

	public JScrollBar getVerticalScrollBar() {
		return verticalScrollBar;
	}
}
