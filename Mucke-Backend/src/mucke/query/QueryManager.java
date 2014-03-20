package mucke.query;

import java.io.File;
import java.util.List;

import org.apache.log4j.Logger;

import mucke.concept.Concept;
import mucke.concept.DocumentFacetProcessor;
import mucke.concept.StandardDocumentFacetProcessor;
import mucke.config.ConfigConstants;
import mucke.config.ConfigurationManager;
import mucke.documentmodel.Facet;


/** Prepares queries from the user input and from collections (i.e. sets of queries) by turning 
 * them into Query objects and lists of Query objects and processes queries into Concepts with 
 * the use of the concept package. */
public class QueryManager {

    static Logger logger = Logger.getLogger(QueryManager.class);
    private ConfigurationManager configManager;
    private DocumentFacetProcessor visitor;

    /** Standard Constructor */
    public QueryManager(ConfigurationManager configManager) {
	this.configManager = configManager;
	this.visitor = new StandardDocumentFacetProcessor();
	// check parameterization
	this.checkParameters();
    }

    /** Prepare a single query (as provided by the user interface) into a Query object with the given reader
     * @param query
     * @param reader
     * @return Query
     */
    public Query prepare(String query, QueryReader reader) {
	return reader.prepare(query);
    }

    /** Prepare a query collection (as provided by an IR collection) into a list of Query objects.
     * The query collection file is defined by configuration via <see>ConfigConstants. QUERY_COLLECTION_FILE</see>.
     * The query collection reader is defined by configuration via <see>ConfigConstants.QUERY_COLLECTION_READER</see>.
     * @param listOfQueries
     * @param reader
     * @return List<Query>
     */
    public List<Query> prepareCollection() {
	
	// extract query reader from configuration
	String queryReaderValue = configManager.getProperty(ConfigConstants.QUERY_COLLECTION_READER);
	QueryCollectionReader queryReader = (QueryCollectionReader)configManager.getQueryReaderClass(queryReaderValue);
	
	return queryReader.prepare();
    }

    /** 
     * Transforms a query into a list of concepts contained in the query 
     * @param query The query
     * @return A list of concepts
     */
    public List<Concept> process(Query query) {
	List<Facet> facets = query.getFacets();
	if (facets != null) {
	    for (Facet facet : facets) {
		facet.accept(visitor);
	    }
	}
	return null;
    }
    
    /** Verifies the common system parameters for the query. 
     * 
     * @return true, if all parameters are correctly defined, false otherwise */
    private boolean checkParameters(){
	
	// checks query file
	if (!configManager.isProperty(ConfigConstants.QUERY_COLLECTION_FILE)){
	    logger.error("No query collection file provided. You must declare a file that contains the queries.");
	    return false;	
	}
	String queryFileName = configManager.getProperty(ConfigConstants.QUERY_COLLECTION_FILE);
	try {
	    File queryFile = new File(queryFileName);
	    if (!queryFile.exists()){
		logger.error("Query file '" + queryFileName + "' does not exist.");
	    }
	} catch (Exception e){
	    logger.error("Exception while accessing query file '" + queryFileName + "'.");
	}
	
	// query reader
	if (!configManager.isProperty(ConfigConstants.QUERY_COLLECTION_READER)){
	    logger.error("No query collection reader provided. You must declare exactly one query collection reader.");
	    return false;
	}
	
	// query collection signature
	if (!configManager.isProperty(ConfigConstants.QUERY_COLLECTION_SIG) || 
		configManager.getProperty(ConfigConstants.QUERY_COLLECTION_SIG) == null || 
		configManager.getProperty(ConfigConstants.QUERY_COLLECTION_SIG).equals("")){
	    logger.error("No query collection signature defined. You must declare a signature that tells the system which element separates two queries.");
	    return false;
	}
	
	// query id 
	if (!configManager.isProperty(ConfigConstants.QUERY_ID)){
	    logger.error("No field for query id defined. You must declare one field that defines the position of the unique query identifier.");
	    return false;
	}
	
	// check query facets in detail
	List<String> facets = configManager.getProperties(ConfigConstants.QUERY_FACETS, false);
	if (facets.size() == 0){
	    logger.error("No query facets defined. You must at least declare one query facet to build a query.");
	    return false;
	}
	for (String facet : facets){
	    List<String> facetValueList = configManager.getProperties(facet, false);
	    if (facetValueList.size() == 3){
		if (!(facetValueList.get(0).equals("XPATH") ||  facetValueList.get(0).equals("REGEX"))){
		    logger.error("Query facet '" + facet + "' has problem with first parameter (i.e. language). Must be 'XPATH' or 'REGEX'.");
		    return false;
		}
		if (!(facetValueList.get(2).equals("TEXT") ||  facetValueList.get(2).equals("IMAGE"))){
		    logger.error("Query facet '" + facet + "' has problem with third parameter (i.e. facettype). Must be 'TEXT' or 'IMAGE'.");
		    return false;
		}
	    } else {
		logger.error("Query facet '" + facet + "' misses parameters. Must contains 3 parameters. 1) language (XPATH or REGEX), 2) signature, 3) facet type (TEXT or IMAGE)");
		
	    }
	}
	
	// at this point, everything looked fine
	return true;
    }
       
}