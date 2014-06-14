/*
 * Copyright (c) 2003, the JUNG Project and the Regents of the University of
 * California All rights reserved.
 * 
 * This software is open-source under the BSD license; see either "license.txt"
 * or http://jung.sourceforge.net/license.txt for a description.
 */
package g.visualization;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;

import javax.swing.CellRendererPane;
import javax.swing.Icon;
import javax.swing.JComponent;

import o.c15.Predicate;
import o.c15.Transformer;
import o.c15.functors.ConstantTransformer;
import o.c15.functors.TruePredicate;


import g.algorithms.layout.GraphElementAccessor;
import g.graph.Graph;
import g.graph.util.Context;
import g.graph.util.DefaultParallelEdgeIndexFunction;
import g.graph.util.EdgeIndexFunction;
import g.graph.util.IncidentEdgeIndexFunction;
import g.visualization.decorators.ConstantDirectionalEdgeValueTransformer;
import g.visualization.decorators.DirectionalEdgeArrowTransformer;
import g.visualization.decorators.EdgeShape;
import g.visualization.picking.PickedState;
import g.visualization.renderers.DefaultEdgeLabelRenderer;
import g.visualization.renderers.DefaultVertexLabelRenderer;
import g.visualization.renderers.EdgeLabelRenderer;
import g.visualization.renderers.VertexLabelRenderer;
import g.visualization.transform.shape.GraphicsDecorator;


/**
 */
@SuppressWarnings("unchecked")
public class PluggableRenderContext<V, E> implements RenderContext<V, E> {
    
	protected float arrowPlacementTolerance = 1;
    protected Predicate<Context<Graph<V,E>,V>> vertexIncludePredicate = TruePredicate.getInstance();
    protected Transformer<V,Stroke> vertexStrokeTransformer = 
    	new ConstantTransformer(new BasicStroke(1.0f));
    
    protected Transformer<V,Shape> vertexShapeTransformer = 
        		new ConstantTransformer(
        		new Ellipse2D.Float(-10,-10,20,20));

    protected Transformer<V,String> vertexLabelTransformer = new ConstantTransformer(null);
    protected Transformer<V,Icon> vertexIconTransformer;
    protected Transformer<V,Font> vertexFontTransformer = 
        new ConstantTransformer(new Font("Helvetica", Font.PLAIN, 12));
    
    protected Transformer<V,Paint> vertexDrawPaintTransformer = new ConstantTransformer(Color.BLACK);
    protected Transformer<V,Paint> vertexFillPaintTransformer = new ConstantTransformer(Color.RED);
    
    protected Transformer<E,String> edgeLabelTransformer = new ConstantTransformer(null);
    protected Transformer<E,Stroke> edgeStrokeTransformer = new ConstantTransformer(new BasicStroke(1.0f));
    protected Transformer<E,Stroke> edgeArrowStrokeTransformer = new ConstantTransformer(new BasicStroke(1.0f));
    
    protected Transformer<Context<Graph<V,E>,E>,Shape> edgeArrowTransformer = 
        new DirectionalEdgeArrowTransformer<V,E>(10, 8, 4);
    
    protected Predicate<Context<Graph<V,E>,E>> edgeArrowPredicate = new DirectedEdgeArrowPredicate<V,E>();
    protected Predicate<Context<Graph<V,E>,E>> edgeIncludePredicate = TruePredicate.getInstance();
    protected Transformer<E,Font> edgeFontTransformer =
        new ConstantTransformer(new Font("Helvetica", Font.PLAIN, 12));
    protected Transformer<Context<Graph<V,E>,E>,Number> edgeLabelClosenessTransformer = 
        new ConstantDirectionalEdgeValueTransformer<V,E>(0.5, 0.65);
    protected Transformer<Context<Graph<V,E>,E>,Shape> edgeShapeTransformer = 
        new EdgeShape.QuadCurve<V,E>();
    protected Transformer<E,Paint> edgeFillPaintTransformer =
        new ConstantTransformer(null);
    protected Transformer<E,Paint> edgeDrawPaintTransformer =
        new ConstantTransformer(Color.black);
    protected Transformer<E,Paint> arrowFillPaintTransformer =
        new ConstantTransformer(Color.black);
    protected Transformer<E,Paint> arrowDrawPaintTransformer =
        new ConstantTransformer(Color.black);
    
