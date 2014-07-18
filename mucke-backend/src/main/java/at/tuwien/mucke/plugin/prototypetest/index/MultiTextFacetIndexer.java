package at.tuwien.mucke.plugin.prototypetest.index;

import at.tuwien.mucke.config.ConfigConstants;
import at.tuwien.mucke.config.ConfigurationManager;
import at.tuwien.mucke.index.IndexFieldGenerator;
import at.tuwien.mucke.index.StandardTextFacetIndexer;
import at.tuwien.mucke.util.Util;
import org.apache.commons.io.FilenameUtils;
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

import java.io.*;
import java.util.Date;
import java.util.List;

/**
 * Indexes documents from a collection based on configuration parameters. Each file in the collection contains information for multiple
 * documents. These documents within one file are separated by newline.
 *
 * @author Ralf Bierig
 */
public class MultiTextFacetIndexer extends StandardTextFacetIndexer {

     /**
     * Logging facility
     */
    static Logger logger = Logger.getLogger(MultiTextFacetIndexer.class);

    /**
     * Constructor
     */
    public MultiTextFacetIndexer(String facetName, ConfigurationManager configManager) {
        super(facetName, configManager);
    }


    /**
     * Indexes all text files stored in the given directory
     */
    public void index() {

        // true creates a new index / false updates the existing index
        boolean create = false;

        // check if data directory exists
        logger.debug("content Dir = " + this.contentFolder);
        final File docDir = new File(this.contentFolder);
        if (!docDir.exists() || !docDir.canRead()) {
            logger.error("Document directory '" + docDir.getAbsolutePath()
                    + "' does not exist or is not readable, please check the path");
            System.exit(1);
        }

        // to calculate indexing time as a performance measure
        Date start = new Date();

        try {
            logger.debug("Indexing to directory '" + this.indexFolder + "'...");

            Directory dir = FSDirectory.open(new File(this.indexFolder));
            Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_42);
            IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_42, analyzer);

            if (create) {
                // Create new index, remove previous index
                logger.debug("Creating a new index in directory: '" + this.indexFolder + "'...");
                iwc.setOpenMode(OpenMode.CREATE);
            } else {
                // Add new documents to existing index
                logger.debug("Updating the index in directory: '" + this.indexFolder + "'...");
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


    /**
     * Indexes individual document with a set of configured IndexFieldGenerators.
     *
     * @param writer A writing handle to the index
     * @param file The file to be indexed
     * @param fieldGenerators A list of generators that populate the fields with content
     * @throws IOException
     */
    private void indexDocuments(IndexWriter writer, File file, List<IndexFieldGenerator> fieldGenerators) throws IOException {

        // do not try to index files that cannot be read
        if (file.canRead()) {

            // recursive walk down the directory structure,
            // but not for temporary files that where created by this indexer (allows files to stay and be re-used!)
            if (file.isDirectory() && !file.getName().endsWith("_temp")) {
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

                    // splitting up the file in many lines - each line is one Lucene document
                    List<String> contents = Util.getContents(file, true, "\n");

                    // create temporary file
                    int lineNumber = 1;
                    String fileNameWithoutExtension = file.getName();
                    String tempDir = file.getAbsolutePath() + "_temp/";
                    for (String content : contents) {

                        // file path
                        String path = tempDir + fileNameWithoutExtension + "_" + lineNumber;

                        Util.putContents(new File(path), content, false);
                        lineNumber++;
                    }

                    // get list of all temp files
                    File[] tempFiles = (new File(tempDir)).listFiles();

                    // process each of the temp files as before
                    for (File tempFile : tempFiles) {

                        // create a new, empty document
                        Document doc = new Document();

                        //
                        // default fields each index must have
                        //

                        // extract facetid
                        String facetIdSignature = configManager.getProperty(facetName + ".field.facetid");
                        String facetId = null;

                        if (facetIdSignature.equals("FILENAME")) {
                            facetId = FilenameUtils.removeExtension(tempFile.getName());
                        } else if (facetIdSignature.equals("XPATH")) {
                            logger.info("TODO: implement facetId extraction via xPath!");
                        } else if (facetIdSignature.equals("REGEX")) {
                            logger.info("TODO: implement facetId extraction via regular expression!");
                        } else {
                            logger.error("Error in configuration. facetId has false format. Check parameter: "
                                    + configManager.getProperty(ConfigConstants.DOCINDEX_DOCID));
                        }
                        // check if docId was read successfully, stop if not
                        if (facetId == null) {
                            logger.error("Facet Id unknown for file: '" + tempFile.getName() + "'. Nothing done.");
                            return;
                        }

                        // filename of the indexed file
                        Field filenameField = new StringField("filename", tempFile.getName(), Field.Store.YES);
                        doc.add(filenameField);

                        // path of the indexed file
                        Field pathField = new StringField("path", tempFile.getPath(), Field.Store.YES);
                        doc.add(pathField);

                        // last modified date of the file. Uses a LongField that is indexed (i.e. efficiently
                        // filterable with NumericRangeFilter) which uses a milli-second resolution.
                        doc.add(new LongField("modified", tempFile.lastModified(), Field.Store.NO));


                        //
                        // optional fields defines by fieldGenerators
                        //

                        for (IndexFieldGenerator fieldGenerator : fieldGenerators) {
                            Field f = fieldGenerator.generate(tempFile);

                            // skip entire document, if fields are not complete
                            if (f != null) {
                                doc.add(f);
                            } else {
                                break;    // do not look at any fields following a bad field!
                            }
                        }

                        // the entire contents of the indexed file. FileReader expects the file to be in UTF-8
                        // encoding. If that's not the case searching for special characters will fail.
                        // doc.add(new TextField("contents", new BufferedReader(new InputStreamReader(fis, "UTF-8"))));

                        if (writer.getConfig().getOpenMode() == OpenMode.CREATE) {
                            // New index, so we just add the document (no old document can be there):
                            logger.debug("Adding " + tempFile);
                            writer.addDocument(doc);
                        } else {
                            // Existing index (an old copy of this document may have been indexed) so we use
                            // updateDocument instead to replace the old one matching the exact path, if present:
                            logger.debug("Updating " + tempFile);
                            writer.updateDocument(new Term("path", tempFile.getPath()), doc);
                        }
                        writer.commit();
                    }

                } finally {
                    fis.close();
                }
            }
        }
    }
}