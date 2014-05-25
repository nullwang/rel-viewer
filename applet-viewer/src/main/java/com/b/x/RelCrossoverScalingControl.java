package com.b.x;

import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import com.b.g.Generator;
import com.b.u.GraphicsUtility;

import g.algorithms.layout.Layout;
import g.visualization.Layer;
import g.visualization.VisualizationServer;
import g.visualization.control.CrossoverScalingControl;
import g.visualization.transform.MutableTransformer;

public class RelCrossoverScalingControl extends CrossoverScalingControl {
	
	public void scaleOne(VisualizationServer<?,?> vv,  Point2D at) {
		 MutableTransformer layoutTransformer = vv.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.LAYOUT);
		 MutableTransformer viewTransformer = vv.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.VIEW);		 
		 
		 double modelScale = layoutTransformer.getScale();
		 double viewScale = viewTransformer.getScale();
		 
		 layoutTransformer.scale(1/modelScale, 1/modelScale, at);
		 viewTransformer.scale(1/viewScale, 1/viewScale, at);
		
	}
	
	public void scaleFit(RelVisualizationViewer relView, Point2D at) {
	    	Dimension vd = relView.getPreferredSize();
	    	if(relView.isShowing()) {
	    		vd = relView.getSize();
	    	}
	    	
			//Dimension ld = relView.getGraphLayout().getSize();
			 MutableTransformer layoutTransformer = relView.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.LAYOUT);
			 MutableTransformer viewTransformer = relView.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.VIEW);		 
			 
			 double modelScale = layoutTransformer.getScale();
			 double viewScale = viewTransformer.getScale();

			double inverseModelScale = Math.sqrt(crossover)/modelScale;
			double inverseViewScale = Math.sqrt(crossover)/viewScale;
			double scale = modelScale * viewScale;
			
			Layout layout = relView.getGraphLayout();
			
			double minX = Integer.MAX_VALUE;
			double minY = Integer.MAX_VALUE;
			double maxX = Integer.MIN_VALUE;
			double maxY = Integer.MIN_VALUE;
			
			for ( Object v: layout.getGraph().getVertices())
			{
				Point2D vp =  (Point2D) layout.transform(v);
				
				if( vp == null) return;
				
				Rectangle2D r2d = Generator.getTransformedVertexShape(relView.getRenderContext(),v,(float)vp.getX(),(float)vp.getY()).getBounds();
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
			
			minX -= GraphicsUtility.LEFTPADDING ;
			maxX += GraphicsUtility.RIGHTPADDING ;
			minY -= GraphicsUtility.TOPPADDING ;
			maxY += GraphicsUtility.BOTTOMPADDING ;
			
			double amountx = vd.getWidth() /  ( scale * (maxX-minX));
			double amounty = vd.getHeight() / (scale * (maxY-minY));
			
			double amount = ( amountx < amounty ? amountx : amounty ); 
			
			Point2D transformedAt = relView.getRenderContext().getMultiLayerTransformer().inverseTransform(Layer.VIEW, at);
			
			 if((scale*amount - crossover)*(scale*amount - crossover) < 0.001) {
		            // close to the control point, return both transformers to a scale of sqrt crossover value
		            layoutTransformer.scale(inverseModelScale, inverseModelScale, transformedAt);
		            viewTransformer.scale(inverseViewScale, inverseViewScale, at);
		        } else if(scale*amount < crossover) {
		            // scale the viewTransformer, return the layoutTransformer to sqrt crossover value
			        viewTransformer.scale(amount/inverseModelScale, amount/inverseModelScale, at);
			        layoutTransformer.scale(inverseModelScale, inverseModelScale, transformedAt);
			    } else {
		            // scale the layoutTransformer, return the viewTransformer to crossover value
			        layoutTransformer.scale(amount/inverseViewScale, amount/inverseViewScale, transformedAt);
			        viewTransformer.scale(inverseViewScale, inverseViewScale, at);
			    }
			 relView.repaint();
			
  }

}
