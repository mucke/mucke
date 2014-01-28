package mucke.index;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import mucke.config.ConfigurationManager;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

/**
 * Indexes documents from a collection based on configuration parameters.
 * 
 * @author Ralf Bierig
 */
public class StandardTextFacetIndexer implements FacetIndexer {

    private ConfigurationManager configManager = null;

    /** Logging facility */
    static Logger logger = Logger.getLogger(StandardTextFacetIndexer.class);
    
    /** Constructor */
    public StandardTextFacetIndexer(ConfigurationManager configManager) {
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
	    logger.error("Document directory '" + docDir.getAbsolutePath()
		    + "' does not exist or is not readable, please check the path");
	    System.exit(1);
	}

	// to calculate indexing time as a performance measure
	Date start = new Date();

	try {
	    logger.debug("Indexing to directory '" + indexDirectory + "'...");

	    Directory dir = FSDirectory.open(new File(indexDirectory));
	    Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_42);
	    IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_42, analyzer);

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
	    indexDocuments(writer, docDir, fieldGenerators);
	    writer.close();

	    // time stamping
	    Date end = new Date();
	    logger.debug("Indexing time: " + (end.getTime() - start.getTime()) + " total milliseconds");

	} catch (IOException e) {
	    logger.error("Exception: " + e.getMessage());
	}
    }

    /** Indexes individual document with a set of configured IndexFieldGenerators.  
     * @param IndexWriter A writing handle to the index
     * @param File The file to be indexed 
     * @param List<IndexFieldGenerator> A list of generators that populate the fields with content
     * @throws IOException */
    private void indexDocuments(IndexWriter writer, File file, List<IndexFieldGenerator> fieldGenerators) throws IOException {
	
	// do not try to index files that cannot be read
	if (file.canRead()) {
	    
	    if (file.isDirectory()) {
		String[] files = file.list();
		
		// an IO error could occur
		if (files != null) {
		    for (int i = 0; i < files.length; i++) {
			indexDocuments(writer, new File(file, files[i]), fieldGenerators);
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

		    // create a new, empty document
		    Document doc = new Document();

		    //
		    // default fields each index must have
		    //
		    
		    // path of the indexed file
		    Field pathField = new StringField("path", file.getPath(), Field.Store.YES);
		    doc.add(pathField);

		    // filename of the indexed file
		    Field filenameField = new StringField("filename", file.getName(), Field.Store.YES);
		    doc.add(filenameField);

		    // last modified date of the file. Uses a LongField that is indexed (i.e. efficiently
		    // filterable with NumericRangeFilter) which uses a milli-second resolution.
		    doc.add(new LongField("modified", file.lastModified(), Field.Store.NO));

		    //
		    // optional fields defines by fieldGenerators
		    //
		    
		    for (IndexFieldGenerator fieldGenerator : fieldGenerators){
			doc.add(fieldGenerator.generate(file));
		    }		    
		    
		    // the entire contents of the indexed file. FileReader expects the file to be in UTF-8
		    // encoding. If that's not the case searching for special characters will fail.
		    //doc.add(new TextField("contents", new BufferedReader(new InputStreamReader(fis, "UTF-8"))));	    
		    
		    if (writer.getConfig().getOpenMode() == OpenMode.CREATE) {
			// New index, so we just add the document (no old document can be there):
			logger.debug("Adding " + file);
			writer.addDocument(doc);
		    } else {
			// Existing index (an old copy of this document may have  been indexed) so we use 
			// updateDocument instead to replace the old one matching the exact path, if present:
			logger.debug("Updating " + file);
			writer.updateDocument(new Term("path", file.getPath()), doc);
		    }

		} finally {
		    fis.close();
		}
	    }
	}
    }
}