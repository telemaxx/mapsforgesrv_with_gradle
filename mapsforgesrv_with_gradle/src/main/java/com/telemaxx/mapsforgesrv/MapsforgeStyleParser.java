package com.telemaxx.mapsforgesrv;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

/**
 * reading Mapsforge theme. after making instance call readXML
 * @author telemaxx
 * @see http://www.vogella.com/tutorials/JavaXML/article.html
 * 
 */
public class MapsforgeStyleParser {
	static final String ID = "id";
	static final String XML_LAYER = "layer";
	static final String STYLE_MENU = "stylemenu";
	static final String VISIBLE = "visible";
	static final String NAME = "name";
	static final String LANG = "lang";
	static final String DEFAULTLANG = "defaultlang";
	static final String DEFAULTSTYLE = "defaultvalue";
	static final String VALUE = "value";
	static  Boolean Style = false;
	String na_language = "";
	String na_value = "";
	String defaultlanguage = "";
	String defaultstyle = "";

	/**
	 * just for test purposes
	 * @param args
	 */
	public static void main(final String args[]) {
		final MapsforgeStyleParser mapStyleParser = new MapsforgeStyleParser();
		//List<Style> styles = mapStyleParser.readXML("C:\\Users\\top\\BTSync\\oruxmaps\\mapstyles\\TMS\\Tiramisu_3_0_beta1.xml");
		final List<Style> styles = mapStyleParser.readXML("C:\\Users\\top\\BTSync\\oruxmaps\\mapstyles\\ELV4\\Elevate.xml");
		System.out.println("Stylecount: " + styles.size());
		System.out.println("Defaultlanguage: " + mapStyleParser.getDefaultLanguage());
		System.out.println("Defaultstyle:    " + mapStyleParser.getDefaultStyle());
		//System.out.println("Defaultstylename de:" + styles.);
		for (final Style style : styles) {
			System.out.println(style);
			System.out.println("local Name: " + style.getName(""));
		}
	}
	
	public String getDefaultLanguage() {
		return defaultlanguage;
	}
	
	public String getDefaultStyle() {
		return defaultstyle;
	}

	/**
	 * reading mapsforgetheme and return a list mit selectable layers
	 * @param xmlFile
	 * @return a list with availible, visible layers
	 */
	@SuppressWarnings({ "unchecked", "null" })
	public List<Style> readXML(final String xmlFile) {

		final List<Style> items = new ArrayList<>();
		try {
			// First, create a new XMLInputFactory
			final XMLInputFactory inputFactory = XMLInputFactory.newInstance();
			// Setup a new eventReader
			final InputStream in = new FileInputStream(xmlFile);
			final XMLEventReader eventReader = inputFactory.createXMLEventReader(in);
			// read the XML document
			Style item = null;

			while (eventReader.hasNext()) {
				XMLEvent event = eventReader.nextEvent();
				if (event.isStartElement()) {
					final StartElement startElement = event.asStartElement();
					// if stylemenue, getting the defaults
					if (startElement.getName().getLocalPart().equals(STYLE_MENU)) {
						final Iterator<Attribute> sm_attributes = startElement.getAttributes();
						while (sm_attributes.hasNext()) { //in the same line as <layer>
							final Attribute sm_attribute = sm_attributes.next();
							if (sm_attribute.getName().toString().equals(DEFAULTLANG)) {
								defaultlanguage = sm_attribute.getValue();
							}
							if (sm_attribute.getName().toString().equals(DEFAULTSTYLE)) {
								defaultstyle =  sm_attribute.getValue();
								//System.out.println("default style: " + defaultstyle);
							}
						}
					}					
					// If we have an item(layer) element, we create a new item
					if (startElement.getName().getLocalPart().equals(XML_LAYER)) {
						Style = false;
						item = new Style();
						final Iterator<Attribute> attributes = startElement.getAttributes();
						while (attributes.hasNext()) { //in the same line as <layer>
							final Attribute attribute = attributes.next();
							if (attribute.getName().toString().equals(ID)) {
								item.setXmlLayer(attribute.getValue());
							}
							if (attribute.getName().toString().equals(VISIBLE)) {
								if(attribute.getValue().equals("true")){
									Style = true;
								}
							}
						}
					}
               if (event.isStartElement()) {
                  if (event.asStartElement().getName().getLocalPart().equals(NAME)) {
                  	final Iterator<Attribute> name_attributes = startElement.getAttributes();
   						while (name_attributes.hasNext()) { //in the same line as <layer>
   							final Attribute name_attribute = name_attributes.next();
   							if (name_attribute.getName().toString().equals(LANG)){
   								na_language = name_attribute.getValue();
   							}
   							if (name_attribute.getName().toString().equals(VALUE)){
   								na_value = name_attribute.getValue();
   							} 							
   						}  
   						if (Style) {
   							item.setName(na_language, na_value);
   						}
                      event = eventReader.nextEvent();
                      continue;
                  }
              }
				}
				// If we reach the end of an item element, we add it to the list
				if (event.isEndElement()) {
					final EndElement endElement = event.asEndElement();
					if (endElement.getName().getLocalPart().equals(XML_LAYER) && Style) {
						item.setDefaultLanguage(defaultlanguage);
						items.add(item);
					}
				}
			}
		} catch (final FileNotFoundException e) {
			e.printStackTrace();
		} catch (final XMLStreamException e) {
			e.printStackTrace();
		}
		return items;
	} //end ReadConfig
}

/**
 * Bean Style containes a visible Style
 * @author telemaxx
 *
 */

class Style {
	private Map<String, String> name = new HashMap<>();
   private String xmlLayer;
   private String defaultlanguage = "de";

   public String getDefaultLaguage() {
      return defaultlanguage;
  }
	/**
    * getting the name as map with all localizations
    * @return Map<String language,String name>
    */  
   public Map<String, String> getName() {
   	return name;
   }   
	
   /**
    * getting a local name of the mapstyle
    * @param language string like "en"
    * @return a String with the local name like "hiking"
    */
   public String getName(final String language) {
   	if(language.equals("default")){
   		return name.get(defaultlanguage);
   	} else
   	if(name.containsKey(language)){
   		return name.get(language);
   	} else {
   		return name.get(defaultlanguage);
   	}
   }
	/**
    * get the style name like
    * @return String containing the stylename like "elv-mtb"
    */ 
   public String getXmlLayer() {
      return xmlLayer;
  } 
	
	public void setDefaultLanguage(final String language) {
      this.defaultlanguage = language;
  }
   
   /**
	 * set the style name with a given language
	 * @param language
	 * @param name
	 */	
   public void setName(final String language, final String name) {
   	//System.out.println("setname: " + language + " name: " + name);
		this.name.put(language, name);
	}
   
   public void setXmlLayer(final String xmlLayer) {
      this.xmlLayer = xmlLayer;
  }  
   
   @Override
   public String toString() {
       return "Item [xmlLayer=" + xmlLayer + " Name= " + name.get(defaultlanguage) + "]";
   }
}