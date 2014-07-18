package at.tuwien.mucke.search;

import at.tuwien.mucke.config.ConfigConstants;
import at.tuwien.mucke.config.ConfigurationManager;
import at.tuwien.mucke.documentmodel.Facet;
import org.apache.log4j.Logger;

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
     * Interactive facet search interface that is used by a user interface
     * @param queryString The query string of the form: facet:myfacet term OP term where
     *                    myfacet refers to the name of the facet as defined in the index creation,
     *                    term refers to the search terms and OP referes to Boolean operators.
     * @param filterResults Results by which new results will be filtered.
     */
    public List<Result> facetSearch(String queryString, List<Result> filterResults) {

        // Extract index information from configuration
        List<String> facetSearchers = configManager.getProperties(ConfigConstants.SEARCH_FACETSEARCHERS, false);

        for (String facetSearcher : facetSearchers){

            logger.info("Instantiating FacetSearcher...");
            String searchClassName = configManager.getProperty(facetSearcher + ".class");
            logger.debug("searchClassName ======> " + searchClassName);
            FacetSearcher searcher = (FacetSearcher) configManager.getFacetSearcherClass(facetSearcher, searchClassName);

            //call searcher
            List<Result> results = searcher.search(queryString, null);

            // return normalized results
            return SearchManager.normalize(results);
        }

        return new ArrayList<>();
    }

    /**
     * Batched facet search interface that runs on configuration parameters
     */
    public List<Result> facetSearch() {

        // check parametrization of batch mode
        this.checkParameters();

        // TODO

        return new ArrayList<>();
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

    /** Normalizes Results so that scores are between 0.0 and 1.0 if at least one value
     * is larger than 1.0. It does not normalize if all values are below the value of 1.0.
     * @param results The original result list
     * @return Normalized results */
    public static List<Result> normalize(List<Result> results){

        // determine the largest value
        float maxScore = 0.0f;
        for (Result result : results){
            if (result.getScore() > maxScore) {
                maxScore = result.getScore();
            }
        }

        // normalize
        if (maxScore > 1.0){
            // only normalize if scores rise over 1.0
            for (Result result : results){
                // reset score
                result.setScore(1 / maxScore * result.getScore());
            }
        }

        return results;
    }

    /**
     * Interactive search interface for searching images
     */
    /*public List<Result> imageFacetSearch(Query query, List<Result> filterResults) {

        List<Result> results = new ArrayList<Result>();

        // currently hard wired response
        try {

            Result r1 = new Result("http://abcnews.go.com/images/Lifestyle/GTY_yawning_dog_dm_130807.jpg", "Result1", 1.0f, "User1");
            results.add(r1);
            Result r2 = new Result("http://abcnews.go.com/images/Lifestyle/GTY_yawning_dog_dm_130807.jpg", "Result2", 0.9f, "User1");
            results.add(r2);
            Result r3 = new Result("http://jameni.com/wp-content/uploads/easycare.jpg", "Result3", 0.7f, "User2");
            results.add(r3);

        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        //ImageFacetSearcher searcher = new ImageFacetSearcher();
        //results = searcher.search(query, filterResults);

        return results;
    }*/



    public static void main (String[] args ){
        List<Result> results = new ArrayList<Result>();
        results.add(new Result("1", "Title1", 0.0f, "user1"));
        results.add(new Result("2", "Title2", 0.25f, "user1"));
        results.add(new Result("3", "Title3", 0.5f, "user1"));
        results.add(new Result("4", "Title4", 0.8f, "user1"));
        results.add(new Result("5", "Title5", 0.0f, "user1"));


        results = SearchManager.normalize(results);
        for (Result r : results){
            logger.info("result id: " + r.getId() + " score: " + r.getScore());
        }

    }
}