package com.b.u;

import java.util.Map;

public class ParameterUtil {
	
	static Map map = new NotNullMap();
	
	@SuppressWarnings ("unchecked")
	public static void putString(String name, String value)
	{
		map.put(name, value);
	}
	
	public static String getString(String name)
	{
		Object obj= map.get(name);
		return obj==null?null:obj.toString();
	}
	
	public static String getString(String name, String defaultValue)
	{
		Object obj= map.get(name);
		return ( obj == null)? defaultValue: obj.toString();
	}
	
	public static void putBoolean(String name, String value)
	{
		Boolean b = ( value == null? true: Boolean.valueOf(value));
		putBoolean(name,b);
	}
	
	@SuppressWarnings ("unchecked")
	public static void putBoolean(String name, Boolean b)
	{
		map.put(name, b);
	}
	
	public static void putInteger(String name, String value)
	{
		putInteger(name,value,0);
	}	
		
	public static void putInteger(String name, String value ,Integer defaultValue)
	{
		try{
			Integer i = Integer.valueOf(value);
			putInteger(name, i);
		}catch(NumberFormatException e){
			putInteger(name, defaultValue);
		}
	}
	
	@SuppressWarnings ("unchecked")
	public static void putInteger(String name, Integer i)
	{
		map.put(name, i);
	}
	
	
	public static Integer getInteger(String name)
	{
		Object obj = map.get(name);
		if( obj instanceof Integer){
			return (Integer)obj;
		}
		return null;
	}
	
	public static Integer getInteger(String name, Integer i)
	{
		Integer r = getInteger(name);
		return r == null? i	: r ;
	}
	
	public static Boolean getBoolean(String name)
	{
		Object obj = map.get(name);
		if( obj instanceof Boolean){
			return (Boolean)obj;
		}
		return null;
	}
	
	public static Boolean getBoolean(String name, Boolean b)
	{
		Boolean r = getBoolean(name);
		return r == null? b	: r ;
	}
	
	public static void main(String[] args)
	{
		System.out.println(Boolean.valueOf(null));
	}

}
