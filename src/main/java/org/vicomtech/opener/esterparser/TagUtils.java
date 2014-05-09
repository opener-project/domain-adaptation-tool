package org.vicomtech.opener.esterparser;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * XML tag utilities for ESTER corpus
 * 
 * org.vicomtech.opener.esterparser is a module of Domain Adaptation Tool for OpeNER
 * @author Andoni Azpeitia (aazpeitia@vicomtech.org) - Vicomtech-IK4 (http://www.vicomtech.es/)
 *
 */
public class TagUtils {

	/**
	 * Entity category
	 */
	private EntityCategories categories;
	
	/**
	 * ESTER corpus XML tags, attribute names and its values
	 */
	protected static final String SEED_TAG = "Event";
	protected static final String OPEN_VAL = "begin";
	protected static final String CLOSE_VAL = "end";
	protected static final String CATEGORY_ATTR = "desc";
	
	/**
	 * Public constructor
	 * @param categories : entity categories
	 */
	public TagUtils(EntityCategories categories) {
		this.categories = categories;
	}
	
	/**
	 * Get the root element of a ESTER file
	 * @param file
	 * @return
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	protected Element getRootElement(File file) throws ParserConfigurationException, SAXException, IOException {
		
		// xml parsing vars
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setIgnoringElementContentWhitespace(true);
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document document = db.parse(file);
		
		return document.getDocumentElement();
	}
	
	/**
	 * Get all the entities of a category down a XML node using XPATH
	 * @param category : the entities category
	 * @param type : XML begin or end tags
	 * @param element : root element
	 * @return
	 * @throws XPathExpressionException
	 */
	protected NodeList getEntityNodes(String category, String type,
			Element element) throws XPathExpressionException {
		
		// get entities query string
		String entityQuery = this.getEntityQuery(category, type);
		
		// xpath var
		XPath xPath = XPathFactory.newInstance().newXPath();
		
		// query
		NodeList entityNodes = (NodeList) xPath.evaluate(
				entityQuery, element, XPathConstants.NODESET);
		
		return entityNodes;
		
	}
	
	/**
	 * Return a XPATH query to find entities of a category
	 * @param category : the entities category
	 * @param type : XML begin or end tags
	 * @return
	 */
	protected String getEntityQuery(String category, String type) {
		
		// get tags for the category
		List<String> tags = this.categories.getTags(category);
		
		// xpath query
		String entityQuery = "//"+SEED_TAG+"[@type='entities' " +
				"and @extent='"+type+"' and ( ";

		// add attribute query for each tag
		for (int i=0; i<tags.size(); i++) {
			String tag = tags.get(i);
			entityQuery += "@desc='"+tag+"' ";
			if (i < tags.size()-1) {
				entityQuery += "or ";
			}
			else
				entityQuery += ") ]";
		}
		
		return entityQuery;
	}
	
	/**
	 * True if the node is a XML close tag of the specified subcategory:
	 * e.g.: pers.hum
	 * @param node : the input node
	 * @param subcategory : the subcategory
	 * @return
	 */
	protected boolean isEntityCloseNode(Node node, String subcategory) {
		
		if (node.getNodeType() == Node.ELEMENT_NODE) {
			return this.hasAttributeValue(
					(Element) node, CATEGORY_ATTR, subcategory);
		}
		else {
			return false;
		}
	}
	
	/**
	 * Get the text value of the node
	 * @param node : input node
	 * @return
	 */
	protected String getText(Node node) {
		
		String text = node.getNodeValue();
		
		// delete first and last '\n' chars
		text = text.substring(1, text.length()-1);
		
		// if ends with '.' char, insert a space char at last
		if (text.endsWith("."))
			text = text+" ";
		
		return text;
	}
	
	/**
	 * True if the node has an specified attribute and value
	 * @param node : input node
	 * @param attribute : the attribute
	 * @param value : the value
	 * @return
	 */
	public boolean hasAttributeValue(Element node,
			String attribute,String value) {
		
		return (node.hasAttribute(attribute)
				&& node.getAttribute(attribute)
				.equalsIgnoreCase(value));
	}

}
