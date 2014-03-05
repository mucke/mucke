package mucke.documentmodel;
import java.util.List;

import mucke.concept.Concept;
import mucke.concept.DocumentFacetProcessor;


/**
 * Determines an abstract content type, a facet.  
 * 
 * @author Alexandra-Mihaela Siriteanu
 */
public abstract class Facet {

    protected String content; 
    
    public abstract List<Concept> accept(DocumentFacetProcessor visitor);
    
    public String getContent(){
	return content;
    }
}
