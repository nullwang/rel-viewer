/*
 * Copyright (c) 2005, the JUNG Project and the Regents of the University of
 * California All rights reserved.
 *
 * This software is open-source under the BSD license; see either "license.txt"
 * or http://jung.sourceforge.net/license.txt for a description.
 *
 * Created on Aug 26, 2005
 */

package g.visualization.control;

import java.awt.event.ItemListener;

import g.visualization.VisualizationViewer.GraphMouse;

/**
 * Interface for a GraphMouse that supports modality.
 * 
 * @author Tom Nelson 
 *
 */
public interface ModalGraphMouse extends GraphMouse {
    
    void setMode(Mode mode);
    
    /**
     * @return Returns the modeListener.
     */
    ItemListener getModeListener();
    
    /**
     */
    enum Mode { TRANSFORMING, PICKING, ANNOTATING, EDITING }
    
}