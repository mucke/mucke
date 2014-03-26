package at.tuwien.mucke.search;

import at.tuwien.mucke.config.ConfigConstants;
import at.tuwien.mucke.config.ConfigurationManager;
import at.tuwien.mucke.query.Query;
import org.apache.log4j.Logger;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;


/**
 * Provides the main search interface for faceted (Text, Tag, and Image) and for concepts
 * for the user interface and for batch-based processing (i.e. based on configuration).
 */
public class SearchManager {

    static Logger logger = Logger.getLogger(SearchManager.class);
    private ConfigurationManager configManager;

    /**
     * Standard Constructor
     */
    public SearchManager(ConfigurationManager configManager) {
        this.configManager = configManager;
    }

    /**
     * Interactive search interface for searching images
     */
    public List<Result> imageFacetSearch(Query query, List<Result> filterResults) {

        List<Result> results = new ArrayList<Result>();

        // currently hard wired response
        try {

            Result r1 = new Result(new URL("http://abcnews.go.com/images/Lifestyle/GTY_yawning_dog_dm_130807.jpg"), 1.0f);
            results.add(r1);
            Result r2 = new Result(new URL("http://abcnews.go.com/images/Lifestyle/GTY_yawning_dog_dm_130807.jpg"), 0.9f);
            results.add(r2);
            Result r3 = new Result(new URL("http://jameni.com/wp-content/uploads/easycare.jpg"), 0.7f);
            results.add(r3);

        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        //ImageFacetSearcher searcher = new ImageFacetSearcher();
        //results = searcher.search(query, filterResults);

        return results;
    }

    /**
     * Batched search interface that runs on configuration parameters
     */
    public List<Result> facetSearch() {
        // check parameterization of batch mode
        this.checkParameters();

        // TODO

        return new ArrayList<Result>();
    }

    /**
     * Verifies the common system parameters for the query.
     *
     * @return true, if all parameters are correctly defined, false otherwise
     */
    private boolean checkParameters() {

        // checks declaration of facet searchers
        if (!configManager.isProperty(ConfigConstants.SEARCH_FACETSEARCHERS)) {
            logger.error("No search facet defined. You must declare at least one search facet.");
            return false;
        }
        List<String> facets = configManager.getProperties(ConfigConstants.SEARCH_FACETSEARCHERS, false);
        if (facets.size() == 0) {
            logger.error("No search facet defined. You must at least declare one query facet.");
            return false;
        }

        // TODO check search facets individually


        // at this point, everything looked fine
        return true;
    }

}