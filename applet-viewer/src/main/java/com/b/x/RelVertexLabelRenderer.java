package com.b.x;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.JComponent;

import g.visualization.renderers.DefaultVertexLabelRenderer;

public class RelVertexLabelRenderer extends DefaultVertexLabelRenderer {

	 protected Color pickedVertexLabelBackgroundColor = Color.GRAY;
	
	public RelVertexLabelRenderer(Color pickedVertexLabelColor) {
		super(pickedVertexLabelColor);
		//this.setOpaque(false);
	}
	
	public RelVertexLabelRenderer()
	{
		this(Color.white);
	}
	
	@Override
	public <V> Component getVertexLabelRendererComponent(JComponent vv, Object value,
	            Font font, boolean isSelected, V vertex) {	

				super.setBackground(vv.getBackground());
		        super.setForeground(vv.getForeground());
		        if(isSelected) {
		        	setBackground(pickedVertexLabelBackgroundColor);
			        setForeground(pickedVertexLabelColor);
		        	//setForeground(pickedVertexLabelColor);
		        }
		        if(font != null) {
		            setFont(font);
		        } else {
		            setFont(vv.getFont());
		        }
		        setIcon(null);
		        setBorder(noFocusBorder);
		        setValue(value);
		        return this;
		    }

}
