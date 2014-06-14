package com.b.u;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class RightBaseHourRuler extends HourRuler {
	
	protected double startx;
	protected long startTime;
	protected double endx;
	protected long endTime;
	
	public int getType() {
		return TimeRuler.H3RULER;
	}
	
	public RightBaseHourRuler( double sx, long et, double ex)
	{
		startx = sx;
		endx = ex;
		endTime = et;
		
		if (endx < startx)
			throw new IllegalArgumentException(
					"endx must be greater than startx");
		
		calc();
	}
	
	private void calc()
	{
		double ex = endx;
		long et = endTime;
		
		Calendar c1 = Calendar.getInstance();
		c1.setTimeInMillis(et);
		
		Calendar c = Calendar.getInstance();
		c.clear();
		c.set(c1.get(Calendar.YEAR), c1.get(Calendar.MONTH), c1.get(Calendar.DATE));
		c.add(Calendar.DATE,1);
		//c.set(Calendar.HOUR_OF_DAY, c1.get(Calendar.HOUR_OF_DAY));
		
		double pos = ex;
		
		while(pos >= 0)
		{
			long t = c.getTimeInMillis();
			if( t > et ) {
				c.add(Calendar.HOUR_OF_DAY, -3);
				continue;
			}
			
			marks.add(t);
			if( TimeUtil.isExactDay(t)){
				labelMarks.add(t);
			}
			
			pos = ex - measure * (et-t);
			c.add(Calendar.HOUR_OF_DAY, -3);
		}
		
		startTime = c.getTimeInMillis();
		startx = pos;
		
		while(true){
			long t = c.getTimeInMillis();
			if( TimeUtil.isExactDay(t)) {
				labelMarks.add(t);
				break;
			}
			c.add(Calendar.HOUR_OF_DAY, -3);
		}		
	}
	
	public List<Long> getMarks() {
		List<Long> l = new ArrayList<Long>();
		for( int i=marks.size() -1 ; i>=0; i--){
			l.add(marks.get(i));
		}
		return l;
	}
	
	public List<Long> getLabelMarks() {
		List<Long> l = new ArrayList<Long>();
		for( int i=labelMarks.size() -1 ; i>=0; i--){
			l.add(labelMarks.get(i));
		}
		return l;
	}
	
	public double getMarkPos(Long mark) {		
		return  endx - ( endTime - mark ) * measure;
	}

	public double getLength() {
		return endx-startx;
	}

}