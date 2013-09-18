/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mucke.concept.model;

import java.util.List;

import mucke.concept.model.Concept;
import mucke.concept.Visitor;

/**
 * Defines a field that describes a tag
 * @author Alexandra-Mihaela Siriteanu
 */
public class TagField extends Field {

    @Override
    public List<Concept> accept(Visitor visitor) {
        return visitor.process(this);
    }
}
