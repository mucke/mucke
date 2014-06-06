package at.tuwien.mucke.index;

import at.tuwien.mucke.config.ConfigConstants;
import at.tuwien.mucke.config.ConfigurationManager;
import com.hp.hpl.jena.shared.uuid.Bits;
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

        // index accessory indices
        List<String> accessoryIndexers = configManager.getProperties(ConfigConstants.ACCESSORYINDEX_FACETS, false);
        if (accessoryIndexers != null && accessoryIndexers.size() > 0)
        {
            for (String assessoryIndexName : accessoryIndexers) {

                // create indexer
                String indexClassName = configManager.getProperty(assessoryIndexName);
                logger.debug("indexClassName:" + indexClassName);
                logger.debug("Instantiating...");
                FacetIndexer indexer = (FacetIndexer) configManager.getFacetIndexerClass(assessoryIndexName, indexClassName);

                // Call indexer
                indexer.index();
            }
        }

        // dispatch document indexer
        //this.indexDocument();

        // extract and dispatch document facet indexers
        List<String> facetIndexers = configManager.getProperties(ConfigConstants.DOCINDEX_FACETS, false);

        // dispatches facets indexers
        for (String facetIndexName : facetIndexers) {

            // create indexer
            String indexClassName = configManager.getProperty(facetIndexName);
            logger.debug("indexClassName:" + indexClassName);
            logger.debug("Instantiating...");
            FacetIndexer indexer = (FacetIndexer) configManager.getFacetIndexerClass(facetIndexName, indexClassName);

            // Call indexer
            indexer.index();

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
}