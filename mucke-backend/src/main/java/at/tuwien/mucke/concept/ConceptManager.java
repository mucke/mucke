package at.tuwien.mucke.concept;

import at.tuwien.mucke.documentmodel.Document;
import at.tuwien.mucke.documentmodel.Facet;

import java.util.List;

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
