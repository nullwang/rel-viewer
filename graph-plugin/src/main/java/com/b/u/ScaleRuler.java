package com.b.u;

import java.util.ArrayList;
import java.util.List;

public class ScaleRuler extends HourRuler {

	protected double startx;
	protected long startTime;
	protected double endx;
	protected long endTime;
	protected int rulerType;
	
	protected List<Long> smarks = null;

	public ScaleRuler(long st, double sx, long et, double ex) {
		startx = sx;
		startTime = st;

		endx = ex;
		endTime = et;

		if (endx < startx)
			throw new IllegalArgumentException(
					"endx must be greater than startx");
		if (endTime < startTime)
			throw new IllegalArgumentException(
					"endTime must be greater than startTime");

		calc();
	}

	private void calc() {
		double sx = startx;
		long st = startTime;
		double ex = endx;
		long et = endTime;

		double len = getLength();

		if (ex == sx || ex - sx < 0.1) {
			measure = 0;
			return;
		}

		measure = (double) (ex - sx)/(et - st) ;

		int yearCount = TimeUtil.getMarksCount(TimeUtil.YEARMARK, st, et);
		if (yearCount > 0) {
			if (yearCount   > len / ( 2 * stdMarkLength ) ) {
				setYearRuler(st, et);
			} else  {
				setMonthRuler(st, et);
			}
			return;
		}

		int monthCount = TimeUtil.getMarksCount(TimeUtil.MONTHMARK, st, et);
		if (monthCount > 0) {
			if (monthCount  > len / ( 2 * stdMarkLength ) ) {
				setMonthRuler(st, et);
			} else {
				setDayRuler(st, et);
			}
			return;
		}

		int dayCount = TimeUtil.getMarksCount(TimeUtil.DAYMARK, st, et);
		if (dayCount > 0) {
			if (dayCount  > len / ( 4 * stdMarkLength ) ) {
				setDayRuler(st, et);
			} else {
				int h6Count = TimeUtil.getMarksCount(TimeUtil.H6MARK, st, et);
				if (h6Count  >= len / (  2 * stdMarkLength) ) {
					setH6Ruler(st, et);
				}
				else {
					int h3Count = TimeUtil.getMarksCount(TimeUtil.H3MARK, st, et);				
					if (h3Count  >= len / ( 2 *  stdMarkLength)) {
						setH3Ruler(st, et);
					}
					else setHourRuler(st,et);		
				}
			}
			return;
		}
		
		int hourCount = TimeUtil.getMarksCount(TimeUtil.HMARK, st, et);
		if( hourCount > 0){
			setHourRuler(st,et);
			return;
		}
		
		int minuteCount = TimeUtil.getMarksCount(TimeUtil.MMARK, st, et);
		if( minuteCount > 0){
			setMinuteRuler(st,et);
			return;
		}
	}
	
	public List<Long> getMarks() {
		if( smarks !=null) return smarks;
		else {
			smarks = new ArrayList<Long> ();
			double markAverageLen = getLength() / marks.size();
			
			int disFactor = (int) (2d / markAverageLen) + 1;
			
			for( int j=0; j<marks.size(); j++){
				if( disFactor > 1 && j % disFactor != 0 )
					continue;
				smarks.add(marks.get(j));
			}
			
			return smarks;
		}
		
	}

	private void setYearRuler(long st, long et) {
		rulerType = TimeRuler.YEARRULER;
		Long[] years = TimeUtil.getMarks(TimeUtil.YEARMARK, st, et);
		for (int i = 0; i < years.length; i++) {
			marks.add(years[i]);
		}
	}

	private void setMonthRuler(long st, long et) {
		rulerType = TimeRuler.MONTHRULER;
		Long[] months = TimeUtil.getMarks(TimeUtil.MONTHMARK, st, et);
		for (int i = 0; i < months.length; i++) {
			marks.add(months[i]);
			if (TimeUtil.isExactYear(months[i])) {
				labelMarks.add(months[i]);
			}
		}
	}

	private void setDayRuler(long st, long et) {
		rulerType = TimeRuler.DAYRULER;
		Long[] days = TimeUtil.getMarks(TimeUtil.DAYMARK, st, et);
		for (int i = 0; i < days.length; i++) {
			marks.add(days[i]);
			if (TimeUtil.isExactMonth(days[i])) {
				labelMarks.add(days[i]);
			}
		}
	}
	
	private void setH6Ruler(long st, long et){
		rulerType = TimeRuler.H6RULER;
		Long[] hours = TimeUtil.getMarks(TimeUtil.H6MARK, st, et);
		for (int i = 0; i < hours.length; i++) {
			marks.add(hours[i]);
			if (TimeUtil.isExactDay(hours[i])) {
				labelMarks.add(hours[i]);
			}
		}
	}

	private void setH3Ruler(long st, long et) {
		rulerType = TimeRuler.H3RULER;
		Long[] hours = TimeUtil.getMarks(TimeUtil.H3MARK, st, et);
		for (int i = 0; i < hours.length; i++) {
			marks.add(hours[i]);
			if (TimeUtil.isExactDay(hours[i])) {
				labelMarks.add(hours[i]);
			}
		}
	}
	
	private void setHourRuler(long st, long et)
	{
		rulerType = TimeRuler.HRULER;
		Long[] hours = TimeUtil.getMarks(TimeUtil.HMARK, st, et);
		for (int i = 0; i < hours.length; i++) {
			marks.add(hours[i]);
			if (TimeUtil.isExactDay(hours[i])) {
				labelMarks.add(hours[i]);
			}
		}
	}
	
	private void setMinuteRuler(long st, long et)
	{
		rulerType = TimeRuler.MRULER;
		Long[] minutes = TimeUtil.getMarks(TimeUtil.MMARK, st, et);
		for (int i = 0; i < minutes.length; i++) {
			marks.add(minutes[i]);
			if (TimeUtil.isExactDay(minutes[i])) {
				labelMarks.add(minutes[i]);
			}
		}
	}
	
	public int getType() {
		return rulerType;
	}

	public double getLength() {
		return endx - startx;
	}

	public double getMarkPos(Long mark) {
		return startx + (mark - startTime) * measure;
	}

}