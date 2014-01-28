package mucke.query;

import java.util.List;

import mucke.concept.Concept;
import mucke.concept.DocumentFacetProcessor;
import mucke.concept.StandardDocumentFacetProcessor;
import mucke.documentmodel.Facet;

/** Prepares queries from the user input and from collections (i.e. sets of queries) by turning 
 * them into Query objects and lists of Query objects and processes queries into Concepts with 
 * the use of the concept package. */
public class QueryManager {

    private DocumentFacetProcessor visitor;

    /** Standard Constructor */
    public QueryManager() {
	visitor = new StandardDocumentFacetProcessor();
    }

    /** Prepare query into Query object with the given reader
     * @param query
     * @param reader
     * @return Query
     */
    public Query prepare(String query, QueryReader reader) {
	return reader.prepare(query);
    }

    /** Prepare query collection (as provided by an IR collection) into a list of Query objects 
     * @param listOfQueries
     * @param reader
     * @return List<Query>
     */
    public List<Query> prepareCollection(String listOfQueries, QueryCollectionReader reader) {
	return reader.prepare(listOfQueries);
    }

    /** 
     * Transforms a query into a list of concepts contained in the query 
     * @param query The query
     * @return A list of concepts
     */
    public List<Concept> process(Query query) {
	List<Facet> fields = query.getFields();
	if (fields != null) {
	    for (Facet field : fields) {
		field.accept(visitor);
	    }
	}
	return null;
    }
}