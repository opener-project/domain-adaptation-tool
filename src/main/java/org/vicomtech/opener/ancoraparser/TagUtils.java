package org.vicomtech.opener.ancoraparser;

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

import org.vicomtech.opener.nlp.TagsetMappings;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * XML tag utilities for AnCora corpus
 * 
 * org.vicomtech.opener.ancoraparser is a module of Domain Adaptation Tool for OpeNER
 * @author Andoni Azpeitia (aazpeitia@vicomtech.org) - Vicomtech-IK4 (http://www.vicomtech.es/)
 *
 */
public class TagUtils {

	/**
	 * Entity category
	 */
	private EntityCategories categories;
	
	/**
	 * Tag attributes and names
	 */
	private static final String WORD_ATTRIBUTE = "wd";
	private static final String LEMMA_ATTRIBUTE = "lem";
	private static final String POS_ATTRIBUTE = "pos";
	private static final String NE_ATTRIBUTE = "ne";
	private static final String SENTENCE_NAME = "sentence";
	private static final String NOUN_PHRASE_NAME = "sn";
	
	/**
	 * Attribute values that could be extract from AnCora XML nodes
	 */
	protected enum Attribute {
		WORD, LEMMA, POS
	}
	
	/**
	 * Public constructor
	 * @param categories : entity categories
	 */
	public TagUtils(EntityCategories categories) {
		this.categories = categories;
	}
	
	/**
	 * Get all the entities of a category down a XML node using XPATH
	 * @param category : the entities category
	 * @param element : root element
	 * @return
	 * @throws XPathExpressionException
	 */
	protected NodeList getEntityNodes(String category, Element element) throws XPathExpressionException {
		
		// get entities query string
		String entityQuery = this.getEntityQuery(category);
		
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
	 * @return
	 */
	protected String getEntityQuery(String category) {
		
		// get tags for the category
		List<String> tags = this.categories.getTags(category);
		
		// xpath query
		String entityQuery = "//*[ (" ;

		// add attribute query for each tag
		for (int i=0; i<tags.size(); i++) {
			String tag = tags.get(i);
			entityQuery += "@"+NE_ATTRIBUTE+"='"+tag+"' ";
			if (i < tags.size()-1) {
				entityQuery += "or ";
			}
			else
				entityQuery += ") ]";
		}
		
		return entityQuery;
	}
	
	/**
	 * Get the root element of a AnCora file
	 * @param file
	 * @return
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	protected static Element getRootElement(File file) throws ParserConfigurationException, SAXException, IOException {
		
		// xml parsing vars
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setIgnoringElementContentWhitespace(true);
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document document = db.parse(file);
		
		return document.getDocumentElement();
	}
	
	/**
	 * Get attribute values within the node and its children
	 * @param node : input node
	 * @param attribute : attribute value that will be extracted
	 * @return
	 */
	protected static String getAttributes(Node node, Attribute attribute) {
		
		if (node.getNodeType() == Node.ELEMENT_NODE) {
			Element e = (Element) node;
			if (e.hasAttribute(WORD_ATTRIBUTE)) {
				return getAttribute(e, attribute);
			}
			else {
				String text = new String();
				for (int i=0; i<node.getChildNodes().getLength(); i++) {
					Node n = node.getChildNodes().item(i);
					String t = getAttributes(n, attribute);
					if (t.length() > 0)
						t += " ";
					text += t;
				}
				return text.trim();
			}
		}
		else {
			return new String();
		}
	}
	
	/**
	 * Get attribute values within the node and its children except for the 'exceptNode'
	 * @param node : input node
	 * @param exceptNode : the attributes of this node wont be extracted
	 * @param attribute : attribute value that will be extracted
	 * @return
	 */
	protected static String getAttributesExcept(Node node, Node exceptNode, Attribute attribute) {
		
		if (node.getNodeType() == Node.ELEMENT_NODE || node.equals(exceptNode)) {
			Element e = (Element) node;
			if (e.hasAttribute(WORD_ATTRIBUTE)) {
				return getAttribute(e, attribute);
			}
			else {
				String text = new String();
				for (int i=0; i<node.getChildNodes().getLength(); i++) {
					Node n = node.getChildNodes().item(i);
					String t = getAttributesExcept(n, exceptNode, attribute);
					if (t.length() > 0)
						t += " ";
					text += t;
				}
				return text.trim();
			}
		}
		else {
			return new String();
		}
	}
	
	/**
	 * Get attribute values of the nodes until
	 * we reach the 'untilNode' node.
	 * Attribute can be text, lemma or PoS
	 * @param node : input node
	 * @param untilNode : input until node
	 * @param attribute : attribute value that will be extracted
	 * @return
	 */
	protected static String getAttributesUntil(Node node, Node untilNode, Attribute attribute) {
		
		// if node not equals until node, get the text
		if (node.getNodeType() == Node.ELEMENT_NODE
				&& !node.equals(untilNode)) {
			Element e = (Element) node;
			// has no children, get the text
			if (e.hasAttribute(WORD_ATTRIBUTE)) {
				return getAttribute(e, attribute);
			}
			else {
				// process children recursively until 'until node' found
				String text = new String();
				for (int i=0; i<node.getChildNodes().getLength(); i++) {
					Node child = node.getChildNodes().item(i);
					// if equals until node break from loop
					if (child.equals(untilNode)) break;
					else {
						// get the text
						String t = getAttributesUntil(child, untilNode, attribute);
						if (t.length() > 0)
							t += " ";
						text += t;
						// if current child has until node, break from loop
						if (hasNode(child, untilNode))
							break;
					}
				}
				return text.trim();
			}
		}
		else {
			return new String();
		}
	}
	
	/**
	 * Get chunk features of the nodes until
	 * we reach the 'untilNode' node.
	 * Attribute can be text, lemma or PoS
	 * @param node : input node
	 * @param untilNode : input until node
	 * @param attribute : attribute value that will be extracted
	 * @return
	 */
/*	protected static String[] getChunkFeaturesUntil(Node node, Node untilNode, Attribute attribute) {
		hfd
		// if node not equals until node, get the text
		if (node.getNodeType() == Node.ELEMENT_NODE
				&& !node.equals(untilNode) && ) {
			Element e = (Element) node;
			// has no children, get the text
			if (e.hasAttribute(WORD_ATTRIBUTE)) {
				return getAttribute(e, attribute);
			}
			else {
				// process children recursively until 'until node' found
				String text = new String();
				for (int i=0; i<node.getChildNodes().getLength(); i++) {
					Node child = node.getChildNodes().item(i);
					// if equals until node break from loop
					if (child.equals(untilNode)) break;
					else {
						// get the text
						String t = getAttributesUntil(child, untilNode, attribute);
						if (t.length() > 0)
							t += " ";
						text += t;
						// if current child has until node, break from loop
						if (hasNode(child, untilNode))
							break;
					}
				}
				return text.trim();
			}
		}
		else {
			return new String();
		}
	}
	*/
	/**
	 * Get attribute value of a XML element
	 * @param element : input XML element
	 * @param attribute : attribute value that will be extracted
	 * @return
	 */
	private static String getAttribute(Element element, Attribute attribute) {
		
		if (attribute.equals(Attribute.WORD)) {
			return element.getAttribute(WORD_ATTRIBUTE);
		}
		else if (attribute.equals(Attribute.LEMMA)) {
			return element.getAttribute(LEMMA_ATTRIBUTE);
		}
		else if (attribute.equals(Attribute.POS)) {
			return TagsetMappings.convertFromAnCoraToKaf(element.getAttribute(POS_ATTRIBUTE));
		}
		else return null;
	}
	
	/**
	 * True if 'n' node is child of 'node' node
	 * @param node
	 * @param n
	 * @return
	 */
	private static boolean hasNode(Node node, Node n) {
		
		if (node.getNodeType() == Node.ELEMENT_NODE) {
			// if current node not equals n, process children recursively
			if (node.equals(n)) return true;
			else {
				// for each children...
				for (int i=0; i<node.getChildNodes().getLength(); i++) {
					Node child = node.getChildNodes().item(i);
					// if current child has n, return true
					if (hasNode(child, n))
						return true;
				}
				return false;
			}
		}
		else {
			return false;
		}
	}
	
	/**
	 * Return previous node of the input node. If previous sibling
	 * node is null, returns parent node.
	 * @param node
	 * @return
	 */
	protected static Node getPrevNode(Node node) {
		if (node.getNodeName().equals(SENTENCE_NAME))
			return null;
		else {
			Node prevNode = node.getPreviousSibling();
			if (prevNode != null) {
				if (prevNode.getNodeType() == Node.ELEMENT_NODE)
					return prevNode;
				else
					return getPrevNode(prevNode);
			}
			else {
				return node.getParentNode();
			}
		}
	}
	
	/**
	 * Return next node of the input node. If next sibling
	 * node is null, returns his grandparent sibling child node.
	 * @param node
	 * @return
	 */
	protected static Node getNextNode(Node node) {
		
		Node nextNode = node.getNextSibling();
		// return next sibling node
		if (nextNode != null) {
			return nextNode;
		}
		// search for his grandparent sibling child node
		else {
			// if parent node is 'sentence' there is no sibling child node
			if (node.getParentNode().getNodeName().equals(SENTENCE_NAME))
				return null;
			else {
				// get parent and grandparent
				Node parent = node.getParentNode();
				Node grandparent = parent.getParentNode();
				while (grandparent != null) {
					int i=0;
					// search parent node from grandparent children
					for (; i<grandparent.getChildNodes().getLength(); i++) {
						Node c = grandparent.getChildNodes().item(i);
						if (c.equals(parent))
							break;
					}
					// get parent sibling node
					if (i<grandparent.getChildNodes().getLength()-1)
						return grandparent.getChildNodes().item(i+1);
					// if there is no sibling node and grandparent is sentence
					// node, there is no sibling node
					else if (grandparent.getNodeName().equals(SENTENCE_NAME))
						return null;
					
					grandparent = grandparent.getParentNode();
					parent = parent.getParentNode();
				}
				return null;
			}
		}
	}
	
	/**
	 * True if input XML element is a noun phrase
	 * @param node : input XML node
	 * @return
	 */
	protected static boolean isNounPhrase(Node node) {
		if (node.getNodeName().equalsIgnoreCase(NOUN_PHRASE_NAME)) {
			Element element = (Element) node;
			if (element.hasAttribute("elliptic")) {
				return element.getAttribute("elliptic").equalsIgnoreCase("yes");
			}
			else {
				return true;
			}
		}
		else {
			return false;
		}
	}

}
