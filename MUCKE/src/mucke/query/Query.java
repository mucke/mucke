package mucke.query;

import java.util.List;

import mucke.documentmodel.Facet;

/** A user query consisting of fields. Like documents, each query can be using text, tag or images.
 * CAN THIS BE MAPPED TO A LUCENE QUERY? */
public class Query {

    private List<Facet> fields;

    /** 
     * Return fields of the Query
     * @return fields 
     */
    public List<Facet> getFields() {
	return fields;
    }
}
