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
public interface Visitor {

    public abstract List<Concept> process(TextField textField);

    public abstract List<Concept> process(ImageField imageField);

    public abstract List<Concept> process(TagField tagField);
}
