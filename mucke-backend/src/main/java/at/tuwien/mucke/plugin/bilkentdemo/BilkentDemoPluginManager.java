package at.tuwien.mucke.plugin.bilkentdemo;

import at.tuwien.mucke.config.ConfigurationManager;
import at.tuwien.mucke.plugin.PluginManager;
import at.tuwien.mucke.plugin.bilkentdemo.index.BilkenDemoConceptIndexer;
import at.tuwien.mucke.plugin.bilkentdemo.search.BilkentDemoConceptSearcher;
import at.tuwien.mucke.search.Result;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * Runs IR evaluations of the ImageCLEF2011 collection based on the parameterization declared in the properties files. It offers methods for
 * 1) indexing, 2) searching and 3) evaluating results.
 * <p/>
 * The index builds two indexes, one for Wikipedia content and one for metadata. Search is performed first on the documents of the Wiki
 * document collection as defined in the document index and then, for each document, a search is performed on the metadata index. The
 * evaluation generates performance measures based on a gold standard.
 * <p/>
 * Each of the three parts can run independently.
 *
 * @author Ralf Bierig
 */
public class BilkentDemoPluginManager extends PluginManager {

    /**
     * Logging facility
     */
    static Logger logger = Logger.getLogger(BilkentDemoPluginManager.class);


    /**
     * Constructor
     */
    public BilkentDemoPluginManager(ConfigurationManager configManager) {
        super(configManager);
    }


    /**
     * Runs the Plugin
     */
    public void run(String propertiesFile) {
        super.run(propertiesFile);

        logger.info("Start BilkentDemoPluginManager run...");

        // determine current working directory for Python call
        String currentDir = "";
        try {
            currentDir = new File(".").getCanonicalPath();
        } catch (Exception e){
            logger.error("Error while determining current location: " + e.getMessage());
        }

        // step 1: classfication call (Python/Anaconda)
        //String call = "python " + currentDir +
        //"/src/main/java/at/tuwien/mucke/plugin/bilkentdemo/FullClassification.py " +
        //        configManager.getProperty(ConfigConstants.TRAIN_FOLDER) + " " +
        //        configManager.getProperty(ConfigConstants.TEST_FOLDER) + " " +
        //        configManager.getProperty(ConfigConstants.MAPPING_FILE) + " " +
        //        configManager.getProperty(ConfigConstants.RESULT_FOLDER);
        //logger.info("call: " + call);
        //String resultFull = externalCall(call);
        //logger.info("Result full:" + resultFull);

        // Step 2: indexing
        this.index();

        // Step 3: search
        this.search("red");

        logger.info("Done!");

    }

    /** Build concept index */
    public void index(){
        logger.info("index() entered...");
        BilkenDemoConceptIndexer indexer = new BilkenDemoConceptIndexer(configManager);
        indexer.index();
    }

    public List<Result> search(String query){
        logger.info("search() entered ...");
        logger.info("search() query: " + query);
        BilkentDemoConceptSearcher searcher = new BilkentDemoConceptSearcher(configManager);
        return searcher.search(query);
    }

    /** Calls external command
     * @param commandLine
     * @return Result */
    public String externalCall(String commandLine){

        String resultValue = "";
        try {

            // create Runtime environment and call
            Runtime r = Runtime.getRuntime();
            Process p = r.exec(commandLine);
            p.waitFor();

            // log output return values
            BufferedReader b = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = "";
            while ((line = b.readLine()) != null) {
                resultValue = resultValue + line;
            }
            b.close();

        } catch (Exception e){
            logger.error("Exception while executing command: " + e.getMessage());
        }
        return resultValue;

    }

}