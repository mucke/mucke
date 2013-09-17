/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mucke.concept;

import java.util.List;

/**
 *
 * @author Alexandra
 */
public interface Visitor {

    public abstract List<Concept> process(TextField textField);

    public abstract List<Concept> process(ImageField imageField);

    public abstract List<Concept> process(TagField tagField);
}
