package com.b.g;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.CellRendererPane;
import javax.swing.JPanel;
import javax.swing.ToolTipManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import o.c15.Transformer;
import o.c15.functors.ConstantTransformer;


import com.b.u.LeftBaseHourRuler;
import com.b.u.RightBaseHourRuler;
import com.b.u.ScaleRuler;
import com.b.u.TimeRuler;
import com.b.u.TimeUtil;
import com.b.x.MarkFactory;
import com.b.x.TimeLabelRenderer;
import com.b.x.TimeSeqLayout;

import g.algorithms.layout.Layout;
import g.visualization.Layer;
import g.visualization.VisualizationViewer;
import g.visualization.layout.ObservableCachingLayout;
import g.visualization.transform.BidirectionalTransformer;

public class TimeBar extends JPanel {

	private CellRendererPane rendererPane = new CellRendererPane();
	private Transformer<Long, Font> labelFontTransformer = new ConstantTransformer(
			new Font("Helvetica", Font.PLAIN, 10));
	private Transformer<Long, Font> textFontTransformer = new ConstantTransformer(
			new Font("Helvetica", Font.PLAIN, 10));

	List<TimeRuler> timeRulers = new ArrayList<TimeRuler>();

	private int basey = 30;

	protected VisualizationViewer<String, String> vv;

	protected List<Point2D> controlPositions = new ArrayList<Point2D>();
	protected List<Long> controlMarks = new ArrayList<Long>();

