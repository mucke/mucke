package mucke.concept;

import java.util.List;

import mucke.concept.model.Concept;
import mucke.concept.model.ImageField;
import mucke.concept.model.TagField;
import mucke.concept.model.TextField;

/**
 * Standard processor of transforming each of the three document field types (text, tag and image) into concepts. 
 * Use inheritance to selectively overwrite these methods with more specific functionality if needed. 
 * 
 * @author Alexandra-Mihaela Siriteanu
 */
public class StandardDocumentFieldProcessor implements DocumentFieldProcessor {
    
    @Override
    public List<Concept> process(TextField textField) {
        System.out.println("Processing text field");
        return null;
    }
    
    @Override
    public List<Concept> process(ImageField imageField) {
        System.out.println("Processing image field");
        return null;
    }
    
    @Override
    public List<Concept> process(TagField tagField) {
        System.out.println("Processing tag field");
        return null;
    }
}