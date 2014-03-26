package at.tuwien.mucke.query;

import at.tuwien.mucke.config.ConfigConstants;
import at.tuwien.mucke.config.ConfigurationManager;
import at.tuwien.mucke.util.xml.XMLTools;
import org.apache.log4j.Logger;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Test implementation of a a PrepareCollectionStrategy
 */
public class XMLQueryCollectionReader implements QueryCollectionReader {

    static Logger logger = Logger.getLogger(XMLQueryCollectionReader.class);
    private ConfigurationManager configManager;
    private QueryReader queryReader;

    /**
     * Constructor
     */
    public XMLQueryCollectionReader(ConfigurationManager configManager) {
        this.configManager = configManager;
        this.queryReader = new XMLQueryReader(configManager);
    }

    @Override
    public List<Query> prepare() {

        List<Query> queries = new ArrayList<Query>();

        // extract XML topics from entire XML file
        File queryFile = new File(configManager.getProperty(ConfigConstants.QUERY_COLLECTION_FILE));
        List<String> signatureParams = configManager.getProperties(ConfigConstants.QUERY_COLLECTION_SIG, false);
        if (!signatureParams.get(0).equals("XPATH")) {
            logger.error("Query collection signature type is '" + signatureParams.get(0) +
                    "'. This reader can only process XPATH signature types. Nothing done.");
            return null;
        }
        NodeList queryList = XMLTools.getNodes(queryFile, signatureParams.get(1));

        // loop over each topic and create topic objects
        for (int i = 0; i < queryList.getLength(); i++) {
            Node topicNode = queryList.item(i);

            // convert back into String representation
            DOMImplementationLS domImplLS = (DOMImplementationLS) topicNode.getOwnerDocument().getImplementation();
            LSSerializer serializer = domImplLS.createLSSerializer();
            String queryString = serializer.writeToString(topicNode);

            // generate single query
            logger.debug("Reading topic # " + (i + 1) + " ...");
            Query query = queryReader.prepare(queryString);
            queries.add(query);

        }

        return queries;
    }

}
