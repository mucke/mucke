package at.tuwien.mucke.plugin.bilkentdemo.index;

import at.tuwien.mucke.config.ConfigurationManager;
import at.tuwien.mucke.plugin.bilkentdemo.ConfigConstants;
import at.tuwien.mucke.util.Util;
import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.payloads.FloatEncoder;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class BilkenDemoConceptIndexer {

    /**
     * Logging facility
     */
    static Logger logger = Logger.getLogger(BilkenDemoConceptIndexer.class);

    protected ConfigurationManager configManager = null;

    /**
     * Constructor
     */
    public BilkenDemoConceptIndexer(ConfigurationManager configManager) {

        this.configManager = configManager;

        // check index parameters against errors
        //this.checkParams();
    }


    public void index() {
        try {

            logger.info("Index will be written to: " + configManager.getProperty(ConfigConstants.CONCEPT_INDEX_FOLDER));

            Directory dir = FSDirectory.open(new File(configManager.getProperty(ConfigConstants.CONCEPT_INDEX_FOLDER)));
            Analyzer analyzer = new BilkentDemoPayloadAnalyzer(new FloatEncoder());
            IndexWriterConfig iwconfig = new IndexWriterConfig(Version.LUCENE_4_10_1, analyzer);
            iwconfig.setSimilarity(new BilkentDemoPayloadSimilarity());
            iwconfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE);

            // load mappings and classifiers
            HashMap<String, String> mappings = this.loadDataMappings();
            HashMap<String, HashMap> cMaps = this.loadData();

            IndexWriter writer = new IndexWriter(dir, iwconfig);
            indexDocuments(writer, mappings, cMaps);
            writer.commit();
            writer.close();

        } catch (IOException e) {
            System.out.println("Exception while indexing: " + e.getMessage());
        }
    }

    private void indexDocuments(IndexWriter writer, HashMap<String, String> fileMappings, HashMap<String, HashMap> concepts) throws IOException {

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
            // lookup all concept probabilities for this fileID
            Iterator<String> conceptIterator = concepts.keySet().iterator();
            while (conceptIterator.hasNext()){
                String conceptName = conceptIterator.next();
                HashMap conceptMap = concepts.get(conceptName);
                doc.add(new TextField("concept", ("" + conceptName + "|").trim() + (conceptMap.get(fileID) + "").trim(), Field.Store.YES));
            }
            writer.addDocument(doc);
        }
    }

    /** TODO Later to be turned into Reddis directories for more optimized handling */
    private HashMap loadDataMappings(){

        // load mapping file
        File mappingsFile = new File(configManager.getProperty(ConfigConstants.MAPPING_FILE));
        logger.info("Mappings file: " + mappingsFile.getAbsolutePath());
        if (!mappingsFile.exists() || !mappingsFile.canRead()) {
            logger.error("Mappings file: '" + mappingsFile.getAbsolutePath()
                    + "' does not exist or is not readable, please check the path");
            System.exit(1);
        }

        return Util.getContents(mappingsFile, ",");

    }

    /** TODO Later to be turned into Reddis directories for more optimized handling */
    private HashMap<String, HashMap> loadData(){

        // load data files
        File conceptFolder = new File(configManager.getProperty(ConfigConstants.RESULT_FOLDER));
        if (!conceptFolder.exists() || !conceptFolder.canRead()) {
            logger.error("Concept file folder: '" + conceptFolder.getAbsolutePath()
                    + "' does not exist or is not readable, please check the path: " + ConfigConstants.RESULT_FOLDER);
            System.exit(1);
        }
        if (!conceptFolder.isDirectory()){
            logger.error(conceptFolder.getAbsolutePath() + " is not a folder!");
        }

        HashMap conceptMap = new HashMap();

        //logger.info("Load all concept files, one after another, and put them into respective HashMaps...");
        File[] files = conceptFolder.listFiles();
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