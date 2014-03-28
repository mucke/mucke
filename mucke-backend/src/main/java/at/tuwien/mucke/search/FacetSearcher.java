package at.tuwien.mucke.search;

import at.tuwien.mucke.query.Query;

import java.util.List;

/**
 * Implement this interface if you want to write a new searcher for a particular facet type.
 *
 * @author Ralf Bierig
 */
public interface FacetSearcher {

    /**
     * Searches an index for a particular facet type and optionally filtered by a pre-existing
     * set of results
     *
     * @param query   The Query of the Search
     * @param results The pre-existing results that are used as a filter (e.g. results from a previous
     *                search). Results outside that list will be ignored. If the result list is null, a normal search
     *                will be performed with all results considered.
     * @return List of results
     */
    public List<Result> search(String queryString, List<Result> results);

}