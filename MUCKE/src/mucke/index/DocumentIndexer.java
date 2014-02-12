package mucke.index;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import mucke.config.ConfigConstants;
import mucke.config.ConfigurationManager;
import mucke.data.DBConstants;
import mucke.util.xml.XMLTools;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.w3c.dom.NodeList;

/** The DocumentIndexer combines all information about the facet indexes in a database table for quick lookup. */
public class DocumentIndexer {

    static Logger logger = Logger.getLogger(DocumentIndexer.class);
    
    private int counter = 0;

    public final static String FILENAME_DOCID = "FILENAME";

    /** Defines the location of the document id (e.g. the XPath or by constant (e.g. FILENAME) */
    private String docIdSignature = null;

    /** Handle for Database manager to prepare index table and store index */
    private ConfigurationManager configManager = null;

    /**
     * Constructs the document indexer
     * 
     * @param dbManager Handle to the DatabaseManager for writing the index to the database
     * @param docIdSignature The signature of how to find the document identifier
     */
    public DocumentIndexer(ConfigurationManager configManager, String docIdSignature) {
	this.docIdSignature = docIdSignature;
	this.configManager = configManager;
    }

    /**
     * Creates the document index
     * 
     * @param contentDirectory The location of the documents that input to the document index
     * @param signatures A list of signatures that help extract facet id's from content
     */
    public void index(String contentDirectory, List<FacetIdSignature> signatures) {

	// check if data directory exists
	final File docDir = new File(contentDirectory);
	if (!docDir.exists() || !docDir.canRead()) {
	    logger.error("Document directory '" + docDir.getAbsolutePath() + "' does not exist or is not readable, please check the path");
	    System.exit(1);
	}

	// to calculate indexing time as a performance measure
	Date start = new Date();

	// empty table for index
	configManager.getDbManager().emptyTable(DBConstants.DOCINDEX_TABLE_NAME);

	try {

	    // index
	    indexDocuments(docDir, signatures);

	} catch (IOException e) {
	    logger.error("Exception: " + e.getMessage());
	}

	// time stamping
	Date end = new Date();
	logger.debug("Statistics: Total indexing time was " + (end.getTime() - start.getTime()) / 1000 + " total seconds");
	logger.debug("Statistics: Total number of document IDs: " + counter);
    }

    /** Indexes individual files, or traverses folder structures recursively to get to individual files.
     * @param file
     * @param signatures
     * @throws IOException
     */
    private void indexDocuments(File file, List<FacetIdSignature> signatures) throws IOException {

	//logger.debug("----- Indexing file: " + file.getName() + " ------");
	
	// do not try to index files that cannot be read
	if (file.canRead()) {

	    if (file.isDirectory()) {
		String[] files = file.list();

		// an IO error could occur
		if (files != null) {
		    for (int i = 0; i < files.length; i++) {
			indexDocuments(new File(file, files[i]), signatures);
		    }
		}

	    } else {

		FileInputStream fis;
		try {
		    fis = new FileInputStream(file);
		} catch (FileNotFoundException fnfe) {
		    // at least on windows, some temporary files raise this
		    // exception with an "access denied" message
		    // checking if the file can be read doesn't help
		    return;
		}

		try {
		    
		    String docId = null;
		    // Extract document Id
		    if (docIdSignature.equals("FILENAME")){
			docId = FilenameUtils.removeExtension(file.getName());
		    } else if (docIdSignature.equals("XPATH")){
			logger.info("TODO: implement docId extraction via xPath!");
		    } else if (docIdSignature.equals("REGEX")){
			logger.info("TODO: implement docId extraction via regular expression!");
		    } else {
			logger.error("Error in configuration. DocId has false format. Check parameter: " + 
				configManager.getProperty(ConfigConstants.DOCINDEX_DOCID));
		    }
		    // check if docId was read successfully, stop if not
		    if (docId == null){
			logger.error("Document Id unknown for file: '" + file.getName() + "'. Nothing done.");
			return;
		    } else {
			//logger.debug("docId = " + docId);
		    }
		    
		    // reading all facet signatures
		    for (FacetIdSignature signature : signatures){
			
			String sig = signature.getIdSignature();

			// collecting facet ids
			List<String> facetIds = new ArrayList<String>();
			
			// first check the default constants where facet ids can be located  
			if (sig.equals("FILENAME")){
			    facetIds.add(FilenameUtils.removeExtension(file.getName()));
			// than check their declared format
			} else {
			    if (configManager.getProperty(ConfigConstants.DOCINDEX_FACET_SIG_FORMAT).equals("XPATH")){
				    
				    // extract nodes
				    NodeList nodes = XMLTools.getNodes(file, sig);
				    if (nodes != null && nodes.getLength() > 0) {
					//logger.debug("length=" + nodes.getLength());
					for (int i = 0; i < nodes.getLength(); i++){
					    facetIds.add(nodes.item(i).getTextContent().trim());
					    //logger.debug("facetId added!");
					}
				    } else {
					logger.debug("Empty nodelist!");
				    }
				    
				} else if (configManager.getProperty(ConfigConstants.DOCINDEX_FACET_SIG_FORMAT).equals("REGEX")){
				    logger.debug("Lets do RegEx!");
				} else {
				    logger.error("Error in configuraton file. Facet Signature has wrong format: '" + 
					    configManager.getProperty(ConfigConstants.DOCINDEX_FACET_SIG_FORMAT) + "'");
				}
			}

			
			// check if facetId was read successfully, stop if not
			if (facetIds.size() == 0){
			    logger.error("Facet Id unknown for file: '" + file.getName() + "'. Nothing done.");
			    return;
			}
			
			// write facet in database
			for (int i = 0; i < facetIds.size(); i++){
			    configManager.getDbManager().insertDocument(docId, facetIds.get(i), signature.getName(), signature.getType());			    
			}
			
		    }
		    
		    // counts the number of document IDs indexed
		    counter++;

		} finally {
		    fis.close();
		}
	    }
	}
    }
}