package com.b.g;

import com.b.e.CatProperty;
import com.b.e.Entity;
import com.b.e.EntityType;
import com.b.e.Form;
import com.b.e.Formatter;
import com.b.e.Formatting;
import com.b.e.ImageURL;
import com.b.e.Link;
import com.b.e.LinkType;
import com.b.e.Property;
import com.b.e.Service;
import com.b.e.TypeCatalogue;
import com.b.s.DataService;
import com.b.u.StringUtil;
import com.b.u.Tag;

public class XmlTraverse {
	
	private DataService dataService;
	
	public XmlTraverse(DataService dataService)
	{
		this.dataService = dataService;
	}
	
	public String toXml()
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
		sb.append("\n");
		sb.append("<rel>");
		
		for( TypeCatalogue typeCatalogue: dataService.getTypeService().getCatalogues())
		{
			sb.append("<" + Tag.TYPECATALOGUE);
			xmlProp(Tag.AttrNames.ID,typeCatalogue.getId(),sb);
			sb.append(">");
			sb.append("<" + Tag.ENTTYPES + ">");
			for(EntityType entityType:typeCatalogue.getEntityTypes().values()){
				xmlEntityType(entityType,sb);
			}
			sb.append("</" + Tag.ENTTYPES + ">");
			
			sb.append("<" + Tag.LINKTYPES + ">");
			for(LinkType linkType: typeCatalogue.getLinkTypes().values()){
				xmlLinkType(linkType,sb);
			}			
			sb.append("</"+Tag.LINKTYPES + ">");
			
			sb.append("<" + Tag.FORMS + ">");
			for(Form form:typeCatalogue.getForms().values()){
				xmlForm(form,sb);
			}			
			sb.append("</" + Tag.FORMS + ">");
			
			sb.append("<"+Tag.SERVICES+">");
			for(Service service:typeCatalogue.getServices().values()){
				xmlService(service,sb);
			}
			sb.append("</"+Tag.SERVICES+">");
			sb.append("</" + Tag.TYPECATALOGUE + ">");
		}
		
