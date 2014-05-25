package com.b.u;

import java.util.Calendar;

public class LeftBaseHourRuler extends HourRuler {
	protected double startx;
	protected long startTime;
	protected double endx;
	protected long endTime;
	
	public int getType() {
		return TimeRuler.H3RULER;
	}
	
	public LeftBaseHourRuler(long st, double sx, double ex)
	{
		startx = sx;
		startTime = st;
		endx = ex;
		
		if (endx < startx)
			throw new IllegalArgumentException(
					"endx must be greater than startx");
		
		calc();
	}
	
	private void calc()
	{
		double sx = startx;
		long st = startTime;
		
		Calendar c1 = Calendar.getInstance();
		c1.setTimeInMillis(st);
		
		Calendar c = Calendar.getInstance();
		c.clear();
		c.set(c1.get(Calendar.YEAR), c1.get(Calendar.MONTH), c1.get(Calendar.DATE));
		c.add(Calendar.DATE, -1);
		//c.set(Calendar.HOUR_OF_DAY, c1.get(Calendar.HOUR_OF_DAY));
		
		double pos = sx;
		while(pos <= endx)
		{
			long t = c.getTimeInMillis();
			if( t < st ) {
				c.add(Calendar.HOUR_OF_DAY, 3);
				continue;
			}
			
			marks.add(t);
			if( TimeUtil.isExactDay(t)) labelMarks.add(t);
			
			pos = sx + measure * (t-st);
			c.add(Calendar.HOUR_OF_DAY, 3);
		}
		
		endTime = c.getTimeInMillis();
		endx = pos;
		
		while(true){
			long t = c.getTimeInMillis();
			if( TimeUtil.isExactDay(t)) {
				labelMarks.add(t);
				break;
			}
			c.add(Calendar.HOUR_OF_DAY, 3);
		}	
	}

	public double getLength() {		
		return endx - startx;
	}

	public double getMarkPos(Long mark) {		
		return  startx + ( mark - startTime) * measure;
	}
	
}