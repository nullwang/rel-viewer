package com.b.x;

import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.QuadCurve2D;

import com.b.a.Parameter;
import com.b.g.Generator;
import com.b.u.ParameterUtil;

import g.graph.Graph;
import g.graph.util.Context;
import g.graph.util.EdgeIndexFunction;
import g.graph.util.Pair;
import g.visualization.decorators.AbstractEdgeShapeTransformer;
import g.visualization.decorators.EdgeShape;

public class RelEdgeShape<V, E> extends EdgeShape<V, E> {

	public static class MultiCurve<V, E> extends
			AbstractEdgeShapeTransformer<V, E> implements
			IndexedRendering<V, E> {

		private static QuadCurve2D instance = new QuadCurve2D.Float();
		private static Line2D lineInstance = new Line2D.Float(0.0f, 0.0f, 1.0f,
				0.0f);
		private static GeneralPath parallelInstance = new GeneralPath();

		protected EdgeIndexFunction<V, E> parallelEdgeIndexFunction;

		@SuppressWarnings("unchecked")
		public void setEdgeIndexFunction(
				EdgeIndexFunction<V, E> parallelEdgeIndexFunction) {
			this.parallelEdgeIndexFunction = parallelEdgeIndexFunction;
			loop.setEdgeIndexFunction(parallelEdgeIndexFunction);
		}

		/**
		 * @return the parallelEdgeIndexFunction
		 */
		public EdgeIndexFunction<V, E> getEdgeIndexFunction() {
			return parallelEdgeIndexFunction;
		}

		/**
		 * Get the shape for this edge, returning either the shared instance or,
		 * in the case of self-loop edges, the Loop shared instance.
		 */
		@SuppressWarnings("unchecked")
		public Shape transform(Context<Graph<V, E>, E> context) {
			Graph<V, E> graph = context.graph;
			E e = context.element;
			Pair<V> endpoints = graph.getEndpoints(e);
			if (endpoints != null) {
				boolean isLoop = endpoints.getFirst().equals(
						endpoints.getSecond());
				if (isLoop) {
					return loop.transform(context);
				}
			}
			constructQuad(graph,e);
			constructParallel(graph,e);
			
			String linkShape = Generator.getDataService().getLinkShape(
					(String) e);
			if (Parameter.Shape.LINE.equals(linkShape))
				return lineInstance;
			else if (Parameter.Shape.QUAD.equals(linkShape)) {
				return instance;
			} else if (Parameter.Shape.PARALLEL.equals(linkShape)) {
				return parallelInstance;
			} else {
				linkShape = ParameterUtil.getString(Parameter.LINK_SHAPE);
				if (Parameter.Shape.LINE.equals(linkShape))
					return lineInstance;
				else if (Parameter.Shape.QUAD.equals(linkShape)) {
					return instance;
				} else if (Parameter.Shape.PARALLEL.equals(linkShape)) {
					return parallelInstance;
				}
			}

			return parallelInstance;
		}
		
		
		@SuppressWarnings("unchecked")
		protected void constructQuad(Graph<V, E> graph, E e)
		{
			int index = 1;
			float m = 1;
			
//			if (parallelEdgeIndexFunction != null) {
//				index = parallelEdgeIndexFunction.getIndex(graph, e);
//			}
			
			if (parallelEdgeIndexFunction instanceof RelParallelEdgeIndexFunction) {
				RelParallelEdgeIndexFunction rpf = (RelParallelEdgeIndexFunction) parallelEdgeIndexFunction;
				index = rpf.getIndex(graph, e)+1;
				int commonEdgeNumber = rpf.getCommonEdgeNumber(graph, e);
				m = (1 + commonEdgeNumber) / 2f;
			}

//			float controlY = control_offset_increment
//					+ control_offset_increment * index;
			float controlY = control_offset_increment * (index - m);
			
			instance.setCurve(0.0f, 0.0f, 0.5f, controlY, 1.0f, 0.0f);
		}
		
		@SuppressWarnings("unchecked")
		protected void constructParallel(Graph<V, E> graph, E e)
		{
			parallelInstance.reset();
			float index = 1;
			float m = 1;
			if (parallelEdgeIndexFunction instanceof RelParallelEdgeIndexFunction) {
				RelParallelEdgeIndexFunction rpf = (RelParallelEdgeIndexFunction) parallelEdgeIndexFunction;
				index = rpf.getIndex(graph, e) + 1;
				int commonEdgeNumber = rpf.getCommonEdgeNumber(graph, e);
				m = (1 + commonEdgeNumber) / 2f;
			}
			float controlY = (float) ( control_offset_increment * (index - m) * 0.5f );

			parallelInstance.moveTo(0.0f, 0.0f);
			if( controlY != 0 ){
				parallelInstance.lineTo(0.05f, controlY);
				parallelInstance.lineTo(0.95f, controlY);
			}
			parallelInstance.lineTo(1.0f, 0.0f);
		}
	}
}