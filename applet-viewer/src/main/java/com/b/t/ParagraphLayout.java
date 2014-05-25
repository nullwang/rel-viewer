package com.b.t;

import java.awt.*; 
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.font.*;
import java.text.*;

import com.b.g.ApplicationFrame;

public class ParagraphLayout {
	final static int tabCount = 3;
	
	public static void main(String[] args) { 
	    final Frame f = new ApplicationFrame("ParagraphLayout v1.0") { 
	    	
	   
	      public void paint(Graphics g) { 
	        Graphics2D g2 = (Graphics2D)g; 
	 
	        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
	            RenderingHints.VALUE_ANTIALIAS_ON); 
	     // From _One Hundred Years of Solitude_ by Gabriel Garcia Marquez. 
	        String s = "�����й���ɽ�Ŀ��Ȼ��˵�ʱ"+"\r\n"+
	        		"�о��� ������˹�ĸо����˶��ٹ��"
	        	+"����ɽ�Ŀ��Ȼ��쵼�ſ�����" ;
	        Font font = new Font("Serif", Font.PLAIN, 24); 
	        AttributedString as = new AttributedString(s); 
	        as.addAttribute(TextAttribute.FONT, font); 
	        AttributedCharacterIterator aci = as.getIterator(); 
	         
	        FontRenderContext frc = g2.getFontRenderContext(); 
	       
	        LineBreakMeasurer lbm = new LineBreakMeasurer(aci, frc); 
	        Insets insets = getInsets(); 
	        float wrappingWidth = getSize().width - insets.left - insets.right; 
	        System.out.println(wrappingWidth);
	        float x = insets.left; 
	        float y = insets.top; 
	        
	        
	        while (lbm.getPosition() < aci.getEndIndex()) { 
	          TextLayout textLayout = lbm.nextLayout(wrappingWidth); 
	          y += textLayout.getAscent(); 
	          textLayout.draw(g2, x, y); 
	          Shape shape = textLayout.getOutline(null);
	          y += textLayout.getDescent() + textLayout.getLeading(); 
	          x = insets.left; 
	        } 
//	        AttributedCharacterIterator styledText = aci;
//	        
//	        float leftMargin = 10, rightMargin = 310;
//	        float[] tabStops = { 100, 250 };
//
//	        // assume styledText is an AttributedCharacterIterator, and the number
//	        // of tabs in styledText is tabCount
//
//	        int[] tabLocations = new int[tabCount+1];
//
//	        int i = 0;
//	        for (char c = styledText.first(); c != styledText.DONE; c = styledText.next()) {
//	            if (c == '\t') {
//	                tabLocations[i++] = styledText.getIndex();
//	            }
//	        }
//	        tabLocations[tabCount] = styledText.getEndIndex() - 1;
//
//	        // Now tabLocations has an entry for every tab's offset in
//	        // the text.  For convenience, the last entry is tabLocations
//	        // is the offset of the last character in the text.
//
//	        LineBreakMeasurer measurer = new LineBreakMeasurer(styledText,frc);
//	        int currentTab = 0;
//	        float verticalPos = 20;
//
//	        while (measurer.getPosition() < styledText.getEndIndex()) {
//
//	            // Lay out and draw each line.  All segments on a line
//	            // must be computed before any drawing can occur, since
//	            // we must know the largest ascent on the line.
//	            // TextLayouts are computed and stored in a Vector;
//	            // their horizontal positions are stored in a parallel
//	            // Vector.
//
//	            // lineContainsText is true after first segment is drawn
//	            boolean lineContainsText = false;
//	            boolean lineComplete = false;
//	            float maxAscent = 0, maxDescent = 0;
//	            float horizontalPos = leftMargin;
//	            Vector layouts = new Vector(1);
//	            Vector penPositions = new Vector(1);
//
//	            while (!lineComplete) {
//	                float wrappingWidth = rightMargin - horizontalPos;
//	                TextLayout layout =
//	                        measurer.nextLayout(wrappingWidth,
//	                                            tabLocations[currentTab]+1,
//	                                            lineContainsText);
//
//	                // layout can be null if lineContainsText is true
//	                if (layout != null) {
//	                    layouts.addElement(layout);
//	                    penPositions.addElement(new Float(horizontalPos));
//	                    horizontalPos += layout.getAdvance();
//	                    maxAscent = Math.max(maxAscent, layout.getAscent());
//	                    maxDescent = Math.max(maxDescent,
//	                        layout.getDescent() + layout.getLeading());
//	                } else {
//	                    lineComplete = true;
//	                }
//
//	                lineContainsText = true;
//
//	                if (measurer.getPosition() == tabLocations[currentTab]+1) {
//	                    currentTab++;
//	                }
//
//	                if (measurer.getPosition() == styledText.getEndIndex())
//	                    lineComplete = true;
//	                else if (horizontalPos >= tabStops[tabStops.length-1])
//	                    lineComplete = true;
//
//	                if (!lineComplete) {
//	                    // move to next tab stop
//	                    int j;
//	                    for (j=0; horizontalPos >= tabStops[j]; j++) {}
//	                    horizontalPos = tabStops[j];
//	                }
//	            }
//
//	            verticalPos += maxAscent;
//
//	            Enumeration layoutEnum = layouts.elements();
//	            Enumeration positionEnum = penPositions.elements();
//
//	            // now iterate through layouts and draw them
//	            while (layoutEnum.hasMoreElements()) {
//	                TextLayout nextLayout = (TextLayout) layoutEnum.nextElement();
//	                Float nextPosition = (Float) positionEnum.nextElement();
//	                nextLayout.draw(g2, nextPosition.floatValue(), verticalPos);
//	            }
//
//	            verticalPos += maxDescent;
//	        }

	        
	        
	        
	      } 
	    }; 
	  f.addComponentListener(new ComponentListener (){

		public void componentHidden(ComponentEvent e) {
			// TODO Auto-generated method stub
			
		}

		public void componentMoved(ComponentEvent e) {
			// TODO Auto-generated method stub
			
		}

		public void componentResized(ComponentEvent e) {
			f.repaint();			
		}

		public void componentShown(ComponentEvent e) {
			// TODO Auto-generated method stub
			
		}

		
	  });
	    f.setVisible(true); 
	  } 
}
