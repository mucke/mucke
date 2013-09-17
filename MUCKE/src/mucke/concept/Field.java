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
public abstract class Field {

    public abstract List<Concept> accept(Visitor visitor);
}
