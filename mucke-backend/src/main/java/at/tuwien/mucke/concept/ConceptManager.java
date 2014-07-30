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

    /** Allows access to configuration details */
    protected ConfigurationManager configurationManager = null;

    /** Constructor */
    public ConceptManager(ConfigurationManager configurationManager) {
        visitor = new StandardDocumentFacetProcessor(configurationManager);
    }

    /** Processes the document with FacetProcessors into concepts
     * @param doc Documents
     * @return List of concepts that represent this document (and its facets) */
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
