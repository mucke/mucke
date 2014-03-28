package at.tuwien.mucke.index;

import at.tuwien.mucke.config.ConfigConstants;
import at.tuwien.mucke.config.ConfigurationManager;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;


/**
 * Manages all index building.
 *
 * @author Ralf Bierig
 */
public class IndexManager {

    static Logger logger = Logger.getLogger(IndexManager.class);

    /**
     * Provides access to central configuration
     */
    private ConfigurationManager configManager;

    /**
     * Constructor
     */
    public IndexManager(ConfigurationManager configManager) {
        this.configManager = configManager;
    }

    /**
     * Runs the facet indexers based on the configuration file
     */
    public void index() {

        // create document index
        //this.indexDocument();

        // extract the facet indexers
        List<String> facetIndexers = configManager.getProperties(ConfigConstants.DOCINDEX_FACETS, false);

        // dispatches facets indexers
        for (String facetIndexName : facetIndexers) {
            dispatchIndexer(facetIndexName);
        }

    }

    /**
     * Calls the document indexer
     */
    public void indexDocument() {

        // extract content folder
        String contentFolder = configManager.getProperty("docindex.contentfolder");

        // extract document Id signature
        String docIdSignature = configManager.getProperty("docindex.docid");

        // extract facet signatures
        List<FacetIdSignature> facetIdSignatures = new ArrayList<FacetIdSignature>();
        List<String> facetNames = configManager.getProperties("docindex.facets", false);
        for (String facetName : facetNames) {
            List<String> facetDetails = configManager.getProperties("docindex.facet." + facetName, false);
            // error check
            if (facetDetails.size() != 3) {
                logger.error("Facet must provide facet id signature and facet type. Something is missing!");
                return;
            }

            FacetIdSignature signature = new FacetIdSignature(facetName, facetDetails.get(0), facetDetails.get(1), facetDetails.get(2));
            facetIdSignatures.add(signature);

        }

        // call document indexer
        DocumentIndexer indexer = new DocumentIndexer(configManager, docIdSignature);
        indexer.index(contentFolder, facetIdSignatures);

    }

    /**
     * Triggers the facetIndex defined by the name. The name is used in the properties file and read dynamically.
     */
    public void dispatchIndexer(String facetIndexName) {

        String indexFolder = facetIndexName + ".indexfolder";
        String contentFolder = facetIndexName + ".contentfolder";

        // check index parameters against errors
        checkParameters(facetIndexName);

        // extract content folder
        String contentFolderValue = configManager.getProperty(contentFolder);
        logger.debug("contentFolderValue: " + contentFolderValue);

        // extract index folder
        String indexFolderValue = configManager.getProperty(indexFolder);
        logger.debug("indexFolderValue: " + indexFolderValue);

        // create generators
        List<IndexFieldGenerator> generatorValues = prepareFieldGenerators(facetIndexName);

        // create and start the indexer with generators
        String indexClassName = configManager.getProperty(facetIndexName);
        logger.debug("indexClassName:" + indexClassName);

        logger.debug("Instantiating...");
        FacetIndexer indexer = (FacetIndexer) configManager.getFacetIndexerClass(facetIndexName, indexClassName);

        if (indexer != null) {
            logger.debug("Calling indexer: " + indexer);
            indexer.index(contentFolderValue, indexFolderValue, generatorValues);
            logger.debug("Called and finisehd! :)");
        }

    }

