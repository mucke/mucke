package mucke.concept;

import java.util.ArrayList;
import java.util.List;

import mucke.documentmodel.Document;
import mucke.documentmodel.Facet;
import mucke.documentmodel.ImageFacet;
import mucke.documentmodel.TagFacet;
import mucke.documentmodel.TextFacet;

/**
 * Tests the concept package
 * @author Alexandra-Mihaela Siriteanu
 */
public class Main {
    
    /** Tests the concept package */
    public static void main(String[] args) {
        
	ConceptManager processor = new ConceptManager();
        List<Facet> facets = new ArrayList<Facet>();
        
        for (int i = 0; i < 3; i++) {
            facets.add(new TagFacet());
            facets.add(new TextFacet());
            facets.add(new ImageFacet());
        }
        
        Document doc = new Document(facets);
        processor.process(doc);
    }
}