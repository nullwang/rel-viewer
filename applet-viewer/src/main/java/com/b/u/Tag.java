package com.b.u;

public class Tag {
	
	public static final Tag TYPECATALOGUE= Tag.tagOf("typecatalogue");
	public static final Tag ENTTYPES= Tag.tagOf("enttypes");
	public static final Tag ENTTYPE= Tag.tagOf("enttype");
	public static final Tag CATPROPERTY= Tag.tagOf("catproperty");
	public static final Tag IMGURL= Tag.tagOf("imgurl");
	public static final Tag LINKTYPES = Tag.tagOf("linktypes");
	public static final Tag LINKTYPE = Tag.tagOf("linktype");
	public static final Tag FORMS = Tag.tagOf("forms");
	public static final Tag FORM = Tag.tagOf("form");
	public static final Tag FROMATTERS = Tag.tagOf("formatters");
	public static final Tag FORMATTER = Tag.tagOf("formatter");
	public static final Tag SERVICES = Tag.tagOf("services");
	public static final Tag SERVICE = Tag.tagOf("service");
	public static final Tag CONTENT = Tag.tagOf("content");
	public static final Tag ENTS = Tag.tagOf("ents");	
	public static final Tag ENT = Tag.tagOf("ent");	
	public static final Tag PROPERTIES = Tag.tagOf("properties");
	public static final Tag FORMATTING = Tag.tagOf("formatting");
	public static final Tag LINKS = Tag.tagOf("links");
	public static final Tag LINK = Tag.tagOf("link");
	
	
	public interface AttrNames{
		public static final String ID ="id";
		public static final String LOCALNAME="localName";
		public static final String REPRESENTATION =	"representation";
		public static final String LNDATETIME = "LNDateTime";
		public static final String DISPLAYNAME="displayName";
		public static final String ISHIDDEN="isHidden";
		public static final String ISLABEL="isLabel";
		public static final String ISTOOLTIP="isToolTip";
		public static final String FGUID="fGUID";
		public static final String PGUID="pGUID";
		public static final String HEIGHT="height";
		public static final String WIDTH="width";
		public static final String URL="URL";
		public static final String SHOWARROWS="showArrows";
		public static final String FORMNAME="formName";
		public static final String BASEEFORM="baseForm";
		public static final String SYNTAX="syntax";
		public static final String SGUID="sGUID";
		public static final String SERVICENAME="serviceName";
		public static final String PROVIDERNAME="providerName";
		public static final String CATTYPE="catType";
		public static final String XPOS="xpos";
		public static final String YPOS="ypos";
		public static final String ISFIXED="isFixed";
		public static final String DIRECTION="direction";
		public static final String DOTSTYLE="dotStyle";
		public static final String ENT1ID="ent1id";
		public static final String ENT2ID="ent2id";
		public static final String LINETHICKNESS="lineThickness";
		public static final String COLOR = "color";
	}
	
	private Object obj;
	private Tag(Object obj)
	{
		this.obj = obj;
	}	
	
	@Override
	public boolean equals(Object o){
		if( !(o instanceof Tag)) return false;
		Tag t = (Tag) o;
		if( t== this || t !=null && t.obj.equals(obj)) return true;
		
		return false;
	}
	
	@Override
	public int hashCode()
	{
		return  obj==null ? 0 :obj.hashCode();
	}
	
	@Override
	public String toString()
	{
		return obj.toString();
	}
	
	public static Tag tagOf(Object tag)
	{
		return new Tag(tag);
	}
}
