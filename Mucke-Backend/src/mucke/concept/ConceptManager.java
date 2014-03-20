package mucke.concept;

import java.util.List;

import mucke.documentmodel.Document;
import mucke.documentmodel.Facet;

/**
 * Main service interface for concept identification that transforms documents into lists of concepts. 
 * 
 * @author Alexandra-Mihaela Siriteanu
 */
public class ConceptManager {

    private DocumentFacetProcessor visitor;

    public ConceptManager() {
        visitor = new StandardDocumentFacetProcessor();
    }

    public Concept[] process(Document doc) {
	
        List<Facet> facets = doc.getFacets();
        
        if (facets != null) {
            for (Facet facet : facets) {
        	facet.accept(visitor);
            }
        }
        return null;
    }
}
