package mucke.query;

import java.util.ArrayList;
import java.util.List;

import mucke.documentmodel.Facet;

/** A user query consisting of facets. Similar to documents (as defined in the document model) each query may
 * contain text and image facets. */
public class Query {

    private List<Facet> facets = new ArrayList<Facet>();

    /** Adds a facet to the query */
    public void add(Facet facet){
	facets.add(facet);
    }
    
    /** Adds a facet to the query */
    public void add(List<Facet> facets){
	facets.addAll(facets);
    }
    
    /** 
     * Return fields of the Query
     * @return fields 
     */
    public List<Facet> getFacets() {
	return facets;
    }
}
