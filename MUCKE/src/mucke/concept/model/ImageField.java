package mucke.concept.model;

import java.util.List;

import mucke.concept.model.Concept;
import mucke.concept.DocumentFieldProcessor;

/**
 * Defines an image field that used for a document
 * @author Alexandra-Mihaela Siriteanu
 */
public class ImageField extends Field{
    @Override
    public List<Concept> accept(DocumentFieldProcessor visitor) {
        return visitor.process(this);
    }
}
