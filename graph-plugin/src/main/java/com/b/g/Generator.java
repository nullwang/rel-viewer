package com.b.g;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import o.c15.Predicate;
import o.c15.Transformer;
import o.c15.functors.ConstantTransformer;


import com.b.a.Parameter;
import com.b.e.CatProperty;
import com.b.e.Entity;
import com.b.e.Link;
import com.b.e.LinkType;
import com.b.s.DataService;
import com.b.u.NotNullStringBuilder;
import com.b.u.ParameterUtil;
import com.b.x.RelEdgeLabelShapeTransformer;
import com.b.x.RelEdgeShape;
import com.b.x.RelVertexIconShapeTransformer;
import com.b.x.RelVertexLabelShapeTransformer;

import g.algorithms.layout.Layout;
import g.algorithms.layout.util.RandomLocationTransformer;
import g.algorithms.shortestpath.MinimumSpanningForest2;
import g.graph.DelegateForest;
import g.graph.DelegateTree;
import g.graph.Forest;
import g.graph.Graph;
import g.graph.SparseMultigraph;
import g.graph.util.Context;
import g.graph.util.EdgeType;
import g.visualization.RenderContext;
import g.visualization.decorators.ConstantDirectionalEdgeValueTransformer;
import g.visualization.decorators.DefaultVertexIconTransformer;
import g.visualization.decorators.EllipseVertexShapeTransformer;
import g.visualization.decorators.VertexIconShapeTransformer;
import g.visualization.picking.PickedInfo;

public class Generator {
	public static float idealLen = 138;
	public static float alphaFactor = .3f;
	
	private static int maxEmphasised = 1;
	
	private static Graph<String, String> g ;
	
	private static Set emphasisedEnds = Collections.synchronizedSet( new HashSet());
	
	private static String newLineChar="\n";	
	
	private static String toolTipLineChar = "<br>";
	
	private static Layout<String,String> layout;
	
	private static Dimension size;
	
	public static Set getEmphasisedEnds() {
		return emphasisedEnds;
	}

	public static void setEmphasisedEnds(Set emphasisedEnds) {
		Generator.emphasisedEnds = emphasisedEnds;
	}

	private static Boolean emphasised = false;
	
	public static Boolean getEmphasised() {
		return emphasised;
	}
	
	public static void setEndEmphasis(String strEntityList, boolean blnEmphasis	)
	{
		String[] endIds = strEntityList.split(",");
		for(int i=0; i<endIds.length; i++)
		{
			String endId = endIds[i];
			if( blnEmphasis){
				emphasisedEnds.add(endId);
			}
			else
				emphasisedEnds.remove(endId);
		}
	}
	
	public static void clearEndEmphasis()
	{
		emphasisedEnds.clear();
	}

	public static void setEmphasised(Boolean emphasised) {
		Generator.emphasised = emphasised;
	}

	public static int getMaxEmphasised() {
		return maxEmphasised;
	}

	public static void setMaxEmphasised(int maxEmphasised) {
		Generator.maxEmphasised = maxEmphasised;
	}
		
	public static void setConnectivityEmphasis( String strEntityTypeList)
	{
		emphasisedEnds.clear();
		
		if( g == null ) return;
		//final String typeList = strEntityTypeList;
		String[] typeListArray = strEntityTypeList.split(",");
		final Set<String> typeList = new HashSet<String>();
		for(String str: typeListArray)
		{
			typeList.add(str);			
		}
		
		//descent sort
		Set<String> ordEnd = new TreeSet<String>(
				new Comparator<String>(){
					public int compare(String o1, String o2) {
						int result = 0;						
						String type1 = dataService.getEntityTypeName(o1);
						String type2 = dataService.getEntityTypeName(o2);
						
						if( ! typeList.contains(type1) ) result = -1;
						else if (! typeList.contains(type2))
						{
							return 1;
						}	
						else {
							result = g.getNeighborCount(o1) - g.getNeighborCount(o2);
						}
						if( result ==0 ) result = -1;
						return -result;
					}
				}		
		);
		
		for( String end : g.getVertices())
		{
			ordEnd.add(end);
		}
		int i=0;
		for( String end: ordEnd)
		{
			if( i>=maxEmphasised) break;
			emphasisedEnds.add(end);
			i++;
		}
	}