		sb.append("<"+Tag.ENTS+">");
		for(Entity entity: dataService.getEntityService().getEntities()){
			xmlEntity(entity,sb);
		}
		sb.append("</"+Tag.ENTS+">");
		sb.append("<"+Tag.LINKS+">");
		for(Link link:dataService.getEntityService().getLinks()){
			xmlLink(link,sb);
		}
		sb.append("</"+Tag.LINKS+">");
		sb.append("</rel>");
		return sb.toString();
	}
	
	private void xmlCatProperty(CatProperty catProperty,StringBuilder sb)
	{
		sb.append("<" + Tag.CATPROPERTY );
		xmlProp(Tag.AttrNames.LOCALNAME , catProperty.getLocalName(),sb);
		xmlProp(Tag.AttrNames.DISPLAYNAME , catProperty.getDisplayName(),sb);
		xmlProp(Tag.AttrNames.ISHIDDEN , catProperty.isHidden(),sb);
		xmlProp(Tag.AttrNames.ISLABEL , catProperty.isLabel(),sb);
		xmlProp(Tag.AttrNames.ISTOOLTIP , catProperty.isToolTip(),sb);
		xmlProp(Tag.AttrNames.FGUID , catProperty.getFguid(),sb);
		xmlProp(Tag.AttrNames.PGUID , catProperty.getPguid(),sb);	
		sb.append(">");
		sb.append("</" + Tag.CATPROPERTY + ">");
	}
	
	private void xmlLinkType(LinkType linkType, StringBuilder sb)
	{
		sb.append("<" + Tag.LINKTYPE );
		
		xmlProp(Tag.AttrNames.LOCALNAME , linkType.getLocalName(),sb);
		xmlProp(Tag.AttrNames.DISPLAYNAME , linkType.getDisplayName(),sb);
		xmlProp(Tag.AttrNames.LNDATETIME , linkType.getLNDateTime(),sb);
		xmlProp(Tag.AttrNames.SHOWARROWS , linkType.isShowArrows(),sb);
		sb.append(">");
		
		for(CatProperty catProperty:linkType.getCatProperties().values())
		{
			xmlCatProperty(catProperty, sb);
		}
		
		sb.append("</"+Tag.LINKTYPE + ">");
	}
	
	private void xmlEntityType(EntityType entityType, StringBuilder sb)
	{
		sb.append("<" + Tag.ENTTYPE );

		xmlProp(Tag.AttrNames.LOCALNAME , entityType.getLocalName(),sb);
		xmlProp(Tag.AttrNames.DISPLAYNAME , entityType.getDisplayName(),sb);
		xmlProp(Tag.AttrNames.REPRESENTATION , entityType.getRepresentation(),sb);
		xmlProp(Tag.AttrNames.LNDATETIME , entityType.getLNDateTime(),sb);
		 sb.append(">");
		 
		for(CatProperty catProperty:entityType.getCatProperties().values())
		{
			xmlCatProperty(catProperty, sb);
		}
		
		xmlImageURL(entityType.getImageURL(), sb);
		
		sb.append("</"+Tag.ENTTYPE + ">");
	}
	
	private void xmlImageURL(ImageURL imageURL, StringBuilder sb)
	{
		if( imageURL == null) return;
		
		 sb.append("<" + Tag.IMGURL);
		 xmlProp(Tag.AttrNames.HEIGHT , imageURL.getHeight(),sb);
		 xmlProp(Tag.AttrNames.WIDTH , imageURL.getWidth(),sb);
		 xmlProp(Tag.AttrNames.URL , imageURL.getURL(),sb);
		 sb.append(">");
		 
		 sb.append(imageURL.getImgCode());
		 
		 sb.append("</" + Tag.IMGURL + ">");
		 
	}
	
	private void xmlForm(Form form, StringBuilder sb)
	{
		 sb.append("<" + Tag.FORM);
		 xmlProp(Tag.AttrNames.FGUID , form.getFguid(),sb);
		 xmlProp(Tag.AttrNames.FORMNAME , form.getName(),sb);
		 xmlProp(Tag.AttrNames.BASEEFORM , form.getBase(),sb);
		 
		 sb.append(">");
		 
		 sb.append("<" + Tag.FROMATTERS + ">");
		 for(Formatter formatter : form.getFormatters())
		 {
			 xmlFormatter(formatter,sb);
		 }
		 
		 sb.append("</" + Tag.FROMATTERS + ">");		 
		 sb.append("</" + Tag.FORM + ">");
	}
	
	private void xmlFormatter(Formatter formatter, StringBuilder sb)
	{
		sb.append("<" + Tag.FORMATTER);
		 xmlProp(Tag.AttrNames.SYNTAX, formatter.getSyntax(),sb);
		 sb.append(">");
		 
		 sb.append(formatter.getText());	 
		 sb.append("</" + Tag.FORMATTER + ">"); 
	}
	
	private void xmlService(Service service, StringBuilder sb)
	{
		 sb.append("<" + Tag.SERVICE);
		 xmlProp(Tag.AttrNames.LOCALNAME, service.getLocalName(),sb);
		 xmlProp(Tag.AttrNames.SGUID, service.getSguid(),sb);
		 xmlProp(Tag.AttrNames.SERVICENAME, service.getServiceName(),sb);
		 xmlProp(Tag.AttrNames.PROVIDERNAME, service.getProviderName(),sb);
		 
		 sb.append(">");
		 sb.append("</" + Tag.SERVICE + ">");
	}
	
	private void xmlEntity(Entity entity,StringBuilder sb)
	{
		sb.append("<" + Tag.ENT );
		xmlProp(Tag.AttrNames.CATTYPE, entity.getCatType(),sb);
		xmlProp(Tag.AttrNames.ID, entity.getId(),sb);
		xmlProp(Tag.AttrNames.XPOS, entity.getXpos(),sb);
		xmlProp(Tag.AttrNames.YPOS, entity.getYpos(),sb);
		xmlProp(Tag.AttrNames.ISFIXED, entity.isFixed(),sb);
		xmlProp(Tag.AttrNames.REPRESENTATION, entity.getRepresentation(),sb);
		xmlProp(Tag.AttrNames.ISHIDDEN, entity.isHidden(),sb);
	
		sb.append(">");
		
		for(Property property:entity.getProperties().values())
		{
			xmlProperty(property, sb);
		}		
		
		xmlFormatting(entity.getFormatting(), sb);
		
		sb.append("</"+Tag.ENT + ">");
	}
	
	private void xmlFormatting(Formatting formatting, StringBuilder sb) {
		if( formatting == null) return;
		
		sb.append("<" + Tag.FORMATTING + ">");
		for(ImageURL imageURL: formatting.getImgs())
		{
			xmlImageURL(imageURL, sb);
		}
		sb.append("</" + Tag.FORMATTING + ">");		
	}

	private void xmlProperty(Property property, StringBuilder sb)
	{
		sb.append("<properties>");
		sb.append("<" + property.getName() + ">");
		sb.append(property.getText());
		sb.append("</" + property.getName() + ">");
		sb.append("</properties>");
	}
	
	private void xmlLink(Link link , StringBuilder sb)
	{
		sb.append("<" + Tag.LINK );
		xmlProp(Tag.AttrNames.CATTYPE, link.getCatType(),sb);
		xmlProp(Tag.AttrNames.ID, link.getId(),sb);
		xmlProp(Tag.AttrNames.DIRECTION, link.getDirection(),sb	);
		xmlProp(Tag.AttrNames.DOTSTYLE, link.getDotStyle(),sb	);
		xmlProp(Tag.AttrNames.ENT1ID, link.getEnt1Id(),sb	);
		xmlProp(Tag.AttrNames.ENT2ID, link.getEnt2Id(),sb	);
		xmlProp(Tag.AttrNames.LINETHICKNESS, link.getLineThickness(),sb	);
		xmlProp(Tag.AttrNames.ISHIDDEN, link.isHidden(),sb	);
		xmlProp(Tag.AttrNames.XPOS, link.getXpos(),sb	);
		sb.append(">");
		 
		for(Property property:link.getProperties().values())
		{
			xmlProperty(property, sb);
		}		
				
		sb.append("</"+Tag.LINK + ">");
	}
	
	private void xmlProp(String propName, String value, StringBuilder sb)
	{
		if( value != null)
		{
			sb.append(" " + propName + "=" + StringUtil.quoteString(value));
		}		
	}
	
	private void xmlProp(String propName, boolean b , StringBuilder sb)
	{
		xmlProp(propName, String.valueOf(b), sb);
	}
	
	private void xmlProp(String propName, int b , StringBuilder sb)
	{
		xmlProp(propName, String.valueOf(b), sb);
	}

}
