package at.tuwien.mucke.search;

import org.apache.log4j.Logger;

import java.util.List;

/**
 * Searches for documents based on images
 */
public class ImageSearcher {

    static Logger logger = Logger.getLogger(ImageSearcher.class);

    /**
     * Searches for documents based on an image query.
     *
     * @param imageID Image identifier of the image that is used to search the image index
     * @param results List of already existing results as a filter for the results found by
     *                the search. Results outside that list will be ignored. If the result list is null, a normal
     *                search will be performed with all results considered.
     */
    public List<Result> search(String imageID, List<Result> results) {

        // logging
        logger.info("Searching for the following image:" + imageID);

        // for now, return everything that was received
        return results;
    }

}