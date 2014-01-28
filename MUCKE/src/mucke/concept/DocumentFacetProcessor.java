package mucke.concept;

import java.util.List;

import mucke.documentmodel.ImageFacet;
import mucke.documentmodel.TagFacet;
import mucke.documentmodel.TextFacet;

/**
 * Vistor pattern interface. This interface allows the three document field types (text, tag and image) to have alternative 
 * implementations leading to a large variety of how to generate concepts from document fields.
 * 
 * @see DocumentFacetProcessor 
 * @author Alexandra-Mihaela Siriteanu
 */
public interface DocumentFacetProcessor {

    /** Process a text field to identify concepts */
    public abstract List<Concept> process(TextFacet textFacet);

    /** Process an image field to identify concepts */
    public abstract List<Concept> process(ImageFacet imageFacet);

    /** Process a tag field to identify concepts */
    public abstract List<Concept> process(TagFacet tagFacet);
}
