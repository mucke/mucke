package mucke.index;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;

import mucke.config.ConfigConstants;
import mucke.config.ConfigurationManager;
import net.semanticmetadata.lire.DocumentBuilder;
import net.semanticmetadata.lire.DocumentBuilderFactory;
import net.semanticmetadata.lire.impl.ChainedDocumentBuilder;
import net.semanticmetadata.lire.utils.FileUtils;
import net.semanticmetadata.lire.utils.LuceneUtils;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

/**
 * Indexes images from a collection based on configuration parameters.
 * 
 * @author Ralf Bierig
 */
public class StandardImageFacetIndexer implements FacetIndexer {

    private String facetName = null;
    private ConfigurationManager configManager = null;

    /** Logging facility */
    static Logger logger = Logger.getLogger(StandardTextFacetIndexer.class);
    
    /** Constructor */
    public StandardImageFacetIndexer(String facetName, ConfigurationManager configManager) {
	this.facetName = facetName;
	this.configManager = configManager; 
    }

    /**
     * Indexes all text files stored in the given directory
     * 
     * @param contentDirectory File path to content directory
     * @param indexDirectory File path to index directory to be produced
     * @param fieldGenerators A list of field generators that create die fields of the index
     */
    public void index(String contentDirectory, String indexDirectory, List<IndexFieldGenerator> fieldGenerators) {

	// true creates a new index / false updates the existing index
	boolean create = false;

	// check if data directory exists
	logger.debug("content Dir = " + contentDirectory);
	final File docDir = new File(contentDirectory);
	if (!docDir.exists() || !docDir.canRead()) {
	    logger.error("Image directory '" + docDir.getAbsolutePath()
		    + "' does not exist or is not readable, please check the path");
	    System.exit(1);
	}

	// to calculate indexing time as a performance measure
	Date start = new Date();

	try {
	    logger.debug("Indexing to directory '" + indexDirectory + "'...");

	    // Getting all images from a directory and its sub directories.
	    ArrayList<String> images = FileUtils.getAllImages(docDir, true);
	    
	    // Use multiple Lire DocumentBuilder instances 
	    // and set them based on configuration parameter: wikiimage.features
	    String features = facetName + ".features";
	    List<String> featuresValues = configManager.getProperties(features, false);
	    ChainedDocumentBuilder builder = new ChainedDocumentBuilder();
	    for (String featureValue : featuresValues){
		if (featureValue.equals(ConfigConstants.IMAGEFACET_FEATURE_AUTOCOLORCORRELOGRAM)){
		    builder.addBuilder(DocumentBuilderFactory.getAutoColorCorrelogramDocumentBuilder());
		    logger.debug("AutoColorCorrelogram feature added");
		} else if (featureValue.equals(ConfigConstants.IMAGEFACET_FEATURE_CEDD)){
		    builder.addBuilder(DocumentBuilderFactory.getCEDDDocumentBuilder());
		    logger.debug("CEDD feature added");
		} else if (featureValue.equals(ConfigConstants.IMAGEFACET_FEATURE_COLORHIST)){
		    builder.addBuilder(DocumentBuilderFactory.getColorHistogramDocumentBuilder());
		    logger.debug("ColorHistogram feature added");
		} else if (featureValue.equals(ConfigConstants.IMAGEFACET_FEATURE_COLOR)){
		    builder.addBuilder(DocumentBuilderFactory.getColorLayoutBuilder());
		    logger.debug("ColorLayout feature added");
		} else if (featureValue.equals(ConfigConstants.IMAGEFACET_FEATURE_EDGEHIST)){
		    builder.addBuilder(DocumentBuilderFactory.getEdgeHistogramBuilder());
		    logger.debug("EdgeHistogram feature added");
		} else if (featureValue.equals(ConfigConstants.IMAGEFACET_FEATURE_FCTH)){
		    builder.addBuilder(DocumentBuilderFactory.getFCTHDocumentBuilder());
		    logger.debug("FCTH feature added");
		} else if (featureValue.equals(ConfigConstants.IMAGEFACET_FEATURE_FULL)){
		    builder.addBuilder(DocumentBuilderFactory.getFullDocumentBuilder());
		    logger.debug("All available features added");
		} else if (featureValue.equals(ConfigConstants.IMAGEFACET_FEATURE_GABOR)){
		    builder.addBuilder(DocumentBuilderFactory.getGaborDocumentBuilder());
		    logger.debug("Gabor feature added");
		} else if (featureValue.equals(ConfigConstants.IMAGEFACET_FEATURE_JCD)){
		    builder.addBuilder(DocumentBuilderFactory.getJCDDocumentBuilder());
		    logger.debug("JCD feature added");
		} else if (featureValue.equals(ConfigConstants.IMAGEFACET_FEATURE_JOINTHIST)){
		    builder.addBuilder(DocumentBuilderFactory.getJointHistogramDocumentBuilder());
		    logger.debug("JointHistogram feature added");
		} else if (featureValue.equals(ConfigConstants.IMAGEFACET_FEATURE_JPEG)){
		    builder.addBuilder(DocumentBuilderFactory.getJpegCoefficientHistogramDocumentBuilder());
		    logger.debug("JpegCoefficientHistogram feature added");
		} else if (featureValue.equals(ConfigConstants.IMAGEFACET_FEATURE_OPPONENTHIST)){
		    builder.addBuilder(DocumentBuilderFactory.getOpponentHistogramDocumentBuilder());
		    logger.debug("OpponentHistogram feature added");
		} else if (featureValue.equals(ConfigConstants.IMAGEFACET_FEATURE_SCALABLECOLOR)){
		    builder.addBuilder(DocumentBuilderFactory.getScalableColorBuilder());
		    logger.debug("ScalableColor feature added");
		} else if (featureValue.equals(ConfigConstants.IMAGEFACET_FEATURE_TAMURA)){
		    builder.addBuilder(DocumentBuilderFactory.getTamuraDocumentBuilder());
		    logger.debug("Tamura feature added");
		} 
	    }
	    
	    // Creating a Lire IndexWriter
	    IndexWriterConfig iwc = new IndexWriterConfig(LuceneUtils.LUCENE_VERSION,
		    new WhitespaceAnalyzer(LuceneUtils.LUCENE_VERSION));
	    Directory dir = FSDirectory.open(new File(indexDirectory));
	    
	    if (create) {
		// Create new index, remove previous index
		logger.debug("Creating a new index in directory: '" + indexDirectory + "'...");
		iwc.setOpenMode(OpenMode.CREATE);
	    } else {
		// Add new documents to existing index
		logger.debug("Updating the index in directory: '" + indexDirectory + "'...");
		iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);
	    }

	    // index
	    IndexWriter writer = new IndexWriter(dir, iwc);
	    indexDocuments(writer, builder, images, fieldGenerators);
	    writer.close();

	    // time stamping
	    Date end = new Date();
	    logger.debug("Indexing time: " + (end.getTime() - start.getTime()) + " total milliseconds");

	} catch (IOException e) {
	    logger.error("Exception: " + e.getMessage());
	}
	       
    }

    /** Indexes individual images with a set of configured builder features and a set of configured IndexFieldGenerators.  
     * @param IndexWriter A writing handle to the index
     * @param DocumentBuilder A document builder that contains all selected features (based on configuration)
     * @param ArrayList<String> A list of images to be indexed 
     * @param List<IndexFieldGenerator> A list of generators that populate additional fields with content
     * @throws IOException */
    private void indexDocuments(IndexWriter writer, DocumentBuilder builder, ArrayList<String> images, 
	    List<IndexFieldGenerator> fieldGenerators) throws IOException {
	
	// Iterating through images building the low level features
        for (Iterator<String> iterator = images.iterator(); iterator.hasNext(); ) {
            String imageFilePath = iterator.next();
            System.out.println("Indexing " + imageFilePath);
            try {
        	// read image
                BufferedImage img = ImageIO.read(new FileInputStream(imageFilePath));
                
                // create image document based all image features based on configuration 
                Document document = builder.createDocument(img, imageFilePath);
                
                // path of the indexed file
		Field pathField = new StringField("path", imageFilePath, Field.Store.YES);
		document.add(pathField);
		
		
		//
		// need to change it so that we dont work with array of strings with filenames, but with file objects!
		//
		
		
		// extract facetid
		//String facetIdSignature = configManager.getProperty(facetName + ".field.facetid");
		//String facetId = null;
		//if (facetIdSignature.equals("FILENAME")){
		//    facetId = FilenameUtils.removeExtension(file.getName());
		//} else if (facetIdSignature.equals("XPATH")){
		//    logger.info("TODO: implement facetId extraction via xPath!");
		//} else if (facetIdSignature.equals("REGEX")){
		//    logger.info("TODO: implement facetId extraction via regular expression!");
		//} else {
		//    logger.error("Error in configuration. facetId has false format. Check parameter: " + 
		//	    configManager.getProperty(ConfigConstants.DOCINDEX_DOCID));
		//}
		// check if docId was read successfully, stop if not
		//if (facetId == null){
		//    logger.error("Facet Id unknown for file: '" + file.getName() + "'. Nothing done.");
		//    return;
		//} else {
		//    logger.debug("facetId = " + facetId);
		//}
		
		
		// filename of the indexed file
		//Field filenameField = new StringField("filename", file.getName(), Field.Store.YES);
		//document.add(filenameField);

		//
		// optional fields defines by fieldGenerators
		//
		    
		//for (IndexFieldGenerator fieldGenerator : fieldGenerators){
		//    document.add(fieldGenerator.generate(file));
		//}
                
                // index document
		if (writer.getConfig().getOpenMode() == OpenMode.CREATE) {
		    // New index, so we just add the document (no old document can be there):
		    logger.debug("Adding " + imageFilePath);
		    writer.addDocument(document);
		} else {
		    // Existing index (an old copy of this document may have  been indexed) so we use 
		    // updateDocument instead to replace the old one matching the exact path, if present:
		    logger.debug("Updating " + imageFilePath);
		    writer.updateDocument(new Term("path", imageFilePath), document);
		}
                
            } catch (Exception e) {
                System.err.println("Error reading image: " + imageFilePath);
                e.printStackTrace();
            }
        }
    }
}