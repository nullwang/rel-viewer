package rel.explorer.e;

public class Property implements Cloneable{

	String name;
	
	String text;
	
	@Override
	public  Object clone()
	{
		Property o = null;
		
			try {
				o = (Property) super.clone();
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}
			
		return o;
		
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
	
	public boolean equals(Object o)
	{
		if( !(o instanceof Property)) return false;
		Property p = (Property) o;
		
		if( p == this || (p!=null && p.getName().equals(name))) return true;
		
		return false;
	}
	
	public int hashCode()
	{
		return name == null? 0 : name.hashCode();
	}
	
}
