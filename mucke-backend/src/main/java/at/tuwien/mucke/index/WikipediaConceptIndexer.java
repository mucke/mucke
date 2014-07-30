package at.tuwien.mucke.index;

import at.tuwien.mucke.concept.Concept;
import at.tuwien.mucke.concept.DocumentFacetProcessor;
import at.tuwien.mucke.concept.StandardDocumentFacetProcessor;
import at.tuwien.mucke.config.ConfigurationManager;
import at.tuwien.mucke.documentmodel.TextFacet;
import at.tuwien.mucke.util.Util;
import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * Indexes concepts based on configuration parameters.
 *
 * @author Ralf Bierig
 */
public class WikipediaConceptIndexer implements ConceptIndexer {

    protected String name = null;
    protected ConfigurationManager configManager = null;
    protected String contentFolder = null;
    protected String indexFolder = null;
    protected DocumentFacetProcessor processor = null;

    /**
     * Logging facility
     */
    static Logger logger = Logger.getLogger(WikipediaConceptIndexer.class);

    /**
     * Constructor
     */
    public WikipediaConceptIndexer(String name, ConfigurationManager configManager) {

        this.name = name;
        this.configManager = configManager;
        // processsor is hardwired inside the concept indexer
        // implement your own ConceptIndexer or overwrite this class to add another one
        this.processor = new StandardDocumentFacetProcessor(configManager);

        // check index parameters against errors
        this.checkParams();

        // extract content folder
        this.contentFolder = configManager.getProperty(this.name + ".contentfolder");
        logger.debug("contentFolder: " + contentFolder);

        // extract index folder
        this.indexFolder = configManager.getProperty(this.name + ".indexfolder");
        logger.debug("indexFolder: " + indexFolder);

    }

    /**
     * Indexes all concepts contained in a collection
     */
    public void index() {

        // true creates a new index / false updates the existing index
        boolean create = false;

        // check if data directory exists
        logger.debug("content Dir = " + this.contentFolder);
        final File contentFolderDir = new File(this.contentFolder);
        if (!contentFolderDir.exists() || !contentFolderDir.canRead()) {
            logger.error("Document directory '" + contentFolderDir.getAbsolutePath()
                    + "' does not exist or is not readable, please check the path");
            System.exit(1);
        }

        // to calculate indexing time as a performance measure
        Date start = new Date();

        try {
            logger.debug("Indexing concepts to directory '" + this.indexFolder + "'...");

            Directory dir = FSDirectory.open(new File(this.indexFolder));
            Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_42);
            IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_42, analyzer);

            if (create) {
                // Create new index, remove previous index
                logger.debug("Creating a new concept index in directory: '" + this.indexFolder + "'...");
                iwc.setOpenMode(OpenMode.CREATE);
            } else {
                // Add new documents to existing index
                logger.debug("Updating the concept index in directory: '" + this.indexFolder + "'...");
                iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);
            }

            // index
            IndexWriter writer = new IndexWriter(dir, iwc);
            indexDocuments(writer, contentFolderDir);
            writer.close();

            // time stamping
            Date end = new Date();
            logger.debug("Indexing time: " + (end.getTime() - start.getTime()) + " total milliseconds");

        } catch (IOException e) {
            logger.error("Exception: " + e.getMessage());
        }
    }

    /**
     * Indexes individual documents with concepts as looked up from the Wikipedia index of concepts.
     *
     * @param writer               A writing handle to the index
     * @param file                      The file to be indexed
     * @throws java.io.IOException
     */
    private void indexDocuments(IndexWriter writer, File file) throws IOException {

        // do not try to index files that cannot be read
        if (file.canRead()) {

            if (file.isDirectory()) {
                String[] files = file.list();

                // an IO error could occur
                if (files != null) {
                    for (int i = 0; i < files.length; i++) {
                        indexDocuments(writer, new File(file, files[i]));
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

                    // path of the indexed file
                    Field pathField = new StringField("path", file.getAbsolutePath(), Field.Store.YES);
                    doc.add(pathField);

                    // last modified date of the file. Uses a LongField that is indexed (i.e. efficiently
                    // filterable with NumericRangeFilter) which uses a milli-second resolution.
                    doc.add(new LongField("modified", file.lastModified(), Field.Store.NO));

                    // translate into concepts
                    String id = file.getAbsolutePath();
                    String name = file.getName();
                    String contents =  Util.getContents(file, true);
                    List<Concept> concepts = processor.process(new TextFacet(id, name, contents));

                    logger.info("CONCEPTS:");
                    for (Concept c : concepts){
                        logger.info("Concept: " + c.getId());
                    }


                    // add facet contents to index
                    doc.add(new TextField("contents", contents, Field.Store.YES));

                    // add all concepts to the index
                    for (Concept concept : concepts){
                        doc.add(new StringField("concept", concept.getId().toString(), Field.Store.YES));
                    }

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

    @Override
    public boolean checkParams() {

        // checks content folder
        String indexContentFolderProperty = this.name + ".contentfolder";
        if (!configManager.isProperty(indexContentFolderProperty)) {
            logger.error("No content folder provided for facet index " + this.name +
                    ". You must declare a content folder in the properties.");
            return false;
        }

        // checks index folder
        String indexFolderProperty = this.name + ".indexfolder";
        if (!configManager.isProperty(indexFolderProperty)) {
            logger.error("No index folder provided for facet index " + this.name +
                    ". You must declare an index folder in the properties.");
            return false;
        }
        return true;
    }

}