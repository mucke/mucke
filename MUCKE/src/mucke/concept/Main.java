package mucke.concept;

import java.util.ArrayList;
import java.util.List;

import mucke.concept.model.Document;
import mucke.concept.model.Field;
import mucke.concept.model.ImageField;
import mucke.concept.model.TagField;
import mucke.concept.model.TextField;

/**
 * Tests the concept package
 * @author Alexandra-Mihaela Siriteanu
 */
public class Main {
    
    /** Tests the concept package */
    public static void main(String[] args) {
        ConceptManager processor = new ConceptManager();
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