    protected EdgeIndexFunction<V,E> parallelEdgeIndexFunction = 
        DefaultParallelEdgeIndexFunction.<V,E>getInstance();
    
    protected EdgeIndexFunction<V,E> incidentEdgeIndexFunction = 
        IncidentEdgeIndexFunction.<V,E>getInstance();
    
    protected MultiLayerTransformer multiLayerTransformer = new BasicTransformer();
    
	/**
	 * pluggable support for picking graph elements by
	 * finding them based on their coordinates.
	 */
	protected GraphElementAccessor<V, E> pickSupport;

    
    protected int labelOffset = LABEL_OFFSET;
    
    /**
     * the JComponent that this Renderer will display the graph on
     */
    protected JComponent screenDevice;
    
    protected PickedState<V> pickedVertexState;
    protected PickedState<E> pickedEdgeState;
    
    /**
     * The CellRendererPane is used here just as it is in JTree
     * and JTable, to allow a pluggable JLabel-based renderer for
     * Vertex and Edge label strings and icons.
     */
    protected CellRendererPane rendererPane = new CellRendererPane();
    
    /**
     * A default GraphLabelRenderer - picked Vertex labels are
     * blue, picked edge labels are cyan
     */
    protected VertexLabelRenderer vertexLabelRenderer = 
        new DefaultVertexLabelRenderer(Color.blue);
    
    protected EdgeLabelRenderer edgeLabelRenderer = new DefaultEdgeLabelRenderer(Color.cyan);
    
    protected GraphicsDecorator graphicsContext;
    
    PluggableRenderContext() {
        this.setEdgeShapeTransformer(new EdgeShape.QuadCurve<V,E>());
    }

	/**
	 * @return the vertexShapeTransformer
	 */
	public Transformer<V, Shape> getVertexShapeTransformer() {
		return vertexShapeTransformer;
	}

	/**
	 * @param vertexShapeTransformer the vertexShapeTransformer to set
	 */
	public void setVertexShapeTransformer(
			Transformer<V, Shape> vertexShapeTransformer) {
		this.vertexShapeTransformer = vertexShapeTransformer;
	}

	/**
	 * @return the vertexStrokeTransformer
	 */
	public Transformer<V, Stroke> getVertexStrokeTransformer() {
		return vertexStrokeTransformer;
	}

	/**
	 * @param vertexStrokeTransformer the vertexStrokeTransformer to set
	 */
	public void setVertexStrokeTransformer(
			Transformer<V, Stroke> vertexStrokeTransformer) {
		this.vertexStrokeTransformer = vertexStrokeTransformer;
	}

	public static float[] getDashing() {
        return dashing;
    }

    public static float[] getDotting() {
        return dotting;
    }

    /**
     * @see g.visualization.RenderContext#getArrow_placement_tolerance()
     */
    public float getArrowPlacementTolerance() {
        return arrowPlacementTolerance;
    }

    /**
     * @see g.visualization.RenderContext#setArrow_placement_tolerance(float)
     */
    public void setArrowPlacementTolerance(float arrow_placement_tolerance) {
        this.arrowPlacementTolerance = arrow_placement_tolerance;
    }

    /**
     * @see g.visualization.RenderContext#getEdgeArrowTransformer()
     */
    public Transformer<Context<Graph<V,E>,E>,Shape> getEdgeArrowTransformer() {
        return edgeArrowTransformer;
    }

