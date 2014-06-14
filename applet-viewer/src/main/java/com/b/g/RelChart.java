package com.b.g;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.border.LineBorder;

import o.c15.Transformer;

import com.b.a.ClientRelFileSupport;
import com.b.a.Parameter;
import com.b.a.ViewerEvent;
import com.b.e.RelReader;
import com.b.s.ClientSupportAdapter;
import com.b.s.DataService;
import com.b.u.GraphicsUtility;
import com.b.u.StringUtil;
import com.b.u.ParameterUtil;
import com.b.x.RelEdgeLabelRenderer;
import com.b.x.RelEdgeLabelShapeTransformer;
import com.b.x.RelEdgeShape;
import com.b.x.RelGraphZoomScrollPane;
import com.b.x.RelGraphicsDecorator;
import com.b.x.RelIncidentEdgeIndexFunction;
import com.b.x.RelModalGraphMouse;
import com.b.x.RelMultiEdgeRenderer;
import com.b.x.RelParallelEdgeIndexFunction;
import com.b.x.RelVertexLabelRenderer;
import com.b.x.RelVertexLabelShapeTransformer;
import com.b.x.RelVertexRenderer;
import com.b.x.RelVisualizationViewer;

import g.algorithms.layout.FRLayout;
import g.algorithms.layout.GraphElementAccessor;
import g.graph.Graph;
import g.graph.util.Context;
import g.graph.util.EdgeIndexFunction;
import g.visualization.LayeredIcon;
import g.visualization.VisualizationViewer;
import g.visualization.control.AbstractPopupGraphMousePlugin;
import g.visualization.control.ModalGraphMouse;
import g.visualization.decorators.ConstantDirectionalEdgeValueTransformer;
import g.visualization.decorators.DefaultVertexIconTransformer;
import g.visualization.decorators.VertexIconShapeTransformer;
import g.visualization.picking.PickedState;
import g.visualization.renderers.CenterEdgeArrowRenderingSupport;
import g.visualization.renderers.Checkmark;
import g.visualization.renderers.Renderer;

