package com.b.u;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class TimeMark {
	
	public Calendar getTime() {
		return time;
	}
	
	public int getTime( int field){
		return time.get(field);
	}
	
	public String getDate()
	{		
		return new SimpleDateFormat("yyyyMMdd").format(this.time.getTime());
	}
	
	public String getHours(){
		return String.valueOf(this.getTime(Calendar.HOUR_OF_DAY));
	}
	
	public String getText()
	{
		StringBuilder sb = new StringBuilder();
		switch(this.type){
		case TimeMark.HOUR:
			sb.append(getHours()); break;
		case TimeMark.DAY:
			sb.append(getDate()); break;
		}
		
		return sb.toString();
	}

	public void setTime(Calendar time) {
		this.time = time;
		
		int hourOfDay = time.get(time.HOUR_OF_DAY);
		int minute = time.get(time.MINUTE);
		int second = time.get(time.SECOND);
		
		if( hourOfDay == 0 && minute == 0 && second == 0)
			setType(TimeMark.DAY);	
		else if(  minute == 0 && second == 0)
			setType(TimeMark.HOUR);
		
	}

	public double x;
	
	Calendar time;

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}
	
	int type;	
	
	public int getType() {
		return type;
	}

	private void setType(int type) {
		this.type = type;
	}

	public   static  final int HOUR = 0;
	public static final int DAY=1;
	
	int height[] = new int[2];
	
	public TimeMark()
	{
		height[HOUR] = 4;
		height[DAY]= 12;		
	}
	
	public void setTypeHeight(int heigth, int type)
	{	
		if (type < 0 || type > height.length-1 ) return;
		
		this.height[type] = heigth;		
	}
	
	
	public int getHeight()
	{
		return this.height[this.type];
	}

	public String toString()
	{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		StringBuilder sb = new StringBuilder();
		
		sb.append("[x=").append(x).append("]");
		sb.append("[time=").append(sdf.format(time.getTime())).append("]");
		
		return sb.toString();
	}
}