package mucke.query;

/** Transforms a query string into a Query object */
public interface QueryReader {

    /** 
     * Transforms a query string into a Query object 
     * @param query The query string
     * @return A query
     */
    public abstract Query prepare(String query);
    
}
