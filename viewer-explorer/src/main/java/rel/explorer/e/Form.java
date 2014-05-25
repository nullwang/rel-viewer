package rel.explorer.e;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Form implements Cloneable{

	String fguid;

	public String getFguid() {
		return fguid;
	}

	public void setFguid(String fguid) {
		this.fguid = fguid;
	}

	String base;

	String name;

	public String getBase() {
		return base;
	}

	public void setBase(String base) {
		this.base = base;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	List<Formatter> formatters = new ArrayList<Formatter>();
	
	public void addFormatter(Formatter formatter)
	{
		this.formatters.add(formatter);
	}

	public List<Formatter> getFormatters() {
		return formatters;
	}

	public void setFormatters(List<Formatter> formatters) {
		this.formatters = formatters;
	}

	public Date formate(String strOfDate) {
		Date d = null;
		for( Formatter formatter : formatters){
			d = formatter.formatDate(strOfDate);
			if( d != null) return d;
		}
		return d;
	}
	
	@Override
	public Object clone()
	{
		Form o = null;
		
		try {
			o = (Form) super.clone();
			o.formatters = new ArrayList<Formatter>();
			for(int i=0; i<formatters.size(); i++){
				o.formatters.add((Formatter) formatters.get(i).clone());
			}
			
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		
		return o;
		
	}
	
	public boolean equals(Object o)
	{
		if( !(o instanceof Form)) return false;
		
		Form f = (Form ) o;
		
		if(f==this ||(f!=null && f.getFguid().equals(fguid))) return true;
		
		return false;
		
	}
	
	public int hashCode()
	{
		return fguid == null? 0 : fguid.hashCode();
	}
	
	public Date formatDate (String strOfDate)
	{
		Date d = null;
		for( Formatter formatter : formatters)
		{
			d = formatter.formatDate(strOfDate) ;
			if( d!=null) return d;
		}
		return null;
	}	
}
