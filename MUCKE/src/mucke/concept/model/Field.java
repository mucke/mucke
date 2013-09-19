package mucke.concept.model;
import java.util.List;

import mucke.concept.model.Concept;
import mucke.concept.DocumentFieldProcessor;


/**
 * Determines an abstract content type that can be added to a document.  
 * 
 * @author Alexandra-Mihaela Siriteanu
 */
public abstract class Field {

    public abstract List<Concept> accept(DocumentFieldProcessor visitor);
}
