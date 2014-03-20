package mucke.query;

import java.io.ByteArrayInputStream;
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

import mucke.config.ConfigConstants;
import mucke.config.ConfigurationManager;
import mucke.documentmodel.Facet;
import mucke.documentmodel.ImageFacet;
import mucke.documentmodel.TextFacet;
import mucke.index.FacetIdSignature;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/** Test implementation of a a PrepareQueryStrategy */
public class XMLQueryReader implements QueryReader {

    static Logger logger = Logger.getLogger(XMLQueryCollectionReader.class);
    
    /** Allow access to configuration parameters */
    private ConfigurationManager configManager;
    
    
    /** Constructor */
    public XMLQueryReader(ConfigurationManager configManager){
	this.configManager = configManager;
    }
    
    @Override
    public Query prepare(String queryString) {
	
	//logger.debug("Preparing single query: " + queryString);
	
	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	DocumentBuilder builder = null;
	Document document = null;
	NodeList nodeList = null;
	Query query = new Query();
	
	try {
		builder = factory.newDocumentBuilder();
		String all = "";
		InputStreamReader isr = new InputStreamReader(new ByteArrayInputStream(
			queryString.getBytes("ISO-8859-1")), Charset.forName("ISO-8859-1"));
		
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
		
		// extract all signatures from configuration file
		List<String> queryFacetNames = configManager.getProperties(ConfigConstants.QUERY_FACETS, false);
			
		// extract query facets for each 
		int k = 1;
		for (String queryFacetName : queryFacetNames){
		    
		    // extract facet and build signature object
		    List<String> values = configManager.getProperties(queryFacetName, false);	// simple lookup
		    
		    FacetIdSignature signature = new FacetIdSignature(queryFacetName, values.get(0), values.get(1), values.get(2));
		    String parsingType = values.get(0);
		    String idSignature = values.get(1);
		    String type = values.get(2);
		    
		    // text against XPATH parsing types, since this QueryReader can only do those
		    if (!parsingType.equals("XPATH")){
			logger.debug("Query id signature '" + signature.getName() + "' has parsing type: '" + signature.getParsingType() + 
				"'. Can't be handled by this QueryParser. Nothing Done.");
			return null;
		    }
		    
		    // extract query facet
		    nodeList = (NodeList) xpath.compile(idSignature).evaluate(document, XPathConstants.NODESET);
		    if (nodeList.getLength() == 0){
			logger.error("Query facet '" + queryFacetName + "' was emptry.");    
		    }
		    
		    if (type.equals("TEXT")){
		
			for (int i = 0; i < nodeList.getLength(); i++){
			    query.add(new TextFacet("" + (k+i), queryFacetName, nodeList.item(i).getTextContent().trim()));
			}
		
		    } else if (type.equals("IMAGE")){
			
			for (int i = 0; i < nodeList.getLength(); i++){
			    query.add(new ImageFacet("" + (k+i), queryFacetName, nodeList.item(i).getTextContent().trim()));
			}
			
		    } else {
			logger.error("Query facet '" + signature.getName() + "' has unknown type '" + type + 
				"'. Check your configuration. Nothing done.");
		    }
		    k++;
		}
		    
		
	} catch (Exception e) {
	    System.err.println("Exception while parsing queries: " + e.getMessage());
	    e.printStackTrace();
	} 
	return query;
    }
    
}