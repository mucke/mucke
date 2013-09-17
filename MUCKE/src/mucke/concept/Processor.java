/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mucke.concept;

import java.util.List;

import mucke.concept.model.Concept;
import mucke.concept.model.Document;
import mucke.concept.model.Field;

/**
 *
 * @author Alexandra
 */
public class Processor {

    private Visitor visitor;

    public Processor() {
        visitor = new FieldProcessor();
    }

    public Concept[] process(Document doc) {
        List<Field> fields = doc.getFields();
        if (fields.size() > 0) {
            for (Field field : fields) {
                field.accept(visitor);
            }
        }
        return null;
    }
}
