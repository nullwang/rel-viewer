package com.b.x;

import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import o.c15.Transformer;

import g.algorithms.layout.AbstractLayout;
import g.algorithms.layout.FRLayout;
import g.algorithms.layout.util.RandomLocationTransformer;
import g.algorithms.util.IterativeContext;
import g.graph.Graph;

@SuppressWarnings(value = { "unchecked" })
public class TimeSeqLayout<V, E> extends AbstractLayout<V, E> implements
		IterativeContext {

	public static int TOP = 0;
	public static int DOWN = 1;

	protected double controlMutiplier = 0.75;

	protected FRLayout<V, E> frLayout;
	protected List<V> themedVs;
	protected Set<V> fadedVs = new TreeSet<V>();

	protected Map<V, Set<V>> controllingVs = new HashMap<V, Set<V>>();
	protected TreeSet<V> cset = null;

	protected int direction = 0;

	protected Map<V, Long> timeCoord;
	protected float dist;

	protected double width = 30;
	protected double height = 30;

	private class TimeCompare implements Comparator {
		public TimeCompare() {
		}

		public int compare(Object o1, Object o2) {
			if (timeCoord != null) {

				Long t1 = timeCoord.get(o1);
				Long t2 = timeCoord.get(o2);
				t1 = t1 == null ? new Long(0) : t1;
				t2 = t2 == null ? new Long(0) : t2;

				if (t1 - t2 > 0)
					return 1;
				else if (t1 - t2 == 0)
					return 0;
				else
					return -1;
			}

			return 0;
		}
	}
	
	@Override
	public Dimension getSize() {
		return frLayout.getSize();
//		double x = size.getWidth() > width ? size.getWidth() : width;
//		double y = size.getHeight() > height ? size.getHeight() : height;
//
//		Dimension d = new Dimension();
//		d.setSize(x, y);
//		return d;
	}

	public TimeSeqLayout(Graph graph, float dis, Map<V, Long> timeCoord,
			List<V> themedVs) {
		super(graph);
		this.dist = dis;
		this.timeCoord = timeCoord;
		this.themedVs = themedVs;
		this.frLayout = new FRLayout<V, E>(graph);
		frLayout.setRepulsionMultiplier(1);
		frLayout.setAttractionMultiplier(1);
		//force frLayout initializing
		//frLayout.setSize(new Dimension(1,1));
	}
	
	@Override
	public void setInitializer(Transformer<V,Point2D> initializer) {	
		this.initialized = true;
		frLayout.setInitializer(initializer);
	}

	@Override
	public void setSize(Dimension size) {
		initialize();
		double x = size.getWidth() > width ? size.getWidth() : width;
		double y = size.getHeight() > height ? size.getHeight() : height;

		Dimension d = new Dimension();
		d.setSize(x, y);
		//super.setSize(d);
		//frLayout.setInitializer(this);
		if( !initialized) {
			frLayout.setInitializer(new RandomLocationTransformer<V>(d));
		}
		frLayout.setSize(d);
		// TestLayout.printCoord(frLayout);
	}

	public void initialize() {
		while (true) {
			try {
				double starty = 0;
				Iterator it = themedVs.iterator();
				while (it.hasNext()) {
					starty += dist;
					V v1 = (V) it.next();					
					frLayout.setLocation(v1, 0, starty);
					if (starty > height)
						height = starty;

					frLayout.lock(v1, true);

					for (V v2 : graph.getNeighbors(v1)) {
						if (timeCoord.containsKey(v2) && v1 != v2 
								&& ! themedVs.contains(v2)) {
							double y = starty;

							if (direction == TOP)
								y -= controlMutiplier * dist;
							else if (direction == DOWN)
								y += controlMutiplier * dist;

							Set s = controllingVs.get(v1);
							if (s == null) {
								s = new TreeSet<V>();
							}
							frLayout.setLocation(v2, 0, y);

							s.add(v2);
							frLayout.lock(v2, true);
							controllingVs.put(v1, s);
						}
					}
					
					//no controlling items
					Set s = controllingVs.get(v1);
					if( s == null || s!=null && s.size()<1)
					{
						frLayout.lock(v1,false);
						it.remove();
						if( height == starty )
							height -= dist;
						starty -= dist;					
					}
				}

				Set<V> cs = getControllingItems();
				double startx = 0;
				for (V v2 : cs) {
					startx += dist;
					double y = frLayout.getY(v2);
					if (startx > width)
						width = startx;
					frLayout.setLocation(v2, startx, y);
					frLayout.lock(v2, true);
				}
				for (V v1 : graph.getVertices()) {
					if (!themedVs.contains(v1) && !cs.contains(v1)) {
						fadedVs.add(v1);
						frLayout.lock(v1,false);
					}
				}

				break;
			} catch (ConcurrentModificationException cme) {
			}
		}
	}

	public void reset() {
		initialize();
	}

	public boolean done() {
		return frLayout.done();
	}

	public void step() {
		frLayout.step();
	}

	public double getControlMutiplier() {
		return controlMutiplier;
	}

	public void setControlMutiplier(double controlMutiplier) {
		this.controlMutiplier = controlMutiplier;
	}

	public int getDirection() {
		return direction;
	}

	public void setDirection(int direction) {
		this.direction = direction;
	}

	public TreeSet<V> getControllingItems() {
		if (cset != null)
			return cset;
		else {
			cset = new TreeSet<V>(new TimeCompare());
			for (Set<V> s : controllingVs.values()) {
				for (V v1 : s) {
					if (!cset.contains(v1))
						cset.add(v1);
				}
			}
		}

		return cset;
	}
	
	public Object getLastItem(Object o)
	{
		Object[] objs = getControllingItems().toArray();
		for( int i=0; i<objs.length; i++)
		{
			Object v = objs[i];
			if((v ==o || v.equals(o))  && i-1 >=0)
			{
				return objs[i-1];				
			}
		}
		return null;	
	}
	
	public Object getNextItem(Object o)
	{
		Object[] objs = getControllingItems().toArray();
		for( int i=0; i<objs.length; i++)
		{
			Object v = objs[i];
			if( (v ==o || v.equals(o)) && i+1 < objs.length)
			{
				return objs[i+1];				
			}
		}
		return null;	
	}

	public Map<V, Long> getTimeCoord() {
		return timeCoord;
	}

	public void setTimeCoord(Map<V, Long> timeCoord) {
		this.timeCoord = timeCoord;
	}

	public Set<V> getFadedItems() {
		return fadedVs;
	}

	public List<V> getThemedItems() {
		return themedVs;
	}

	public boolean isControllingItem(V v) {
		return getControllingItems().contains(v);
	}

	public boolean isFadedItem(V v) {
		return fadedVs.contains(v);
	}

	public boolean isThemedItem(V v) {
		return themedVs.contains(v);
	}

	@Override
	public Point2D transform(V v) {
		return frLayout.transform(v);
	}

	@Override
	public void setLocation(V picked, double x, double y) {
		frLayout.setLocation(picked, x, y);
	}

	@Override
	public void setLocation(V picked, Point2D p) {
		frLayout.setLocation(picked, p);
	}

	@Override
	public double getX(V v) {
		return frLayout.getX(v);
	}

	@Override
	public double getY(V v) {
		return frLayout.getY(v);
	}
	
	@Override
	public void lock(V v, boolean state) {
		frLayout.lock(v,state);
	}
	
	@Override
	public void lock(boolean lock)
	{
		frLayout.lock(lock);
	}

}