	public TimeBar(VisualizationViewer<String, String> vv) {
		// super(new BorderLayout());
		this.vv = vv;
		addComponentListener(new ResizeListener());

		vv.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent evt) {
				VisualizationViewer<String, String> vv = (VisualizationViewer<String, String>) evt
						.getSource();
				setMarks(vv);
			}
		});
		
		ToolTipManager.sharedInstance().registerComponent(this);
	}

	public Dimension getPreferredSize() {
		return new Dimension(600, 35);
	}

	protected void setMarks(VisualizationViewer<String, String> vv) {
		this.vv = vv;

		Layout<String, String> layout = vv.getGraphLayout();
		Layout<String, String> relLayout = layout;
		TimeSeqLayout tsl = null;

		if (layout instanceof ObservableCachingLayout) {
			ObservableCachingLayout ol = (ObservableCachingLayout) layout;
			relLayout = ol.getDelegate();
		}
		// time layout
		if (relLayout instanceof TimeSeqLayout) {
			tsl = (TimeSeqLayout) relLayout;
		}

		if (tsl == null)
			return;

		List marks = new ArrayList<Long>();
		List pos = new ArrayList<Point2D>();

		Map timeCoord = tsl.getTimeCoord();
		Set c = tsl.getControllingItems();

		for (Object object : c) {
			marks.add(timeCoord.get(object));
			Point2D point = new Point2D.Double(tsl.getX(object), tsl
					.getY(object));
			pos.add(point);
		}

		setMarks(marks, pos);
	}

	/**
	 * 
	 * @param marks
	 */
	protected void setMarks(List<Long> marks, List<Point2D> pos) {
		this.controlPositions.clear();
		this.controlMarks.clear();

		BidirectionalTransformer viewTransformer = vv.getRenderContext()
				.getMultiLayerTransformer().getTransformer(Layer.VIEW);
		BidirectionalTransformer layoutTransformer = vv.getRenderContext()
				.getMultiLayerTransformer().getTransformer(Layer.LAYOUT);

		for (int i = 0; i < marks.size(); i++) {
			Point2D p = pos.get(i);
			Point2D lp = layoutTransformer.transform(p);
			Point2D vp = viewTransformer.transform(lp);
			vp.setLocation(vp.getX(), basey);
			if (posHit(vp)) {
				this.controlPositions.add(vp);
				this.controlMarks.add(marks.get(i));
			}
		}

		buildRulers();

		repaint();
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		Graphics2D g2d = (Graphics2D) g;

		for (int i = 0; i < controlMarks.size(); i++) {
			double x = controlPositions.get(i).getX();

			Shape s = MarkFactory.getWedgeArrow(4, 4);
			AffineTransform xf = AffineTransform.getTranslateInstance(x, basey);
			xf.rotate(-Math.PI / 2);
			Shape as = xf.createTransformedShape(s);

			Paint oldPaint = g2d.getPaint();
			g2d.setPaint(Color.BLACK);
			g2d.fill(as);
			g2d.setPaint(oldPaint);
			g2d.draw(as);
		}

		int stype = -1;
		int etype = -1;
		Long smark = null;
		double sx = 0;
		Long emark = null;
		double ex = 0;
		String slabel = null;
		String elabel = null;

		for (int j = 0; j < timeRulers.size(); j++) {
			TimeRuler tr = timeRulers.get(j);
			etype = tr.getType();
			// draw label
			List<Long> labelMarks = tr.getLabelMarks();
			for (int kk = 0; kk < labelMarks.size(); kk++) {
				emark = labelMarks.get(kk);
				ex = tr.getMarkPos(emark);
				elabel = tr.getLabelText(emark);
				Component c = null;
				if (slabel != null) {
					TimeLabelRenderer labelRenderer = new TimeLabelRenderer();
					c = labelRenderer.getTimeLabelRendererComponent(this,
							slabel, labelFontTransformer.transform(smark));
				}
//				else if (elabel != null) {
//					TimeLabelRenderer labelRenderer = new TimeLabelRenderer();
//					c = labelRenderer.getTimeLabelRendererComponent(this,
//							elabel, labelFontTransformer.transform(emark));
//				}
				if (c != null) {
					Dimension d = c.getPreferredSize();
					if (d.getWidth() < ex - sx) {
						Shape s = MarkFactory.getLine(15);
						AffineTransform xf = AffineTransform
								.getTranslateInstance(sx, 1);
						Shape as = xf.createTransformedShape(s);

						AffineTransform exf = AffineTransform
								.getTranslateInstance(ex, 1);
						Shape eas = exf.createTransformedShape(s);
						Paint oldPaint = g2d.getPaint();
						g2d.setPaint(Color.BLACK);
						g2d.fill(as);
						g2d.fill(eas);
						g2d.setPaint(oldPaint);
						g2d.draw(as);
						g2d.draw(eas);
						//g2d.drawLine((int) sx, basey - 19, (int) ex, basey - 19);
						
						rendererPane.paintComponent(g2d, c, c.getParent(),
								(int) ( (ex + sx - d.width)/ 2d), 1, d.width,
								d.height, true);
					}
				}

				slabel = elabel;
				smark = emark;
				sx = ex;
			}
			stype = etype;

			List<Long> marks = tr.getMarks();
			for (int k = 0; k < marks.size(); k++) {
				Long mark = marks.get(k);
				double x = tr.getMarkPos(mark);

				Shape s = MarkFactory.getLine(2);
				AffineTransform xf = AffineTransform.getTranslateInstance(x,
						basey - 2);
				Shape as = xf.createTransformedShape(s);
				Paint oldPaint = g2d.getPaint();
				g2d.setPaint(Color.BLACK);
				g2d.fill(as);
				g2d.setPaint(oldPaint);
				g2d.draw(as);

				String text = tr.getMarkText(mark);
				TimeLabelRenderer labelRenderer = new TimeLabelRenderer();
				Component c = labelRenderer.getTimeLabelRendererComponent(this, text,
						textFontTransformer.transform(mark));
				Dimension d = c.getPreferredSize();

				if (tr.getLength() <= 1)
					break;
				double markAverageLen = tr.getLength() / marks.size();

				int disFactor = (int) ((1.5 * d.getWidth()) / markAverageLen) + 1;

				if (disFactor > 1 && k % disFactor != 0)
					continue;

				rendererPane.paintComponent(g2d, c, c.getParent(),
						(int) (x - d.width / 2), basey - d.height-2, d.width,
						d.height, true);
				if (disFactor > 1) {
					s = MarkFactory.getLine(2);
					xf = AffineTransform.getTranslateInstance(x, basey - 4);
					as = xf.createTransformedShape(s);
					oldPaint = g2d.getPaint();
					g2d.setPaint(Color.BLACK);
					g2d.fill(as);
					g2d.setPaint(oldPaint);
					g2d.draw(as);
				}

			}
		}
	}

	protected void buildRulers() {
		this.timeRulers.clear();

		Dimension d = vv.getSize();
		double sx = 0, ex = 0;
		long st = 0, et = 0;

		if (controlMarks.size() == 0) {
			sx = ex = d.getWidth() / 2;
			Calendar c = Calendar.getInstance();
			st = et = c.getTimeInMillis();

			RightBaseHourRuler rr = new RightBaseHourRuler(sx, et, ex);
			timeRulers.add(rr);

			LeftBaseHourRuler lr = new LeftBaseHourRuler(st, sx, d.getWidth());
			timeRulers.add(lr);

			return;
		}

		for (int i = 0; i < controlMarks.size(); i++) {
			sx = controlPositions.get(i).getX();
			st = controlMarks.get(i);
			if (i < controlMarks.size() - 1) {
				ex = controlPositions.get(i + 1).getX();
				et = controlMarks.get(i + 1);
			}

			if (i == 0) {
				RightBaseHourRuler rr = new RightBaseHourRuler(0, st, sx);
				timeRulers.add(rr);
			}

			if (i == controlMarks.size() - 1) {
				LeftBaseHourRuler lr = new LeftBaseHourRuler(st, sx, d
						.getWidth());
				timeRulers.add(lr);
			}

			if (i >= 0 && i < controlMarks.size() - 1) {
				ScaleRuler sr = new ScaleRuler(st, sx, et, ex);
				timeRulers.add(sr);
			}
		}
	}
	
	 public String getToolTipText(MouseEvent event)
	 {
		 Point2D p = event.getPoint();
		 for (int i = 0; i < controlMarks.size(); i++) {
				if( p.distance(controlPositions.get(i)) <= 4 )
					return TimeUtil.getDayHourName(controlMarks.get(i));				
		 }
		 for( int i=0; i<timeRulers.size(); i++)
		 {
			 TimeRuler tr = timeRulers.get(i);
			 List<Long> marks = tr.getMarks();
			 for( int j=0; j<marks.size(); j++)
			 {
				 double x = tr.getMarkPos(marks.get(j));
				 if( p.getY() - basey <= 4 && p.getY() - basey >= -2 &&
						 p.getX() - x <=2 && p.getX() - x >= -2 	)
				 {
					 return TimeUtil.getDayHourName(marks.get(j));
				 }
			 }
		 }
		 
		 return super.getToolTipText(event);
	 }

	protected boolean posHit(Point2D p) {
		Dimension d = vv.getSize();

		if (p.getX() <= d.getWidth() && p.getX() >= 0)
			return true;

		return false;
	}

	protected class ResizeListener extends ComponentAdapter {

		public void componentHidden(ComponentEvent e) {
		}

		public void componentResized(ComponentEvent e) {
			setMarks(vv);
		}

		public void componentShown(ComponentEvent e) {
		}
	}
}