/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mucke.concept.model;
import java.util.List;

import mucke.concept.model.Concept;
import mucke.concept.Visitor;


/**
 *
 * @author Alexandra
 */
public abstract class Field {

    public abstract List<Concept> accept(Visitor visitor);
}
