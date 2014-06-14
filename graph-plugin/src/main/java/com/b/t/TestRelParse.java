package com.b.t;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.b.e.RelReader;
import com.b.g.XmlTraverse;
import com.b.s.DataService;

public class TestRelParse {
	
	
	public static void main(String[] args)
	{
		DataService dataService = new DataService();
		try {
			RelReader relReader = new RelReader();
			
			relReader.load("G:\\tech_docs\\bi\\dev\\vid.xml", dataService);
			
			XmlTraverse dt = new XmlTraverse(dataService);
			
			System.out.print(dt.toXml());
			
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		
	}

}
