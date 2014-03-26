package at.tuwien.mucke.search;

import at.tuwien.mucke.query.Query;
import org.apache.log4j.Logger;

import java.util.List;

public class TextFacetSearcher implements FacetSearcher {

    static Logger logger = Logger.getLogger(TextFacetSearcher.class);

    /**
     * Searches for documents based on a text query.
     *
     * @param query         Text query that is used to search the text index
     * @param filterResults List of already existing results as a filter for the results found by
     *                      the search. Results outside that list will be ignored. If the result list is null, a normal
     *                      search will be performed with all results considered.
     */
    public List<Result> search(Query query, List<Result> results) {

        // logging
        logger.info("Searching for the following query:" + query);

        // for now, return everything that was received
        return results;
    }

}