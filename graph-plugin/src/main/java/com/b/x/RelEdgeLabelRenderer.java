package com.b.x;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.JComponent;

import g.visualization.renderers.DefaultEdgeLabelRenderer;

public class RelEdgeLabelRenderer extends DefaultEdgeLabelRenderer {
	
	 protected Color pickedEdgeLabelBackgroundColor = Color.GRAY;
	
	 public RelEdgeLabelRenderer()
	 {
		 this(Color.white, false);
	 }
	 
	public RelEdgeLabelRenderer(Color pickedEdgeLabelColor) {
		super(pickedEdgeLabelColor);
	}

	public RelEdgeLabelRenderer(Color pickedEdgeLabelColor,
			boolean rotateEdgeLabels) {
		super(pickedEdgeLabelColor, rotateEdgeLabels);
	}
	
	 public <E> Component getEdgeLabelRendererComponent(JComponent vv, Object value,
	            Font font, boolean isSelected, E edge) {
		 super.setForeground(vv.getForeground());
	        super.setBackground(vv.getBackground());
	        if(isSelected) {
	        	setBackground(pickedEdgeLabelBackgroundColor);
		        setForeground(pickedEdgeLabelColor);
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
