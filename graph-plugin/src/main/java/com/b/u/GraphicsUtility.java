package com.b.u;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;

public class GraphicsUtility {
	public static int LEFTPADDING = 20;
	public static int RIGHTPADDING = 20;
	public static int TOPPADDING = 20;
	public static int BOTTOMPADDING = 20;
	
	public static void drawLine(Graphics2D g2, double x1, double y1, double x2, double y2)
	{
		Line2D line = new Line2D.Double(x1, y1, x2, y2);
		g2.draw(line);		
	}
	
	public static void drawTextAtCenter(Graphics2D g2,Font font, String s, float x, float y)
	{
		if( s == null ) return;
		int width = -1;
		FontMetrics labelFontMetrics = Toolkit.getDefaultToolkit().getFontMetrics(
				font);
		width = labelFontMetrics.stringWidth(s);
		drawText(g2,font,s,x-width/2f,y);
	}
	
	public static void drawText(Graphics2D g2, Font font, String s, float x, float y)
	{
		if( s == null ) return;
		Font oldFont = g2.getFont();
		g2.setFont(font);
		g2.drawString(s, x, y);
		g2.setFont(oldFont);
	}
	
	/**
	 * @param f
	 * @return
	 */
	public static Font getScaleFont(Font f, int scale)
	{
		int oSize = f.getSize();
		int nSize = oSize;
		int oStyle = f.getStyle();
		String name = f.getName();
		
		nSize =  oSize + scale;
		
		return new Font(name,oStyle,nSize);
	}
	
	public static int getStringHeight(Font f, String s)
	{
		if ( f == null || s == null ) return 0;
		FontMetrics fontMetrics = Toolkit.getDefaultToolkit().getFontMetrics(f);
		
		return fontMetrics.getHeight();
	}
	
	public static int getStringHeight(Font f)
	{
		FontMetrics fontMetrics = Toolkit.getDefaultToolkit().getFontMetrics(f);
		
		return fontMetrics.getHeight();
	}
	
	public static int getStringHeight(Font f, String[] s)
	{
		if( f== null || s == null) return 0;
		int height = 0;
		
		for( int i=0 ; i < s.length; i++)
			height += getStringHeight(f,s[i]);
		
		return height;
	}
	
	public static int getStringWidth(Font f, String s)
	{
		if ( f == null || s == null ) return 0;
		FontMetrics fontMetrics = Toolkit.getDefaultToolkit().getFontMetrics(f);
		
		return fontMetrics.stringWidth(s);
	}
	
	
	public static int getStringWidth(Font f, String[] s)
	{
		if( f== null || s == null )return 0;
		int width = 0;
		for( int i=0; i<s.length; i++){
			int w = getStringWidth(f,s[i]);
			if( w > width)
				width = w;
		}
		return width;
	}
	
	/**
	 * @param iterator
	 * @param shape
	 * @return
	 */
	public static Line2D getAdjacentLine(PathIterator iterator, Shape shape) {
        Point2D point1 = null;
        Point2D point2 = null;
        float[] segment = new float[6];

        for (; !iterator.isDone(); iterator.next()) {
            int type = iterator.currentSegment(segment);
            if (type != PathIterator.SEG_MOVETO
                    && type != PathIterator.SEG_LINETO) {
                continue;
            }

            point1 = point2;
            point2 = new Point2D.Float(segment[0], segment[1]);

             if (point1 != null && !shape.contains(point1)
                    && shape.contains(point2)) {
                Line2D line = new Line2D.Float(point1, point2);
                Point2D pointOnBoundary = getPointOnBoundary(line, shape);
                
                line.setLine(point1, pointOnBoundary);
                return line;
            }
        }

        return null;
    }

	
	
	/**
	 * get intersection point 
	 * @param line
	 * @param shape
	 * @return
	 */
	public static Point2D getPointOnBoundary(Line2D line, Shape shape) {
		
        double leftX = line.getX1();
        double leftY = line.getY1();
        double rightX = line.getX2();
        double rightY = line.getY2();
        
//        double h = shape.getBounds2D().getHeight();
//        double w = shape.getBounds2D().getWidth();
//        double lh = line.getBounds2D().getHeight();
//        double lw = line.getBounds2D().getWidth();
//        double lf = lh > lw ? lh : lw;
//        double f = h > w? h :w;
//        double factor = lf < f ? lf : f;
//        
//        if( factor == 0 ) factor = 2;
        
        double factor = 10;

        // shape contains line or shape do not intersect line  
        if (shape.contains(leftX, leftY) == shape.contains(rightX, rightY)) {
            //throw new IllegalArgumentException();
        	return null;
        }

        if (shape.contains(leftX, leftY)) {
            double temp;

            // swap
            temp = leftX;
            leftX = rightX;
            rightX = temp;

            // swap
            temp = leftY;
            leftY = rightY;
            rightY = temp;
        }

        double tolerance = 1;//1.0;
        double centerX = leftX + (rightX-leftX ) / factor;
        double centerY = leftY + (rightY -leftY ) / factor;
        while (Math.abs(rightX - centerX) > tolerance
                || Math.abs(rightY - centerY) > tolerance) {
            if (shape.contains(centerX, centerY)) {
                rightX = centerX;
                rightY = centerY;
            } else {
                leftX = centerX;
                leftY = centerY;
            }

            centerX = leftX + (rightX -leftX ) / factor;
            centerY = leftY + ( rightY- leftY) / factor;
        }

        return new Point2D.Double(centerX, centerY);
    }
	
	
	public static Color getColor(String color, Color defaultColor)
	{
		Color c = defaultColor;
		if (color != null && !"".equals(color)) {
			try {
				c = Color.decode(color);
			} catch (NumberFormatException ex) {
			}
		}
		
		return c;
	}


	
}
