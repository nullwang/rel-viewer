package com.b.x;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.util.Set;

import o.c15.Transformer;


import com.b.g.Generator;

import g.visualization.Layer;
import g.visualization.RenderContext;
import g.visualization.decorators.VertexIconShapeTransformer;
import g.visualization.transform.MutableTransformer;

public class RelVertexIconShapeTransformer<V> extends
		VertexIconShapeTransformer<V> {
	
	public Boolean getEmphasised() {
		return Generator.getEmphasised();
	}

	protected RenderContext renderContext;
	
	public Set getEmphasisedVertex() {
		return Generator.getEmphasisedEnds();
	}

	public RenderContext getRenderContext() {
		return renderContext;
	}

	public void setRenderContext(RenderContext renderContext) {
		this.renderContext = renderContext;
	}

	public RelVertexIconShapeTransformer(Transformer<V, Shape> delegate) {
		super(delegate);
	}
	
	@Override
	public Shape transform(V v) {
		Shape shape = super.transform(v);
		
		if(this.getEmphasisedVertex().contains(v) && this.getEmphasised() && renderContext!=null )
		{
			MutableTransformer viewTransformer = renderContext.getMultiLayerTransformer().getTransformer(Layer.VIEW);
			double scalex = 1/viewTransformer.getScaleX();
			double scaley = 1/viewTransformer.getScaleY();
			AffineTransform af = AffineTransform.getScaleInstance(scalex,scaley);
			return af.createTransformedShape(shape);			
		}
		
		return shape;
	}
}