    /**
     * Verifies the common parameters for an index. Index parameters consist of:
     * <ul>
     * <li> The indexer (defined e.g. defined in textindex)</li>
     * <li> The content folder where the indexer can find the content </li>
     * <li> The index folder where the indexer places the index </li>
     * <li> The facet id that defines what is used as a unique identifier for the indexed items in this facet </li>
     * <li> A list of index fields that are declared afterwards by a name and a signature. At least one
     * index field needs to be declared. </li>
     * <li> An IndexFieldGenerator (defined e.g. in textindex.fields.generator). The IndexFieldGenerator is a
     * class that transforms content into Lucene index fields based on the name and the signature of the field
     * provided by the list. The class is named by it fully qualified name (e.g. at.tuwien.mucke.index.IndexFieldGenerator)</li>
     * </ul>
     *
     * @param indexName The name of the index
     * @return true, if all parameters are correctly defined, false otherwise
     */
    private boolean checkParameters(String indexName) {

        // check indexName
        if (indexName == null || indexName.equals("")) {
            logger.error("No indexer defined. You must at least declare one indexer in the properties.");
            return false;
        }
        if (!configManager.isProperty(indexName)) {
            logger.error("No such indexer '" + indexName + "'. You must at least declare one indexer in the properties.");
            return false;
        }

        // checks content folder
        String indexContentFolderProperty = indexName + ".contentfolder";
        if (!configManager.isProperty(indexContentFolderProperty)) {
            logger.error("No content folder provided for facet index " + indexName +
                    ". You must declare a content folder in the properties.");
            return false;
        }

        // checks index folder
        String indexFolderProperty = indexName + ".indexfolder";
        if (!configManager.isProperty(indexFolderProperty)) {
            logger.error("No index folder provided for facet index " + indexName +
                    ". You must declare an index folder in the properties.");
            return false;
        }

        // checks facet id -- every facet needs an ID
        String facetIdProperty = indexName + ".field.facetid";
        if (!configManager.isProperty(facetIdProperty) || configManager.getProperty(facetIdProperty) == null || configManager.getProperty(facetIdProperty).equals("")) {
            logger.error("No facet id defined for index '" + indexName + "'. You must declare one id field for this index.");
            logger.debug("facet index '" + indexName + "' is property?" + configManager.isProperty(facetIdProperty));
            logger.debug("facetIdProperty: " + facetIdProperty);
            return false;
        }

        // check index fields definition
        String indexFieldsProperty = indexName + ".fields";
        if (!configManager.isProperty(indexFieldsProperty)) {
            logger.error("No index fields for " + indexName + ". You must at least declare one index field in the properties.");
            return false;
        }

        // check IndexFieldGenerator
        String indexFieldsGeneratorProperty = indexFieldsProperty + ".generator";
        if (!configManager.isProperty(indexFieldsGeneratorProperty)) {
            logger.error("No index field generator defined for '" + indexName + "'. Please declare in the properties.");
            return false;
        }

        // check index fields in detail
        List<String> fields = configManager.getProperties(indexFieldsProperty, false);
        if (fields.size() == 0) {
            logger.error("No index fields for " + indexName + ". You must at least declare one index field in the properties.");
            return false;
        }
        List<String> generators = configManager.getProperties(indexFieldsGeneratorProperty, false);
        if (generators.size() == 0) {
            logger.error("No index field generator defined for " + indexName + ". Please declare in the properties.");
            return false;
        } else if (generators.size() > 1) {
            logger.error("No index field generator defined for '" + indexName + "'. Please declare in the properties.");
            return false;
        }

        // at this point, everything looked fine
        return true;
    }


    /**
     * Prepares IndexFieldGenerator Objects, that contain the method with which an index field is filled with
     * data from a content file during indexing. A so-called signature is used for that (e.g. a XML query or a
     * regular expression). The information for building the index field names and the generator objects are
     * obtained from the configuration file.
     *
     * @param indexName The name of the index is used to find the configuration for this index in the properties file
     * @return A list of IndexFieldGenerator objects
     */
    private List<IndexFieldGenerator> prepareFieldGenerators(String indexName) {

        String indexFieldsProperty = indexName + ".fields";
        //logger.debug("index field: " + indexFieldsProperty);
        String indexFieldsGeneratorProperty = indexFieldsProperty + ".generator";
        //logger.debug("generator field: " + indexFieldsGeneratorProperty);

        // Collection of IndexFieldGenerators, one per field
        List<IndexFieldGenerator> generators = new ArrayList<IndexFieldGenerator>();

        // extract IndexFieldGenerator information from configuration
        List<String> generatorProperties = configManager.getProperties(indexFieldsGeneratorProperty, false); // simple lookup
        if (generatorProperties.size() != 1) {
            logger.error("There must be exactly on IndexFieldGenerator per Index. Check your configuration.");
        }

        // check if index fields and generator are accurately declared
        checkParameters(indexName);

        // read index configuration (fields and signatures for fields)
        List<String> indexFields = configManager.getProperties(indexFieldsProperty, false);

        // extract index fields
        for (String indexField : indexFields) {

            // option NONE stops the process
            if (indexField.equals("NONE")) {
                logger.debug("No Index Fields declared. Nothing to parse.");
                continue;
            }

            List<String> values = configManager.getProperties(indexField, false);    // simple lookup

            if (values.size() != 2) {
                logger.error("Index field '" + indexField + "' is incorrect. It requires one name and one signature. Please modify configuration.");
                break;
            }

            // extract field, signature pair
            String fieldToken = values.get(0);
            String signatureToken = values.get(1);

            try {

                // create generator object
                IndexFieldGenerator generator = (IndexFieldGenerator) configManager.getIndexFieldGeneratorClass(generatorProperties.get(0), fieldToken, signatureToken);

                // add to list
                generators.add(generator);

            } catch (Exception e) {
                logger.error("Exception while creating IndexFieldGenerator. Class either does not exist or " +
                        "has no such constructor. Check configuration file.");
            }
        }

        return generators;
    }
}