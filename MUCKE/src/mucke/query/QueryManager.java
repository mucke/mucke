package mucke.query;

import java.util.List;
import mucke.concept.DocumentFieldProcessor;
import mucke.concept.model.Concept;
import mucke.concept.model.Field;
import mucke.concept.StandardDocumentFieldProcessor;

/** Prepares queries from the user input and from collections (i.e. sets of queries) by turning 
 * them into Query objects and lists of Query objects and processes queries into Concepts with 
 * the use of the concept package. */
public class QueryManager {

    private DocumentFieldProcessor visitor;

    /** Standard Constructor */
    public QueryManager() {
	visitor = new StandardDocumentFieldProcessor();
    }

    /** Prepare query into Query object with the given reader
     * @param String query
     * @param QueryReader reader
     * @return Query
     */
    public Query prepare(String query, QueryReader reader) {
	return reader.prepare(query);
    }

    /** Prepare query collection (as provided by an IR collection) into a list of Query objects 
     * @param (String listOfQueries
     * @param QueryCollectionReader reader
     * @return List<Query>
     */
    public List<Query> prepareCollection(String listOfQueries, QueryCollectionReader reader) {
	return reader.prepare(listOfQueries);
    }

    /** 
     * Transforms a query into a list of concepts contained in the query 
     * @param Query query
     * @return List<Query> 
     */
    public List<Concept> process(Query query) {
	List<Field> fields = query.getFields();
	if (fields != null) {
	    for (Field field : fields) {
		field.accept(visitor);
	    }
	}
	return null;
    }
}