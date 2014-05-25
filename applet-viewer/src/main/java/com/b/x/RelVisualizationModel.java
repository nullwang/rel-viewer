package com.b.x;

import java.awt.Dimension;

import g.algorithms.layout.Layout;
import g.algorithms.util.IterativeContext;
import g.visualization.DefaultVisualizationModel;
import g.visualization.layout.ObservableCachingLayout;
import g.visualization.util.ChangeEventSupport;


public class RelVisualizationModel<V, E> extends DefaultVisualizationModel<V, E> {

	public RelVisualizationModel(Layout<V, E> layout, Dimension d) {
		super(layout, d);
	}

	public RelVisualizationModel(Layout<V, E> layout) {
		super(layout);
	}
	
	@Override
	public synchronized void setGraphLayout(Layout<V,E> layout, Dimension viewSize) {
		// remove listener from old layout
	    if(this.layout != null && this.layout instanceof ChangeEventSupport) {
	        ((ChangeEventSupport)this.layout).removeChangeListener(changeListener);
        }
	    // set to new layout
	    if(layout instanceof ChangeEventSupport) {
	    	this.layout = layout;
	    } else {
	    	this.layout = new ObservableCachingLayout<V,E>(layout);
	    }
		
		((ChangeEventSupport)this.layout).addChangeListener(changeListener);

        if(viewSize == null) {
            viewSize = new Dimension(600,600);
        }
		Dimension layoutSize = layout.getSize();
		// if the layout has NOT been initialized yet, initialize its size
		// now to the size of the VisualizationViewer window
		if(layoutSize == null) {
		    layout.setSize(viewSize);
        }
        if(relaxer != null) {
        	relaxer.stop();
        	relaxer = null;
        	//relaxer.relax();
        }
        if(layout instanceof IterativeContext) {
        	layout.initialize();
            if(relaxer == null) {
            	relaxer = new RelVisRunner((IterativeContext)this.layout);
            	relaxer.prerelax();
            	relaxer.relax();
            }
        }
        fireStateChanged();
	}
	
	//used in animation iterator, to solve the shaking
	public synchronized void setAnimatorGraphLayout(Layout<V,E> layout) {
		// remove listener from old layout
	    if(this.layout != null && this.layout instanceof ChangeEventSupport) {
	        ((ChangeEventSupport)this.layout).removeChangeListener(changeListener);
        }
	    // set to new layout
	    if(layout instanceof ChangeEventSupport) {
	    	this.layout = layout;
	    } else {
	    	this.layout = new ObservableCachingLayout<V,E>(layout);
	    }
		
		((ChangeEventSupport)this.layout).addChangeListener(changeListener);

		//Dimension layoutSize = layout.getSize();
		// if the layout has NOT been initialized yet, initialize its size
		// now to the size of the VisualizationViewer window
				
//		if(layout instanceof IterativeContext) {
//	        layout.initialize();
//		 }
       
        fireStateChanged();
	}

}