    /**
     * @see g.visualization.RenderContext#setEdgeArrowTransformer(edu.uci.ics.jung.visualization.decorators.EdgeArrowTransformer)
     */
    public void setEdgeArrowTransformer(Transformer<Context<Graph<V,E>,E>,Shape> edgeArrowTransformer) {
        this.edgeArrowTransformer = edgeArrowTransformer;
    }

    /**
     * @see RenderContext#getEdgeArrowPredicate()
     */
    public Predicate<Context<Graph<V,E>,E>> getEdgeArrowPredicate() {
        return edgeArrowPredicate;
    }

    /**
     * @see RenderContext#setEdgeArrowPredicate(Predicate)
     */
    public void setEdgeArrowPredicate(Predicate<Context<Graph<V,E>,E>> edgeArrowPredicate) {
        this.edgeArrowPredicate = edgeArrowPredicate;
    }

    /**
     * @see g.visualization.RenderContext#getEdgeFontTransformer()
     */
    public Transformer<E,Font> getEdgeFontTransformer() {
        return edgeFontTransformer;
    }

    /**
     * @see g.visualization.RenderContext#setEdgeFontTransformer(edu.uci.ics.jung.visualization.decorators.EdgeFontTransformer)
     */
    public void setEdgeFontTransformer(Transformer<E,Font> edgeFontTransformer) {
        this.edgeFontTransformer = edgeFontTransformer;
    }

    /**
     * @see g.visualization.RenderContext#getEdgeIncludePredicate()
     */
    public Predicate<Context<Graph<V,E>,E>> getEdgeIncludePredicate() {
        return edgeIncludePredicate;
    }

    /**
     * @see g.visualization.RenderContext#setEdgeIncludePredicate(o.c15.Predicate)
     */
    public void setEdgeIncludePredicate(Predicate<Context<Graph<V,E>,E>> edgeIncludePredicate) {
        this.edgeIncludePredicate = edgeIncludePredicate;
    }

    /**
     * @see g.visualization.RenderContext#getEdgeLabelClosenessTransformer()
     */
    public Transformer<Context<Graph<V,E>,E>,Number> getEdgeLabelClosenessTransformer() {
        return edgeLabelClosenessTransformer;
    }

    /**
     * @see g.visualization.RenderContext#setEdgeLabelClosenessTransformer(edu.uci.ics.jung.visualization.decorators.NumberDirectionalEdgeValue)
     */
    public void setEdgeLabelClosenessTransformer(
    		Transformer<Context<Graph<V,E>,E>,Number> edgeLabelClosenessTransformer) {
        this.edgeLabelClosenessTransformer = edgeLabelClosenessTransformer;
    }

    /**
     * @see g.visualization.RenderContext#getEdgeLabelRenderer()
     */
    public EdgeLabelRenderer getEdgeLabelRenderer() {
        return edgeLabelRenderer;
    }

    /**
     * @see g.visualization.RenderContext#setEdgeLabelRenderer(edu.uci.ics.jung.visualization.EdgeLabelRenderer)
     */
    public void setEdgeLabelRenderer(EdgeLabelRenderer edgeLabelRenderer) {
        this.edgeLabelRenderer = edgeLabelRenderer;
    }

    /**
     * @see g.visualization.RenderContext#getEdgePaintTransformer()
     */
    public Transformer<E,Paint> getEdgeFillPaintTransformer() {
        return edgeFillPaintTransformer;
    }

    /**
     * @see g.visualization.RenderContext#setEdgePaintTransformer(edu.uci.ics.jung.visualization.decorators.EdgePaintTransformer)
     */
    public void setEdgeDrawPaintTransformer(Transformer<E,Paint> edgeDrawPaintTransformer) {
        this.edgeDrawPaintTransformer = edgeDrawPaintTransformer;
    }

    /**
     * @see g.visualization.RenderContext#getEdgePaintTransformer()
     */
    public Transformer<E,Paint> getEdgeDrawPaintTransformer() {
        return edgeDrawPaintTransformer;
    }

