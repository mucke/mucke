package mucke.query;

/** Transforms a query string into a Query object */
public interface QueryReader {

    /** 
     * Transforms a query string into a Query object 
     * @param String query
     * @return Query
     */
    public abstract Query prepare(String query);
    
}
