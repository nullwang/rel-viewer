package rel.explorer.e;

public class Service implements Cloneable{
	
	String localName;
	
	String sguid;
	
	String serviceName;
	
	String providerName;
	
	String tagName="service";

	public String getLocalName() {
		return localName;
	}

	public void setLocalName(String localName) {
		this.localName = localName;
	}

	public String getSguid() {
		return sguid;
	}

	public void setSguid(String sguid) {
		this.sguid = sguid;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getProviderName() {
		return providerName;
	}

	public void setProviderName(String providerName) {
		this.providerName = providerName;
	}

	public String getTagName() {
		return tagName;
	}

	public void setTagName(String tagName) {
		this.tagName = tagName;
	}
	
	@Override
	public  Object clone()
	{
		Service o = null;
		
			try {
				o = (Service) super.clone();
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}
			
		return o;
		
	}
	
	public boolean equals(Object o)
	{
		if( !(o instanceof Service)) return false;
		
		Service s = (Service) o;
		
		if( s == this || ( s!=null && s.getSguid().equals(sguid))) return true;
		
		return false;
		
	}
	
	public int hashCode()
	{
		return sguid == null? 0 : sguid.hashCode();
	}
	
}
