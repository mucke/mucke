package mucke.query;

import java.util.List;

/** Splits a query collection into a list of individual queries */
public interface QueryCollectionReader {
    
    /** Splits a query collection into a list of individual queries. The query collection is provided by configuration. */
    public abstract List<Query> prepare();
    
}
