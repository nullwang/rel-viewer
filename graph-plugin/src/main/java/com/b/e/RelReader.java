package com.b.e;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.b.g.XmlTraverse;
import com.b.s.DataService;
import com.b.u.Tag;

public class   RelReader extends DefaultHandler{

	enum KeyType { NONE, ENT, LINK, ENTTYPE, LINKTYPE }
	
	protected SAXParser saxp;
	protected DataService dataService;	

	protected TypeCatalogue currentTypeCatalogue;
	protected EntityType currentEntityType;
	protected LinkType currentLinkType;
	protected CatProperty currentCatProperty;
	protected ImageURL currentImageURL;
	protected Form currentForm;
	protected Formatter currentFormatter;
	protected Service currentService;
	protected Entity currentEntity;
	protected Property currentProperty;
	protected Formatting currentFormatting;
	protected Link currentLink;
	
	protected StringBuilder currentText = new StringBuilder(); 
	protected KeyType keyType;
	protected LinkedList<Tag> currentStates;
	
	private static String encoding="utf-8";
	
	public RelReader()throws  SAXException, ParserConfigurationException
	{
		SAXParserFactory factory = SAXParserFactory.newInstance();
		saxp = factory.newSAXParser();
		
		this.keyType = KeyType.NONE;
		this.currentStates = new LinkedList<Tag>();
	}
	
	public static void load(String fileName, DataService dataService) throws IOException
	{
		File f = new File(fileName);
		load(f.toURL(), dataService);
	}
	
	public static void load(URL url, DataService dataService )  throws IOException
	{
		InputStreamReader isr = new InputStreamReader(url.openStream());
		BufferedReader in = new BufferedReader(isr);
		String str = null;
		int i=0;
		do {
			str = in.readLine();
			i++;
		}
		while ( "".equals(str.trim()));	
		
		Pattern p =  Pattern.compile("encoding=\"(.*)\"");
		Matcher m = p.matcher(str);
		if( m.find()) {
			String encode = m.group(1);
			encoding = encode;
			
			in = new BufferedReader( new InputStreamReader(url.openStream(), encode) );
			while(i>0) {
				in.readLine();
				i--;
			}
		}		
		load(in,dataService);
	}
	
	public static void loadString(String str, DataService dataService)throws IOException
	{
		StringReader sr = new StringReader(str);
		load(sr, dataService);
	}
	
	private static void load(Reader reader, DataService dataService) throws IOException
	{
		RelReader relReader;
		try {
			relReader = new RelReader();
		} catch (SAXException e) {
			e.printStackTrace();
			throw new IOException(e.getMessage());
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			throw new IOException(e.getMessage());
		}
		
		relReader.dataService = dataService;
		
		relReader.parse(reader);
	}
	
	protected void parse(Reader reader) throws IOException
	{
		try {
			InputSource inputSource = new InputSource(reader);
			inputSource.setEncoding(encoding);
			saxp.parse(inputSource,this);
			reader.close();
		}catch(SAXException saxe){
			throw new IOException(saxe.getMessage());
		}
	}
	