	public static float getAlphaFactor() {
		return alphaFactor;
	}

	public static void setAlphaFactor(float alphaFactor) {
		Generator.alphaFactor = alphaFactor;
	}

	public static float getIdealLen() {
		return idealLen;
	}

	public static void setIdealLen(float idealLen) {
		Generator.idealLen = idealLen;
	}

	private static DataService dataService;

	public static DataService getDataService() {
		return dataService;
	}

	public static void setDataService(DataService dataService) {
		Generator.dataService = dataService;
	}

	public Generator(DataService dataService) {
		Generator.dataService = dataService;
	}

	public static Graph<String, String> generateMultiGraph() {
		g = new SparseMultigraph<String, String>();
		for (Entity entity : dataService.getEntityService().getEntities()) {
			g.addVertex(entity.getId());
		}
		for (Link link : dataService.getEntityService().getLinks()) {

			if (link.getDirection().equals(Link.Direction.FORWARD)) {
				g.addEdge(link.getId(), link.getEnt1Id(), link.getEnt2Id(),
						EdgeType.DIRECTED);
			} else if (link.getDirection().equals(Link.Direction.REVERSE)) {
				g.addEdge(link.getId(), link.getEnt2Id(), link.getEnt1Id(),
						EdgeType.DIRECTED);
			} else
				g.addEdge(link.getId(), link.getEnt1Id(), link.getEnt2Id());
		}

		return g;
	}

	public static Map<String, Long> getTimeCoord() {
		Map<String, Long> map = new HashMap<String, Long>();

		for (Entity ent : dataService.getEntityService().getEntities()) {
			Date d = dataService.getEntityDateTime(ent);
			if (d != null)
				map.put(ent.getId(), d.getTime());
		}

		for (Link link : dataService.getEntityService().getLinks()) {
			Date d = dataService.getLinkDateTime(link);
			if (d != null)
				map.put(link.getId(), d.getTime());
		}

		return map;
	}

	// public static Forest<String,String> generateForest(Graph<String,String>
	// g)
	// {
	// return generateForest(generateMultiGraph(g));
	// }
	//	
	//	
	public static Forest<String, String> generateForest(
			Graph<String, String> graph) {
		Forest<String, String> tree;

		MinimumSpanningForest2<String, String> prim = new MinimumSpanningForest2<String, String>(
				graph, new DelegateForest<String, String>(), DelegateTree
						.<String, String> getFactory(),
				new ConstantTransformer(1.0));

		tree = prim.getForest();

		return tree;
	}

	public static Dimension getIdealGridLayoutSize(Graph<String, String> g,
			Dimension d) {
		int vertexCount = g.getVertexCount();

		double ratio = d.getHeight() / d.getWidth();

		// if ( idealLen * idealLen * vertexCount < d.height * d.width ){
		// return d;
		// }

		double area = idealLen * idealLen * vertexCount;

		double width = Math.sqrt(area / ratio);
		double height = width * ratio;

		Dimension retd = new Dimension((int) width, (int) height);

		// System.out.println("layout Size:" + retd);

		return retd;
	}

	static Ellipse2D interactionShape = new Ellipse2D.Double(-2, -2, 4, 4);

	public static Ellipse2D getInteractionShape() {
		return interactionShape;
	}

	public static Transformer<Context<Graph<String, String>, String>, Number> getEdgeLabelClosenessTransformer() {
		return new ConstantDirectionalEdgeValueTransformer<String, String>(0.5,
				0.5);
	}

	private static RelEdgeLabelShapeTransformer<String, String> relEdgeLabelShapeTransformer = new RelEdgeLabelShapeTransformer<String, String>();

