package at.tuwien.mucke.concept;

import at.tuwien.mucke.documentmodel.ImageFacet;
import at.tuwien.mucke.documentmodel.TagFacet;
import at.tuwien.mucke.documentmodel.TextFacet;

import java.util.List;

/**
 * Standard processor of transforming each of the three document facets (text, tag and image) into concepts.
 * Use inheritance to selectively overwrite these methods with more specific functionality if needed.
 *
 * @author Alexandra-Mihaela Siriteanu
 */
public class StandardDocumentFacetProcessor implements DocumentFacetProcessor {

    @Override
    public List<Concept> process(TextFacet textFacet) {
        System.out.println("Processing text facet");
        return null;
    }

    @Override
    public List<Concept> process(ImageFacet imageFacet) {
        System.out.println("Processing image facet");
        return null;
    }

    @Override
    public List<Concept> process(TagFacet tagFacet) {
        System.out.println("Processing tag facet");
        return null;
    }
}