package com.b.u;

import java.util.ArrayList;
import java.util.List;

public abstract class HourRuler implements TimeRuler {
	
	protected List<Long> marks = new ArrayList<Long>();
	protected List<Long> labelMarks = new ArrayList<Long>();

	public List<Long> getMarks() {
		return marks;
	}

	public List<Long> getLabelMarks() {
		return labelMarks;
	}

	protected double measure = stdMarkLength / (3600 * 1000D);

	public int getType() {
		return TimeRuler.HRULER;
	}

	public double getMeasure() {
		return measure;
	}
	
	public String getMarkText(Long mark)
	{
		int type = getType();
	
		String name = null;
		switch(type)
		{
		case TimeRuler.YEARRULER: 
			name = TimeUtil.getYearShortName(mark);
			break;
		case TimeRuler.MONTHRULER:
			name = TimeUtil.getMonthShortName(mark);
			break;
		case TimeRuler.DAYRULER:
			name = TimeUtil.getDayShortName(mark);
			break;
		case TimeRuler.H6RULER:
		case TimeRuler.H3RULER:
		case TimeRuler.HRULER:
			name = TimeUtil.getHourShortName(mark);
			break;
		case TimeRuler.MRULER:
			name = TimeUtil.getHourName(mark);
		}
		
		return name;
	}
	
	public String getLabelText(Long mark)
	{
		int type = getType();
		
		String name = null;
		switch(type)
		{
		case TimeRuler.MONTHRULER:
			name = TimeUtil.getYearName(mark);
			break;
		case TimeRuler.DAYRULER:
			name = TimeUtil.getMonthName(mark);
			break;
		case TimeRuler.H6RULER:
		case TimeRuler.H3RULER:
		case TimeRuler.HRULER:
		case TimeRuler.MRULER:
			name = TimeUtil.getDateName(mark);
			break;
		}
		
		return name;
	}
}