	public static RelEdgeLabelShapeTransformer<String, String> getEdgeLabelShapeTransformer() {
		return relEdgeLabelShapeTransformer;
	}

	public static <V> Shape getTransformedVertexShape(RenderContext rc , V v, float x, float y) {
		Shape shape = getVertexShapeTransformer(rc).transform(v.toString());
		GeneralPath gp = new GeneralPath(shape);

		Shape labelShape = getVertexLabelShapeTransformer(rc).transform(
				v.toString());
		if (labelShape != null)
			gp.append(labelShape, true);

		AffineTransform transform = AffineTransform.getTranslateInstance(x, y);
		return transform.createTransformedShape(gp);

	}

	private static RelVertexLabelShapeTransformer<String, String> relVertexLabelShapeTransformer = new RelVertexLabelShapeTransformer<String, String>();

	public static RelVertexLabelShapeTransformer<String, String> getVertexLabelShapeTransformer(RenderContext rc) {
		relVertexLabelShapeTransformer.setRenderContext(rc);
		return relVertexLabelShapeTransformer;
	}

	private static RelVertexIconShapeTransformer<String> vertexShapeTransformer = new RelVertexIconShapeTransformer<String>(
			new EllipseVertexShapeTransformer<String>());

	public static VertexIconShapeTransformer<String> getVertexShapeTransformer(RenderContext rc ) {
		vertexShapeTransformer.setRenderContext(rc);
		return vertexShapeTransformer;
	}

	private static DefaultVertexIconTransformer<String> vertexIconTransformer = new DefaultVertexIconTransformer<String>();

	public static DefaultVertexIconTransformer<String> getVertexIconTransformer() {

		return vertexIconTransformer;
	}

	public static Transformer<String, String> getVertexLabelTransformer() {
		Transformer<String, String> stringer = new Transformer<String, String>() {
			public String transform(String v) {

				boolean hideDisplayName = ParameterUtil.getBoolean(
						Parameter.LABEL_DISPLAY_HIDDEN, true);
				CatProperty[] cats = dataService.getEntityLabelCatProperty(v);

				NotNullStringBuilder sb = new NotNullStringBuilder();
				if (cats.length < 1)
					return null;

				for (int i = 0; i < cats.length; i++) {
					CatProperty cat = cats[i];
					NotNullStringBuilder s = new NotNullStringBuilder();
					if (cat != null) {
						if (!hideDisplayName) {
							s.append(cat.getDisplayName());
							s.append(ParameterUtil
									.getString(Parameter.LABEL_SEP));
						}
						s.append(dataService.getEntityPropertyValue(v, cat
								.getLocalName()));
					}

					if (i > 0 && sb.length() > 0 && s.length()>0)
						sb.append(newLineChar);
					if (s.length() > 0)
						sb.append(s.toString());
				}
//				if (sb.length() > 0) {
//					sb.appendFirst("<html>");
//					sb.append("</html>");
//				}

				return sb.toString();
			}
		};

		return stringer;
	}

	public static Transformer<String, String> getEdgeLabelTransformer() {
		Transformer<String, String> stringer = new Transformer<String, String>() {
			public String transform(String e) {

				boolean hideDisplayName = ParameterUtil.getBoolean(
						Parameter.LABEL_DISPLAY_HIDDEN, true);
				CatProperty[] cats = dataService.getLinkLabelCatProperty(e);

				NotNullStringBuilder sb = new NotNullStringBuilder();
				if (cats.length < 1)
					return null;

				for (int i = 0; i < cats.length; i++) {
					CatProperty cat = cats[i];
					NotNullStringBuilder s = new NotNullStringBuilder();
					if (cat != null) {
						if (!hideDisplayName) {
							s.append(cat.getDisplayName());
							s.append(ParameterUtil
									.getString(Parameter.LABEL_SEP));
						}
						s.append(dataService.getLinkPropertyValue(e, cat
								.getLocalName()));
					}

					if (i > 0 && s.length() > 0 && sb.length() > 0)
						sb.append(newLineChar);
					if (s.length() > 0)
						sb.append(s.toString());
				}
//				if (sb.length() > 0) {
//					sb.appendFirst("<html>");
//					sb.append("</html>");
//				}

				return sb.toString();
			}
		};

		return stringer;
	}

