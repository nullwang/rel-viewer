package com.b.t;

import java.util.StringTokenizer;

public class TestEnum {

	
	public enum n{
		ok, pk
	}
	
	public static void main(String[] args)
	{		
		String s = "<html>do 2</html>" ;
		
		StringTokenizer st = new StringTokenizer(s);
		while(st.hasMoreElements())
		{
			System.out.println(st.nextToken());
		}
		
		
//		if( Boolean.valueOf(null)) {
//			System.out.println(String.valueOf(TestEnum.n.ok));
//
//		}
//		
//		System.out.println(6%3);
	}
}
