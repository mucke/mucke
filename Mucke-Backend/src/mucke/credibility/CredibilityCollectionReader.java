package mucke.credibility;

import java.util.List;

/** Splits a query collection into a list of individual queries */
public interface CredibilityCollectionReader {
    
    /** Splits a query collection into a list of individual queries. The query collection is provided by configuration. */
    public abstract List<User> prepare();
    
}
