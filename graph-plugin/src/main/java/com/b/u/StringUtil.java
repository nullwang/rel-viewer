package com.b.u;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;

public class StringUtil {	
	
	/**
	 * @param str
	 * @return
	 */
	public static String quoteString(String str)
	{
		return new String("\"" + (str == null ? "" : str) + "\"");
	}
	
	public static String quoteString(boolean b)
	{
		return quoteString(String.valueOf(b));
	}

	public static String quoteString(int i) {
		return new String ("\"" + String.valueOf(i)+"\"");
	}
	
	/**
	 * @param s
	 * @param context
	 * @return
	 */
	public static URL newURL(String s, URL context)
	{
		URL url = null;

		try {
			url = new URL(s);
		} catch (MalformedURLException e) {
			try {
				url = new URL(context, s);
			} catch (MalformedURLException e1) {
				//e1.printStackTrace();
			}
		}
		
		return url;
	}
	
	public static String commaSeparate(Object[] objs)
	{
		return charSeparate(objs,",");
	}
	
	public static String commaSeparate(Object obj, Object obj2)
	{
		return charSeparate(obj,obj2,",");
	}
	
	public static String charSeparate(Object obj, Object obj2, String split)
	{
		return obj.toString() + split + obj2.toString();
	}
	
	@SuppressWarnings ("unchecked")
	public static String commaSeparate(Collection objs)
	{
		return charSeparate(objs, ",");
	}	
	
	@SuppressWarnings ("unchecked")
	public static String charSeparate(Collection objs, String c)
	{
		return charSeparate(objs.toArray(), c);
	}
	
	public static String charSeparate(Object[] objs, String c)
	{
		StringBuilder sb = new StringBuilder();
		for(int i=0; i<objs.length; i++){
			if( i > 0) sb.append(c);
			if( objs[i] != null)
			sb.append(objs[i].toString());
		}
		return sb.toString();
	}
	
	public static void main(String [] args){
		
		//System.err.println(StringUtil.newURL(null, null));
		//System.out.println("ok");
		
		System.out.println(quoteString("a"));
	}
	
	

}
