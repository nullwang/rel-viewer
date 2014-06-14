package com.b.u;

public class NotNullStringBuilder {
	
	StringBuilder sb = new StringBuilder();	
	
	public void appendFirst(String s)
	{
		StringBuilder isb = new StringBuilder();
		isb.append(s);
		isb.append(this.sb.toString());
		
		this.sb = isb;
	}

	public void append(String s)
	{
		if( s != null)
			sb.append(s);
	}
	
	public String toString()
	{
		return sb.toString();
	}
	
	public int length()
	{
		return sb.length();
	}
	
	public static void main(String[] args)
	{
		NotNullStringBuilder sb = new NotNullStringBuilder();
		sb.append(null);
		
		System.out.println(sb.length());
		sb.append("");

		System.out.println(sb.length());
		
		sb.append("b");

		
		System.out.println(sb.toString());
		sb.append("c");

		
		System.out.println(sb.toString());

		
		sb.appendFirst("a");

		
		System.out.println(sb.toString());
		
	}

}
