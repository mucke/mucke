package mucke.documentmodel;

import java.util.List;

import mucke.concept.Concept;
import mucke.concept.DocumentFacetProcessor;

/**
 * Defines an tag facet
 * @author Alexandra-Mihaela Siriteanu
 */
public class TagFacet extends Facet {

    public TagFacet(String id, String name, String content){
	super.id = id;
	super.name = name;
	super.content = content;
    }
    
    @Override
    public List<Concept> accept(DocumentFacetProcessor visitor) {
        return visitor.process(this);
    }
}
