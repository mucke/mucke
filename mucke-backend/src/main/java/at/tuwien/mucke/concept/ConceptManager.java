package at.tuwien.mucke.concept;

import at.tuwien.mucke.config.ConfigurationManager;
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

    protected ConfigurationManager configurationManager = null;

    public ConceptManager(ConfigurationManager configurationManager) {
        visitor = new StandardDocumentFacetProcessor(configurationManager);
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
