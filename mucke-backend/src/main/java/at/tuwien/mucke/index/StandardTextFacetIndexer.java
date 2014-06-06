package at.tuwien.mucke.index;

import at.tuwien.mucke.config.ConfigConstants;
import at.tuwien.mucke.config.ConfigurationManager;
import org.apache.commons.io.FilenameUtils;
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Indexes documents from a collection based on configuration parameters.
 *
 * @author Ralf Bierig
 */
public class StandardTextFacetIndexer implements FacetIndexer {

    protected String facetName = null;
    protected ConfigurationManager configManager = null;
    protected String contentFolder = null;
    protected String indexFolder = null;
    protected List<IndexFieldGenerator> fieldGenerators = null;

    /**
     * Logging facility
     */
    static Logger logger = Logger.getLogger(StandardTextFacetIndexer.class);

    /**
     * Constructor
     */
    public StandardTextFacetIndexer(String facetName, ConfigurationManager configManager) {
        this.facetName = facetName;
        this.configManager = configManager;

        // check index parameters against errors
        this.checkParams();

        // extract content folder
        this.contentFolder = configManager.getProperty(this.facetName + ".contentfolder");
        logger.debug("contentFolder: " + contentFolder);

        // extract index folder
        this.indexFolder = configManager.getProperty(this.facetName + ".indexfolder");
        logger.debug("indexFolder: " + indexFolder);

        // create generators
        this.fieldGenerators = prepareFieldGenerators(this.facetName);
        if (this.fieldGenerators == null){
            logger.debug("Currently no fieldGenerators active!");
        } else {
            logger.debug("Number of active fieldGenerators:" + this.fieldGenerators.size());
        }
    }


