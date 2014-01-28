package mucke.index;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import mucke.config.ConfigurationManager;

import org.apache.log4j.Logger;


/** 
 * Manages all index building. 
 * 
 * @author Ralf Bierig
 */
public class IndexManager {

    static Logger logger = Logger.getLogger(IndexManager.class);
    
    /** Provides access to central configuration */
    private ConfigurationManager configManager; 
    
    /** Constructor */
    public IndexManager(ConfigurationManager configManager){
	this.configManager = configManager;
    }
    
    
    /** Checks if index parameters are set. Index parameters consist of a list of index fields and an IndexFieldGenerator. The list of 
     * index fields points to index field properties that are also declared in the properties file (consisting of a name and a signature).
     * The IndexFieldGenerator is a class that transforms content into Lucene index fields based on the name and the signature of the field 
     * provided by the list. Both need to be defined and it is necessary that at least one index field is declared in the list and a generator 
     * class is named by it fully qualified name (e.g. mucke.index.IndexFieldGenerator). 
     * @param indexName The name of the index 
     * @return true, if the parameters are defined correctly, false otherwise */
    private boolean checkIndexParameters(String indexName){
	
	String indexFieldsProperty = indexName + ".fields";
	String indexFieldsGeneratorProperty = indexFieldsProperty + ".generator";
	
	if (!configManager.isProperty(indexFieldsProperty)){
	    logger.error("No index fields for " + indexName + ". You must at least declare one index field in the properties.");
	    return false;
	}
	if (!configManager.isProperty(indexFieldsGeneratorProperty)){
	    logger.error("No index field generator defined for '" + indexName + "'. Please declare in the properties.");
	    return false;
	}
	List<String> fields = new ArrayList<String>();
	configManager.getProperties(indexFieldsProperty, fields, false);
	if (fields.size() == 0){
	    logger.error("No index fields for " + indexName + ". You must at least declare one index field in the properties.");
	    return false;
	}
	List<String> generators = new ArrayList<String>();
	configManager.getProperties(indexFieldsGeneratorProperty, generators, false);
	if (generators.size() == 0){
	    logger.error("No index field generator defined for " + indexName + ". Please declare in the properties.");
	    return false;
	} else if (generators.size() > 1){
	    logger.error("No index field generator defined for '" + indexName + "'. Please declare in the properties.");
	    return false;
	}
	
	// everything looked fine 
	return true;
    }
    
    
    /** Prepares IndexFieldGenerator Objects, that contain the method with which an index field is filled with 
     * data from a content file during indexing. A so-called signature is used for that (e.g. a XML query or a 
     * regular expression). The information for building the index field names and the generator objects are 
     * obtained from the configuration file.
     * 
     * @param indexName The name of the index is used to find the configuration for this index in the properties file
     * @return A list of IndexFieldGenerator objects */
    private List<IndexFieldGenerator> prepareFieldGenerators(String indexName){
	
	String indexFieldsProperty = indexName + ".fields";
	logger.debug("index field: " + indexFieldsProperty);
	String indexFieldsGeneratorProperty = indexFieldsProperty + ".generator";
	logger.debug("generator field: " + indexFieldsGeneratorProperty);
	
	// Collection of IndexFieldGenerators, one per field
	List<IndexFieldGenerator> generators = new ArrayList<IndexFieldGenerator>();
	
	// extract IndexFieldGenerator information from configuration
	List<String> generatorProperties = new ArrayList<String>();
	configManager.getProperties(indexFieldsGeneratorProperty, generatorProperties, false); // simple lookup
	if (generatorProperties.size() != 1){
	    logger.error("There must be exactly on IndexFieldGenerator per Index. Check your configuration.");
	} 
	
	// check if index fields and generator are accurately declared
	checkIndexParameters(indexName);
	
	// read index configuration (fields and signatures for fields)
	List<String> indexFields = new ArrayList<String>();
	configManager.getProperties(indexFieldsProperty, indexFields, false);
	
	// extract index fields
	for (String indexField : indexFields){
	    List<String> values = new ArrayList<String>();
	    configManager.getProperties(indexField, values, false);	// simple lookup
	    if (values.size() != 2){
		logger.error("Index field '" + indexField + "' is no properly defined. Requires one name and one signature. Please modify configuration.");
		break;
	    } 
	    
	    // extract field, signature pair
	    String fieldToken = values.get(0);
	    logger.debug("field: " + fieldToken);
	    String signatureToken = values.get(1);
	    logger.debug("signatureToken: " + signatureToken);
	    
	    try {
		
		Class<?> clazz = Class.forName(generatorProperties.get(0));
		Constructor constructor = clazz.getConstructor(String.class, String.class);
		IndexFieldGenerator generator = (IndexFieldGenerator) constructor.newInstance(fieldToken, signatureToken);
		// add to list
		generators.add(generator);
		
	    } catch (Exception e){
		logger.error("Exception while creating IndexFieldGenerator. Class either does not exist or " +
				"has no such constructor. Check configuration file.");
	    }
	}
	
	return generators;
    }
    
    
    /** Calls the text indexer to index (defined by the indexName) the given content directory and store the index 
     * in the given index directory
     * @param contentDirectory
     * @param indexDirectory 
     * @param indexName A unique name of the index that is also used in the configuration file to refer to the fields
     * that are used and the generator that is applied to create the fields.
     */
    public void indexText(String contentDirectory, String indexDirectory, String indexName){
	
	// create generators
	List<IndexFieldGenerator> generators = prepareFieldGenerators(indexName);
	    
	// create and start the indexer with generators
	StandardTextFacetIndexer indexer = new StandardTextFacetIndexer(configManager);
	logger.info("Lets start!");
	indexer.index(contentDirectory, indexDirectory, generators);
    }
       
    
    /** Calls the tag indexer to index (defined by the indexName) the given content directory and store the index 
     * in the given index directory
     * @param contentDirectory
     * @param indexDirectory 
     * @param indexName A unique name of the index that is also used in the configuration file to refer to the fields
     * that are used and the generator that is applied to create the fields.
     */
    public void indexTag(String contentDirectory, String indexDirectory, String indexName){
	
	// create generators
	List<IndexFieldGenerator> generators = prepareFieldGenerators(indexName);
	    
	// create and start the indexer with generators
	StandardTagFacetIndexer indexer = new StandardTagFacetIndexer(configManager);
	logger.info("Lets start!");
	indexer.index(contentDirectory, indexDirectory, generators);
    }
    
    
    
    /** Calls the image indexer to index (defined by the indexName) the given content directory and store the index 
     * in the given index directory
     * @param contentDirectory
     * @param indexDirectory 
     */
    public void indexImage(String contentDirectory, String indexDirectory, String indexName){

	// create generators
	List<IndexFieldGenerator> generators = prepareFieldGenerators(indexName);
    
	// TODO
    }
    
    /** Calls the concept indexer to index (defined by the indexName) the given content directory and store the index 
     * in the given index directory
     * @param contentDirectory
     * @param indexDirectory 
     */
    public void indexConcept(String contentDirectory, String indexDirectory, String indexName){
	
	// create generators
	List<IndexFieldGenerator> generators = prepareFieldGenerators(indexName);
	
	// TODO
    }
    
}