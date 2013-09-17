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
 *
 * @author Alexandra
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
