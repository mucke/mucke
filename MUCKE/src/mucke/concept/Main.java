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
public class Main {
    
    public static void main(String[] args) {
        Processor processor = new Processor();
        List<Field> fields = new ArrayList<Field>();
        for (int i = 0; i < 3; i++) {
            fields.add(new TagField());
            fields.add(new TextField());
            fields.add(new ImageField());
        }
        Document doc = new Document(fields);
        processor.process(doc);
    }
}
