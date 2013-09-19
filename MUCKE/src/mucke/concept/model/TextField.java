/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mucke.concept.model;

import java.util.List;

import mucke.concept.model.Concept;
import mucke.concept.DocumentFieldProcessor;

/**
 * Defines an text field that is used for a document
 * @author Alexandra-Mihaela Siriteanu
 */
public class TextField extends Field {

    @Override
    public List<Concept> accept(DocumentFieldProcessor visitor) {
        return visitor.process(this);
    }
}
