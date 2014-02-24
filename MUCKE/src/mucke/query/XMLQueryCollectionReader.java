package mucke.query;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import mucke.plugin.clef2011.ConfigConstants;
import mucke.util.xml.XMLTools;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;

/** Test implementation of a a PrepareCollectionStrategy */
public class XMLQueryCollectionReader implements QueryCollectionReader {

    @Override
    public List<Query> prepare(String listOfQueries) {

	List<Query> queries = new ArrayList<Query>();
	
//	// extract XML topics from entire XML file
//	NodeList topicList = XMLTools.getNodes(new File(System.getProperty(ConfigConstants.TOPICS_FILE)), xTopic);
//
//	// loop over each topic and create topic objects
//	for (int i = 0; i < topicList.getLength(); i++) {
//	    Node topicNode = topicList.item(i);
//
//	    // convert back into String representation
//	    DOMImplementationLS domImplLS = (DOMImplementationLS) topicNode.getOwnerDocument().getImplementation();
//	    LSSerializer serializer = domImplLS.createLSSerializer();
//	    String topicString = serializer.writeToString(topicNode);
//
//	    // read single topic
//	    OLD_CLEF2011Query topic = readTopic(topicString);
//
//	    // store
//	    this.topics.add(topic);
//
//	}
	
	return queries;
    }

}
