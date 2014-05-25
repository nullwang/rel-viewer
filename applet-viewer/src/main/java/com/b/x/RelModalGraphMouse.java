package com.b.x;

import java.awt.event.InputEvent;

import com.b.a.ClientEventSupport;
import com.b.a.Parameter;
import com.b.u.ParameterUtil;

import g.visualization.control.AnimatedPickingGraphMousePlugin;
import g.visualization.control.CrossoverScalingControl;
import g.visualization.control.DefaultModalGraphMouse;
import g.visualization.control.RotatingGraphMousePlugin;
import g.visualization.control.ScalingGraphMousePlugin;
import g.visualization.control.ShearingGraphMousePlugin;
import g.visualization.control.TranslatingGraphMousePlugin;

public class RelModalGraphMouse<V,E> extends DefaultModalGraphMouse<V,E> {
	
	@SuppressWarnings ("unchecked")
	public void setEventSupport(ClientEventSupport eventSupport) {
		if(pickingPlugin instanceof  RelPickingGraphMousePlugin)
        ((RelPickingGraphMousePlugin)pickingPlugin ).setEventSupport(eventSupport);
	}
	
	@Override
	 protected void loadPlugins() {
        pickingPlugin = new RelPickingGraphMousePlugin<V,E>();
        animatedPickingPlugin = new AnimatedPickingGraphMousePlugin<V,E>();
        translatingPlugin = new TranslatingGraphMousePlugin(InputEvent.BUTTON1_MASK);
        
        if( ParameterUtil.getBoolean(Parameter.MOUSE_SCALING, true) ) {
        	scalingPlugin = new ScalingGraphMousePlugin(new CrossoverScalingControl(), 0, in, out);
        }
        
        rotatingPlugin = new RotatingGraphMousePlugin();
        shearingPlugin = new ShearingGraphMousePlugin();
        
        if( scalingPlugin instanceof ScalingGraphMousePlugin) {        	
        	(( ScalingGraphMousePlugin) scalingPlugin).setZoomAtMouse(false);
        }
        
        
        add(scalingPlugin);
        setMode(Mode.PICKING);
    }
	

}
