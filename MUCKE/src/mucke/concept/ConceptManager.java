package mucke.concept;

import java.util.List;

import mucke.concept.model.Concept;
import mucke.concept.model.Document;
import mucke.concept.model.Field;

/**
 * Main service interface for concept identification that transforms documents into lists of concepts. 
 * @author Alexandra-Mihaela Siriteanu
 */
public class ConceptManager {

    private DocumentFieldProcessor visitor;

    public ConceptManager() {
        visitor = new StandardDocumentFieldProcessor();
    }

    public Concept[] process(Document doc) {
	
        List<Field> fields = doc.getFields();
        
        if (fields != null) {
            for (Field field : fields) {
                field.accept(visitor);
            }
        }
        return null;
    }
}
