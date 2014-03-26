package at.tuwien.mucke.concept;

import at.tuwien.mucke.documentmodel.*;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Tests the concept package
 *
 * @author Alexandra-Mihaela Siriteanu
 */
public class Main {

    static Logger logger = Logger.getLogger(Main.class);

    /**
     * Tests the concept package
     */
    public static void main(String[] args) {

        ConceptManager processor = new ConceptManager();
        List<Facet> facets = new ArrayList<Facet>();

        try {

            for (int i = 0; i < 3; i++) {
                facets.add(new TagFacet("1", "elephantTag", "Elephant"));
                facets.add(new TextFacet("2", "elephantText", "This is an Elephant"));
                facets.add(new ImageFacet("3", "elephantImage", "http://images.nationalgeographic.com/wpf/media-live/photos/000/004/cache/african-elephant_435_600x450.jpg"));
            }

        } catch (Exception e) {
            logger.error("Expection while testing concept package: " + e.getMessage());
        }


        Document doc = new Document(facets);
        processor.process(doc);
    }
}