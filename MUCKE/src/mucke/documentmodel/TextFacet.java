package mucke.documentmodel;

import java.util.List;

import mucke.concept.Concept;
import mucke.concept.DocumentFacetProcessor;

/**
 * Defines the text facet
 * @author Alexandra-Mihaela Siriteanu
 */
public class TextFacet extends Facet {
    
    public TextFacet(String content){
	super.content = content;
    }
    
    @Override
    public List<Concept> accept(DocumentFacetProcessor visitor) {
        return visitor.process(this);
    }
}
