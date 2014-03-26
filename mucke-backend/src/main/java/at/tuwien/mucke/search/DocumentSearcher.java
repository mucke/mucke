/**
 *
 */
package at.tuwien.mucke.search;

import at.tuwien.mucke.config.ConfigurationManager;
import at.tuwien.mucke.documentmodel.Facet;
import at.tuwien.mucke.documentmodel.ImageFacet;
import at.tuwien.mucke.documentmodel.TagFacet;
import at.tuwien.mucke.documentmodel.TextFacet;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Basic document lookup for each of the facets
 */
public class DocumentSearcher {

    static Logger logger = Logger.getLogger(DocumentSearcher.class);

    private ConfigurationManager configManager = null;

    public DocumentSearcher(ConfigurationManager configManager) {
        this.configManager = configManager;
    }

    /**
     * Interactive document search interface
     *
     * @param facet The facet for which the document is sought
     * @return List of document results
     */
    public List<Result> search(Facet facet, List<Result> results) {

        List<Result> newResults = new ArrayList<Result>();

        if (facet instanceof TextFacet) {
            // TODO
        } else if (facet instanceof TagFacet) {
            // TODO
        } else if (facet instanceof ImageFacet) {
            // TODO
        } else {
            logger.error("Unknown facet type: " + facet.getClass().getName());
        }

        return newResults;
    }

    /**
     * Batch document search interface that runs on configuration parameters
     */
    public List<Result> search() {
        // TODO
        return new ArrayList<Result>();
    }
}
