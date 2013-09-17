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
public class TagField extends Field {

    @Override
    public List<Concept> accept(Visitor visitor) {
        return visitor.process(this);
    }
}
