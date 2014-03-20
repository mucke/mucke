package mucke.search;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import mucke.query.Query;
import net.semanticmetadata.lire.DocumentBuilder;
import net.semanticmetadata.lire.ImageSearchHits;
import net.semanticmetadata.lire.ImageSearcher;
import net.semanticmetadata.lire.ImageSearcherFactory;

import org.apache.log4j.Logger;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.FSDirectory;

/** Searches for documents based on images */
public class ImageFacetSearcher implements FacetSearcher {

    static Logger logger = Logger.getLogger(ImageFacetSearcher.class);
    
    @Override
    public List<Result> search(Query query, List<Result> results) {
	
	List<Result> imageResults = new ArrayList<Result>();
	
	// logging
	logger.debug("Searching for images!");
		
	// extract image facets and turn them into urls
	
	
	// Checking if arg[0] is there and if it is an image.
        /*BufferedImage img = null;
        boolean passed = false;
        if (image.exists()) {
            try {
        	img = ImageIO.read(image);
        	passed = true;
            } catch (IOException e) {
        	e.printStackTrace();  
            }
        }
        if (!passed) {
            System.out.println("No image given as first argument.");
            System.out.println("Run \"Searcher <query image>\" to search for <query image>.");
            System.exit(1);
        }
 
        IndexReader ir = DirectoryReader.open(FSDirectory.open(new File("index")));
        ImageSearcher searcher = ImageSearcherFactory.createCEDDImageSearcher(10);
 
        ImageSearchHits hits = searcher.search(img, ir);
        for (int i = 0; i < hits.length(); i++) {
            String fileName = hits.doc(i).getValues(DocumentBuilder.FIELD_NAME_IDENTIFIER)[0];
            System.out.println(hits.score(i) + ": \t" + fileName);
        }*/
	
	return imageResults;
	
    }
    
}