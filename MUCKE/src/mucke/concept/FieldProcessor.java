/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mucke.concept;

import java.util.List;

import mucke.concept.model.Concept;
import mucke.concept.model.ImageField;
import mucke.concept.model.TagField;
import mucke.concept.model.TextField;

/**
 * Processes document fields using different methods. Selectively overwriting the methods
 * allows to further adapt the functionality of the processor. 
 * @author Alexandra-Mihaela Siriteanu
 */
public class FieldProcessor implements Visitor{
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