	@Override
	public void startElement (String uri, String localName,
		      String qName, Attributes attributes) throws SAXException
	{
		String tag = qName.toLowerCase();
        Tag tagState = Tag.tagOf(tag);
        this.currentText = new StringBuilder();
        
        if(tagState.equals(Tag.TYPECATALOGUE)){
        	this.currentTypeCatalogue = new TypeCatalogue();
        	currentTypeCatalogue.setId(attributes.getValue(Tag.AttrNames.ID));
        }
        else if(tagState.equals(Tag.ENTTYPE)){
        	this.keyType = KeyType.ENTTYPE;
        	this.currentEntityType = new EntityType();
        	currentEntityType.setLocalName(attributes.getValue(Tag.AttrNames.LOCALNAME));
        	currentEntityType.setDisplayName(attributes.getValue(Tag.AttrNames.DISPLAYNAME));
        	currentEntityType.setRepresentation(attributes.getValue(Tag.AttrNames.REPRESENTATION));
        	currentEntityType.setLNDateTime(attributes.getValue(Tag.AttrNames.LNDATETIME));
        	
        }
        else if(tagState.equals(Tag.CATPROPERTY)){
        	this.currentCatProperty = new CatProperty();
        	currentCatProperty.setLocalName(attributes.getValue(Tag.AttrNames.LOCALNAME));
            currentCatProperty.setDisplayName(attributes.getValue(Tag.AttrNames.DISPLAYNAME));
            currentCatProperty.setFguid(attributes.getValue(Tag.AttrNames.FGUID));
            currentCatProperty.setHidden(attributes.getValue(Tag.AttrNames.ISHIDDEN));
            currentCatProperty.setLabel(attributes.getValue(Tag.AttrNames.ISLABEL));
            currentCatProperty.setToolTip(attributes.getValue(Tag.AttrNames.ISTOOLTIP));
            currentCatProperty.setFguid(attributes.getValue(Tag.AttrNames.FGUID));
            currentCatProperty.setPguid(attributes.getValue(Tag.AttrNames.PGUID));
        }
        else if( tagState.equals(Tag.IMGURL)){
        	this.currentImageURL = new ImageURL();
        	currentImageURL.setHeight(attributes.getValue(Tag.AttrNames.HEIGHT));
        	currentImageURL.setWidth(attributes.getValue(Tag.AttrNames.WIDTH));
        	currentImageURL.setURL(attributes.getValue(Tag.AttrNames.URL));
        }
        else if(tagState.equals(Tag.LINKTYPE)){
        	this.keyType = KeyType.LINKTYPE;
        	this.currentLinkType = new LinkType();
        	currentLinkType.setLocalName(attributes.getValue(Tag.AttrNames.LOCALNAME));
        	currentLinkType.setDisplayName(attributes.getValue(Tag.AttrNames.DISPLAYNAME));
        	currentLinkType.setLNDateTime(attributes.getValue(Tag.AttrNames.LNDATETIME));
        	currentLinkType.setShowArrows(attributes.getValue(Tag.AttrNames.SHOWARROWS));
        }
        else if( tagState.equals(Tag.FORM)){
        	this.currentForm = new Form();
        	currentForm.setBase(attributes.getValue(Tag.AttrNames.BASEEFORM));
        	currentForm.setFguid(attributes.getValue(Tag.AttrNames.FGUID));
        	currentForm.setName(attributes.getValue(Tag.AttrNames.FORMNAME));
        }
        else if(tagState.equals(Tag.FORMATTER)){
        	this.currentFormatter = new Formatter();
        	currentFormatter.setSyntax(attributes.getValue(Tag.AttrNames.SYNTAX));
        }
        else if(tagState.equals(Tag.SERVICE)){
        	this.currentService  = new Service();
        	currentService.setLocalName(attributes.getValue(Tag.AttrNames.LOCALNAME));
        	currentService.setSguid(attributes.getValue(Tag.AttrNames.SGUID));
        	currentService.setServiceName(attributes.getValue(Tag.AttrNames.SERVICENAME));
        	currentService.setProviderName(attributes.getValue(Tag.AttrNames.PROVIDERNAME));
        }
        
        //ents and links
        else if(tagState.equals(Tag.ENT)){
        	this.keyType = KeyType.ENT;
        	this.currentEntity = new Entity();
        	currentEntity.setCatType(attributes.getValue(Tag.AttrNames.CATTYPE));
        	currentEntity.setId(attributes.getValue(Tag.AttrNames.ID));
        	currentEntity.setXpos(attributes.getValue(Tag.AttrNames.XPOS));
        	currentEntity.setYpos(attributes.getValue(Tag.AttrNames.YPOS));
        	currentEntity.setFixed(attributes.getValue(Tag.AttrNames.ISFIXED));
        	currentEntity.setRepresentation(attributes.getValue(Tag.AttrNames.REPRESENTATION));
        	currentEntity.setHidden(attributes.getValue(Tag.AttrNames.ISHIDDEN));        	
        }
        else if(tagState.equals(Tag.FORMATTING)){
        	this.currentFormatting = new Formatting();
        }
        else if(tagState.equals(Tag.LINK)){
        	this.keyType = KeyType.LINK;
        	this.currentLink = new Link();
        	currentLink.setCatType(attributes.getValue(Tag.AttrNames.CATTYPE));
        	currentLink.setId(attributes.getValue(Tag.AttrNames.ID));
        	currentLink.setDirection(attributes.getValue(Tag.AttrNames.DIRECTION));
        	currentLink.setDotStyle(attributes.getValue(Tag.AttrNames.DOTSTYLE));
        	currentLink.setEnt1Id(attributes.getValue(Tag.AttrNames.ENT1ID));
        	currentLink.setEnt2Id(attributes.getValue(Tag.AttrNames.ENT2ID));
        	currentLink.setHidden(attributes.getValue(Tag.AttrNames.ISHIDDEN));
        	currentLink.setXpos(attributes.getValue(Tag.AttrNames.XPOS));
        	currentLink.setLineThickness(attributes.getValue(Tag.AttrNames.LINETHICKNESS));
        	currentLink.setColor(attributes.getValue(Tag.AttrNames.COLOR));        	
        }

        else {
        	if( ( keyType == KeyType.ENT || keyType == KeyType.LINK)&& currentStates.contains(Tag.PROPERTIES) )
        		this.currentProperty = new Property();
        	
        }
        
       currentStates.addFirst(tagState);
	}
	
