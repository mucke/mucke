package mucke.documentmodel;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an abstract document consisting of a list of facets.
 * 
 * @author Alexandra-Mihaela Siriteanu
 */
public class Document {

    private List<Facet> facets;

    /** Standard Constructor */
    public Document() {
	facets = new ArrayList<Facet>();
    }
    
    /** Constructor
     * @param facets A list of facets
     */
    public Document(List<Facet> facets) {
    	this.facets = facets;
    }
    
    /** 
     * Return facets of the document
     * @return facets 
     */
    public List<Facet> getFacets() {
        return facets;
    }
}