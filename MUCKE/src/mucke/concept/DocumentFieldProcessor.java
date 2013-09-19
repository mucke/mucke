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
 * Vistor pattern interface. This interface allows the three document field types (text, tag and image) to have alternative 
 * implementations leading to a large variety of how to generate concepts from document fields.
 * 
 * @see DocumentFieldProcessor 
 * @author Alexandra-Mihaela Siriteanu
 */
public interface DocumentFieldProcessor {

    /** Process a text field to identify concepts */
    public abstract List<Concept> process(TextField textField);

    /** Process an image field to identify concepts */
    public abstract List<Concept> process(ImageField imageField);

    /** Process a tag field to identify concepts */
    public abstract List<Concept> process(TagField tagField);
}
