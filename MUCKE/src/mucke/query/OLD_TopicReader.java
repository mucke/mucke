package mucke.query;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import mucke.plugin.clef2011.ConfigConstants;
import mucke.util.xml.XMLTools;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.InputSource;


/** 
 * Extracts topics (text amd image queries) from the topic XML file 
 * @author Ralf Bierig
 */
public class OLD_TopicReader {

	/** All topics of the CLEF2011. Populate it by calling <code>readTopics</code>. */
	private List<OLD_CLEF2011Query> topics = new ArrayList<OLD_CLEF2011Query>();
	
	// define all xPaths
	public final static String xTopic = "/topics/topic";
	public final static String xID = "/topic/number";
	public final static String xEN = "/topic/title[@lang='en']";
	public final static String xDE = "/topic/title[@lang='de']";
	public final static String xFR = "/topic/title[@lang='fr']";
	public final static String xImage = "/topic/image";
	public final static String xNarrative = "/topic/narrative";

	
	/** No need for instantiation */
	public OLD_TopicReader() {
	}
	
	public void readTopics(){
		
		// extract XML topics from entire XML file
		NodeList topicList = XMLTools.getNodes(new File(System.getProperty(ConfigConstants.TOPICS_FILE)), xTopic);	
		
		// loop over each topic and create topic objects 
		for (int i = 0; i < topicList.getLength(); i++){
			Node topicNode = topicList.item(i);
			
			// convert back into String representation
			DOMImplementationLS domImplLS = (DOMImplementationLS) topicNode.getOwnerDocument().getImplementation();
			LSSerializer serializer = domImplLS.createLSSerializer();
			String topicString = serializer.writeToString(topicNode);
		
			// read single topic
			OLD_CLEF2011Query topic = readTopic(topicString);
			
			// store
			this.topics.add(topic);
			
		}
	}
	
	
	/** Populates a single Topic from the given XML */
	private OLD_CLEF2011Query readTopic(String xml){
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		Document document = null;
		NodeList nodeList = null;
		OLD_CLEF2011Query topic = new OLD_CLEF2011Query();
		
		try {
			builder = factory.newDocumentBuilder();
			String all = "";
			InputStreamReader isr = new InputStreamReader(new ByteArrayInputStream(
					xml.getBytes("ISO-8859-1")), Charset.forName("ISO-8859-1"));
			
			try {
				while (isr.ready()) {
					all += ("" + (char) isr.read());
				}
			} catch (IOException e) {
				System.err.println("Exception while parsing topic");
			} finally {
				isr.close();
			}
			document = builder.parse(new InputSource(new StringReader(all)));
			XPathFactory xPathfactory = XPathFactory.newInstance();
			XPath xpath = xPathfactory.newXPath();
			
			// topic ID
			nodeList = (NodeList) xpath.compile(xID).evaluate(document, XPathConstants.NODESET);
			if (nodeList.getLength() < 1){
				System.err.println("No topicID found. Check your topic XML file!");
				return null;
			} else if (nodeList.getLength() > 1) {
				System.err.println("More than one topicID found. Check your topic XML file!");
				return null;
			} else {
				topic.setTopicID(nodeList.item(0).getTextContent().trim());
			}
						
			// English query text
			nodeList = (NodeList) xpath.compile(xEN).evaluate(document, XPathConstants.NODESET);
			if (nodeList.getLength() > 0){
				topic.setEn(nodeList.item(0).getTextContent().trim());
			}
			
			// French query text
			nodeList = (NodeList) xpath.compile(xFR).evaluate(document, XPathConstants.NODESET);
			if (nodeList.getLength() > 0){
				topic.setFr(nodeList.item(0).getTextContent().trim());
			}
			
			// German query text
			nodeList = (NodeList) xpath.compile(xDE).evaluate(document, XPathConstants.NODESET);
			if (nodeList.getLength() > 0){
				topic.setDe(nodeList.item(0).getTextContent().trim());
			}
			
			// Image file names
			nodeList = (NodeList) xpath.compile(xImage).evaluate(document, XPathConstants.NODESET);
			if (nodeList.getLength() > 0){
				List<String> imageList = new ArrayList();
				for (int i = 0; i < nodeList.getLength(); i++){
					imageList.add(nodeList.item(i).getTextContent().trim());
				}
				topic.setImages(imageList);
			}
			
			// Narrative text
			nodeList = (NodeList) xpath.compile(xNarrative).evaluate(document, XPathConstants.NODESET);
			if (nodeList.getLength() > 0){
				topic.setNarrative(nodeList.item(0).getTextContent().trim());
			}
			
		} catch (Exception e) {
			System.err.println("Exception while parsing topics: " + e.getMessage());
			e.printStackTrace();
		} 
		return topic;
	}
	
	
	/**
	 * @return the topics
	 */
	public final List<OLD_CLEF2011Query> getTopics() {
		return topics;
	}
	
	public static void main(String[] args) {
		
		// read topics
		OLD_TopicReader tReader = new OLD_TopicReader();
		tReader.readTopics();
		
		// display topic information
		List<OLD_CLEF2011Query> topics = tReader.getTopics();
		for (int i = 0; i < topics.size(); i++){
			System.out.println();
			System.out.println("Topic ID:	" + topics.get(i).getTopicID());
			System.out.println("English Query Text: " + topics.get(i).getEn());
			System.out.println("French Query Text:  " + topics.get(i).getFr());
			System.out.println("German Query Text:  " + topics.get(i).getDe());
			
			List<String> images = topics.get(i).getImages();
			String imageCSV = "";
			for (int j = 0; j < images.size(); j++){
				imageCSV += (images.get(j) + ", ");
			}
			System.out.println("Images:  " + imageCSV);
			
			System.out.println("Narrative:  " + topics.get(i).getNarrative());
		}
	}

}