public class RelChart extends JPanel implements ActionListener,
		ClientRelFileSupport {

	protected URL codeBase;
	protected RelViewer relViewer;
	Properties tooltipProp = new Properties();

	protected int x = 0;
	protected int y = 0;

	RelVisualizationViewer<String, String> vv;
	DataService dataService = new DataService();
	String relFile;
	TimeBar timeBar;
	JToolBar toolBar;
	
	boolean waitingStatus = false;
	
	protected JLabel messageLabel;
	protected JPanel messagePanel;
	RelGraphZoomScrollPane chartPanel;
	JPanel toolPanel = new JPanel();
	
	Boolean keyEmphasis = false;
	
	final RelModalGraphMouse<String, String> graphMouse = new RelModalGraphMouse<String, String>();

	public RelChart(RelViewer rel) {
		relViewer = rel;
		try {
			URL propUrl = this.getClass().getResource("/t.p");
			if( propUrl != null ) tooltipProp.load((propUrl.openStream()));
		} catch (IOException e) {
			e.printStackTrace();
			relViewer.putEvent(ViewerEvent.EVENT_ERROR, e.getLocalizedMessage());
		}
		
		Generator.setDataService(dataService);
		Graph<String, String> g = Generator.generateMultiGraph();
		
		FRLayout<String, String> layout = new FRLayout<String, String>(g);
		layout.setAttractionMultiplier(1);
		layout.setRepulsionMultiplier(1);
		layout.setSize(this.getSize());
		
		vv = new RelVisualizationViewer<String, String>(layout);

		chartPanel = new RelGraphZoomScrollPane(vv);
		
		toolBar = createToolBar();

		this.setLayout(new BorderLayout());
		
		toolPanel.setLayout(new BorderLayout());
		toolPanel.add("North", toolBar);
		
		timeBar = new TimeBar(vv);
		toolPanel.add("Center",timeBar);
		timeBar.setVisible(false);
		
		this.add("North", toolPanel);
		
		this.add("Center", chartPanel);		

		addComponentListener(new ResizeListener());
	}
	
	private void initMessage()
	{
		String l = "please wait...";
		String str = ParameterUtil.getString(Parameter.INITIAL_WAITING_MESSAGE,l);
		if( str.length() < 1 ) str = l;
		
		messageLabel = new JLabel(str);
		
		messageLabel.setFont(new Font("Helvetica", 1, 14));
		messageLabel.setForeground(this.getForeground());
		messageLabel.setBackground(this.getBackground());
		messagePanel = new JPanel();
		
		JPanel jp = new JPanel();
		jp.add(messageLabel);
		jp.setBorder(new LineBorder(Color.BLACK));
		
		messagePanel.setLayout(new GridBagLayout());			
		//messagePanel.validate();
		messagePanel.add(jp,new GridBagConstraints());
	}

	public void init() {
		if(! (ParameterUtil.getBoolean(Parameter.TOOL_BAR,true))){
			toolBar.setVisible(false);
		}
		
		initMessage();
		
		Generator.setTextPickingBgColor(ParameterUtil.getString(Parameter.TEXT_PICKING_BGCOLOR));
		Generator.setTextPickingColor(ParameterUtil.getString(Parameter.TEXT_PICKING_COLOR));
		
		Color c = GraphicsUtility.getColor(
				ParameterUtil.getString(Parameter.TIMEBAR_BGCOLOR), Color.decode("#FFFACD"));
		
		timeBar.setBackground(c);
		
		vv.setVertexToolTipTransformer(Generator.getVertexToolTipTransformer());
		vv.setEdgeToolTipTransformer(Generator.getEdgeToolTipTransformer());

		vv.getRenderContext().setVertexLabelTransformer(
				Generator.getVertexLabelTransformer());
		vv.getRenderContext().setEdgeLabelTransformer(
				Generator.getEdgeLabelTransformer());

		vv.getRenderContext().setVertexIncludePredicate(
				Generator.getVertexShowPredicate());
		vv.getRenderContext().setEdgeIncludePredicate(
				Generator.getEdgeShowPredicate());
		
		vv.getRenderContext().setGraphicsContext(new RelGraphicsDecorator());
		
		vv.getRenderContext().setEdgeArrowPredicate(Generator.getArrowPredicate());

		graphMouse.add(new PopupGraphMousePlugin());

		vv.setGraphMouse(graphMouse);
		graphMouse.setMode(ModalGraphMouse.Mode.PICKING);
		graphMouse.setEventSupport(relViewer);

		VertexIconShapeTransformer<String> relVertexIconShapeTransformer = Generator
		.getVertexShapeTransformer(vv.getRenderContext());
		
		VertexIconShapeTransformer<String> vertexIconShapeTransformer = relVertexIconShapeTransformer;
		DefaultVertexIconTransformer<String> vertexIconTransformer = Generator
				.getVertexIconTransformer();

		vertexIconShapeTransformer
				.setIconMap(ClientSupportAdapter.getIconMap());
		vertexIconTransformer.setIconMap(ClientSupportAdapter.getIconMap());

		vv.getRenderContext().setEdgeLabelClosenessTransformer(Generator.getEdgeLabelClosenessTransformer());
		vv.getRenderContext().setVertexShapeTransformer(
				vertexIconShapeTransformer);
		vv.getRenderContext().setVertexIconTransformer(vertexIconTransformer);
		
		Transformer<Context<Graph<String, String>, String>, Shape> edgeShapeTransformer = Generator.getEdgeShapeTransformer();
		
		//set edge shape transformer and the index function of shape transformer
		EdgeIndexFunction<String,String> parallelEdgeIndexFunction = 
	        RelParallelEdgeIndexFunction.<String,String>getInstance();
	    
	    EdgeIndexFunction<String,String> incidentEdgeIndexFunction = 
	        RelIncidentEdgeIndexFunction.<String,String>getInstance();		
		
		vv.getRenderContext().setEdgeShapeTransformer(edgeShapeTransformer);
		 if(edgeShapeTransformer instanceof RelEdgeShape.Orthogonal) {
	        	((RelEdgeShape.IndexedRendering<String, String>)edgeShapeTransformer).setEdgeIndexFunction(incidentEdgeIndexFunction);
	        } else 
	        if(edgeShapeTransformer instanceof RelEdgeShape.IndexedRendering) {
	            ((RelEdgeShape.IndexedRendering<String,String>)edgeShapeTransformer).setEdgeIndexFunction(parallelEdgeIndexFunction);
	        }
		
		
		vv.getRenderContext().setEdgeDrawPaintTransformer(Generator.getEdgeDrawPaintTransformer(vv.getPickedEdgeState()));
		vv.getRenderContext().setEdgeStrokeTransformer(Generator.getEdgeStrokeTransformer());

		vv.getRenderer().setVertexRenderer(new RelVertexRenderer<String,String>());
		//vv.getRenderer().setVertexLabelRenderer(new RelBasicVertexLabelRenderer());
		//vv.getRenderer().setVertexLabelRenderer(relVertexIconLabelShapeTransformer);
		
		RelVertexLabelShapeTransformer<String,String> relVertexLabelShapeTransformer = Generator.getVertexLabelShapeTransformer(vv.getRenderContext());

		vv.getRenderer().setVertexLabelRenderer(relVertexLabelShapeTransformer);
		vv.setVertexLabelShapeTransformer(relVertexLabelShapeTransformer);
		
		vv.getRenderContext().setVertexLabelRenderer(
				new RelVertexLabelRenderer());
		
		//vv.getRenderer().setEdgeRenderer(new RelEdgeRenderer<String,String>());
		//parallel edge 
		vv.getRenderer().setEdgeRenderer(new RelMultiEdgeRenderer<String,String>());
		
		String arrowPosition = ParameterUtil.getString(Parameter.ARROW_POSITION, Parameter.Position.ENDPOINT);
		if( Parameter.Position.CENTER.equals(arrowPosition) ) {
			vv.getRenderer().getEdgeRenderer().setEdgeArrowRenderingSupport(
			new CenterEdgeArrowRenderingSupport());
		}
		
		RelEdgeLabelShapeTransformer<String,String> relEdgeLabelShapeTransformer = Generator.getEdgeLabelShapeTransformer();
		vv.getRenderContext().setEdgeLabelClosenessTransformer( new ConstantDirectionalEdgeValueTransformer<String,String>(0.5, 0.5));
		//vv.getRenderContext().setLabelOffset(1);
		
		vv.getRenderer().setEdgeLabelRenderer(relEdgeLabelShapeTransformer);
		vv.setEdgeLabelShapeTransformer(relEdgeLabelShapeTransformer);
		//vv.getRenderer().setEdgeLabelRenderer(new RelBasicEdgeLabelRenderer<String,String>());	
		
		vv.getRenderContext().setEdgeLabelRenderer(new RelEdgeLabelRenderer());
		
		vv.getRenderer().getVertexLabelRenderer().setPosition(Renderer.VertexLabel.Position.S);
		
		PickedState<String> ps = vv.getPickedVertexState();
		ps.addItemListener(new PickWithIconListener(vv.getRenderContext()
				.getVertexIconTransformer()));
		
		relViewer.setBackgroundImage(
				ParameterUtil.getString(Parameter.BACKGROUND_IMAGE), 
				ParameterUtil.getBoolean(Parameter.BACKGROUND_IMAGE_STRETCH, true));
		
		setFile(ParameterUtil.getString(Parameter.REL_FILE));
	}
	
	public void setTimeBar(boolean bln)
	{
		this.timeBar.setVisible(bln);
	}

	private String getToolTip(String command) {
		return (String) tooltipProp.get(command);
	}

	public RelVisualizationViewer<String, String> getVv() {
		return vv;
	}

	public void setVv(RelVisualizationViewer<String, String> vv) {
		this.vv = vv;
	}

	public DataService getDataService() {
		return dataService;
	}

	public void setDataService(DataService dataService) {
		this.dataService = dataService;
	}

	public URL getCodeBase() {
		return codeBase;
	}

	public void setCodeBase(URL codeBase) {
		this.codeBase = codeBase;
	}

	private JComponent createButton(URL iconURL, String command) {
		ImageIcon imageIcon = new ImageIcon(iconURL);
		JButton button = new JButton(imageIcon);
		button.setActionCommand(command);
		button.addActionListener(this);
		button.setToolTipText(getToolTip(command));
		return button;
	}

	private JToolBar createToolBar() {
		JToolBar toolBar = new JToolBar();

		if (ParameterUtil.getBoolean(Parameter.BUTTON_PANNING, true)) {
			URL url = this.getClass().getResource("/images/handJ2.gif");
			Icon imageIcon = new ImageIcon(url);
			JToggleButton button = new JToggleButton(imageIcon);
			button.setActionCommand(Parameter.Command.PANNING);
			button.addActionListener(this);
			button.setToolTipText(getToolTip(Parameter.Command.PANNING));
			toolBar.add(button);
		}
		if (ParameterUtil.getBoolean(Parameter.BUTTON_ZOOM_IN, true)) {
			URL url = this.getClass().getResource("/images/zoomin.gif");
			toolBar.add(createButton(url, Parameter.Command.ZOOMIN));
		}
		if (ParameterUtil.getBoolean(Parameter.BUTTON_ZOOM_OUT, true)) {
			URL url = this.getClass().getResource("/images/zoomout.gif");
			toolBar.add(createButton(url, Parameter.Command.ZOOMOUT));
		}
		if (ParameterUtil.getBoolean(Parameter.BUTTON_ZOOM_ONE, true)) {
			URL url = this.getClass().getResource("/images/zoomto1.gif");
			toolBar.add(createButton(url, Parameter.Command.ZOOMONE));
		}
		if(ParameterUtil.getBoolean(Parameter.BUTTON_ZOOM_FIT,true)){
			URL url = this.getClass().getResource("/images/zoomfit.gif");
			toolBar.add(createButton(url, Parameter.Command.ZOOMFIT));
		}
		if(ParameterUtil.getBoolean(Parameter.BUTTON_REORGANIZE,true)){
			URL url = this.getClass().getResource("/images/reorganize.gif");
			toolBar.add(createButton(url, Parameter.Command.REORGANIZE));			
		}
		if (ParameterUtil.getBoolean(Parameter.BUTTON_VIEW_GRID, true)) {
			URL url = this.getClass().getResource("/images/viewpeacock.gif");
			toolBar.add(createButton(url, Parameter.Command.VIEWGRID));
		}
		if (ParameterUtil.getBoolean(Parameter.BUTTON_VIEW_GROUP, true)) {
			URL url = this.getClass().getResource("/images/viewgroup.gif");
			toolBar.add(createButton(url, Parameter.Command.VIEWGROUP));
		}
		if (ParameterUtil.getBoolean(Parameter.BUTTON_VIEW_HIERARCHY, true)) {
			URL url = this.getClass().getResource("/images/viewhierarchy.gif");
			toolBar.add(createButton(url, Parameter.Command.VIEWHIERARCY));
		}
		if (ParameterUtil.getBoolean(Parameter.BUTTON_VIEW_ROUND, true)) {
			URL url = this.getClass().getResource("/images/viewround.gif");
			toolBar.add(createButton(url, Parameter.Command.VIEWROUND));
		}
		if (ParameterUtil.getBoolean(Parameter.BUTTON_VIEW_TIMELINE, true)) {
			URL url = this.getClass().getResource("/images/viewtimeline.gif");
			toolBar.add(createButton(url, Parameter.Command.VIEWTIMELINE));
		}
		if (ParameterUtil.getBoolean(Parameter.BUTTON_DELETE, true)) {
			URL url = this.getClass().getResource("/images/delete.gif");
			toolBar.add(createButton(url, Parameter.Command.DELETE));
		}
		if (ParameterUtil.getBoolean(Parameter.BUTTON_LOCK, true)) {
			URL url = this.getClass().getResource("/images/lock.gif");
			toolBar.add(createButton(url, Parameter.Command.LOCK));
		}
		if (ParameterUtil.getBoolean(Parameter.BUTTON_HIDDEN, true)) {
			URL url = this.getClass().getResource("/images/hideselection.gif");
			toolBar.add(createButton(url, Parameter.Command.HIDDEN));
		}
		if (ParameterUtil.getBoolean(Parameter.BUTTON_UNLOCK, true)) {
			URL url = this.getClass().getResource("/images/unlock.gif");
			toolBar.add(createButton(url, Parameter.Command.UNLOCK));
		}
		if (ParameterUtil.getBoolean(Parameter.BUTTON_UNDELETE, true)) {
			URL url = this.getClass().getResource("/images/undelete.gif");
			toolBar.add(createButton(url, Parameter.Command.UNDELETE));
		}
		if (ParameterUtil.getBoolean(Parameter.BUTTON_INVERTSELECTION, true)) {
			URL url = this.getClass().getResource("/images/selectinvert.gif");
			toolBar.add(createButton(url, Parameter.Command.INVERTSELECTION));
		}
		if (ParameterUtil.getBoolean(Parameter.BUTTON_SHOWALL, true)) {
			URL url = this.getClass().getResource("/images/showall.gif");
			toolBar.add(createButton(url, Parameter.Command.SHOWALL));
		}
		
		if (ParameterUtil.getBoolean(Parameter.BUTTON_KEY_EMPHASIS, true)) {
			URL url = this.getClass().getResource("/images/keyemphasis.gif");
			toolBar.add(createButton(url, Parameter.Command.KEYEMPHASIS));
		}

		URL url = this.getClass().getResource("/images/open.gif");
		toolBar.add(createButton(url, Parameter.Command.TESTLOAD));

		return toolBar;
	}

	public void setFile(String file) {
		relFile = file;
		dataService.clear();
		mergeRel(file);
	}

	public void mergeRel(String file) {
		mergeRel(StringUtil.newURL(file, this.codeBase));
	}

	private void mergeRel(final URL url) {

		Runnable runnable = new Runnable() {

			public void run() {
				if (url == null)
					return;

				try {
					DataService ds = new DataService();
					RelReader.load(url, ds);

					dataService.merge(ds);

					relViewer.putEvent(ViewerEvent.EVENT_FILEDONE, url
							.toString());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		};

		Thread t = new Thread(runnable);
		t.start();
	}
	
	public void setMode(String mode)
	{
		if( Parameter.Mode.TRANSFORMING.equals( mode))
		{
			graphMouse.setMode(ModalGraphMouse.Mode.TRANSFORMING);
		}
		else if( Parameter.Mode.PICKING.equals( mode))
		{
			graphMouse.setMode(ModalGraphMouse.Mode.PICKING);
		}
	}

	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		if (cmd.equals(Parameter.Command.PANNING)) {
			JToggleButton b = (JToggleButton) e.getSource();
			if (b.isSelected()) {
				graphMouse.setMode(ModalGraphMouse.Mode.TRANSFORMING);
			} else {
				graphMouse.setMode(ModalGraphMouse.Mode.PICKING);
			}
		} else if (cmd.equals(Parameter.Command.ZOOMIN)) {
			relViewer.setZoom(1.1f);
		} else if (cmd.equals(Parameter.Command.ZOOMOUT)) {
			relViewer.setZoom(1 / 1.1f);
		} else if (cmd.equals(Parameter.Command.ZOOMONE)) {
			relViewer.zoomOne();
		} else if (cmd.equals(Parameter.Command.ZOOMFIT)) {
			relViewer.zoomFit();
		}else if (cmd.equals(Parameter.Command.REORGANIZE)) {
			//relViewer.showWaiting(true);
			relViewer.reorganize();
		}
		else if (cmd.equals(Parameter.Command.VIEWGRID)) {
			relViewer.setTimeBar(false);
			relViewer.reorganizeEx(Parameter.Layout.GRID_LAYOUT);			
		} else if (cmd.equals(Parameter.Command.VIEWGROUP)) {
			relViewer.setTimeBar(false);
			relViewer.reorganizeEx(Parameter.Layout.GROUP_LAYOUT);
		} else if (cmd.equals(Parameter.Command.VIEWHIERARCY)) {
			relViewer.setTimeBar(false);
			relViewer.reorganizeEx(Parameter.Layout.HIERARCHY_LAYOUT);
		} else if (cmd.equals(Parameter.Command.VIEWROUND)) {
			relViewer.setTimeBar(false);
			relViewer.reorganizeEx(Parameter.Layout.ROUND_LAYOUT);
		} else if (cmd.equals(Parameter.Command.VIEWTIMELINE)) {
			relViewer.setTimeBar(true);
			relViewer.reorganizeEx(Parameter.Layout.TIMELINE_LAYOUT);
		} else if (cmd.equals(Parameter.Command.VIEWPEACOCK)) {
			relViewer.reorganizeEx(Parameter.Layout.PEACOCK_LAYOUT);
		} else if (cmd.equals(Parameter.Command.DELETE)) {
			String str = relViewer.getSelectedElements();
			relViewer.setElementsDeleted(str, true);
		} else if (cmd.equals(Parameter.Command.LOCK)) {
			String str = relViewer.getSelectedElementsEx(true, false);
			relViewer.setEndsFixed(str, true);
		} else if (cmd.equals(Parameter.Command.HIDDEN)) {
			String str = relViewer.getSelectedElements();
			relViewer.setElementsHidden(str, true);
		} else if (cmd.equals(Parameter.Command.UNLOCK)) {
			String str = relViewer.getSelectedElementsEx(true, false);
			relViewer.setEndsFixed(str, false);
		} else if (cmd.equals(Parameter.Command.UNDELETE)) {
			String strEnds = relViewer.getDeleteEnds();
			String strLinks = relViewer.getDeleteLinks();
			relViewer.setElementsDeleted(strEnds, false);
			relViewer.setElementsDeleted(strLinks, false);
		} else if (cmd.equals(Parameter.Command.INVERTSELECTION)) {
			relViewer.invertSelection();
		}else if( cmd.equals(Parameter.Command.KEYEMPHASIS)){
			keyEmphasis = !keyEmphasis;
			relViewer.setMaxEmphasised(3);
			relViewer.setConnectivityEmphasis(relViewer.getEndTypesInUse());
			relViewer.setEmphasisOn(keyEmphasis);
		}		
		else if (cmd.equals(Parameter.Command.SHOWALL)) {
			String entStr = relViewer.getInVisibleEndIDs();
			String linkStr = relViewer.getInVisibleEndIDs();

			relViewer.setElementsHidden(entStr, false);
			relViewer.setElementsHidden(linkStr, false);
		}

		else if (e.getSource() instanceof MenuItem) {
			relViewer.putEvent(ViewerEvent.EVENT_MENU_COMMAND, e
					.getActionCommand());
		}

		else if (cmd.equals(Parameter.Command.TESTLOAD)) {
			// System.out.print(getRel());
			//relViewer.showWaiting(true);
			mergeRel(this.getClass().getResource("/xdl.xml"));
		}
	}

	public String getFile() {
		return relFile;
	}

	public String getRel() {
		XmlTraverse xmlTraverse = new XmlTraverse(dataService);
		return xmlTraverse.toXml();
	}

	public void mergeRelString(final String strRel) {
		Runnable runnable = new Runnable() {

			public void run() {
				try {
					DataService ds = new DataService();
					RelReader.loadString(strRel, ds);

					dataService.merge(ds);

					relViewer.putEvent(ViewerEvent.EVENT_FILEDONE,
							"from string");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};

		Thread t = new Thread(runnable);
		t.start();
	}

	public void putRelString(String strRel) {
		dataService.clear();
		mergeRelString(strRel);
	}

	public void reloadFile() {
		setFile(relFile);
	}

	protected class PopupGraphMousePlugin extends AbstractPopupGraphMousePlugin
			implements MouseListener {

		public PopupGraphMousePlugin() {
			this(MouseEvent.BUTTON3_MASK);
		}

		public PopupGraphMousePlugin(int modifiers) {
			super(modifiers);
		}

		@SuppressWarnings("unchecked")
		protected void handlePopup(MouseEvent e) {
			final VisualizationViewer<String, String> vv = (VisualizationViewer<String, String>) e
					.getSource();
			Point2D p = e.getPoint();// vv.getRenderContext().getBasicTransformer().inverseViewTransform(e.getPoint());

			GraphElementAccessor<String, String> pickSupport = vv
					.getPickSupport();
			if (pickSupport != null) {
				final String v = pickSupport.getVertex(vv.getGraphLayout(), p
						.getX(), p.getY());
				String id = v;
				if (id == null) {
					final String edge = pickSupport.getEdge(
							vv.getGraphLayout(), p.getX(), p.getY());
					id = edge;
				}
				if (id != null)
					vv.getPickedVertexState().pick(id, true);
				else
					vv.getPickedVertexState().clear();

				x = e.getX();
				y = e.getY();
				relViewer.putEvent(ViewerEvent.EVENT_CONTEXTMENU, id);
			}
		}
	}

	public static class PickWithIconListener implements ItemListener {
		Transformer<String, Icon> imager;
		static Icon checked = new Checkmark(Color.red);;

		public PickWithIconListener(Transformer<String, Icon> imager) {
			this.imager = imager;
		}

		public void itemStateChanged(ItemEvent e) {
			Icon icon = imager.transform((String) e.getItem());

			if (icon != null && icon instanceof LayeredIcon) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					((LayeredIcon) icon).add(checked);
				} else {
					((LayeredIcon) icon).remove(checked);
				}
			}
		}
	}

	public void showMenu(PopupMenu popupMenu) {
		vv.add(popupMenu);
		popupMenu.show(vv, x, y);
	}
	
	public void showWaiting(boolean blnWaiting)
	{
		if( blnWaiting && !waitingStatus ){
			waitingStatus = true;
			this.remove(chartPanel);		
			this.add("Center",messagePanel);
			//messagePanel.repaint();
			this.validate();
		}
		if( !blnWaiting && waitingStatus){
			waitingStatus = false;
			this.remove(messagePanel);
			this.add("Center", chartPanel);
			chartPanel.repaint();
			this.validate();
		}
	}

	protected class ResizeListener extends ComponentAdapter {

		public void componentHidden(ComponentEvent e) {
		}

		public void componentResized(ComponentEvent e) {
			relViewer.scrollCenter();
		}

		public void componentShown(ComponentEvent e) {
		}
	}
}