    /**
     * @see g.visualization.RenderContext#setEdgePaintTransformer(edu.uci.ics.jung.visualization.decorators.EdgePaintTransformer)
     */
    public void setEdgeFillPaintTransformer(Transformer<E,Paint> edgeFillPaintTransformer) {
        this.edgeFillPaintTransformer = edgeFillPaintTransformer;
    }

    /**
     * @see g.visualization.RenderContext#getEdgeShapeTransformer()
     */
    public Transformer<Context<Graph<V,E>,E>,Shape> getEdgeShapeTransformer() {
        return edgeShapeTransformer;
    }

    /**
     * @see g.visualization.RenderContext#setEdgeShapeTransformer(edu.uci.ics.jung.visualization.decorators.EdgeShapeTransformer)
     */
    public void setEdgeShapeTransformer(Transformer<Context<Graph<V,E>,E>,Shape> edgeShapeTransformer) {
        this.edgeShapeTransformer = edgeShapeTransformer;
        if(edgeShapeTransformer instanceof EdgeShape.Orthogonal) {
        	((EdgeShape.IndexedRendering<V, E>)edgeShapeTransformer).setEdgeIndexFunction(this.incidentEdgeIndexFunction);
        } else 
        if(edgeShapeTransformer instanceof EdgeShape.IndexedRendering) {
            ((EdgeShape.IndexedRendering<V,E>)edgeShapeTransformer).setEdgeIndexFunction(this.parallelEdgeIndexFunction);
        }
    }

    /**
     * @see g.visualization.RenderContext#getEdgeLabelTransformer()
     */
    public Transformer<E,String> getEdgeLabelTransformer() {
        return edgeLabelTransformer;
    }

    /**
     * @see g.visualization.RenderContext#setEdgeLabelTransformer(edu.uci.ics.jung.visualization.decorators.EdgeLabelTransformer)
     */
    public void setEdgeLabelTransformer(Transformer<E,String> edgeLabelTransformer) {
        this.edgeLabelTransformer = edgeLabelTransformer;
    }

    /**
     * @see g.visualization.RenderContext#getEdgeStrokeTransformer()
     */
    public Transformer<E,Stroke> getEdgeStrokeTransformer() {
        return edgeStrokeTransformer;
    }

    /**
     * @see g.visualization.RenderContext#setEdgeStrokeTransformer(edu.uci.ics.jung.visualization.decorators.EdgeStrokeTransformer)
     */
    public void setEdgeStrokeTransformer(Transformer<E,Stroke> edgeStrokeTransformer) {
        this.edgeStrokeTransformer = edgeStrokeTransformer;
    }

    /**
     * @see g.visualization.RenderContext#getEdgeStrokeTransformer()
     */
    public Transformer<E,Stroke> getEdgeArrowStrokeTransformer() {
        return edgeArrowStrokeTransformer;
    }

    /**
     * @see g.visualization.RenderContext#setEdgeStrokeTransformer(edu.uci.ics.jung.visualization.decorators.EdgeStrokeTransformer)
     */
    public void setEdgeArrowStrokeTransformer(Transformer<E,Stroke> edgeArrowStrokeTransformer) {
        this.edgeArrowStrokeTransformer = edgeArrowStrokeTransformer;
    }

    /**
     * @see RenderContext#getGraphicsContext()
     */
    public GraphicsDecorator getGraphicsContext() {
        return graphicsContext;
    }

    /**
     * @see RenderContext#setGraphicsContext(GraphicsDecorator)
     */
    public void setGraphicsContext(GraphicsDecorator graphicsContext) {
        this.graphicsContext = graphicsContext;
    }

    /**
     * @see RenderContext#getLabelOffset()
     */
    public int getLabelOffset() {
        return labelOffset;
    }

    /**
     * @see RenderContext#setLabelOffset(int)
     */
    public void setLabelOffset(int labelOffset) {
        this.labelOffset = labelOffset;
    }

