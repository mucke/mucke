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
