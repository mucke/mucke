package at.tuwien.mucke.concept;

import at.tuwien.mucke.documentmodel.ImageFacet;
import at.tuwien.mucke.documentmodel.TagFacet;
import at.tuwien.mucke.documentmodel.TextFacet;

import java.util.List;

/**
 * Vistor pattern interface. This interface allows the three document field types (text, tag and image) to have alternative
 * implementations leading to a large variety of how to generate concepts from document fields.
 *
 * @author Alexandra-Mihaela Siriteanu
 * @see DocumentFacetProcessor
 */
public interface DocumentFacetProcessor {

    /**
     * Process a text field to identify concepts
     */
    public abstract List<Concept> process(TextFacet textFacet);

    /**
     * Process an image field to identify concepts
     */
    public abstract List<Concept> process(ImageFacet imageFacet);

    /**
     * Process a tag field to identify concepts
     */
    public abstract List<Concept> process(TagFacet tagFacet);
}
