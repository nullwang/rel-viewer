package com.b.e;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Formatter implements Cloneable{
	
	String syntax;
	
	String text;

	public String getSyntax() {
		return syntax;
	}

	public void setSyntax(String syntax) {
		this.syntax = syntax;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
	
	public Date formatDate(String strOfDate)
	{
		SimpleDateFormat sdf = new SimpleDateFormat();
		Date d = null;
		try {
			sdf.applyPattern(getText());
			d = sdf.parse(strOfDate, new ParsePosition(0));
			if (d != null)
				return d;		
		}catch ( Exception e) {}
		return d;
	}
	
	@Override
	public Object clone()
	{
		Formatter o = null;
		try {
			o = (Formatter) super.clone();
			
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		
		return o;
		
	}

}
