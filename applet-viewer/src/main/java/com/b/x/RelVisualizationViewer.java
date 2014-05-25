package com.b.x;

import java.awt.Dimension;
import java.awt.Shape;

import o.c15.Transformer;


import g.algorithms.layout.Layout;
import g.visualization.VisualizationModel;
import g.visualization.VisualizationViewer;

public class RelVisualizationViewer<V, E> extends VisualizationViewer<V,E> {

	
	public RelVisualizationViewer(Layout<V,E> layout) {
		this(new RelVisualizationModel<V,E>(layout));
	}
	
    /**
     * Create an instance with passed parameters.
     * 
     * @param layout		The Layout to apply, with its associated Graph
     * @param renderer		The Renderer to draw it with
     * @param preferredSize the preferred size of this View
     */
	public RelVisualizationViewer(Layout<V,E> layout, Dimension preferredSize) {
		this(new RelVisualizationModel<V,E>(layout, preferredSize), preferredSize);
	}
	
	/**
	 * Create an instance with passed parameters.
	 * 
	 * @param model
	 * @param renderer
	 */
	public RelVisualizationViewer(VisualizationModel<V,E> model) {
	    this(model, new Dimension(600,600));
	}
	/**
	 * Create an instance with passed parameters.
	 * 
	 * @param model
	 * @param renderer
	 * @param preferredSize initial preferred size of the view
	 */
	@SuppressWarnings("unchecked")
    public RelVisualizationViewer(VisualizationModel<V,E> model,
	        Dimension preferredSize) {
       super(model,preferredSize);
      this.setPickSupport(new RelShapePickSupport<V,E>(this));
	}
	
	protected Transformer<V,Shape> vertexLabelShapeTransformer;
	protected Transformer<E,Shape> edgeLabelShapeTransformer;
	
	public void setVertexLabelShapeTransformer(Transformer<V,Shape> vertexLabelShapeTransformer) {
		this.vertexLabelShapeTransformer = vertexLabelShapeTransformer;
	}
	
	 public Transformer<V,Shape> getVertexLabelShapeTransformer() {
		return vertexLabelShapeTransformer;
	}

	public Transformer<E, Shape> getEdgeLabelShapeTransformer() {
		return edgeLabelShapeTransformer;
	}

	public void setEdgeLabelShapeTransformer(
			Transformer<E, Shape> edgeLabelShapeTransformer) {
		this.edgeLabelShapeTransformer = edgeLabelShapeTransformer;
	}
}