	public static Transformer<String, String> getVertexToolTipTransformer() {
		Transformer<String, String> stringer = new Transformer<String, String>() {
			public String transform(String v) {

				boolean hideDisplayName = ParameterUtil.getBoolean(
						Parameter.TOOLTIP_DISPLAY_HIDDEN, true);
				CatProperty[] cats = dataService.getEntityToolTipCatProperty(v);

				NotNullStringBuilder sb = new NotNullStringBuilder();
				if (cats.length < 1)
					return null;

				for (int i = 0; i < cats.length; i++) {
					CatProperty cat = cats[i];
					NotNullStringBuilder s = new NotNullStringBuilder();
					if (cat != null) {
						if (!hideDisplayName) {
							s.append(cat.getDisplayName());
							s.append(ParameterUtil
									.getString(Parameter.TOOLTIP_SEP));
						}
						s.append(dataService.getEntityPropertyValue(v, cat
								.getLocalName()));
					}

					if (i > 0 && s.length() > 0 && sb.length() > 0)
						sb.append(toolTipLineChar);
					if (s.length() > 0)
						sb.append(s.toString());
				}
				if (sb.length() > 0) {
					sb.appendFirst("<html>");
					sb.append("</html>");
				}

				return sb.toString();
			}
		};

		return stringer;
	}

	public static Transformer<String, String> getEdgeToolTipTransformer() {
		Transformer<String, String> stringer = new Transformer<String, String>() {
			public String transform(String e) {

				boolean hideDisplayName = ParameterUtil.getBoolean(
						Parameter.TOOLTIP_DISPLAY_HIDDEN, true);
				CatProperty[] cats = dataService.getLinkToolTipCatProperty(e);

				NotNullStringBuilder sb = new NotNullStringBuilder();
				if (cats.length < 1)
					return null;

				for (int i = 0; i < cats.length; i++) {
					CatProperty cat = cats[i];
					NotNullStringBuilder s = new NotNullStringBuilder();
					if (cat != null) {
						if (!hideDisplayName) {
							s.append(cat.getDisplayName());
							s.append(ParameterUtil
									.getString(Parameter.TOOLTIP_SEP));
						}
						s.append(dataService.getLinkPropertyValue(e, cat
								.getLocalName()));
					}

					if (i > 0 && s.length() > 0 && sb.length() > 0)
						sb.append(toolTipLineChar);
					if (s.length() > 0)
						sb.append(s.toString());
				}
				if (sb.length() > 0) {
					sb.appendFirst("<html>");
					sb.append("</html>");
				}

				return sb.toString();
			}
		};

		return stringer;
	}

	private static Transformer<Context<Graph<String, String>, String>, Shape> edgeShapeTransform = new RelEdgeShape.MultiCurve<String, String>();

	public static Transformer<Context<Graph<String, String>, String>, Shape> getEdgeShapeTransformer() {
		return edgeShapeTransform;
	}
	
	private static Color textPickingColor = Color.white;
	private static Color textPickingBgColor = Color.gray;
	
	public static Color getTextPickingBgColor() {
		return textPickingBgColor;
	}

	public static void setTextPickingBgColor(String textPickingBgColor) {
		String color = textPickingBgColor;
		if (color != null && !"".equals(color)) {
			try {
				Generator.textPickingBgColor = Color.decode(color);
			} catch (NumberFormatException ex) {
			}
		}
	}

	public static Color getTextPickingColor()
	{
		return textPickingColor;
	}
	
	public static void setTextPickingColor(String textPickingBgColor )
	{
		String color = textPickingBgColor;
		if (color != null && !"".equals(color)) {
			try {
				Generator.textPickingBgColor = Color.decode(color);
			} catch (NumberFormatException ex) {
			}
		}
	}