    /**
     * Prepares IndexFieldGenerator Objects, that contain the method with which an index field is filled with
     * data from a content file during indexing. A so-called signature is used for that (e.g. a XML query or a
     * regular expression). The information for building the index field names and the generator objects are
     * obtained from the configuration file.
     *
     * @param indexName The name of the index is used to find the configuration for this index in the properties file
     * @return A list of IndexFieldGenerator objects
     */
    private List<IndexFieldGenerator> prepareFieldGenerators(String indexName) {

        String indexFieldsProperty = indexName + ".fields";
        //logger.debug("index field: " + indexFieldsProperty);
        String indexFieldsGeneratorProperty = indexFieldsProperty + ".generator";
        //logger.debug("generator field: " + indexFieldsGeneratorProperty);

        // Collection of IndexFieldGenerators, one per field
        List<IndexFieldGenerator> generators = new ArrayList<IndexFieldGenerator>();

        // extract IndexFieldGenerator information from configuration
        List<String> generatorProperties = configManager.getProperties(indexFieldsGeneratorProperty, false); // simple lookup
        if (generatorProperties.size() != 1) {
            logger.error("There must be exactly on IndexFieldGenerator per Index. Check your configuration.");
        }

        // check if index fields and generator are accurately declared
        this.checkParams();

        // read index configuration (fields and signatures for fields)
        List<String> indexFields = configManager.getProperties(indexFieldsProperty, false);

        // extract index fields
        for (String indexField : indexFields) {

            // option NONE stops the process
            if (indexField.equals("NONE")) {
                logger.debug("No Index Fields declared. Nothing to parse.");
                continue;
            }

            List<String> values = configManager.getProperties(indexField, false);    // simple lookup

            if (values.size() != 2) {
                logger.error("Index field '" + indexField + "' is incorrect. It requires one name and one signature. Please modify configuration.");
                break;
            }

            // extract field, signature pair
            String fieldToken = values.get(0);
            String signatureToken = values.get(1);

            try {

                // create generator object
                IndexFieldGenerator generator = (IndexFieldGenerator) configManager.getIndexFieldGeneratorClass(
                        generatorProperties.get(0), fieldToken, signatureToken);

                // add to list
                generators.add(generator);

            } catch (Exception e) {
                logger.error("Exception while creating IndexFieldGenerator. Class either does not exist or " +
                        "has no such constructor. Check configuration file.");
            }
        }

        return generators;
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
     * @param writer               A writing handle to the index
     * @param file                      The file to be indexed
     * @param fieldGenerators A list of generators that populate the fields with content
     * @throws IOException
     */
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

                    // extract facetid
                    String facetIdSignature = configManager.getProperty(facetName + ".field.facetid");
                    String facetId = null;
                    if (facetIdSignature.equals("FILENAME")) {
                        facetId = FilenameUtils.removeExtension(file.getName());
                    } else if (facetIdSignature.equals("XPATH")) {
                        logger.info("TODO: implement facetId extraction via xPath!");
                    } else if (facetIdSignature.equals("REGEX")) {
                        logger.info("TODO: implement facetId extraction via regular expression!");
                    } else {
                        logger.error("Error in configuration. facetId has false format. Check parameter: " +
                                configManager.getProperty(ConfigConstants.DOCINDEX_DOCID));
                    }
                    // check if docId was read successfully, stop if not
                    if (facetId == null) {
                        logger.error("Facet Id unknown for file: '" + file.getName() + "'. Nothing done.");
                        return;
                    }

                    // filename of the indexed file
                    Field filenameField = new StringField("filename", file.getName(), Field.Store.YES);
                    doc.add(filenameField);

                    // path of the indexed file
                    Field pathField = new StringField("path", file.getPath(), Field.Store.YES);
                    doc.add(pathField);

                    // last modified date of the file. Uses a LongField that is indexed (i.e. efficiently
                    // filterable with NumericRangeFilter) which uses a milli-second resolution.
                    doc.add(new LongField("modified", file.lastModified(), Field.Store.NO));
                    //
                    // optional fields defines by fieldGenerators
                    //

                    for (IndexFieldGenerator fieldGenerator : fieldGenerators) {
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


    @Override
    public boolean checkParams() {

        // check indexName
        if (this.facetName == null || this.facetName.equals("")) {
            logger.error("No indexer defined. You must at least declare one indexer in the properties.");
            return false;
        }
        if (!configManager.isProperty(this.facetName)) {
            logger.error("No such indexer '" + this.facetName + "'. You must at least declare one indexer in the properties.");
            return false;
        }

        // checks content folder
        String indexContentFolderProperty = this.facetName + ".contentfolder";
        if (!configManager.isProperty(indexContentFolderProperty)) {
            logger.error("No content folder provided for facet index " + this.facetName +
                    ". You must declare a content folder in the properties.");
            return false;
        }

        // checks index folder
        String indexFolderProperty = this.facetName + ".indexfolder";
        if (!configManager.isProperty(indexFolderProperty)) {
            logger.error("No index folder provided for facet index " + this.facetName +
                    ". You must declare an index folder in the properties.");
            return false;
        }

        // checks facet id -- every facet needs an ID
        String facetIdProperty = this.facetName + ".field.facetid";
        if (!configManager.isProperty(facetIdProperty) || configManager.getProperty(facetIdProperty) == null || configManager.getProperty(facetIdProperty).equals("")) {
            logger.error("No facet id defined for index '" + this.facetName + "'. You must declare one id field for this index.");
            logger.debug("facet index '" + this.facetName + "' is property?" + configManager.isProperty(facetIdProperty));
            logger.debug("facetIdProperty: " + facetIdProperty);
            return false;
        }

        // check index fields definition
        String indexFieldsProperty = this.facetName + ".fields";
        if (!configManager.isProperty(indexFieldsProperty)) {
            logger.error("No index fields for " + this.facetName + ". You must at least declare one index field in the properties.");
            return false;
        }

        // check IndexFieldGenerator
        String indexFieldsGeneratorProperty = indexFieldsProperty + ".generator";
        if (!configManager.isProperty(indexFieldsGeneratorProperty)) {
            logger.error("No index field generator defined for '" + this.facetName + "'. Please declare in the properties.");
            return false;
        }

        // check index fields in detail
        List<String> fields = configManager.getProperties(indexFieldsProperty, false);
        if (fields.size() == 0) {
            logger.error("No index fields for " + this.facetName + ". You must at least declare one index field in the properties.");
            return false;
        }
        List<String> generators = configManager.getProperties(indexFieldsGeneratorProperty, false);
        if (generators.size() == 0) {
            logger.error("No index field generator defined for " + this.facetName + ". Please declare in the properties.");
            return false;
        } else if (generators.size() > 1) {
            logger.error("No index field generator defined for '" + this.facetName + "'. Please declare in the properties.");
            return false;
        }

        // at this point, everything looked fine
        return true;
    }


    /**
     * Verifies the common parameters for an text index. Index parameters consist of:
     * <li> The indexer (defined e.g. defined in textindex)</li>
     * <li> The content folder where the indexer can find the content </li>
     * <li> The index folder where the indexer places the index </li>
     * <li> The facet id that defines what is used as a unique identifier for the indexed items in this facet </li>
     * <li> A list of index fields that are declared afterwards by a name and a signature. At least one
     * index field needs to be declared. </li>
     * <li> An IndexFieldGenerator (defined e.g. in textindex.fields.generator). The IndexFieldGenerator is a
     * class that transforms content into Lucene index fields based on the name and the signature of the field
     * provided by the list. The class is named by it fully qualified name (e.g. at.tuwien.mucke.index.IndexFieldGenerator)</li>
     * </ul>
     */
    public String explainParams() {
        return "";

    }
}