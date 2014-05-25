package rel.explorer.e;

public class CatProperty implements Cloneable{

	private String localName;
	
	private String displayName;
	
	private boolean isHidden = false;
	
	private boolean isLabel = false;
	
	private boolean isToolTip = false;
	
	private String fguid;

	private String pguid;	
	
	public Object clone() {  
		CatProperty o = null;
        try {  
            o = (CatProperty) super.clone();  
        } catch (CloneNotSupportedException e) {  
            e.printStackTrace();  
        }  
        return o;  
    }  
	
	public String getFguid() {
		return fguid;
	}

	public void setFguid(String fguid) {
		this.fguid = fguid;
	}

	public String getPguid() {
		return pguid;
	}

	public void setPguid(String pguid) {
		this.pguid = pguid;
	}

	public String getLocalName() {
		return localName;
	}

	public void setLocalName(String localName) {
		this.localName = localName;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public boolean isHidden() {
		return isHidden;
	}

	public void setHidden(boolean isHidden) {
		this.isHidden = isHidden;
	}
	
	public void setHidden(String isHidden){		
		Boolean.parseBoolean(isHidden);		
	}

	public boolean isLabel() {
		return isLabel;
	}

	public void setLabel(boolean isLabel) {
		this.isLabel = isLabel;
	}
	
	public void setLabel(String isLabel)
	{
		this.setLabel(Boolean.parseBoolean(isLabel));
	}

	public boolean isToolTip() {
		return isToolTip;
	}

	public void setToolTip(boolean isToolTip) {
		this.isToolTip = isToolTip;
	}
	
	public void setToolTip(String isToolTip)
	{
		this.setToolTip(Boolean.parseBoolean(isToolTip));
	}
	
	public boolean equals(Object o)
	{
		if( !(o instanceof CatProperty)) return false;
		CatProperty cp = (CatProperty) o;
		
		if( cp == this || (cp!=null && cp.getLocalName().equals(localName))) return true;
		
		return false;
	}
	
	public int hashCode()
	{
		return localName == null? 0 : localName.hashCode();
	}
	
	@Override
	public String toString()
	{
		return localName;
	}
}