    /**
     * @see g.visualization.RenderContext#getParallelEdgeIndexTransformer()
     */
    public EdgeIndexFunction<V, E> getParallelEdgeIndexFunction() {
        return parallelEdgeIndexFunction;
    }

    /**
     * @see g.visualization.RenderContext#setParallelEdgeIndexFunction(edu.uci.ics.graph.util.ParallelEdgeIndexFunction)
     */
    public void setParallelEdgeIndexFunction(
            EdgeIndexFunction<V, E> parallelEdgeIndexFunction) {
        this.parallelEdgeIndexFunction = parallelEdgeIndexFunction;
        // reset the edge shape transformer, as the parallel edge index function
        // is used by it
        this.setEdgeShapeTransformer(getEdgeShapeTransformer());
    }

    /**
     * @see g.visualization.RenderContext#getPickedEdgeState()
     */
    public PickedState<E> getPickedEdgeState() {
        return pickedEdgeState;
    }

    /**
     * @see g.visualization.RenderContext#setPickedEdgeState(g.visualization.picking.PickedState)
     */
    public void setPickedEdgeState(PickedState<E> pickedEdgeState) {
        this.pickedEdgeState = pickedEdgeState;
    }

    /**
     * @see g.visualization.RenderContext#getPickedVertexState()
     */
    public PickedState<V> getPickedVertexState() {
        return pickedVertexState;
    }

    /**
     * @see g.visualization.RenderContext#setPickedVertexState(g.visualization.picking.PickedState)
     */
    public void setPickedVertexState(PickedState<V> pickedVertexState) {
        this.pickedVertexState = pickedVertexState;
    }

    /**
     * @see g.visualization.RenderContext#getRendererPane()
     */
    public CellRendererPane getRendererPane() {
        return rendererPane;
    }

    /**
     * @see g.visualization.RenderContext#setRendererPane(javax.swing.CellRendererPane)
     */
    public void setRendererPane(CellRendererPane rendererPane) {
        this.rendererPane = rendererPane;
    }

    /**
     * @see g.visualization.RenderContext#getScreenDevice()
     */
    public JComponent getScreenDevice() {
        return screenDevice;
    }

    /**
     * @see g.visualization.RenderContext#setScreenDevice(g.visualization.VisualizationViewer)
     */
    public void setScreenDevice(JComponent screenDevice) {
        this.screenDevice = screenDevice;
        screenDevice.add(rendererPane);
    }

    /**
     * @see g.visualization.RenderContext#getVertexFontTransformer()
     */
    public Transformer<V,Font> getVertexFontTransformer() {
        return vertexFontTransformer;
    }

    /**
     * @see g.visualization.RenderContext#setVertexFontTransformer(edu.uci.ics.jung.visualization.decorators.VertexFontTransformer)
     */
    public void setVertexFontTransformer(Transformer<V,Font> vertexFontTransformer) {
        this.vertexFontTransformer = vertexFontTransformer;
    }

    /**
     * @see g.visualization.RenderContext#getVertexIconTransformer()
     */
    public Transformer<V,Icon> getVertexIconTransformer() {
        return vertexIconTransformer;
    }

    /**
     * @see g.visualization.RenderContext#setVertexIconTransformer(edu.uci.ics.jung.visualization.decorators.VertexIconTransformer)
     */
    public void setVertexIconTransformer(Transformer<V,Icon> vertexIconTransformer) {
        this.vertexIconTransformer = vertexIconTransformer;
    }

    /**
     * @see g.visualization.RenderContext#getVertexIncludePredicate()
     */
    public Predicate<Context<Graph<V,E>,V>> getVertexIncludePredicate() {
        return vertexIncludePredicate;
    }

    /**
     * @see g.visualization.RenderContext#setVertexIncludePredicate(o.c15.Predicate)
     */
    public void setVertexIncludePredicate(Predicate<Context<Graph<V,E>,V>> vertexIncludePredicate) {
        this.vertexIncludePredicate = vertexIncludePredicate;
    }

