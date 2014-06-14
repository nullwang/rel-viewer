package com.b.u;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class TimeUtil {
	
	public static final int NOMARK =  -1;
	public static final int YEARMARK = 0;
	public static final int MONTHMARK = 1;
	public static final int DAYMARK = 2;
	public static final int H12MARK = 3;
	public static final int H6MARK = 4;
	public static final int H3MARK = 5;
	public static final int HMARK = 6;
	public static final int M30MARK = 7;
	public static final int M10MARK = 8;
	public static final int MMARK = 9;
	public static final int S30MARK = 10;
	public static final int S10MARK = 11;
	public static final int SMARK = 12;
		
	public static int getMarksCount( int markType, long t1, long t2)
	{
		int count = 0;
		
		Calendar c1 = Calendar.getInstance();
		c1.setTimeInMillis(t1);
		Calendar c = Calendar.getInstance();
		c.clear();
		initC(markType, c,c1);
		while(c.getTimeInMillis()<= t2)
		{
			if( c.getTimeInMillis() < t1 )
			{
				addC(markType, c);
				continue;
			}
			count ++;
			addC(markType,c);
		}
	
		return count;
	}
	
	public static Long[] getMarks( int markType, long t1, long t2)
	{
		ArrayList<Long> al = new ArrayList<Long>();
		
		Calendar c1 = Calendar.getInstance();
		c1.setTimeInMillis(t1);
		Calendar c = Calendar.getInstance();
		c.clear();
		initC(markType, c,c1);
		while(c.getTimeInMillis()<= t2)
		{
			if( c.getTimeInMillis() < t1 )
			{
				addC(markType, c);
				continue;
			}
			al.add(c.getTimeInMillis());			
			addC(markType,c);
		}
	
		return al.toArray(new Long[0]);
	}
	
	private static void initC(int markType, Calendar c, Calendar c1)
	{
		switch(markType){
		case YEARMARK:
			c.set(c1.get(Calendar.YEAR),0, 1);
			break;
		case MONTHMARK:
			c.set(c1.get(Calendar.YEAR), c1.get(Calendar.MONTH), 1);
			break;
		case DAYMARK:
			c.set(c1.get(Calendar.YEAR), c1.get(Calendar.MONTH), c1.get(Calendar.DATE));
			break;
		case H12MARK:
		case H6MARK:
		case H3MARK:
		case HMARK:
			c.set(c1.get(Calendar.YEAR), c1.get(Calendar.MONTH), 
					c1.get(Calendar.DATE));
			c.set(Calendar.HOUR_OF_DAY, c1.get(Calendar.HOUR_OF_DAY));
			break;
		case M30MARK:
		case M10MARK:
		case MMARK:
			c.set(c1.get(Calendar.YEAR), c1.get(Calendar.MONTH), 
					c1.get(Calendar.DATE));
			c.set(Calendar.HOUR_OF_DAY, c1.get(Calendar.HOUR_OF_DAY));
			c.set(Calendar.MINUTE, c1.get(Calendar.MINUTE));
			break;
		case S30MARK:
		case S10MARK:
		case SMARK:
			c.set(c1.get(Calendar.YEAR), c1.get(Calendar.MONTH), 
					c1.get(Calendar.DATE));
			c.set(Calendar.HOUR_OF_DAY, c1.get(Calendar.HOUR_OF_DAY));
			c.set(Calendar.MINUTE, c1.get(Calendar.MINUTE));
			c.set(Calendar.SECOND, c1.get(Calendar.SECOND));
			break;
		default:
			c.set(c1.get(Calendar.YEAR), c1.get(Calendar.MONTH), c1.get(Calendar.DATE));
			break;
		}
	}
	
	private static void addC(int markType, Calendar c)
	{
		switch(markType){
		case YEARMARK:
			c.add(Calendar.YEAR, 1);
			break;
		case MONTHMARK:
			c.add(Calendar.MONTH, 1);			
			break;
		case DAYMARK:
			c.add(Calendar.DATE, 1);			
			break;
		case H12MARK:
			c.add(Calendar.HOUR_OF_DAY, 12);
			break;
		case H6MARK:
			c.add(Calendar.HOUR_OF_DAY, 6);
			break;
		case H3MARK:
			c.add(Calendar.HOUR_OF_DAY, 3);
			break;
		case HMARK:
			c.add(Calendar.HOUR_OF_DAY, 1);
			break;			
		case M30MARK:
			c.add(Calendar.MINUTE, 30);
			break;
		case M10MARK:
			c.add(Calendar.MINUTE, 10);
			break;
		case MMARK:
			c.add(Calendar.MINUTE, 1);			
			break;
		case S30MARK:
			c.add(Calendar.SECOND, 30);
			break;
		case S10MARK:
			c.add(Calendar.SECOND, 10);
			break;
		case SMARK:
			c.add(Calendar.SECOND, 1);
			break;
		default:
			c.add(Calendar.YEAR, 1);
			break;
		}
	}
	
	public static boolean isExactHour(long t)
	{
		return t % (60*60*1000) == 0;
	}
	
	public static boolean isExactMinute(long t)
	{
		return t%(60*1000) == 0;
	}
	
	public static boolean isExactDay(long t)
	{
		if( isExactHour(t)){
			Calendar c = Calendar.getInstance();
			c.setTimeInMillis(t);
			if( c.get(Calendar.HOUR_OF_DAY) == 0 ) return true;
		}
		return false;
	}
	
	public static boolean isExactMonth(long t)
	{
		if( isExactDay(t))
		{
			Calendar c = Calendar.getInstance();
			c.setTimeInMillis(t);
			if( c.get(Calendar.DAY_OF_MONTH) == 1 ) return true;
		}
			
		return false;
	}
	
	public static boolean isExactYear(long t)
	{
		if( isExactMonth(t))
		{
			Calendar c = Calendar.getInstance();
			c.setTimeInMillis(t);
			if( c.get(Calendar.MONTH) == 0 ) return true;			
		}
		
		return false;
	}
	
	public static String getYearShortName(long t)
	{
		return getYearName(t);
	}
	
	public static String getYearName(long t)
	{
		return getTimeFieldName(t,"yyyy")+"";
	}
	
	public static String getMonthName(long t)
	{
		return getTimeFieldName(t,"yyyy-MMM");
	}
	
	public static String getMonthShortName(long t)
	{
		return getTimeFieldName(t,"MM");
	}
	
	public static String getDayShortName(long t)
	{
		return getDayName(t);
	}
	
	public static String getDayName(long t)
	{
		return getTimeFieldName(t,"dd")+"th";
	}
	
	public static String getDateName(long t)
	{
		return getTimeFieldName(t, "yyyy-MM-dd EEEE");
	}
	
	public static String getDayHourName(long t)
	{
		return getTimeFieldName(t, "yyyy-MM-dd HH:mm EEE");
	}
	
	public static String getHourShortName(long t)
	{
		return getHourName(t);
	}
	
	public static String getHourName(long t)
	{
		return getTimeFieldName(t, "HH:mm");
	}
	
	public static String getMinuteShortName(long t)
	{
		return getMinuteName(t);
	}
	
	public static String getMinuteName(long t)
	{
		return getHourName(t);
	}
	
	public static String getTimeFieldName(long t, String parttern)
	{
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(t);
		SimpleDateFormat sdf = new SimpleDateFormat(parttern);
		return sdf.format(c.getTime());
	}
	
	public static void main(String args[])
	{
		Calendar c1 = Calendar.getInstance();
		Calendar c2 = Calendar.getInstance();
		
		c2.add(Calendar.YEAR, 1);
		c2.add(Calendar.HOUR, 2);
		c2.add(Calendar.MONTH, -2);
		
		//int count = TimeUtil.getMarksCount(TimeUtil.YEARMARK,c1.getTimeInMillis(),c2.getTimeInMillis());
		
		System.out.println(TimeUtil.getHourName(c2.getTimeInMillis()));
		
		//System.out.println(count);
		
		//Long [] l = TimeUtil.getMiddleYears(c1.getTimeInMillis(),c2.getTimeInMillis());
		//Long [] l = TimeUtil.getMiddleMonths(c1.getTimeInMillis(),c2.getTimeInMillis());
		//Long [] l = TimeUtil.getMiddleDays(c1.getTimeInMillis(),c2.getTimeInMillis());
//		Long [] l = TimeUtil.getMiddle30s(c1.getTimeInMillis(),c2.getTimeInMillis());
//		
//		
//		for( int i=0; i<l.length; i++)
//		{
//			Calendar c = Calendar.getInstance();
//			c.setTimeInMillis(l[i]);
//			System.out.println(c.getTime());
//		}
		
	}

}
