package com.b.g;

import java.awt.*; 
import java.awt.event.*;

public class ApplicationFrame  extends Frame { 

public ApplicationFrame() { this("ApplicationFrame v1.0"); }

public ApplicationFrame(String title) { 
  super(title); 
  createUI(); 
} 
 
protected void createUI() { 
  setSize(500, 400); 
  center(); 

  addWindowListener(new WindowAdapter() { 
    public void windowClosing(WindowEvent e) { 
      dispose(); 
      System.exit(0); 
    } 
  }); 
} 
 
public void center() { 
  Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize(); 
  Dimension frameSize = getSize(); 
  int x = (screenSize.width - frameSize.width) / 2; 
  int y = (screenSize.height - frameSize.height) / 2; 
  setLocation(x, y); 
} 
}