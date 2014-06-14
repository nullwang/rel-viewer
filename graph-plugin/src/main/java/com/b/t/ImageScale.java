package com.b.t;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

public class ImageScale extends JFrame {
		Image image;
	  Insets insets;
	  public ImageScale() {
	    super("Show Image Scales");
	    ImageIcon imageIcon = new ImageIcon(this.getClass().getResource("/src/main/resources/images/zoomto1.gif"));
	    image = imageIcon.getImage();
	  }
	  public void paint(Graphics g) {
	    super.paint(g);
	    if (insets == null) {
	      insets = getInsets();
	    }
	    g.drawImage(image, insets.left, insets.top, this);
	  }
	  public void scale() {
	    reset();
	    Image img = image;
	    image = img.getScaledInstance(250, -1, Image.SCALE_FAST);
	    repaint();
	    reset();
	    
	    image = img.getScaledInstance(300, -1, Image.SCALE_SMOOTH);
	    repaint();
	    reset();
	    
	    image = img.getScaledInstance(450, -1, Image.SCALE_REPLICATE);
	    repaint();
	    reset();
	    
	    image = img.getScaledInstance(400, -1, Image.SCALE_DEFAULT);
	    repaint();
	    reset();
	  image = img.getScaledInstance(350, -1, Image.SCALE_AREA_AVERAGING);
	  repaint();
	    reset();
	    System.exit(0);
	  }
	  private void reset() {
	    try {
	      Thread.sleep(3000);
	    } catch (Exception e) {}
	  }
	  public static void main(String args[]) {
	    ImageScale imgScale = new ImageScale();
	    imgScale.setSize(400, 200);
	    imgScale.show();
	    imgScale.scale();
	  }
}
