package at.tuwien.mucke.documentmodel;

import at.tuwien.mucke.concept.Concept;
import at.tuwien.mucke.concept.DocumentFacetProcessor;

import java.util.List;


/**
 * Determines an abstract content type, a facet.
 *
 * @author Alexandra-Mihaela Siriteanu
 */
public abstract class Facet {

    protected String id;
    protected String name;
    protected String content;

    /**
     * Allows dynamically associating the logic that transforms the facet into concepts
     */
    public abstract List<Concept> accept(DocumentFacetProcessor visitor);

    /**
     * Unique identifier
     */
    public String getId() {
        return id;
    }

    /**
     * Name of the facet, i.e. the facet index that is used to store the facet.
     */
    public String getName() {
        return name;
    }

    /**
     * Content stored in the facet.
     */
    public String getContent() {
        return content;
    }
}
