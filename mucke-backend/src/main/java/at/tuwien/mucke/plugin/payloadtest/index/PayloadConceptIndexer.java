package at.tuwien.mucke.plugin.payloadtest.index;

import at.tuwien.mucke.concept.DocumentFacetProcessor;
import at.tuwien.mucke.concept.StandardDocumentFacetProcessor;
import at.tuwien.mucke.config.ConfigurationManager;
import at.tuwien.mucke.index.ConceptIndexer;
import at.tuwien.mucke.plugin.payloadtest.search.PayloadSimilarity;
import at.tuwien.mucke.util.Util;
import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.payloads.FloatEncoder;
import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;


import java.io.File;
import java.io.IOException;
import java.util.*;

public class PayloadConceptIndexer implements ConceptIndexer {

    protected String name = null;
    protected ConfigurationManager configManager = null;
    protected String contentFolder = null;
    protected String indexFolder = null;
    protected DocumentFacetProcessor processor = null;

    /**
     * Logging facility
     */
    static Logger logger = Logger.getLogger(PayloadConceptIndexer.class);

    /**
     * Constructor
     */
    public PayloadConceptIndexer(String name, ConfigurationManager configManager) {

        this.name = name;
        this.configManager = configManager;
        // processsor is hardwired inside the concept indexer
        // implement your own ConceptIndexer or overwrite this class to add another one
        this.processor = new StandardDocumentFacetProcessor(configManager);

        // check index parameters against errors
        this.checkParams();

        // extract content folder
        this.contentFolder = "D:/data/collections/Sandbox";
        //logger.debug("contentFolder: " + contentFolder);

        // extract index folder
        this.indexFolder = "D:/data/indices/Sandbox";
        //logger.debug("indexFolder: " + indexFolder);

    }

    /**
     * Indexes all concepts contained in a collection
     */
    public void index() {

        // true creates a new index / false updates the existing index
        boolean create = false;

        // check if data directory exists
        //logger.debug("content Dir = " + this.contentFolder);
        final File contentFolderDir = new File(this.contentFolder);
        if (!contentFolderDir.exists() || !contentFolderDir.canRead()) {
            logger.error("Document directory '" + contentFolderDir.getAbsolutePath()
                    + "' does not exist or is not readable, please check the path");
            System.exit(1);
        }

        // to calculate indexing time as a performance measure
        Date start = new Date();

        try {

            //logger.debug("Indexing concepts to directory '" + this.indexFolder + "'...");
            Directory dir = FSDirectory.open(new File(this.indexFolder));

            // defines analyzers based on field types; e.g. concept fields use the PayloadAnalyzer
            //Map<String, Analyzer> analyzerPerField = new HashMap<String, Analyzer>();
            //analyzerPerField.put("concept", new PayloadAnalyzer(new FloatEncoder()));
            //PerFieldAnalyzerWrapper analyzer = new PerFieldAnalyzerWrapper(
            //        new StandardAnalyzer(Version.LUCENE_4_10_1), analyzerPerField);

            Analyzer analyzer = new PayloadAnalyzer(new FloatEncoder());
            IndexWriterConfig iwconfig = new IndexWriterConfig(Version.LUCENE_4_10_1, analyzer);
            iwconfig.setSimilarity(new PayloadSimilarity());

            if (create) {
                // Create new index, remove previous index
                //logger.debug("Creating a new concept index in directory: '" + this.indexFolder + "'...");
                iwconfig.setOpenMode(OpenMode.CREATE);
            } else {
                // Add new documents to existing index
                //logger.debug("Updating the concept index in directory: '" + this.indexFolder + "'...");
                iwconfig.setOpenMode(OpenMode.CREATE_OR_APPEND);
            }

            // load mappings
            HashMap<String, String> mappings = this.loadMappings();

            // load concept classifiers
            HashMap<String, HashMap> conceptMaps = this.loadConceptFiles(contentFolderDir);

            // test
            //logger.info("lookup test: blue->1: " + conceptMaps.get("blue").get("1"));

            // index
            IndexWriter writer = new IndexWriter(dir, iwconfig);
            indexDocuments(writer, mappings, conceptMaps);
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
     * @throws java.io.IOException
     */
    private void indexDocuments(IndexWriter writer, HashMap<String, String> fileMappings, HashMap<String, HashMap> concepts) throws IOException {
        logger.info("indexDocuments() entered...");

        // iterate through mappings and create one document for each file
        Set fileSet = fileMappings.keySet();
        Iterator<String> iterator = fileSet.iterator();
        while (iterator.hasNext()){

            // unique file information
            String fileID = iterator.next();
            String filePath = fileMappings.get(fileID);

            // create a new, empty document
            Document doc = new Document();

            // path of the indexed file
            Field pathField = new StringField("path", filePath, Field.Store.YES);
            doc.add(pathField);
            //logger.info("fileID: " + fileID + "  path: " + filePath);

            // lookup all concept probabilities for this fileID
            Iterator<String> conceptIterator = concepts.keySet().iterator();
            while (conceptIterator.hasNext()){
                String conceptName = conceptIterator.next();
                HashMap conceptMap = concepts.get(conceptName);
                //logger.info("--> " + conceptName + ": " + conceptMap.get(fileID));
                // add concept as field and add PAYLOAD as a delimted part the field token
                doc.add(new TextField("concept", ("" + conceptName + "|").trim() + (conceptMap.get(fileID) + "").trim(), Field.Store.YES));
            }

            if (writer.getConfig().getOpenMode() == OpenMode.CREATE) {
                // New index being built
                //logger.debug("Adding " + filePath);
                writer.addDocument(doc);
            } else {
                // Existing index being updated
                //logger.debug("Updating " + filePath);
                writer.updateDocument(new Term("path", filePath), doc);
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

    /** TODO Later to be turned into Reddis directories for more optimized handling */
    private HashMap loadMappings(){

        // check if mappings file exists
        final File mappingsFile = new File(this.contentFolder + "/_mappings.txt");
        if (!mappingsFile.exists() || !mappingsFile.canRead()) {
            logger.error("Mappings file: '" + mappingsFile.getAbsolutePath()
                    + "' does not exist or is not readable, please check the path");
            System.exit(1);
        }

        return Util.getContents(mappingsFile, ",");

    }

    /** TODO Later to be turned into Reddis directories for more optimized handling */
    private HashMap loadConceptFiles(File conceptFileFolder){

        // check if mappings file exists
        if (!conceptFileFolder.exists() || !conceptFileFolder.canRead()) {
            logger.error("Concept file folder: '" + conceptFileFolder.getAbsolutePath()
                    + "' does not exist or is not readable, please check the path");
            System.exit(1);
        }
        if (!conceptFileFolder.isDirectory()){
            logger.error(conceptFileFolder.getAbsolutePath() + " is not a folder!");
        }

        HashMap conceptMap = new HashMap();

        //logger.info("Load all concept files, one after another, and put them into respective HashMaps...");
        File[] files = conceptFileFolder.listFiles();
        for (File file : files){

            // filter files with leading underscore (i.e. _mappings.txt file)
            if (file.getName().startsWith("_")){
                continue;
            }

            // start a new HashMap
            HashMap concept = new HashMap();

            // populate concept
            concept = Util.getContents(file, ",");

            // store concept by its filename (without extension)
            String conceptName = file.getName().trim();
            if (conceptName.endsWith(".txt")){
                conceptName = conceptName.substring(0, conceptName.length()-4);
            }
            //logger.info("conceptName = " + conceptName);
            conceptMap.put(conceptName, concept);
        }

        return conceptMap;

    }

}