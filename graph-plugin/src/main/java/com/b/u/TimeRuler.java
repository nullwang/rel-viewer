package com.b.u;

import java.util.List;

public interface TimeRuler {
	
	int YEARRULER = 0;
	
	int MONTHRULER = 1;
	
	int DAYRULER = 2;
	
	int H6RULER = 3;
	
	int H3RULER = 4;
	
	int HRULER = 5;
	
	int MRULER = 6;
	
	double stdMarkLength = 9;
		
	double getMeasure();
	
	List<Long> getMarks();
	
	int getType();
	
	List<Long> getLabelMarks();
	
	double getMarkPos(Long mark);
	
	double getLength();
	
	String getMarkText(Long mark);
	
	String getLabelText(Long mark);
}