    /**
     * @see g.visualization.RenderContext#getVertexLabelRenderer()
     */
    public VertexLabelRenderer getVertexLabelRenderer() {
        return vertexLabelRenderer;
    }

    /**
     * @see g.visualization.RenderContext#setVertexLabelRenderer(edu.uci.ics.jung.visualization.VertexLabelRenderer)
     */
    public void setVertexLabelRenderer(VertexLabelRenderer vertexLabelRenderer) {
        this.vertexLabelRenderer = vertexLabelRenderer;
    }

    /**
     * @see g.visualization.RenderContext#getVertexPaintTransformer()
     */
    public Transformer<V,Paint> getVertexFillPaintTransformer() {
        return vertexFillPaintTransformer;
    }

    /**
     * @see g.visualization.RenderContext#setVertexPaintTransformer(edu.uci.ics.jung.visualization.decorators.VertexPaintTransformer)
     */
    public void setVertexFillPaintTransformer(Transformer<V,Paint> vertexFillPaintTransformer) {
        this.vertexFillPaintTransformer = vertexFillPaintTransformer;
    }

    /**
     * @see g.visualization.RenderContext#getVertexPaintTransformer()
     */
    public Transformer<V,Paint> getVertexDrawPaintTransformer() {
        return vertexDrawPaintTransformer;
    }

    /**
     * @see g.visualization.RenderContext#setVertexPaintTransformer(edu.uci.ics.jung.visualization.decorators.VertexPaintTransformer)
     */
    public void setVertexDrawPaintTransformer(Transformer<V,Paint> vertexDrawPaintTransformer) {
        this.vertexDrawPaintTransformer = vertexDrawPaintTransformer;
    }

    /**
     * @see g.visualization.RenderContext#getVertexLabelTransformer()
     */
    public Transformer<V,String> getVertexLabelTransformer() {
        return vertexLabelTransformer;
    }

    /**
     * @see g.visualization.RenderContext#setVertexLabelTransformer(edu.uci.ics.jung.visualization.decorators.VertexLabelTransformer)
     */
    public void setVertexLabelTransformer(Transformer<V,String> vertexLabelTransformer) {
        this.vertexLabelTransformer = vertexLabelTransformer;
    }

	/**
	 * @return the pickSupport
	 */
	public GraphElementAccessor<V, E> getPickSupport() {
		return pickSupport;
	}

	/**
	 * @param pickSupport the pickSupport to set
	 */
	public void setPickSupport(GraphElementAccessor<V, E> pickSupport) {
		this.pickSupport = pickSupport;
	}
	
	/**
	 * @return the basicTransformer
	 */
	public MultiLayerTransformer getMultiLayerTransformer() {
		return multiLayerTransformer;
	}

	/**
	 * @param basicTransformer the basicTransformer to set
	 */
	public void setMultiLayerTransformer(MultiLayerTransformer basicTransformer) {
		this.multiLayerTransformer = basicTransformer;
	}

	/**
	 * @see RenderContext#getArrowDrawPaintTransformer()
	 */
	public Transformer<E, Paint> getArrowDrawPaintTransformer() {
		return arrowDrawPaintTransformer;
	}

	/**
	 * @see RenderContext#getArrowFillPaintTransformer()
	 */
	public Transformer<E, Paint> getArrowFillPaintTransformer() {
		return arrowFillPaintTransformer;
	}

	/**
	 * @see RenderContext#setArrowDrawPaintTransformer(Transformer)
	 */
	public void setArrowDrawPaintTransformer(Transformer<E, Paint> arrowDrawPaintTransformer) {
		this.arrowDrawPaintTransformer = arrowDrawPaintTransformer;
		
	}

	/**
	 * @see RenderContext#setArrowFillPaintTransformer(Transformer)
	 */
	public void setArrowFillPaintTransformer(Transformer<E, Paint> arrowFillPaintTransformer) {
		this.arrowFillPaintTransformer = arrowFillPaintTransformer;
		
	}
}


