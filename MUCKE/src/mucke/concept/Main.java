package mucke.concept;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

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
    
    static Logger logger = Logger.getLogger(Main.class);
    
    /** Tests the concept package */
    public static void main(String[] args) {
        
	ConceptManager processor = new ConceptManager();
        List<Facet> facets = new ArrayList<Facet>();
        
        try {
      
            for (int i = 0; i < 3; i++) {
                facets.add(new TagFacet("Elephant"));
                facets.add(new TextFacet("This is an Elephant"));
                facets.add(new ImageFacet("http://images.nationalgeographic.com/wpf/media-live/photos/000/004/cache/african-elephant_435_600x450.jpg"));
            }
                
        } catch (Exception e){
            logger.error("Expection while testing concept package: " + e.getMessage());
        }
        

        Document doc = new Document(facets);
        processor.process(doc);
    }
}