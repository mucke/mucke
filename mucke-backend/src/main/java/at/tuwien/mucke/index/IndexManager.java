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

        // index wikipedia
        String wikipediaIndexerName = configManager.getProperty(ConfigConstants.WIKIPEDIA_INDEXER);
        logger.debug("Wikipedia indexer class name:" + wikipediaIndexerName);
        logger.debug("Instantiating...");
        FacetIndexer wikipediaIndexer = (FacetIndexer) configManager.getFacetIndexerClass("wikipedia", wikipediaIndexerName);
        wikipediaIndexer.index();

        // concept indexing
        //String conceptIndexerName = configManager.getProperty(ConfigConstants.CONCEPTINDEXER);
        //logger.debug("Concept indexer class name:" + conceptIndexerName);
        //logger.debug("Instantiating...");
        //ConceptIndexer conceptIndexer = (ConceptIndexer) configManager.getFacetIndexerClass("conceptindex", conceptIndexerName);
        //conceptIndexer.index();

        // dispatch document indexer
        //this.indexDocument();

        // extract and dispatch document facet indexers
        //List<String> facetIndexers = configManager.getProperties(ConfigConstants.DOCINDEX_FACETS, false);
        //if (facetIndexers != null) {    // do not do anything if there was no facetindexer configured
            // dispatches facets indexers
        //    for (String facetIndexName : facetIndexers) {

                // create indexer
        //      String indexClassName = configManager.getProperty(facetIndexName);
        //      logger.debug("indexClassName:" + indexClassName);
        //      logger.debug("Instantiating...");
        //      FacetIndexer indexer = (FacetIndexer) configManager.getFacetIndexerClass(facetIndexName, indexClassName);

                // Call indexer
        //      indexer.index();

        //    }
    //}



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
}