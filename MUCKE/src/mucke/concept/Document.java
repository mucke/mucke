/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mucke.concept;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Alexandra
 */
public class Document {

    private List<Field> fields;

    public Document() {
        fields = new ArrayList<Field>();
        for (int i = 0; i < 3; i++) {
            fields.add(new TagField());
            fields.add(new TextField());
            fields.add(new ImageField());
        }
    }
    
    public List<Field> getFields() {
        return fields;
    }
}
