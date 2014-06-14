package com.b.x;

import java.awt.geom.Point2D;
import g.algorithms.layout.Layout;
import g.algorithms.layout.util.VisRunner;
import g.algorithms.util.IterativeContext;
import g.graph.Graph;
import g.visualization.VisualizationViewer;

public class RelLayoutTransition<V, E>implements IterativeContext {
	protected Layout<V,E> startLayout;
	protected Layout<V,E> endLayout;
	protected Layout<V,E> transitionLayout;
	protected boolean done = false;
	protected int count = 20;
	protected int counter = 0;
	protected VisualizationViewer<V,E> vv;
	protected VisRunner relaxer;
	
	public RelLayoutTransition(VisualizationViewer<V, E> vv,
			Layout<V, E> startLayout, Layout<V, E> endLayout) {
		
		this.vv = vv;
		this.startLayout = startLayout;
		this.endLayout = endLayout;
		if(endLayout instanceof IterativeContext) {
			relaxer = new RelVisRunner((IterativeContext)endLayout);
			relaxer.prerelax();			
			relaxer.relax();
		}
		this.transitionLayout =
			new RelStaticLayout<V,E>(startLayout.getGraph(), startLayout);
		vv.setGraphLayout(transitionLayout);	
	}

	public void step() {
		synchronized (vv.getGraphLayout()) {
			Graph<V, E> g = endLayout.getGraph();
			for (V v : g.getVertices()) {
				Point2D tp = transitionLayout.transform(v);
				if (tp == null)
					tp = new Point2D.Double(0, 0);
				Point2D fp = endLayout.transform(v);

				double dx = (fp.getX() - tp.getX()) / (count - counter);
				double dy = (fp.getY() - tp.getY()) / (count - counter);
				
				transitionLayout.setLocation(v, new Point2D.Double(tp.getX()
						+ dx, tp.getY() + dy));
			}
			counter++;
			if (counter >= count) {
				done = true;
				//((RelVisualizationModel)vv.getModel()).setRelaxer(relaxer);
				((RelVisualizationModel)vv.getModel()).setAnimatorGraphLayout(endLayout);
			}
			vv.repaint();
		}
	}

	public boolean done() {
		return done;
	}

}
