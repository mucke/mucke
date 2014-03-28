package at.tuwien.mucke.search;

import at.tuwien.mucke.query.Query;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Searches for documents based on images
 */
public class ImageFacetSearcher implements FacetSearcher {

    static Logger logger = Logger.getLogger(ImageFacetSearcher.class);

    /**
     * Searches for image facets based on a query.
     *
     * @param query         The query that is used to search the text index
     * @param filterResults List of already existing results as a filter for the results found by
     *                      the search. Results outside that list will be ignored. If the result list is null, a normal
     *                      search will be performed with all results considered.
     */
    public List<Result> search(String queryString, List<Result> filterResults) {

        List<Result> results = new ArrayList<Result>();

        // logging
        logger.debug("TODO: Searching for images!");

        return results;

    }

}