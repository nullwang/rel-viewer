package com.b.x;

import java.awt.geom.GeneralPath;

import g.visualization.util.ArrowFactory;

public class MarkFactory extends ArrowFactory {
	
	public static GeneralPath getMark(float width, float height)
	{
		GeneralPath arrow = new GeneralPath();
	        arrow.moveTo(0,0);
	        arrow.lineTo( width/2.0f, 0);
	        arrow.lineTo( width/2.0f, height);
	        arrow.lineTo(-width/2.0f, height);
	        arrow.lineTo(-width/2.0f, 0);
	        arrow.lineTo( 0, 0 );
	        return arrow;
	}
	
	public static GeneralPath getLine(float heigth)
	{
		GeneralPath arrow = new GeneralPath();
		arrow.moveTo(0,0);
		 arrow.lineTo( 0, heigth);
	        arrow.lineTo( 0, 0 );
		 return arrow;
	}

}