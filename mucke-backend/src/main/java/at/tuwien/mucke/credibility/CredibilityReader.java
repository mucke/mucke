package at.tuwien.mucke.credibility;

/**
 * Transforms a query string into a Query object
 */
public interface CredibilityReader {

    /**
     * Transforms the text representation of a query into a Query object
     *
     * @param query The query string
     * @return A query
     */
    public abstract User prepare(String query);

}
