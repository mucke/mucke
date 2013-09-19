package mucke.query;

import java.util.List;

import mucke.concept.model.Field;

/** A user query consisting of fields. Like documents, each query can be using text, tag or images.
 * CAN THIS BE MAPPED TO A LUCENE QUERY. */
public class Query {

    private List<Field> fields;

    /** 
     * Return fields of the Query
     * @return fields 
     */
    public List<Field> getFields() {
	return fields;
    }
}