	public static Transformer<String, Paint> getEdgeDrawPaintTransformer(
			final PickedInfo<String> pi) {
		Transformer<String, Paint> edgeDrawPaintTransformer = new Transformer<String, Paint>() {

			public Paint transform(String e) {
				String color = ParameterUtil
						.getString(Parameter.LINK_PICKING_COLOR);
				Color c = Color.cyan;
				if (color != null && !"".equals(color)) {
					try {
						c = Color.decode(color);
					} catch (NumberFormatException ex) {
					}
				}

				if (pi.isPicked(e)) {
					return c;
				}

				Link link = dataService.getEntityService().getLink(e);

				if (link == null)
					return Color.black;

				else {
					color = link.getColor();
					if (color != null && !"".equals(color)) {
						try {
							c = Color.decode(color);
							return c;
						} catch (NumberFormatException ex) {
						}
					}
					return Color.black;
				}
			}
		};

		return edgeDrawPaintTransformer;
	}

	public static Transformer<String, Stroke> getEdgeStrokeTransformer() {
		Transformer<String, Stroke> edgeStrokeTransformer = new Transformer<String, Stroke>() {

			public Stroke transform(String e) {
				int width = 1;
				Link link = dataService.getEntityService().getLink(e);
				if (link == null)
					return new BasicStroke(width);

				else {
					int w = link.getLineThickness();
					if (w >= 1 && w <= 32)
						width = w;
				}
				return new BasicStroke(width);
			};
		};

		return edgeStrokeTransformer;
	}

	public static Predicate<Context<Graph<String, String>, String>> getVertexShowPredicate() {
		Predicate<Context<Graph<String, String>, String>> predicate = new Predicate<Context<Graph<String, String>, String>>() {
			public boolean evaluate(
					Context<Graph<String, String>, String> context) {
				boolean isHidden = dataService.isEntityHidden(context.element);
				// System.out.println("evaluate "+ context.element + !isHidden);
				return !(isHidden);
			}
		};

		return predicate;
	}

	public static Predicate<Context<Graph<String, String>, String>> getArrowPredicate() {
		Predicate<Context<Graph<String, String>, String>> predicate = new Predicate<Context<Graph<String, String>, String>>() {
			public boolean evaluate(
					Context<Graph<String, String>, String> context) {
				LinkType lt = dataService.getLinkType(context.element);
				if (lt != null)
					return lt.isShowArrows();

				return false;
			}
		};

		return predicate;
	}

	public static Predicate<Context<Graph<String, String>, String>> getEdgeShowPredicate() {
		Predicate<Context<Graph<String, String>, String>> predicate = new Predicate<Context<Graph<String, String>, String>>() {
			public boolean evaluate(
					Context<Graph<String, String>, String> context) {
				boolean isHidden = dataService.isEntityHidden(context.element);
				// System.out.println("evaluate "+ context.element + !isHidden);
				return !(isHidden);
			}
		};

		return predicate;
	}
	
	
	public static void setInitializer(Layout<String,String> layout)
	{
		Generator.layout = layout;
	}
	
	public static Transformer<String,Point2D> getInitializer(final Dimension size)
	{		
		Transformer<String,Point2D> transformer1 = new Transformer<String,Point2D>()
		{
			public Point2D transform(String v) {
				Generator.size = size;
				Point2D point2d = null;
				
				if( layout != null){					
					point2d = layout.transform(v);
				}
				if( point2d == null){
					if( Generator.size == null) Generator.size = new Dimension(300,300);
					double x = Math.random() * Generator.size.width;
					double y = Math.random()*  Generator.size.height;
					point2d = new Point2D.Double(x , y);					
				}
				return point2d;
			}			
		};
	
		
		if( layout != null ) {
			return transformer1;
		}
		
		else {
			if( Generator.size == null) Generator.size = new Dimension(300,300);
			return new RandomLocationTransformer<String>(Generator.size);
		}		
	}
}