	@Override
	public void characters (char ch[], int start, int length)
	throws SAXException
	{
		this.currentText.append( new String(ch,start,length));
	}
	
	@Override
	public void endElement(String uri, String name, String qName) throws SAXException
	{
		String tag = qName.toLowerCase();
        Tag tagState = Tag.tagOf(tag);
       
        if
        (! tagState.equals(currentStates.getFirst())){
        	throw new SAXException("unbalanced tags: opened "+ currentStates.getFirst() +
        			", closed "+tagState); 
        }
        
        if(tagState.equals(Tag.TYPECATALOGUE)){
        	this.dataService.getTypeService().addCatalogue((TypeCatalogue) currentTypeCatalogue.clone());
        }
        else if(tagState.equals(Tag.ENTTYPE))
        {
        	this.currentTypeCatalogue.addEntityType((EntityType) this.currentEntityType.clone());
		}
        else if(tagState.equals(Tag.CATPROPERTY))
        {
        	if(keyType == KeyType.ENTTYPE)
        		this.currentEntityType.addCatProperty((CatProperty) this.currentCatProperty.clone());
        	if (keyType == KeyType.LINKTYPE)
        		this.currentLinkType.addCatProperty((CatProperty) this.currentCatProperty.clone());
        	
        	this.currentCatProperty = null;
        }
        else if(tagState.equals(Tag.IMGURL))
        {
        	this.currentImageURL.setImgCode(this.currentText.toString());
        	
        	if(keyType == KeyType.ENTTYPE )
        		this.currentEntityType.setImageURL((ImageURL) this.currentImageURL.clone());
        	if(keyType == KeyType.ENT)
        		this.currentFormatting.addImg((ImageURL) this.currentImageURL.clone());
        }
        else if(tagState.equals(Tag.LINKTYPE))
        {
        	this.currentTypeCatalogue.addLinkType((LinkType) this.currentLinkType.clone());
        }
        else if(tagState.equals(Tag.FORM))
        {
        	this.currentTypeCatalogue.addForm((Form) this.currentForm.clone());
        }
        else if(tagState.equals(Tag.FORMATTER))
        {
        	this.currentFormatter.setText(this.currentText.toString());
        	this.currentForm.addFormatter((Formatter) this.currentFormatter.clone());
        }
        else if(tagState.equals(Tag.SERVICE))
        {
        	this.currentTypeCatalogue.addService((Service) this.currentService.clone());
        }
        else if(tagState.equals(Tag.ENT))
        {
        	this.dataService.getEntityService().addEntity((Entity) this.currentEntity.clone());
        }
        else if(tagState.equals(Tag.FORMATTING))
        {
        	this.currentEntity.setFormatting((Formatting) this.currentFormatting.clone());
        }
        else if(tagState.equals(Tag.LINK))
        {
        	this.dataService.getEntityService().addLink((Link) this.currentLink.clone());
        }
        else if( this.currentProperty != null ){
        //process properties
        	currentProperty.setText(this.currentText.toString());
        	currentProperty.setName(qName);
        	if(keyType == KeyType.ENT ){        		
        		this.currentEntity.addProperty((Property) currentProperty.clone());
        	}
        	if( keyType == KeyType.LINK){
        		this.currentLink.addProperty((Property) currentProperty.clone());
        	}        	
        	this.currentProperty = null;
        } 
        this.currentText = new StringBuilder();
       currentStates.removeFirst();        	
	}
	
	public static void main(String[] args)
	{
		Pattern p =  Pattern.compile("encoding=\"(.*)\"");
		Matcher m = p.matcher("<?xml  version=\"1.0\" encoding=\"utf-asdg38\"?>asdfasg");
		if( m.find())
		System.out.println ( m.group(1));
		
		
	}
	
	private static void T()
	{
		
		DataService ds = new DataService();
		try {
			URL url = new URL("http://localhost:8080/dqb-explorer/load.do?t=People&c=rybh='SH1234'");
			load(url,ds);
			XmlTraverse xmlTraverse = new XmlTraverse(ds);
			System.out.println(xmlTraverse.toXml());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	
}
