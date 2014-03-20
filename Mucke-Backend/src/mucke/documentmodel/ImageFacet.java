package mucke.documentmodel;

import java.net.URL;
import java.util.List;

import mucke.concept.Concept;
import mucke.concept.DocumentFacetProcessor;

/**
 * Defines an image facet
 * @author Alexandra-Mihaela Siriteanu
 */
public class ImageFacet extends Facet{
    
    public ImageFacet(String id, String name, String content){
	super.id = id;
	super.name = name;
	super.content = content;
    }
    
    @Override
    public List<Concept> accept(DocumentFacetProcessor visitor) {
        return visitor.process(this);
    }
}
