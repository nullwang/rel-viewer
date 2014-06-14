package g.visualization;

import java.awt.Shape;
import java.awt.geom.Point2D;

import g.visualization.transform.BidirectionalTransformer;
import g.visualization.transform.MutableTransformer;
import g.visualization.transform.shape.ShapeTransformer;
import g.visualization.util.ChangeEventSupport;

public interface MultiLayerTransformer extends BidirectionalTransformer, ShapeTransformer, ChangeEventSupport {

	
	/**
	 * @see g.visualization.VisualizationServer#setViewTransformer(g.visualization.transform.MutableTransformer)
	 */
	void setTransformer(Layer layer, MutableTransformer transformer);

	/**
	 * @return the layoutTransformer
	 */
	MutableTransformer getTransformer(Layer layer);

	/**
	 */
	Point2D inverseTransform(Layer layer, Point2D p);

	/**
	 */
	Point2D transform(Layer layer, Point2D p);

	/**
	 */
	Shape transform(Layer layer, Shape shape);
	
	Shape inverseTransform(Layer layer, Shape shape);

	void setToIdentity();

}