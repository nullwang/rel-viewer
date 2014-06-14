package com.b.t;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import g.algorithms.layout.Layout;
import g.graph.Graph;

public class TestLayout {	
	
	public static void printCoord(Layout layout)
	{
		Graph g = layout.getGraph();
		 for(Object v1 : g.getVertices()) {
			 System.out.println(v1 + ": " + layout.transform(v1));
		 }		
	}
	
	public static void main(String[] args) throws ParseException
	{
		//SimpleDateFormat dfm = new SimpleDateFormat("yyyy-MM-dd");
		
		Calendar c = Calendar.getInstance();

		// This is how to initialize a well-known Date (often used in unit test fixtures)
		DateFormat dfm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		dfm.setTimeZone(TimeZone.getTimeZone("Europe/Zurich"));
		Date a = dfm.parse("2007-02-26 20:15:00");
		Date b = dfm.parse("2007-02-27 08:00:00");

		// Only idiots do it like that
		Date badhabit = new Date(106, 1, 26, 20, 15, 00);

		// output is likewise simple
		System.out.println("Result: "+ dfm.format(a));

		// if you don't initialize the DateFormat with a TimeZone, include it in the format!
		 dfm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
		 //a = dfm.parse("2007-02-26 20:15:00 +0200");
		 System.out.println("Result: "+ dfm.format(a));
		 a = dfm.parse("2007-02-26 20:15:00 +0200");

		 System.out.println("Result: "+ dfm.format(a));
	}

}
