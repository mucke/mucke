package at.tuwien.mucke.documentmodel;

import at.tuwien.mucke.concept.Concept;
import at.tuwien.mucke.concept.DocumentFacetProcessor;

import java.util.List;

/**
 * Defines an tag facet
 *
 * @author Alexandra-Mihaela Siriteanu
 */
public class TagFacet extends Facet {

    public TagFacet(String id, String name, String content) {
        super.id = id;
        super.name = name;
        super.content = content;
    }

    @Override
    public List<Concept> accept(DocumentFacetProcessor visitor) {
        return visitor.process(this);
    }
}
