package at.tuwien.mucke.query;

import at.tuwien.mucke.documentmodel.Facet;

import java.util.ArrayList;
import java.util.List;

/**
 * A user query consisting of facets. Similar to documents (as defined in the document model) each query may
 * contain text and image facets.
 */
public class Query {

    /**
     * List of query facets
     */
    private List<Facet> facets = new ArrayList<Facet>();

    /**
     * Adds a facet to the query
     */
    public void add(Facet facet) {
        facets.add(facet);
    }

    /**
     * Return fields of the Query
     *
     * @return fields
     */
    public List<Facet> getFacets() {
        return facets;
    }
}
