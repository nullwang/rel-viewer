package com.b.x;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.Map;
import com.b.u.NotNullMap;
import com.b.a.ClientEventSupport;
import com.b.a.ViewerEvent;

import g.algorithms.layout.GraphElementAccessor;
import g.algorithms.layout.Layout;
import g.visualization.VisualizationViewer;
import g.visualization.control.PickingGraphMousePlugin;
import g.visualization.layout.ObservableCachingLayout;
import g.visualization.picking.PickedState;

public class RelPickingGraphMousePlugin<V, E> extends
		PickingGraphMousePlugin<V, E> {
	protected ClientEventSupport eventSupport;
	protected Map<Object,Boolean> pickedEdge = new NotNullMap<Object,Boolean>();
	protected Map<Object,Boolean> pickedVertex = new NotNullMap<Object,Boolean>();

	protected Map<Object,Boolean> lastPickedEdge = new NotNullMap<Object,Boolean>();
	protected Map<Object,Boolean> lastPickedVertex = new NotNullMap<Object,Boolean>();
	
	protected Point2D mouseOver= new Point();
	protected long lastWhen = 0;
	
	protected void fillPick(PickedState ps, Map<Object,Boolean> map)
	{
		for (Object obj :ps.getPicked())
		{
			map.put(obj, true);
		}		
	}
	
	public ClientEventSupport getEventSupport() {
		return eventSupport;
	}

	public void setEventSupport(ClientEventSupport eventSupport) {
		this.eventSupport = eventSupport;
	}

	@Override
	@SuppressWarnings ("unchecked")
	public void mousePressed(MouseEvent e) {
		VisualizationViewer<V, E> vv = (VisualizationViewer) e.getSource();
		
		fillPick(vv.getPickedEdgeState(),pickedEdge);
		fillPick(vv.getPickedVertexState(),pickedVertex);
		
		super.mousePressed(e);
	}

	@Override
	@SuppressWarnings("unchecked")
	public void mouseDragged(MouseEvent e) {
		if (locked == false) {
			VisualizationViewer<V, E> vv = (VisualizationViewer) e.getSource();
			if (vertex != null) {
								
				Point p = e.getPoint();
				Point2D graphPoint = vv.getRenderContext()
						.getMultiLayerTransformer().inverseTransform(p);
				Point2D graphDown = vv.getRenderContext()
						.getMultiLayerTransformer().inverseTransform(down);
				Layout<V, E> layout = vv.getGraphLayout();
				double dx = graphPoint.getX() - graphDown.getX();
				double dy = graphPoint.getY() - graphDown.getY();
				PickedState<V> ps = vv.getPickedVertexState();

				for (V v : ps.getPicked()) {
					Point2D vp = layout.transform(v);
					vp.setLocation(vp.getX() + dx, vp.getY() + dy);

					// move in view size
					Dimension d = vv.getSize();
					Rectangle layoutRectangle = new Rectangle(0, 0, d.width,
							d.height);
					Shape s = vv.getRenderContext().getMultiLayerTransformer()
							.inverseTransform(layoutRectangle);
					
					if (s.contains(vp)) {
						
						Layout relLayout = layout;
						TimeSeqLayout tsl = null;

						if (layout instanceof ObservableCachingLayout) {
							ObservableCachingLayout ol = (ObservableCachingLayout) layout;
							relLayout = ol.getDelegate();
						}
						// time layout limit vertex move in timeLayout 
						if (relLayout instanceof TimeSeqLayout) {
							tsl = (TimeSeqLayout) relLayout;
							if ( v != null && tsl.isControllingItem(v))
							{
								Object leftVertex = tsl.getLastItem(v);
								Object rightVertex = tsl.getNextItem(v);
								double x = tsl.getX(v);
								if( leftVertex != null) {
									double lx = tsl.getX(leftVertex);
									if( x - lx + dx <= 3 ){
										vp.setLocation(vp.getX()-dx,vp.getY());
									}
								}
								if( rightVertex != null){
									double rx = tsl.getX(rightVertex);
									if( rx - x - dx <= 3 ){
										vp.setLocation(vp.getX()-dx,vp.getY());
									}
								}
							}
						}
						layout.setLocation(v, vp);
						
					}
				}
				down = p;

			} else {
				Point2D out = e.getPoint();
				if (e.getModifiers() == this.addToSelectionModifiers
						|| e.getModifiers() == modifiers) {
					rect.setFrameFromDiagonal(down, out);
				}
			}
			if (vertex != null)
				e.consume();
			vv.repaint();
		}
	}

	public void mouseReleased(MouseEvent e) {
		super.mouseReleased(e);
		
		VisualizationViewer<V, E> vv = (VisualizationViewer) e.getSource();
		fillPick(vv.getPickedVertexState(),lastPickedVertex);
		fillPick(vv.getPickedEdgeState(),lastPickedEdge);

		for(Object obj:pickedEdge.keySet())
		{
			if ( lastPickedEdge.get(obj) == null ) 
			{
				eventSupport.putEvent(ViewerEvent.EVENT_SELECTIONCHANGED, obj.toString());
			}
		}
		
		for(Object obj:lastPickedEdge.keySet())
		{
			if ( pickedEdge.get(obj) == null)
			{
				eventSupport.putEvent(ViewerEvent.EVENT_SELECTIONCHANGED, obj.toString());				
			}
		}
		
		for( Object obj: pickedVertex.keySet())
		{
			if( lastPickedVertex.get(obj) == null )
				eventSupport.putEvent(ViewerEvent.EVENT_SELECTIONCHANGED, obj.toString());			
		}
		
		for( Object obj : lastPickedVertex.keySet())
		{
			if( ! pickedVertex.containsKey(obj))
			{
				eventSupport.putEvent(ViewerEvent.EVENT_SELECTIONCHANGED, obj.toString());				
			}
		}
		
		pickedEdge.clear();
		pickedVertex.clear();
		lastPickedEdge.clear();
		lastPickedVertex.clear();
	}

	@SuppressWarnings ("unchecked")
	@Override
	 public void mouseClicked(MouseEvent e) {
		 super.mouseClicked(e);
		 VisualizationViewer vv = (VisualizationViewer) e.getSource();
			GraphElementAccessor pickSupport = vv.getPickSupport();
			Point2D ip = e.getPoint();
			Layout layout = vv.getGraphLayout();
			Object vertex = pickSupport.getVertex(layout, ip.getX(), ip.getY());
			if (vertex != null) {
				eventSupport.putEvent(ViewerEvent.EVENT_CLICK, vertex.toString());
			} else {
				Object edge = pickSupport.getEdge(layout, ip.getX(), ip.getY());
				if (edge != null)
					eventSupport.putEvent(ViewerEvent.EVENT_CLICK, edge
							.toString());
			}

			if ( e.getClickCount() == 2){
				if (vertex != null) {
					eventSupport.putEvent(ViewerEvent.EVENT_DOUBLECLICK, vertex.toString());
				} else {
					Object edge = pickSupport.getEdge(layout, ip.getX(), ip.getY());
					if (edge != null)
						eventSupport.putEvent(ViewerEvent.EVENT_DOUBLECLICK, edge
								.toString());
				}	
			}

			e.consume();		 
	 }

	@SuppressWarnings ("unchecked")
	@Override
	 public void mouseMoved(MouseEvent e) {
		if ( e.getWhen() - lastWhen < 500) return;
		
		VisualizationViewer vv = (VisualizationViewer) e.getSource();
		GraphElementAccessor pickSupport = vv.getPickSupport();
		Layout layout = vv.getGraphLayout();
		
		Object overVertex = pickSupport.getVertex(layout, mouseOver.getX(), mouseOver.getY());
		Object overEdge = pickSupport.getEdge(layout, mouseOver.getX(),  mouseOver.getY());
		
		Point2D ip = e.getPoint();
		Object vertex = pickSupport.getVertex(layout, ip.getX(), ip.getY());
		Object edge = pickSupport.getEdge(layout, ip.getX(), ip.getY());
		
		
		if( overVertex != vertex && vertex != null)
		{
			eventSupport.putEvent(ViewerEvent.EVENT_MOUSEIN, vertex.toString());
		}
		else if( overVertex != vertex && overVertex != null)
		{
			eventSupport.putEvent(ViewerEvent.EVENT_MOUSEOUT, overVertex.toString());
		}
		else if(overEdge != edge && edge != null	)
		{
			eventSupport.putEvent(ViewerEvent.EVENT_MOUSEIN, edge.toString());
		}
		
		else if(overEdge != edge && overEdge != null	)
		{
			eventSupport.putEvent(ViewerEvent.EVENT_MOUSEOUT, overEdge.toString());
		}
		
		mouseOver = ip;
		lastWhen = e.getWhen();
    }

}
